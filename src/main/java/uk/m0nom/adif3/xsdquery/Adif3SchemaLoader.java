package uk.m0nom.adif3.xsdquery;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
import java.util.HashSet;
import java.util.Set;

/**
 * Not currently used but potentially this will allow loading of the entire Adif3 schema for use when parsing comments
 * TODO
 */
public class Adif3SchemaLoader {
    public Set<Adif3Element> loadFromFile(String filename) throws FileNotFoundException {
        FileInputStream fileIS = new FileInputStream(filename);
        return loadAdif3Schema(fileIS);
    }

    public Set<Adif3Element> loadAdif3Schema(InputStream stream) {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(stream);
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/schema/element[@name='ADX']/complexType/sequence/element[@name='RECORDS']/complexType/sequence/element[@name='RECORD']/complexType/choice/element";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);
            stream.close();
            return getElementsFromNodeList(nodeList);
        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Set<Adif3Element> getElementsFromNodeList(NodeList childs) {
        Set<Adif3Element> elements = new HashSet<>();

        for (int n = childs.getLength() - 1; n >= 0; n--) {
            Node child = childs.item(n);
            short nodeType = child.getNodeType();
            if (nodeType == Node.ELEMENT_NODE) {
                Element e = (Element)child;
                String name = e.getAttribute("name");
                String type = e.getAttribute("type");
                elements.add(new Adif3Element(name, type));
            }
        }
        return elements;
    }
}
