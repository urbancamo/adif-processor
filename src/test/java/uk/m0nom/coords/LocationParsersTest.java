package uk.m0nom.coords;

import org.junit.Assert;
import org.junit.Test;

public class LocationParsersTest {

    @Test
    public void checkBadCotaConversions() {
        LocationParsers parsers = new LocationParsers();
        checkParsing(parsers, "51 53.31'N 0 32.77'W", 51.888464861082646, -0.5462438363385727);

        checkParsing(parsers, "20.813311°N 89.452448°W", 20.813311, -89.452448);
        checkParsing(parsers, "20.500351°N 86.848297°W\n", 20.500351, -86.848297);
        checkParsing(parsers,"ARKAIM (RFF-0306) (52°38'57\"N   59°34'17\"E)", 52.649167, 59.57139);
        checkParsing(parsers, "15.2892° N 91.0892° W", 15.2892, -91.0892);
        checkParsing(parsers, "46° 32' 43,6\"N 14° 2' 27,3\" O", 46.54528, 14.04083);
        checkParsing(parsers, "CARRICKKILDAVNET, ACHILL ISLAND, MAYO COUNTY (53.880875°N 9.945935°W)", 53.880875, -9.945935);
    }

    private void checkParsing(LocationParsers parsers, String toScan, double expectedLatitude, double expectedLongitude) {
        GlobalCoords3D gc = parsers.parseStringForCoordinates(LocationSource.UNDEFINED, toScan).getCoords();
        Assert.assertTrue(String.format("Returned latitude: %f doesn't match expected %f", gc.getLatitude(), expectedLatitude), Math.abs(gc.getLatitude() - expectedLatitude) < 0.0001);
        Assert.assertTrue(String.format("Returned longitude: %f doesn't match expected %f",gc.getLongitude(), expectedLongitude), Math.abs(gc.getLongitude() - expectedLongitude) < 0.0001);
    }
}
