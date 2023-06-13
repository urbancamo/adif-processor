package uk.m0nom.adifproc.adif3.xsdquery;


import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Not currently used but potentially this will allow loading of the entire Adif3 schema for use when parsing comments
 * TODO
 */
public class Adif3SchemaLoader {
    private static final Logger logger = Logger.getLogger(Adif3SchemaLoader.class.getName());


    public static Adif3Schema loadFromFile(String filename) throws FileNotFoundException {
        FileInputStream fileIS = new FileInputStream(filename);
        return loadAdif3Schema(fileIS);
    }

    public static Adif3Schema loadAdif3Schema(InputStream stream) {
        Adif3Schema schema = new Adif3Schema();
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(stream);
            parseTypes(schema, xmlDocument);
            parseFields(schema, xmlDocument);
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
            logger.severe(String.format("Exception %s generated loading schema", e.getMessage()));
            return null;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                logger.severe(String.format("Exception %s generated on schema close", e.getMessage()));
            }
        }
        return schema;
    }

    private static void parseTypes(Adif3Schema schema, Document xmlDocument) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "/schema/simpleType";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        schema.setTypes(getTypesFromNodeList(nodeList));
    }

    private static void parseFields(Adif3Schema schema, Document xmlDocument) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "/schema/element[@name='ADX']/complexType/sequence/element[@name='RECORDS']/complexType/sequence/element[@name='RECORD']/complexType/choice/element";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
        setFieldsFromNodeList(schema, nodeList);
    }

    private static List<String> UNSUPPORTED_FIELDS = Arrays.asList("APP", "USERDEF");

    private static void setFieldsFromNodeList(Adif3Schema schema, NodeList nodes) {
        Set<Adif3Field> elements = new HashSet<>();

        for (int n = nodes.getLength() - 1; n >= 0; n--) {
            Node child = nodes.item(n);
            short nodeType = child.getNodeType();
            if (nodeType == Node.ELEMENT_NODE) {
                Element e = (Element)child;
                String name = e.getAttribute("name");
                if (UNSUPPORTED_FIELDS.contains(name)) {
                    continue;
                }
                String type = e.getAttribute("type");
                Boolean nillable = null;
                String nillableAttr = e.getAttribute("nillable");
                switch (nillableAttr) {
                    case "true":
                        nillable = Boolean.TRUE;
                        break;
                    case "false":
                        nillable = Boolean.FALSE;
                        break;
                }
                Adif3Field field = new Adif3Field(name, schema.getType(type), nillable);
                elements.add(field);

                // Field may have its own type declaration
                if (child.hasChildNodes()) {
                    setFieldSpecificType(schema, field, child.getChildNodes());
                }
            }
        }
        schema.setFields(elements);
    }

    private static void setFieldSpecificType(Adif3Schema schema, Adif3Field field, NodeList nodes) {
        for (int n = nodes.getLength() - 1; n >= 0; n--) {
            Node child = nodes.item(n);
            if ("xs:simpleType".equals(child.getNodeName())) {
                Adif3Type fieldSpecificType = parseTypeNode(child);
                fieldSpecificType.setName(field.getName());
                schema.addType(fieldSpecificType);
                field.setType(fieldSpecificType);
            }
        }
    }

    private static Set<Adif3Type> getTypesFromNodeList(NodeList nodes) {
        Set<Adif3Type> types = new HashSet<>();

        //  <xs:simpleType name="Number">
        //    <xs:restriction base="xs:decimal">
        //      <xs:pattern value="[0-9\.\-]+" />
        //    </xs:restriction>
        //  </xs:simpleType>

        for (int n = nodes.getLength() - 1; n >= 0; n--) {
            Node child = nodes.item(n);
            if ("xs:simpleType".equals(child.getNodeName())) {
                types.add(parseTypeNode(child));
            }
        }

        return types;
    }

    private static Adif3Type parseTypeNode(Node typeNode) {
        Adif3Type type = new Adif3Type();

        if (typeNode.getAttributes().getNamedItem("name") != null) {
            type.setName(typeNode.getAttributes().getNamedItem("name").getNodeValue());
        }
        NodeList nodes = typeNode.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node child = nodes.item(i);
            switch (child.getNodeName()) {
                case "xs:restriction":
                    parseTypeNodeRestriction(type, child);
                    break;
            }
        }
        return type;
    }

    private static void parseTypeNodeRestriction(Adif3Type type, Node node) {
        NamedNodeMap attributes = node.getAttributes();
        Node baseType = attributes.getNamedItem("base");
        if (baseType != null) {
            type.setBaseType(baseType.getNodeValue());
            if (node.getChildNodes() != null && node.getChildNodes().getLength() > 0) {
                parseTypeNodeRestrictionOptions(type, node.getChildNodes());
            }
        }
    }

    private static void parseTypeNodeRestrictionOptions(Adif3Type type, NodeList options) {
        for (int i = 0; i < options.getLength(); i++) {
            Node child = options.item(i);
            switch (child.getNodeName()) {
                case "xs:pattern":
                    type.setRegex(Pattern.compile(child.getAttributes().getNamedItem("value").getNodeValue()));
                    break;
                case "xs:whiteSpace":
                    type.setPreserveWhiteSpace("preserve".equals(getAttributeValue(child)));
                    break;
                case "xs:minInclusive":
                    type.setMinInclusive(Integer.parseInt(getAttributeValue(child)));
                    break;
                case "xs:maxInclusive":
                    type.setMaxInclusive(Integer.parseInt(getAttributeValue(child)));
                    break;
            }
        }
    }

    private static String getAttributeValue(Node node) {
        return node.getAttributes().getNamedItem("value").getNodeValue();
    }
}
