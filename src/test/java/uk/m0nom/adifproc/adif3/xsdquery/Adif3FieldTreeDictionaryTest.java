package uk.m0nom.adifproc.adif3.xsdquery;

import org.junit.jupiter.api.Test;

public class Adif3FieldTreeDictionaryTest {
    @Test
    public void testDictionaryCreation() {
        Adif3ElementTreeDictionary dictionary = new Adif3ElementTreeDictionary();

        Adif3Field opElement = dictionary.getField("OP");
        //(opElement.getName()).isEqualTo("OPERATOR");

        //dictionary.getDictionary().keySet().stream().sorted().forEach(a -> System.out.printf("%s : %s\n", a, dictionary.getElement(a)));
    }
}
