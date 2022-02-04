package uk.m0nom.geolib;

import org.gavaghan.geodesy.*;
import org.junit.Assert;
import org.junit.Test;
import uk.m0nom.coords.LocationSource;
import uk.m0nom.maidenheadlocator.MaidenheadLocatorConversion;

public class GeoLibTest {
    @Test
    public void distanceTest() {
        GlobalCoordinates myLocation = MaidenheadLocatorConversion.locatorToCoords(LocationSource.UNDEFINED,"IO84mj91mb");
        GlobalCoordinates belgium =  MaidenheadLocatorConversion.locatorToCoords(LocationSource.UNDEFINED, "JO11PF");

        GlobalCoordinates start = new GlobalCoordinates(myLocation.getLatitude(), myLocation.getLongitude());
        GlobalCoordinates end = new GlobalCoordinates(belgium.getLatitude(), belgium.getLongitude());

        GeodeticCalculator calculator = new GeodeticCalculator();
        GeodeticCurve curve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, start, end);
        double distance = curve.getEllipsoidalDistance();
        System.out.printf("Distance: %f\n", distance);

        double azimuth = curve.getAzimuth();
        double reverseAzimuth = curve.getReverseAzimuth();

        GlobalCoordinates midpoint1 = calculator.calculateEndingGlobalCoordinates(Ellipsoid.WGS84, start, azimuth, distance / 2.0);
        System.out.printf("Midpoint1: %s\n", midpoint1.toString());

        // Work out the distance of the 'other half'
        GlobalCoordinates midpoint2 = calculator.calculateEndingGlobalCoordinates(Ellipsoid.WGS84, end, reverseAzimuth, distance / 2.0);
        System.out.printf("Midpoint2: %s\n", midpoint2.toString());

        Assert.assertEquals(String.format("%.3f", midpoint1.getLatitude()), String.format("%.3f", midpoint2.getLatitude()));
        Assert.assertEquals(String.format("%.3f", midpoint2.getLongitude()), String.format("%.3f", midpoint1.getLongitude()));
    }
}
