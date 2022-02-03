package uk.m0nom.osgb36;

import org.junit.Test;
import uk.m0nom.coords.GlobalCoords3D;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OsGbConverterTest {
    @Test
    public void osGb36ToLatLongTest() {
        OsGb36Converter converter = new OsGb36Converter();
        OsGb36ConverterResult result = converter.convertOsGb36ToCoords("SD4099197660");
        assertTrue(result.isSuccess());
        assertTrue(Math.abs(result.getCoords().getLatitude() - 54.37088129431402) < 0.01);
        assertTrue(Math.abs(Math.abs(result.getCoords().getLongitude()) - Math.abs(-2.9084127270569127)) < 0.01);
    }

    @Test
    public void latLongToOsGb36Test() {
        OsGb36Converter converter = new OsGb36Converter();
        GlobalCoords3D coords = new GlobalCoords3D(54.371029088698165, -2.909934810139321, null);
        OsGb36ConverterResult result = converter.convertCoordsToOsGb36(coords);
        assertTrue(result.isSuccess());
        assertEquals("SD4089297677", result.getOsGb36());
    }

    @Test
    public void osGb36EastingNorthingToLatLongTest() {
        OsGb36Converter converter = new OsGb36Converter();
        OsGb36ConverterResult result = converter.convertOsGb36EastingNorthingToCoords("332222", "527763");
        assertTrue(result.isSuccess());
        assertTrue(Math.abs(Math.abs(result.getCoords().getLatitude()) - Math.abs(54.6403121148)) < 0.01);
        assertTrue(Math.abs(Math.abs(result.getCoords().getLongitude()) - Math.abs(-3.0502927536)) < 0.01);
    }

    // NY 3231 2775, E 332312 N 527750, 54.6403121148, -3.0502927536
    @Test
    public
    void latLongToOsGb36EastingNorthingTest() {
        OsGb36Converter converter = new OsGb36Converter();
        GlobalCoords3D coords = new GlobalCoords3D(54.6403121148, -3.0502927536, null);
        OsGb36ConverterResult result = converter.convertCoordsToOsGb36EastingNorthing(coords);
        assertTrue(result.isSuccess());
        assertEquals("E 332222", result.getOsGb36EastingString());
        assertEquals("N 527763", result.getOsGb36NorthingString());
    }


}
