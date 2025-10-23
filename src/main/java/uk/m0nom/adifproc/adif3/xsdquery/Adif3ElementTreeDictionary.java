package uk.m0nom.adifproc.adif3.xsdquery;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Component
public class Adif3ElementTreeDictionary implements  Adif3ElementDictionary {
    private final DictionaryTreeNode root;

    public Adif3ElementTreeDictionary() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("adif/adx312generic.xsd");
        Adif3SchemaLoader loader = new Adif3SchemaLoader();
        Adif3Schema schema = loader.loadAdif3Schema(inputStream);
        Map<String, Adif3Field> dictionary = new HashMap<>();

        assert schema != null;
        for (Adif3Field field : schema.getFields().values()) {
            dictionary.put(field.getName(), field);
        }

        // Start the tree
        root = new DictionaryTreeNode(null, 'M');

        // Now incrementally strip each of the element names back, so they are as short as possible but still unique
        // We do this on a stream sorted by name length, the smallest first
        dictionary.keySet().stream().sorted(Comparator.comparingInt(String::length)).forEach(name -> {
            DictionaryTreeNode current = root;
            for (Character c : name.toCharArray()) {
                current = current.add(c);
            }
            current.setKeyword(name);
        });
    }

    @Override
    public Adif3Field getField(String name) {
        return null;
    }
}
