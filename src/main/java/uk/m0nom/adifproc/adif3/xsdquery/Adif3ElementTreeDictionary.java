package uk.m0nom.adifproc.adif3.xsdquery;

import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Adif3ElementTreeDictionary implements  Adif3ElementDictionary {
    private final DictionaryTreeNode root;

    public Adif3ElementTreeDictionary() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("adif/adx312generic.xsd");
        Set<Adif3Element> elements = new Adif3SchemaLoader().loadAdif3Schema(inputStream);
        Map<String, Adif3Element> dictionary = new HashMap<>();

        for (Adif3Element element : elements) {
            dictionary.put(element.getName(), element);
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
    public Adif3Element getElement(String name) {
        return null;
    }
}
