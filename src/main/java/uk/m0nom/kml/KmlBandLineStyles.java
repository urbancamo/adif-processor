package uk.m0nom.kml;

import static org.marsik.ham.adif.enums.Band.*;
import org.marsik.ham.adif.enums.Band;

import java.util.HashMap;
import java.util.Map;


public class KmlBandLineStyles {
    private Map<Band, KmlLineStyle> bandLineStyles;
    
    public KmlBandLineStyles(int width, int transparency) {
        bandLineStyles = new HashMap<>();

        bandLineStyles.put(BAND_2190m,  getKmlLineStyle(250, 0, 0, transparency, width));
        bandLineStyles.put(BAND_630m,   getKmlLineStyle(246, 48, 0, transparency, width));
        bandLineStyles.put(BAND_560m,   getKmlLineStyle(240, 69, 0, transparency, width));
        bandLineStyles.put(BAND_160m,   getKmlLineStyle(235, 86, 0, transparency, width));
        bandLineStyles.put(BAND_80m,    getKmlLineStyle(228, 100, 0, transparency, width));
        bandLineStyles.put(BAND_60m,    getKmlLineStyle(222, 112, 0, transparency, width));
        bandLineStyles.put(BAND_40m,    getKmlLineStyle(214, 123, 0, transparency, width));
        bandLineStyles.put(BAND_30m,    getKmlLineStyle(207, 133, 0, transparency, width));
        bandLineStyles.put(BAND_20m,    getKmlLineStyle(199, 143, 0, transparency, width));
        bandLineStyles.put(BAND_17m,    getKmlLineStyle(191, 151, 0, transparency, width));
        bandLineStyles.put(BAND_15m,    getKmlLineStyle(183, 160, 0, transparency, width));
        bandLineStyles.put(BAND_12m,    getKmlLineStyle(175, 167, 0, transparency, width));
        bandLineStyles.put(BAND_10m,    getKmlLineStyle(166, 174, 0, transparency, width));
        bandLineStyles.put(BAND_6m,     getKmlLineStyle(158, 181, 0, transparency, width));
        bandLineStyles.put(BAND_4m,     getKmlLineStyle(149, 188, 15, transparency, width));
        bandLineStyles.put(BAND_2m,     getKmlLineStyle(140, 194, 42, transparency, width));
        bandLineStyles.put(BAND_1_25m,  getKmlLineStyle(130, 200, 61, transparency, width));
        bandLineStyles.put(BAND_70cm,   getKmlLineStyle(121, 205, 78, transparency, width));
        bandLineStyles.put(BAND_33cm,   getKmlLineStyle(110, 210, 95, transparency, width));
        bandLineStyles.put(BAND_23cm,   getKmlLineStyle(100, 216, 111, transparency, width));
        bandLineStyles.put(BAND_13cm,   getKmlLineStyle(88, 220, 127, transparency, width));
        bandLineStyles.put(BAND_9cm,    getKmlLineStyle(75, 225, 143, transparency, width));
        bandLineStyles.put(BAND_6cm,    getKmlLineStyle(61, 230, 158, transparency, width));
        bandLineStyles.put(BAND_3cm,    getKmlLineStyle(45, 234, 173, transparency, width));
        bandLineStyles.put(BAND_1_25cm, getKmlLineStyle(22, 238, 188, transparency, width));
        bandLineStyles.put(BAND_6mm,    getKmlLineStyle(0, 242, 203, transparency, width));
        bandLineStyles.put(BAND_4mm,    getKmlLineStyle(0, 245, 217, transparency, width));
        bandLineStyles.put(BAND_2_5mm,  getKmlLineStyle(0, 249, 230, transparency, width));
        bandLineStyles.put(BAND_2mm,    getKmlLineStyle(0, 252, 243, transparency, width));
        bandLineStyles.put(BAND_1mm,    getKmlLineStyle(0, 255, 255, transparency, width));
    }

    private KmlLineStyle getKmlLineStyle(int r, int g, int b, int transparency, int width) {
        String name = getRgbColourName(r, g, b);

        return new KmlLineStyle(name, r, g, b, transparency, width);
    }

    public String getRgbColourName(int red, int green, int blue) {
        return String.format("%02X%02X%02X", red, green, blue);
    }

    public KmlLineStyle getLineStyle(Band band) {
        return bandLineStyles.get(band);
    }
}
