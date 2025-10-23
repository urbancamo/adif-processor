package uk.m0nom.adifproc.adif3.xsdquery;

import org.codehaus.plexus.util.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class Adif3SchemaLoaderTest {

    @Test
    public void testSchemaLoader() throws FileNotFoundException {
        Adif3SchemaLoader loader = new Adif3SchemaLoader();
        Adif3Schema schema = loader.loadFromFile("src/main/resources/adif/adx316.xsd");
        assertThat(schema.getFields().size()).isEqualTo(179);
        System.out.printf("Loaded %s elements%n", schema.getFields().size());
        //for (Adif3Type type : schema.getTypes().values()) {
        //    System.out.println(type.toString());
        //}

        //schema.getFields().values().stream().map(Adif3Field::getName).sorted().forEach(System.out::println);
        System.out.println();
        System.out.println("|ADIF Field|ADIF Type|");
        System.out.println("|----------|---------|");
        for (Adif3Field field : schema.getFields().values().stream().sorted().toList()) {
            System.out.printf("| %s | %s |%n", field.getName(), prettyPrintTypeName(field.getType().getName()));
        }

        System.out.println();
        System.out.println("|ADIF Type|Base Type|Min|Max|");
        System.out.println("|---------|---------|---|---|");
        for (Adif3Type type : schema.getTypes().values().stream().sorted().toList()) {
            String name = prettyPrintTypeName(type.getName());
            String baseType = type.getBaseType() != null ? prettyPrintTypeName(type.getBaseType()) : "";
            String min = type.getMinInclusive() != null ? type.getMinInclusive().toString() : "";
            String max = type.getMaxInclusive() != null ? type.getMaxInclusive().toString() : "";
            System.out.printf("| %s | %s | %s | %s |\n", name, baseType, min, max);
        }
    }

    private static String prettyPrintTypeName(String name) {
        String fieldTypePretty = "";
        if (name != null) {
            fieldTypePretty = name;
            fieldTypePretty = fieldTypePretty.trim().replace('_', ' ');
            Matcher m = Pattern.compile("(?<=[a-z])[A-Z]").matcher(fieldTypePretty);
            fieldTypePretty = m.replaceAll(match -> " " + match.group());
            fieldTypePretty = fieldTypePretty.replace("xs:" ,"");
            fieldTypePretty = StringUtils.capitalise(fieldTypePretty);
        }
        return fieldTypePretty;
    }
}
