package uk.m0nom.adifproc.adif3.xsdquery;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Adif3FieldHashedDictionaryTest {
    @Test
    public void testDictionaryCreation() {
        Adif3ElementHashedDictionary dictionary = new Adif3ElementHashedDictionary();
        Adif3Field opElement = dictionary.getField("OP");
        assertThat(opElement.getName()).isEqualTo("OPERATOR");

        dictionary.getDictionary().keySet().stream().sorted().forEach(a -> System.out.printf("|%s|%s|\n", a, dictionary.getField(a).getName()));
    }
}
