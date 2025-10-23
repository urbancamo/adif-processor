package uk.m0nom.adifproc.adif3.xsdquery;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Getter
@Service
public class Adif3ElementHashedDictionary implements Adif3ElementDictionary {
    private Map<String, Adif3Field> dictionary;

    public Adif3ElementHashedDictionary(Adif3SchemaLoader loader) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("adif/adx316.xsd");
        Adif3Schema schema = loader.loadAdif3Schema(inputStream);

        // Now need to find the shortest form for each element name that is unique
        dictionary = new HashMap<>();

        // To start with, map each AdifElement to full name
        assert schema != null;
        for (Adif3Field element : schema.getFields().values()) {
            dictionary.put(element.getName(), element);
        }

        Map<String, Adif3Field> minMap = new HashMap<>();

        // directly add all elements with name length of 3 or less
        dictionary.keySet().stream()
                .filter(name -> name.length() <= 3)
                .sorted(Comparator.comparingInt(String::length))
                .forEach(name -> minMap.put(name, dictionary.get(name)));

        // Now incrementally strip each of the element names back, so they are as short as possible but still unique
        // We do this on a stream sorted by name length, the smallest first
        dictionary.keySet().stream()
                .filter(name -> name.length() > 2)
                .sorted(Comparator.comparingInt(String::length))
                .forEach(name -> {
            int len = name.length();
            while (len >= 2) {
                String nameLessLastChar = name.substring(0, len-1);
                // Check to see if this shorter name exists in either dictionary
                if (minMap.get(nameLessLastChar) != null || dictionary.get(nameLessLastChar) != null ) {
                    // Need to insert at current level because a shorter name exists
                    minMap.put(name.substring(0, len), dictionary.get(name));
                    len = 0;
                } else if (nameLessLastChar.endsWith("_")) {
                    minMap.put(name.substring(0, len), dictionary.get(name));
                    len = 0;
                } else if (len == 2) {
                    // If we've not found a name and we are down to two chars, insert anyway
                    minMap.put(name.substring(0, len), dictionary.get(name));
                    len = 0;
                } else {
                    len = nameLessLastChar.length();
                }
            }
        });

        //assert(minMap.size() == dictionary.size());
        dictionary = minMap;
    }

    public Adif3Field getField(String name) {
        int len = name.length();
        while (len > 1) {
            Adif3Field element = dictionary.get(name.substring(0, len));
            if (element != null) {
                return element;
            }
            len--;
        }
        return null;
    }

}
