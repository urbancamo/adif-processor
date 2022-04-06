package uk.m0nom.adifproc.adif3.xsdquery;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Adif3ElementDictionaryTest {
    @Test
    public void testDictionaryCreation() {
        Adif3ElementDictionary dictionary = new Adif3ElementDictionary();
        Adif3Element opElement = dictionary.getElement("OP");
        assertThat(opElement.getName()).isEqualTo("OPERATOR");

        dictionary.getDictionary().keySet().stream().sorted().forEach(a -> System.out.printf("%s : %s\n", a, dictionary.getElement(a)));
    }
}
