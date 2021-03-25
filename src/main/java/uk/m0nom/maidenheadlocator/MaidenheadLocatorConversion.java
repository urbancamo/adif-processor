package uk.m0nom.maidenheadlocator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Converted from C# source code by Mark Wickens M0NOM, credits and license below
 *
 * Copyright (c) 2011, Yves Goergen, http://unclassified.software/source/maidenheadlocator
 * Copying and distribution of this file, with or without modification, are permitted provided the
 * copyright notice and this notice are preserved. This file is offered as-is, without any warranty.
 * This class is based on a Perl module by Dirk Koopman, G1TLH, from 2002-11-07.
 * Source: http://www.koders.com/perl/fidDAB6FD208AC4F5C0306CA344485FD0899BD2F328.aspx
 */
public class MaidenheadLocatorConversion {

    private final static Pattern LOC_4CHAR = Pattern.compile("^[A-R]{2}[0-9]{2}$");
    private final static Pattern LOC_6CHAR = Pattern.compile("^[A-R]{2}[0-9]{2}[A-X]{2}$");
    private final static Pattern LOC_8CHAR = Pattern.compile("^[A-R]{2}[0-9]{2}[A-X]{2}[0-9]{2}$");
    private final static Pattern LOC_10CHAR = Pattern.compile("^[A-R]{2}[0-9]{2}[A-X]{2}[0-9]{2}[A-X]{2}$");

    public static LatLng locatorToLatLng(String locStr) {
        String locatorTrimmed = locStr.trim().toUpperCase();
        Matcher matcher4Char = LOC_4CHAR.matcher(locatorTrimmed);
        Matcher matcher6Char = LOC_6CHAR.matcher(locatorTrimmed);
        Matcher matcher8Char = LOC_8CHAR.matcher(locatorTrimmed);
        Matcher matcher10Char = LOC_10CHAR.matcher(locatorTrimmed);

        char[] locator = locatorTrimmed.toCharArray();

        if (matcher4Char.matches()) {
            LatLng ll = new LatLng();
            ll.longitude = (locator[0] - 'A') * 20 + (locator[2] - '0' + 0.5) * 2 - 180;
            ll.latitude = (locator[1] - 'A') * 10 + (locator[3] - '0' + 0.5) - 90;
            return ll;
        } else if (matcher6Char.matches()) {
            LatLng ll = new LatLng();
            ll.longitude = (locator[0] - 'A') * 20 + (locator[2] - '0') * 2 + (locator[4] - 'A' + 0.5) / 12 - 180;
            ll.latitude = (locator[1] - 'A') * 10 + (locator[3] - '0') + (locator[5] - 'A' + 0.5) / 24 - 90;
            return ll;
        } else if (matcher8Char.matches()) {
            LatLng ll = new LatLng();
            ll.longitude = (locator[0] - 'A') * 20 + (locator[2] - '0') * 2 + (locator[4] - 'A' + 0.0) / 12 + (locator[6] - '0' + 0.5) / 120 - 180;
            ll.latitude = (locator[1] - 'A') * 10 + (locator[3] - '0') + (locator[5] - 'A' + 0.0) / 24 + (locator[7] - '0' + 0.5) / 240 - 90;
            return ll;
        } else if (matcher10Char.matches()) {
            LatLng ll = new LatLng();
            ll.longitude = (locator[0] - 'A') * 20 + (locator[2] - '0') * 2 + (locator[4] - 'A' + 0.0) / 12 + (locator[6] - '0' + 0.0) / 120 + (locator[8] - 'A' + 0.5) / 120 / 24 - 180;
            ll.latitude = (locator[1] - 'A') * 10 + (locator[3] - '0') + (locator[5] - 'A' + 0.0) / 24 + (locator[7] - '0' + 0.0) / 240 + (locator[9] - 'A' + 0.5) / 240 / 24 - 90;
            return ll;
        } else {
            throw new UnsupportedOperationException(String.format("Invalid locator format: %s", locatorTrimmed));
        }
    }


    
    /**
     * Converts latitude and longitude in degrees to a locator
     *
     * @param ll LatLng structure to convert
     * @return Locator string
     */
    public static String latLngToLocator(LatLng ll) {
        return latLngToLocator(ll.latitude, ll.longitude, 0);
    }

    
    /** Convert latitude and longitude in degrees to a locator
    *
    * @param ll structure to convert
    * @param ext precision (0, 1, 2)
    * @return ocator string</returns>
     */
    public static String latLngToLocator(LatLng ll, int ext) {
        return latLngToLocator(ll.latitude, ll.longitude, ext);
    }

    
    /** Convert latitude and longitude in degrees to a locator
    *
    * @param latitude Latitude to convert
    * @param longitude Longitude to convert
    * @return locator string
     */
    public static String latLngToLocator(double latitude, double longitude) {
        return latLngToLocator(latitude, longitude, 0);
    }

    
    /** Convert latitude and longitude in degrees to a locator
    *
    * @param latitudeIn Latitude to convert
    * @param longitudeIn Longitude to convert
    * @param ext Extra precision (0, 1, 2)
    * @return Locator string
     */
    public static String latLngToLocator(double latitudeIn, double longitudeIn, int ext) {
        int v;
        String locator = "";

        double latitude = latitudeIn + 90;
        double longitude = longitudeIn + 180;

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

        locator += (char) ('A' + Math.floor(longitude * 12));
        locator += (char) ('A' + Math.floor(latitude * 24));
        longitude = Math.IEEEremainder(longitude, (double) 1 / 12);
        if (longitude < 0) longitude += (double) 1 / 12;
        latitude = Math.IEEEremainder(latitude, (double) 1 / 24);
        if (latitude < 0) latitude += (double) 1 / 24;

        if (ext >= 1) {
            locator += (char) ('0' + Math.floor(longitude * 120));
            locator += (char) ('0' + Math.floor(latitude * 240));
            longitude = Math.IEEEremainder(longitude, (double) 1 / 120);
            if (longitude < 0) longitude += (double) 1 / 120;
            latitude = Math.IEEEremainder(latitude, (double) 1 / 240);
            if (latitude < 0) latitude += (double) 1 / 240;
        }

        if (ext >= 2) {
            locator += (char) ('A' + Math.floor(longitude * 120 * 24));
            locator += (char) ('A' + Math.floor(latitude * 240 * 24));
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
     * @param a Start locator string
     * @param b End locator string
     * @return Distance in km<
     */
    public static double distance(String a, String b) {
        return distance(locatorToLatLng(a), locatorToLatLng(b));
    }

    
    /** Calculate the distance in km between two locators
    *
    * @param a Start LatLng structure
    * @param b End LatLng structure
    * @return Distance in km
     */
    public static double distance(LatLng a, LatLng b) {
        if (a.compareTo(b) == 0) return 0;

        double hn = Math.toRadians(a.latitude);
        double he = Math.toRadians(a.longitude);
        double n = Math.toRadians(b.latitude);
        double e = Math.toRadians(b.longitude);

        double co = Math.cos(he - e) * Math.cos(hn) * Math.cos(n) + Math.sin(hn) * Math.sin(n);
        double ca = Math.atan(Math.abs(Math.sqrt(1 - co * co) / co));
        if (co < 0) ca = Math.PI - ca;
        return 6367 * ca;
    }

    
    /** Calculate the azimuth in degrees between two locators
    *
    * @param a Start locator string
    * @param b End locator string
    * @return Azimuth in degrees
     */
    public static double azimuth(String a, String b) {
        return azimuth(locatorToLatLng(a), locatorToLatLng(b));
    }

    
    /** Calculate the azimuth in degrees between two locators
    *
    * @param a Start LatLng structure
    * @param b End LatLng structure
    * @return azimuth in degrees
     */
    public static double azimuth(LatLng a, LatLng b) {
        if (a.compareTo(b) == 0) return 0;

        double hn = Math.toRadians(a.latitude);
        double he = Math.toRadians(a.longitude);
        double n = Math.toRadians(b.latitude);
        double e = Math.toRadians(b.longitude);

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
}
