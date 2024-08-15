package uk.m0nom.adifproc.kml;

import org.marsik.ham.adif.enums.Band;

import java.util.HashMap;
import java.util.Map;

import static org.marsik.ham.adif.enums.Band.*;


public class KmlBandLineStyles {
    private final Map<Band, KmlLineStyle> bandLineStyles;

    public KmlBandLineStyles(int width, int transparency) {
        bandLineStyles = new HashMap<>();

//        "FEEAFA",
//        "FFF3B0",
//        "8E9AAF",
//        "CBF3F0",
//        "540B0E",
//        "00AFB9",
//        "E0FBFC",
//        "6D597A",
//        "9E2A2B",
//        "4361EE",
//        "B56576",
//        "F4978E",
//        "F8AD9D",
//        "F07167",
//        "0077B6",
//        "293241",
//        "EE6C4D",
//        "E09F3E",
//        "2EC4B6",
//        "FFDAB9",
//        "FDFCDC",
//        "FF9F1C",
//        "F08080",
//        "D6DAC8",
//        "3A0CA3",
//        "0081A7",
//        "D6CCC2",
//        "CBC0D3",
//        "EAAC8B",
//        "CAF0F8",
//        "011627",
//        "FDFFFC",
//        "2EC4B6",
//        "E71D36",
//        "FF9F1C",
//        "000814",
//        "001D3D",
//        "003566",
//        "FFC300",
//        "FFD60A",
//        "463F3A",
//        "8A817C",
//        "BCB8B1",
//        "F4F3EE",
//        "E0AFA0",
//        "5F0F40",
//        "9A031E",
//        "FB8B24",
//        "0F4C5C",

        bandLineStyles.put(BAND_2190m, getKmlLineStyle(BAND_2190m, "FCF6BD", transparency, width));
        bandLineStyles.put(BAND_630m, getKmlLineStyle(BAND_630m, "FCF6BD", transparency, width));
        bandLineStyles.put(BAND_560m, getKmlLineStyle(BAND_560m, "A9DEF9", transparency, width));
        bandLineStyles.put(BAND_160m, getKmlLineStyle(BAND_160m, "E4C1F9", transparency, width));
        bandLineStyles.put(BAND_80m, getKmlLineStyle(BAND_80m, "780000", transparency, width));
        bandLineStyles.put(BAND_60m, getKmlLineStyle(BAND_60m, "C1121F", transparency, width));
        bandLineStyles.put(BAND_40m, getKmlLineStyle(BAND_40m, "FDF0D5", transparency, width));
        bandLineStyles.put(BAND_30m, getKmlLineStyle(BAND_30m, "003049", transparency, width));
        bandLineStyles.put(BAND_20m, getKmlLineStyle(BAND_20m, "669BBC", transparency, width));
        bandLineStyles.put(BAND_17m, getKmlLineStyle(BAND_17m, "9A031E", transparency, width));
        bandLineStyles.put(BAND_15m, getKmlLineStyle(BAND_15m, "3C6E71", transparency, width));
        bandLineStyles.put(BAND_12m, getKmlLineStyle(BAND_12m, "FF99C8", transparency, width));
        bandLineStyles.put(BAND_10m, getKmlLineStyle(BAND_10m, "D9D9D9", transparency, width));
        bandLineStyles.put(BAND_6m, getKmlLineStyle(BAND_6m, "284B63", transparency, width));
        bandLineStyles.put(BAND_4m, getKmlLineStyle(BAND_4m, "05668D", transparency, width));
        bandLineStyles.put(BAND_2m, getKmlLineStyle(BAND_2m, "028090", transparency, width));
        bandLineStyles.put(BAND_1_25m, getKmlLineStyle(BAND_1_25m, "00A896", transparency, width));
        bandLineStyles.put(BAND_70cm, getKmlLineStyle(BAND_70cm, "02C39A", transparency, width));
        bandLineStyles.put(BAND_33cm, getKmlLineStyle(BAND_33cm, "F0F3BD", transparency, width));
        bandLineStyles.put(BAND_23cm, getKmlLineStyle(BAND_23cm, "9B5DE5", transparency, width));
        bandLineStyles.put(BAND_13cm, getKmlLineStyle(BAND_13cm, "F15BB5", transparency, width));
        bandLineStyles.put(BAND_9cm, getKmlLineStyle(BAND_9cm, "FEE440", transparency, width));
        bandLineStyles.put(BAND_6cm, getKmlLineStyle(BAND_6cm, "00BBF9", transparency, width));
        bandLineStyles.put(BAND_3cm, getKmlLineStyle(BAND_3cm, "00F5D4", transparency, width));
        bandLineStyles.put(BAND_1_25cm, getKmlLineStyle(BAND_1_25cm, "386641", transparency, width));
        bandLineStyles.put(BAND_6mm, getKmlLineStyle(BAND_6mm, "6A994E", transparency, width));
        bandLineStyles.put(BAND_4mm, getKmlLineStyle(BAND_4mm, "A7C957", transparency, width));
        bandLineStyles.put(BAND_2_5mm, getKmlLineStyle(BAND_2_5mm, "F2E8CF", transparency, width));
        bandLineStyles.put(BAND_2mm, getKmlLineStyle(BAND_2mm, "BC4749", transparency, width));
        bandLineStyles.put(BAND_1mm, getKmlLineStyle(BAND_1mm, "E36414", transparency, width));
    }

    private KmlLineStyle getKmlLineStyle(Band band, String colour, int transparency, int width) {
        int red = Integer.parseInt(colour.substring(0, 2), 16);
        int green = Integer.parseInt(colour.substring(2, 4), 16);
        int blue = Integer.parseInt(colour.substring(4, 6), 16);

        return new KmlLineStyle(band.name(), red, green, blue, transparency, width);
    }

    public KmlLineStyle getLineStyle(Band band) {
        return bandLineStyles.get(band);
    }
}
