package uk.m0nom.adifproc.kml;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KmlLineStyle extends KmlColour {

    private Integer width;

    public KmlLineStyle(String name, int red, int green, int blue, int transparency, int width) {
        super(name, name, getHtmlColor(red, green, blue), red, green, blue, transparency);
        this.width = width;
    }

    public KmlLineStyle(KmlColour colour, int width) {
        super();
        setGreen(colour.getGreen());
        setBlue(colour.getBlue());
        setRed(colour.getRed());
        setTransparency(colour.getTransparency());
        setWidth(width);
    }

    private static String getHtmlColor(int red, int green, int blue) {
        return String.format("ff%02X%02X%02X", red, green, blue);
    }
}
