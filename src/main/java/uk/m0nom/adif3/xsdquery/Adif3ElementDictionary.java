package uk.m0nom.adif3.xsdquery;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Adif3ElementDictionary {
    private Map<String, Adif3Element> dictionary;

    public Adif3ElementDictionary() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("adif/adx312generic.xsd");
        Set<Adif3Element> elements = new Adif3SchemaLoader().loadAdif3Schema(inputStream);

        // Now need to find the shortest form for each element name that is unique
        dictionary = new HashMap<>();

        // To start with, map each AdifElement to full name
        for (Adif3Element element : elements) {
            dictionary.put(element.getName(), element);
        }

        Map<String, Adif3Element> minMap = new HashMap<>();

        // Now incrementally strip each of the element names back, so they are as short as possible but still unique
        for (String name : dictionary.keySet()) {
            int len = name.length();
            while (len > 1) {
                String nameLessLastChar = name.substring(0, len-1);
                // Check to see if this shorter name exists in the dictionary
                if (dictionary.get(nameLessLastChar) != null || len == 2) {
                    minMap.put(name.substring(0, len), dictionary.get(name));
                    len = 0;
                } else {
                    len = nameLessLastChar.length();
                }
            }
        }
        dictionary = minMap;
    }

    public Adif3Element getElement(String name) {
        int len = name.length();
        while (len > 1) {
            Adif3Element element = dictionary.get(name.substring(0, len));
            if (element != null) {
                return element;
            }
            len--;
        }
        return null;
    }

    public Map<String, Adif3Element> getDictionary() { return dictionary; }
}
