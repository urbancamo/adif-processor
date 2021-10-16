package uk.m0nom.osgb36;

import org.junit.Test;
import uk.m0nom.coords.GlobalCoordinatesWithSourceAccuracy;

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
        GlobalCoordinatesWithSourceAccuracy coords = new GlobalCoordinatesWithSourceAccuracy(54.371029088698165, -2.909934810139321, null);
        OsGb36ConverterResult result = converter.convertCoordsToOsGb36(coords);
        assertTrue(result.isSuccess());
        assertEquals("SD4089297677", result.getOsGb36());
    }
}
