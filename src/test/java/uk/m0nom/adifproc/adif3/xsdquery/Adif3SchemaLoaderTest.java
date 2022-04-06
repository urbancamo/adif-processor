package uk.m0nom.adifproc.adif3.xsdquery;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class Adif3SchemaLoaderTest {

    @Test
    public void testSchemaLoader() throws FileNotFoundException {
        Adif3SchemaLoader loader = new Adif3SchemaLoader();
        Set<Adif3Element> elements = loader.loadFromFile("src/main/resources/adif/adx312generic.xsd");
        assertThat(elements.size()).isEqualTo(155);
        //System.out.printf("Loaded %s elements%n", elements.size());
        //for (Adif3Element element : elements) {
        //    System.out.println(element);
        //}
    }
}
