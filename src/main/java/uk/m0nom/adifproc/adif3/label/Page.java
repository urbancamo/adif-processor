package uk.m0nom.adifproc.adif3.label;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Page {
    private int number;
    private char[][] content;
    private int width, height;

    public Page(int number, int width, int height) {
        setNumber(number);
        setWidth(width);
        setHeight(height);
        content = new char[height][width];

        // Everything to spaces
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                content[y][x] = ' ';
            }
        }
    }

    public void writeChar(char c, int x, int y) {
        content[y][x] = c;
    }

    public void writeString(String str, int x, int y) {
        for (int i = 0; i < str.length() && x+i < width; i++) {
            writeChar(str.charAt(i), x+i, y);
        }
    }

    public Collection<String> dumpPage() {
        Collection<String> out = new ArrayList<>(height);

        for (int y = 0; y < height; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < width; x++) {
                sb.append(content[y][x]);
            }
            out.add(sb.toString());
        }
        return out;
    }

    public static void dump(Collection<Page> pages) {
       List<Collection<String>> strs = pages.stream().map(Page::dumpPage).collect(Collectors.toList());
       Iterator<Collection<String>> i = strs.iterator();
       while (i.hasNext()) {
           Collection<String> stringCollection = i.next();
           Iterator<String> i2 = stringCollection.iterator();
           while (i2.hasNext()) {
               System.out.println(i2.next());
           }
       }
    }
}
