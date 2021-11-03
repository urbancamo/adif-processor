package uk.m0nom.adif3.xsdquery;

import org.junit.Ignore;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Set;

public class Adif3SchemaLoaderTest {

    @Test
    @Ignore
    public void testSchemaLoader() throws FileNotFoundException {
        Adif3SchemaLoader loader = new Adif3SchemaLoader();
        Set<Adif3Element> elements = loader.loadFromFile("adif/adx312generic.xsd");
        System.out.println(String.format("Loaded %s elements", elements.size()));
        for (Adif3Element element : elements) {
            System.out.println(element);
        }
    }
}
