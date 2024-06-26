package uk.m0nom.adifproc.maidenheadlocator;

import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.coords.LocationAccuracy;
import uk.m0nom.adifproc.coords.LocationParserResult;
import uk.m0nom.adifproc.coords.LocationSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Converted from C# source code by Mark Wickens M0NOM, credits and license below
 * <p>
 * Copyright (c) 2011, Yves Goergen, <a href="http://unclassified.software/source/maidenheadlocator">...</a>
 * Copying and distribution of this file, with or without modification, are permitted provided the
 * copyright notice and this notice are preserved. This file is offered as-is, without any warranty.
 * This class is based on a Perl module by Dirk Koopman, G1TLH, from 2002-11-07.
 * Source: <a href="http://www.koders.com/perl/fidDAB6FD208AC4F5C0306CA344485FD0899BD2F328.aspx">...</a>
 */
public class MaidenheadLocatorConversion {

    public final static Pattern LOC_4CHAR = Pattern.compile("^[A-R]{2}[0-9]{2}$");
    public final static Pattern LOC_6CHAR = Pattern.compile("^[A-R]{2}[0-9]{2}[A-X]{2}$");
    public final static Pattern LOC_8CHAR = Pattern.compile("^|[A-R]{2}[0-9]{2}[A-X]{2}[0-9]{2}$");
    public final static Pattern LOC_10CHAR = Pattern.compile("^[A-R]{2}[0-9]{2}[A-X]{2}[0-9]{2}[A-X]{2}$");
    public final static Pattern LOC_12CHAR = Pattern.compile("^[A-R]{2}[0-9]{2}[A-X]{2}[0-9]{2}[A-X]{2}[0-9]{2}$");

    private final static Collection<String> DUBIOUS_GRID_SQUARES = Arrays.asList("IO91VL", "JJ00AA", "AA00AA", "JJ00AA00", "AA00AA00");

    public static boolean isADubiousGridSquare(String grid) {
        return grid != null && DUBIOUS_GRID_SQUARES.contains(grid.toUpperCase());
    }

    public static boolean isEmptyOrInvalid(String gridSquare) {
        return gridSquare == null || MaidenheadLocatorConversion.isADubiousGridSquare(gridSquare);
    }

    public static boolean isValid(String gridSquare) {
        return gridSquare != null && isAWellFormedGridsquare(gridSquare) && !isADubiousGridSquare(gridSquare);
    }

    public static GlobalCoords3D locatorToCoords(LocationSource source, String locStr) {
        return locatorToCoords(source, locStr, null);
    }

    public static GlobalCoords3D locatorToCoords(LocationSource source, String locStr, String extStr) {

        String locatorTrimmed = locStr.trim().toUpperCase();
        if (extStr != null) {
            locatorTrimmed = locatorTrimmed + extStr.trim().toUpperCase();
        }
        Matcher matcher4Char = LOC_4CHAR.matcher(locatorTrimmed);
        Matcher matcher6Char = LOC_6CHAR.matcher(locatorTrimmed);
        Matcher matcher8Char = LOC_8CHAR.matcher(locatorTrimmed);
        Matcher matcher10Char = LOC_10CHAR.matcher(locatorTrimmed);
        Matcher matcher12Char = LOC_12CHAR.matcher(locatorTrimmed);

        char[] locator = locatorTrimmed.toCharArray();

        double longitude, latitude;

        try {
            if (matcher12Char.matches()) {
                // TODO work out how to parse 12 character locator - currently using the 10 character code
                longitude = (locator[0] - 'A') * 20 + (locator[2] - '0') * 2 + (locator[4] - 'A' + 0.0) / 12 + (locator[6] - '0' + 0.0) / 120 + (locator[8] - 'A' + 0.5) / 120 / 24 - 180;
                latitude = (locator[1] - 'A') * 10 + (locator[3] - '0') + (locator[5] - 'A' + 0.0) / 24 + (locator[7] - '0' + 0.0) / 240 + (locator[9] - 'A' + 0.5) / 240 / 24 - 90;
                return new GlobalCoords3D(latitude, longitude, source, LocationAccuracy.MHL12);
            } else if (matcher10Char.matches()) {
                longitude = (locator[0] - 'A') * 20 + (locator[2] - '0') * 2 + (locator[4] - 'A' + 0.0) / 12 + (locator[6] - '0' + 0.0) / 120 + (locator[8] - 'A' + 0.5) / 120 / 24 - 180;
                latitude = (locator[1] - 'A') * 10 + (locator[3] - '0') + (locator[5] - 'A' + 0.0) / 24 + (locator[7] - '0' + 0.0) / 240 + (locator[9] - 'A' + 0.5) / 240 / 24 - 90;
                return new GlobalCoords3D(latitude, longitude, source, LocationAccuracy.MHL10);
            } else if (matcher8Char.matches()) {
                longitude = (locator[0] - 'A') * 20 + (locator[2] - '0') * 2 + (locator[4] - 'A' + 0.0) / 12 + (locator[6] - '0' + 0.5) / 120 - 180;
                latitude = (locator[1] - 'A') * 10 + (locator[3] - '0') + (locator[5] - 'A' + 0.0) / 24 + (locator[7] - '0' + 0.5) / 240 - 90;
                return new GlobalCoords3D(latitude, longitude, source, LocationAccuracy.MHL8);
            } else if (matcher6Char.matches()) {
                longitude = (locator[0] - 'A') * 20 + (locator[2] - '0') * 2 + (locator[4] - 'A' + 0.5) / 12 - 180;
                latitude = (locator[1] - 'A') * 10 + (locator[3] - '0') + (locator[5] - 'A' + 0.5) / 24 - 90;
                return new GlobalCoords3D(latitude, longitude, source, LocationAccuracy.MHL6);
            } else if (matcher4Char.matches()) {
                latitude = (locator[1] - 'A') * 10 + (locator[3] - '0' + 0.5) - 90;
                longitude = (locator[0] - 'A') * 20 + (locator[2] - '0' + 0.5) * 2 - 180;
                return new GlobalCoords3D(latitude, longitude, source, LocationAccuracy.MHL4);
            } else {
                throw new UnsupportedOperationException(String.format("Invalid locator format: %s", locatorTrimmed));
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException(String.format("Invalid locator format: %s", locatorTrimmed));
        }
    }


    /**
     * Converts latitude and longitude in degrees to a locator
     *
     * @param coords GlobalCoordinates structure to convert
     * @return Locator string
     */
    public static String coordsToLocator(GlobalCoordinates coords) {
        return coordsToLocator(coords, 6);
    }

    public static String locationParserResultToLocator(LocationParserResult result) {
        LocationAccuracy accuracy = result.getCoords().getLocationInfo().getAccuracy();
        switch (accuracy) {
            case MHL8:
                return coordsToLocator(result.getCoords(), 8);
            case MHL10:
                return coordsToLocator(result.getCoords(), 10);
            case MHL12:
                return coordsToLocator(result.getCoords(), 12);
            default:
                return coordsToLocator(result.getCoords());
        }
    }

    /**
     * Convert latitude and longitude in degrees to a locator
     *
     * @param coords GlobalCoordinates structure to convert
     * @param len    Length of the locator (4/6/8/10)
     * @return Locator string
     */
    public static String coordsToLocator(GlobalCoordinates coords, int len) {
        String locator = "";

        double latitude = coords.getLatitude() + 90;
        double longitude = coords.getLongitude() + 180;

        locator += (char) ('A' + Math.floor(longitude / 20));
        locator += (char) ('A' + Math.floor(latitude / 10));

        longitude = Math.IEEEremainder(longitude, 20);
        if (longitude < 0) longitude += 20;
        latitude = Math.IEEEremainder(latitude, 10);
        if (latitude < 0) latitude += 10;

        locator += (char) ('0' + Math.floor(longitude / 2));
        locator += (char) ('0' + Math.floor(latitude));
        longitude = Math.IEEEremainder(longitude, 2);
        if (longitude < 0) longitude += 2;
        latitude = Math.IEEEremainder(latitude, 1);
        if (latitude < 0) latitude += 1;

        if (len > 4) {
            locator += (char) ('A' + Math.floor(longitude * 12));
            locator += (char) ('A' + Math.floor(latitude * 24));
            longitude = Math.IEEEremainder(longitude, (double) 1 / 12);
            if (longitude < 0) longitude += (double) 1 / 12;
            latitude = Math.IEEEremainder(latitude, (double) 1 / 24);
            if (latitude < 0) latitude += (double) 1 / 24;

            if (len > 6) {
                locator += (char) ('0' + Math.floor(longitude * 120));
                locator += (char) ('0' + Math.floor(latitude * 240));
                longitude = Math.IEEEremainder(longitude, (double) 1 / 120);
                if (longitude < 0) longitude += (double) 1 / 120;
                latitude = Math.IEEEremainder(latitude, (double) 1 / 240);
                if (latitude < 0) latitude += (double) 1 / 240;

                if (len > 8) {
                    locator += (char) ('A' + Math.floor(longitude * 120 * 24));
                    locator += (char) ('A' + Math.floor(latitude * 240 * 24));
                }
            }
        }

        return locator;

        //Lat += 90;
        //Long += 180;
        //v = (int) (Long / 20);
        //Long -= v * 20;
        //locator += (char) ('A' + v);
        //v = (int) (Lat / 10);
        //Lat -= v * 10;
        //locator += (char) ('A' + v);
        //locator += ((int) (Long / 2)).ToString();
        //locator += ((int) Lat).ToString();
        //Long -= (int) (Long / 2) * 2;
        //Lat -= (int) Lat;
        //locator += (char) ('A' + Long * 12);
        //locator += (char) ('A' + Lat * 24);
        //return locator;
    }

    /**
     * Calculate the distance in km between two locators
     *
     * @param a Start locator string
     * @param b End locator string
     * @return Distance in km
     */
    public static double distance(String a, String b) {
        return distance(locatorToCoords(LocationSource.UNDEFINED, a), locatorToCoords(LocationSource.UNDEFINED, b));
    }


    /**
     * Calculate the distance in km between two locators
     *
     * @param a Start GlobalCoordinates structure
     * @param b End GlobalCoordinates structure
     * @return Distance in km
     */
    public static double distance(GlobalCoordinates a, GlobalCoordinates b) {
        if (a.compareTo(b) == 0) return 0;

        double hn = Math.toRadians(a.getLatitude());
        double he = Math.toRadians(a.getLongitude());
        double n = Math.toRadians(b.getLatitude());
        double e = Math.toRadians(b.getLongitude());

        double co = Math.cos(he - e) * Math.cos(hn) * Math.cos(n) + Math.sin(hn) * Math.sin(n);
        double ca = Math.atan(Math.abs(Math.sqrt(1 - co * co) / co));
        if (co < 0) ca = Math.PI - ca;
        return 6367 * ca;
    }


    /**
     * Calculate the azimuth in degrees between two locators
     *
     * @param a Start locator string
     * @param b End locator string
     * @return Azimuth in degrees
     */
    public static double azimuth(String a, String b) {
        return azimuth(locatorToCoords(LocationSource.UNDEFINED, a), locatorToCoords(LocationSource.UNDEFINED, b));
    }


    /**
     * Calculate the azimuth in degrees between two locators
     *
     * @param a Start GlobalCoordinates structure
     * @param b End GlobalCoordinates structure
     * @return azimuth in degrees
     */
    public static double azimuth(GlobalCoordinates a, GlobalCoordinates b) {
        if (a.compareTo(b) == 0) return 0;

        double hn = Math.toRadians(a.getLatitude());
        double he = Math.toRadians(a.getLongitude());
        double n = Math.toRadians(b.getLatitude());
        double e = Math.toRadians(b.getLongitude());

        double co = Math.cos(he - e) * Math.cos(hn) * Math.cos(n) + Math.sin(hn) * Math.sin(n);
        double ca = Math.atan(Math.abs(Math.sqrt(1 - co * co) / co));
        if (co < 0) ca = Math.PI - ca;

        double si = Math.sin(e - he) * Math.cos(n) * Math.cos(hn);
        co = Math.sin(n) - Math.sin(hn) * Math.cos(ca);
        double az = Math.atan(Math.abs(si / co));
        if (co < 0) az = Math.PI - az;
        if (si < 0) az = -az;
        if (az < 0) az = az + 2 * Math.PI;

        return Math.toDegrees(az);
    }

    public static boolean isAWellFormedGridsquare(String locStr) {
        String locatorTrimmed = locStr.trim().toUpperCase();
        Matcher matcher4Char = LOC_4CHAR.matcher(locatorTrimmed);
        Matcher matcher6Char = LOC_6CHAR.matcher(locatorTrimmed);
        Matcher matcher8Char = LOC_8CHAR.matcher(locatorTrimmed);
        Matcher matcher10Char = LOC_10CHAR.matcher(locatorTrimmed);

        return matcher4Char.matches() || matcher6Char.matches() || matcher8Char.matches() || matcher10Char.matches();
    }
}
