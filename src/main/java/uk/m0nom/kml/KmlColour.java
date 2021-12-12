package uk.m0nom.kml;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class KmlColour {
    private String key;
    private String name;
    private String htmlColor;
    private int red, green, blue;
    private int transparency;

    public KmlColour() {}

    public KmlColour(String key, String name, String htmlColor, int red, int green, int blue) {
        this(key, name, htmlColor, red,green, blue, 255);
    }

    public KmlColour(String key, String name, String htmlColor, int red, int green, int blue, int transparency) {
        this.key = key;
        this.name = name;
        this.htmlColor = htmlColor;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.transparency = transparency;
    }

    public KmlColour(String name, int red, int green, int blue, int transparency) {
        this.name = name;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.transparency = transparency;
        this.key = getStringSpecifier();
    }

    public String getStringSpecifier() {
        return String.format("%02X%02X%02X%02X", transparency, blue, green, red);
    }

    public String getStringSpecifier(int transparency) {
        return String.format("%02X%02X%02X%02X", transparency, blue, green, red);
    }
}
