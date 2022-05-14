package uk.m0nom.adifproc.irishgrid;

import lombok.Data;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.coords.LocationAccuracy;
import uk.m0nom.adifproc.coords.LocationSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * GT_OSGB holds Irish grid coordinates
 *
 */
@Data
public class IrishGridConverter {

    private double northings;
    private double eastings;
    private String status;

    private char[][] prefixes = new char[][] {
            {'V',  'Q',  'L',  'F',  'A'}, 
            {'W',  'R',  'M',  'G',  'B'}, 
            {'X',  'S',  'N',  'H',  'C'}, 
            {'Y',  'T',  'O',  'J',  'D'}, 
            {'Z',  'U',  'P',  'K',  'E'}
    };

    public IrishGridConverter() {
        northings = 0.0;
        eastings = 0.0;
        status="Undefined";
    }

    public void setError(String msg) {
        status = msg;
    }

    private String zeroPad(int num, int len) {

        StringBuilder str = new StringBuilder(Integer.toString(num));
        while (str.length() < len) {
            str.insert(0, '0');
        }
        return str.toString();
    }

    public String getGridRef(int precision) {
        if (precision < 0) {
            precision = 0;
        }
        if (precision > 5) {
            precision = 5;
        }

        int x = 0;
        int y = 0;
        int e = 0;
        int n = 0;

        if (precision > 0) {
            y= (int) Math.floor(northings / 100000.0);
            x= (int) Math.floor(eastings / 100000.0);


            e = (int) Math.floor(eastings % 100000);
            n = (int) Math.floor(northings % 100000);

            int div = (5 - precision);

            e = (int) Math.floor(e / Math.pow(10,  div));
            n = (int) Math.floor(n / Math.pow(10,  div));
        }

        char prefix;
        try {
            prefix = prefixes[x][y];
        } catch (ArrayIndexOutOfBoundsException exception) {
            // Not an irish grid reference
            return null;
        }

        return prefix+" "+ zeroPad(e,  precision)+" "+ zeroPad(n,  precision);
    }

    public IrishGridConverterResult convertIrishGridRefToWsg84(String irishGridRef) {
        IrishGridConverterResult result = new IrishGridConverterResult();
        result.setIrishGridRef(irishGridRef);

        if (parseGridRef(irishGridRef)) {
            result.setEasting(eastings);
            result.setNorthing(northings);
            GlobalCoordinates coords = getWGS84(true);
            result.setCoords(new GlobalCoords3D(coords, LocationSource.IRISH_GRID_REF_CONVERTER, LocationAccuracy.IRISH_GRID_REF_5DIGIT));
            result.setSuccess(true);
        }
        return result;
    }

    public IrishGridConverterResult convertCoordsToIrishGridRef(GlobalCoordinates coords) {
        IrishGridConverterResult result = new IrishGridConverterResult();
        result.setCoords(new GlobalCoords3D(coords, LocationSource.IRISH_GRID_REF_CONVERTER, LocationAccuracy.IRISH_GRID_REF_5DIGIT));
        String irishGridRef = getIrishGridRef(coords, true);
        if (irishGridRef != null) {
            result.setIrishGridRef(irishGridRef);
            result.setSuccess(true);
        }
        return result;
    }

    public boolean parseGridRef(String landranger) {

        northings = 0;
        eastings = 0;

        for (int precision = 5; precision >= 1; precision--) {
            var pattern = Pattern.compile("^([A-Z]{1})\\s*(\\d{"+precision+"})\\s*(\\d{"+precision+"})$",  Pattern.CASE_INSENSITIVE);

            Matcher matcher = pattern.matcher(landranger);
            if (matcher.matches()) {
                char gridSheet = matcher.group(1).charAt(0);
                var gridEast = 0.0;
                var gridNorth = 0.0;

                //5x1 4x10 3x100 2x1000 1x10000
                var mult= Math.pow(10, 5 - precision);
                gridEast = Integer.valueOf(matcher.group(2), 10) * mult;
                gridNorth = Integer.valueOf(matcher.group(3), 10) * mult;

                int x,  y;
                for (x = 0; x < prefixes.length; x++) {
                    for (y = 0; y < prefixes[x].length; y++) {
                        if (prefixes[x][y] == gridSheet) {
                            eastings = (x * 100000) + gridEast;
                            northings = (y * 100000) + gridNorth;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public String getIrishGridRef(GlobalCoordinates coords, boolean uselevel2) {

        var height = 0.0;
        double latitude2, longitude2;

        var latitude =coords.getLatitude();
        var longitude = coords.getLongitude();

        if (uselevel2) {
            var x1 = Lat_Long_H_to_X(latitude,longitude,height,6378137.00,6356752.313);
            var y1 = Lat_Long_H_to_Y(latitude,longitude,height,6378137.00,6356752.313);
            var z1 = Lat_H_to_Z     (latitude,height,6378137.00,6356752.313);

            var x2 = Helmert_X(x1,y1,z1,-482.53 ,-0.214,-0.631,-8.15);
            var y2 = Helmert_Y(x1,y1,z1, 130.596,-1.042,-0.631,-8.15);
            var z2 = Helmert_Z(x1,y1,z1,-564.557,-1.042,-0.214,-8.15);

             latitude2  = XYZ_to_Lat (x2,y2,z2,6377340.189,6356034.447);
             longitude2 = XYZ_to_Long(x2,y2);
        } else {
             latitude2  = latitude;
             longitude2 = longitude;
        }

        var e = Lat_Long_to_East (latitude2,longitude2,6377340.189,6356034.447, 200000,1.000035,53.50000,-8.00000);
        var n = Lat_Long_to_North(latitude2,longitude2,6377340.189,6356034.447, 200000,250000,1.000035,53.50000,-8.00000);

        if (!uselevel2) {
            //Level 1 Transformation - 95% of points within 2 metres
            //fixed datum shift correction (instead of fancy hermert translation above!)
            //source http://www.osni.gov.uk/downloads/Making%20maps%20GPS%20compatible.pdf
            e = e + 49;
            n = n - 23.4;
        }

        this.eastings = Math.round(e);
        this.northings = Math.round(n);

        return getGridRef(5);
    }

    public GlobalCoordinates getWGS84(boolean uselevel2) {

        var height = 0;
        double e,  n;
        
        if (uselevel2) {
            e = eastings;
            n = northings;
        } else {
            //fixed datum shift correction (instead of fancy hermert translation below!)
            e = eastings - 49.0;
            n = northings + 23.4;
        }

        var lat1 = E_N_to_Lat (e, n, 6377340.189, 6356034.447, 200000, 250000, 1.000035, 53.50000, -8.00000);
        var lon1 = E_N_to_Long(e, n, 6377340.189, 6356034.447, 200000, 250000, 1.000035, 53.50000, -8.00000);

        if (uselevel2) {
            var x1 = Lat_Long_H_to_X(lat1, lon1, height, 6377340.189, 6356034.447);
            var y1 = Lat_Long_H_to_Y(lat1, lon1, height, 6377340.189, 6356034.447);
            var z1 = Lat_H_to_Z     (lat1,      height, 6377340.189, 6356034.447);

            var x2 = Helmert_X(x1, y1, z1,  482.53 , 0.214, 0.631, 8.15);
            var y2 = Helmert_Y(x1, y1, z1, -130.596, 1.042, 0.631, 8.15);
            var z2 = Helmert_Z(x1, y1, z1,  564.557, 1.042, 0.214, 8.15);

            var latitude = XYZ_to_Lat(x2, y2, z2, 6378137.000, 6356752.313);
            var longitude = XYZ_to_Long(x2, y2);
            return new GlobalCoords3D(latitude,  longitude);
        } 
        
         return new GlobalCoordinates(lat1,  lon1);
    }


    private double E_N_to_Lat(double East, double North, double a, double b, double e0, double n0, double f0, double PHI0, double LAM0)
    {
        //Un-project Transverse Mercator eastings and northings back to latitude.
        //Input: - _
        //eastings (East) and northings (North) in meters; _
        //ellipsoid axis dimensions (a & b) in meters; _
        //eastings (e0) and northings (n0) of false origin in meters; _
        //central meridian scale factor (f0) and _
        //latitude (PHI0) and longitude (LAM0) of false origin in decimal degrees.

        //'REQUIRES THE "Marc" AND "InitialLat" FUNCTIONS

        //Convert angle measures to radians
        var RadPHI0 = PHI0 * (Math.PI / 180);

        //Compute af0, bf0, e squared (e2), n and Et
        var af0 = a * f0;
        var bf0 = b * f0;
        var e2 = (Math.pow(af0,2) - Math.pow(bf0,2)) / Math.pow(af0,2);
        var n = (af0 - bf0) / (af0 + bf0);
        var Et = East - e0;

        //Compute initial value for latitude (PHI) in radians
        var PHId = InitialLat(North, n0, af0, RadPHI0, n, bf0);

        //Compute nu, rho and eta2 using value for PHId
        var nu = af0 / (Math.sqrt(1 - (e2 * ( Math.pow(Math.sin(PHId),2)))));
        var rho = (nu * (1 - e2)) / (1 - (e2 * Math.pow(Math.sin(PHId),2)));
        var eta2 = (nu / rho) - 1;

        //Compute Latitude
        var VII = (Math.tan(PHId)) / (2 * rho * nu);
        var VIII = ((Math.tan(PHId)) / (24 * rho * Math.pow(nu,3))) * (5 + (3 * (Math.pow(Math.tan(PHId),2))) + eta2 - (9 * eta2 * (Math.pow(Math.tan(PHId),2))));
        var IX = ((Math.tan(PHId)) / (720 * rho * Math.pow(nu,5))) * (61 + (90 * (Math.pow(Math.tan(PHId), 2))) + (45 * (Math.pow(Math.tan(PHId),4))));

        return ((180 / Math.PI) * (PHId - (Math.pow(Et,2) * VII) + (Math.pow(Et,4) * VIII) - (Math.pow(Et,6) * IX)));
    }

    private double E_N_to_Long(double East, double North, double a, double b, double e0, double n0, double f0, double PHI0, double LAM0)
    {
        //Un-project Transverse Mercator eastings and northings back to longitude.
        //Input: - _
        //eastings (East) and northings (North) in meters; _
        //ellipsoid axis dimensions (a & b) in meters; _
        //eastings (e0) and northings (n0) of false origin in meters; _
        //central meridian scale factor (f0) and _
        //latitude (PHI0) and longitude (LAM0) of false origin in decimal degrees.

        //REQUIRES THE "Marc" AND "InitialLat" FUNCTIONS

        //Convert angle measures to radians
        var RadPHI0 = PHI0 * (Math.PI / 180);
        var RadLAM0 = LAM0 * (Math.PI / 180);

        //Compute af0, bf0, e squared (e2), n and Et
        var af0 = a * f0;
        var bf0 = b * f0;
        var e2 = (Math.pow(af0,2) - Math.pow(bf0,2)) / Math.pow(af0,2);
        var n = (af0 - bf0) / (af0 + bf0);
        var Et = East - e0;

        //Compute initial value for latitude (PHI) in radians
        var PHId = InitialLat(North, n0, af0, RadPHI0, n, bf0);

        //Compute nu, rho and eta2 using value for PHId
        var nu = af0 / (Math.sqrt(1 - (e2 * (Math.pow(Math.sin(PHId),2)))));
        var rho = (nu * (1 - e2)) / (1 - (e2 * Math.pow(Math.sin(PHId),2)));
        //var eta2 = (nu / rho) - 1;

        //Compute Longitude
        var X = (Math.pow(Math.cos(PHId),-1)) / nu;
        var XI = ((Math.pow(Math.cos(PHId),-1)) / (6 * Math.pow(nu,3))) * ((nu / rho) + (2 * (Math.pow(Math.tan(PHId),2))));
        var XII = ((Math.pow(Math.cos(PHId),-1)) / (120 * Math.pow(nu,5))) * (5 + (28 * (Math.pow(Math.tan(PHId),2))) + (24 * (Math.pow(Math.tan(PHId),4))));
        var XIIA = ((Math.pow(Math.cos(PHId),-1)) / (5040 * Math.pow(nu,7))) * (61 + (662 * (Math.pow(Math.tan(PHId),2))) + (1320 * (Math.pow(Math.tan(PHId),4))) + (720 * (Math.pow(Math.tan(PHId),6))));

        return (180 / Math.PI) * (RadLAM0 + (Et * X) - (Math.pow(Et,3) * XI) + (Math.pow(Et,5) * XII) - (Math.pow(Et,7) * XIIA));
    }

    private double InitialLat(double North, double n0, double afo, double PHI0, double n, double bfo)
    {
        //Compute initial value for Latitude (PHI) IN RADIANS.
        //Input: - _
        //northing of point (North) and northing of false origin (n0) in meters; _
        //semi major axis multiplied by central meridian scale factor (af0) in meters; _
        //latitude of false origin (PHI0) IN RADIANS; _
        //n (computed from a, b and f0) and _
        //ellipsoid semi major axis multiplied by central meridian scale factor (bf0) in meters.

        //REQUIRES THE "Marc" FUNCTION
        //THIS FUNCTION IS CALLED BY THE "E_N_to_Lat", "E_N_to_Long" and "E_N_to_C" FUNCTIONS
        //THIS FUNCTION IS ALSO USED ON IT'S OWN IN THE  "Projection and Transformation Calculations.xls" SPREADSHEET

        //First PHI value (PHI1)
        var PHI1 = ((North - n0) / afo) + PHI0;

        //Calculate M
        var M = Marc(bfo, n, PHI0, PHI1);

        //Calculate new PHI value (PHI2)
        var PHI2 = ((North - n0 - M) / afo) + PHI1;

        //Iterate to get final value for InitialLat
        while (Math.abs(North - n0 - M) > 0.00001)
        {
            PHI2 = ((North - n0 - M) / afo) + PHI1;
            M = Marc(bfo, n, PHI0, PHI2);
            PHI1 = PHI2;
        }
        return PHI2;
    }

    private double Lat_Long_H_to_X(double PHI, double LAM, double H, double a, double b)
    {
        // Convert geodetic coords lat (PHI), long (LAM) and height (H) to cartesian X coordinate.
        // Input: - _
        //    Latitude (PHI)& Longitude (LAM) both in decimal degrees; _
        //  Ellipsoidal height (H) and ellipsoid axis dimensions (a & b) all in meters.

        // Convert angle measures to radians
        var RadPHI = PHI * (Math.PI / 180);
        var RadLAM = LAM * (Math.PI / 180);

        // Compute eccentricity squared and nu
        var e2 = (Math.pow(a,2) - Math.pow(b,2)) / Math.pow(a,2);
        var V = a / (Math.sqrt(1 - (e2 * (  Math.pow(Math.sin(RadPHI),2)))));

        // Compute X
        return (V + H) * (Math.cos(RadPHI)) * (Math.cos(RadLAM));
    }


    private double Lat_Long_H_to_Y(double PHI, double LAM, double H, double a, double b)
    {
        // Convert geodetic coords lat (PHI), long (LAM) and height (H) to cartesian Y coordinate.
        // Input: - _
        // Latitude (PHI)& Longitude (LAM) both in decimal degrees; _
        // Ellipsoidal height (H) and ellipsoid axis dimensions (a & b) all in meters.

        // Convert angle measures to radians
        var RadPHI = PHI * (Math.PI / 180);
        var RadLAM = LAM * (Math.PI / 180);

        // Compute eccentricity squared and nu
        var e2 = (Math.pow(a,2) - Math.pow(b,2)) / Math.pow(a,2);
        var V = a / (Math.sqrt(1 - (e2 * (  Math.pow(Math.sin(RadPHI),2))) ));

        // Compute Y
        return (V + H) * (Math.cos(RadPHI)) * (Math.sin(RadLAM));
    }


    private double Lat_H_to_Z(double PHI, double H, double a, double b)
    {
        // Convert geodetic coord components latitude (PHI) and height (H) to cartesian Z coordinate.
        // Input: - _
        //    Latitude (PHI) decimal degrees; _
        // Ellipsoidal height (H) and ellipsoid axis dimensions (a & b) all in meters.

        // Convert angle measures to radians
        var RadPHI = PHI * (Math.PI / 180);

        // Compute eccentricity squared and nu
        var e2 = (Math.pow(a,2) - Math.pow(b,2)) / Math.pow(a,2);
        var V = a / (Math.sqrt(1 - (e2 * (  Math.pow(Math.sin(RadPHI),2)) )));

        // Compute X
        return ((V * (1 - e2)) + H) * (Math.sin(RadPHI));
    }


    public double Helmert_X(double X, double Y, double Z, double DX, double Y_Rot, double Z_Rot, double s) {

        // (X, Y, Z, DX, Y_Rot, Z_Rot, s)
        // Computed Helmert transformed X coordinate.
        // Input: - _
        //    cartesian XYZ coords (X,Y,Z), X translation (DX) all in meters ; _
        // Y and Z rotations in seconds of arc (Y_Rot, Z_Rot) and scale in ppm (s).

        // Convert rotations to radians and ppm scale to a factor
        var sfactor = s * 0.000001;

        var RadY_Rot = (Y_Rot / 3600) * (Math.PI / 180);

        var RadZ_Rot = (Z_Rot / 3600) * (Math.PI / 180);

        //Compute transformed X coord
        return  (X + (X * sfactor) - (Y * RadZ_Rot) + (Z * RadY_Rot) + DX);
    }


    private double Helmert_Y(double X, double Y, double Z, double DY, double X_Rot, double Z_Rot, double s) {
        // (X, Y, Z, DY, X_Rot, Z_Rot, s)
        // Computed Helmert transformed Y coordinate.
        // Input: - _
        //    cartesian XYZ coords (X,Y,Z), Y translation (DY) all in meters ; _
        //  X and Z rotations in seconds of arc (X_Rot, Z_Rot) and scale in ppm (s).

        // Convert rotations to radians and ppm scale to a factor
        var sfactor = s * 0.000001;
        var RadX_Rot = (X_Rot / 3600) * (Math.PI / 180);
        var RadZ_Rot = (Z_Rot / 3600) * (Math.PI / 180);

        // Compute transformed Y coord
        return (X * RadZ_Rot) + Y + (Y * sfactor) - (Z * RadX_Rot) + DY;

    }


    public double Helmert_Z(double X, double Y, double Z, double DZ, double X_Rot, double Y_Rot, double s) {
        // (X, Y, Z, DZ, X_Rot, Y_Rot, s)
        // Computed Helmert transformed Z coordinate.
        // Input: - _
        //    cartesian XYZ coords (X,Y,Z), Z translation (DZ) all in meters ; _
        // X and Y rotations in seconds of arc (X_Rot, Y_Rot) and scale in ppm (s).
        //
        // Convert rotations to radians and ppm scale to a factor
        var sfactor = s * 0.000001;
        var RadX_Rot = (X_Rot / 3600) * (Math.PI / 180);
        var RadY_Rot = (Y_Rot / 3600) * (Math.PI / 180);

        // Compute transformed Z coord
        return (-1 * X * RadY_Rot) + (Y * RadX_Rot) + Z + (Z * sfactor) + DZ;
    }

    private double XYZ_to_Lat(double X, double Y, double Z, double a, double b) {
        // Convert XYZ to Latitude (PHI) in Dec Degrees.
        // Input: - _
        // XYZ cartesian coords (X,Y,Z) and ellipsoid axis dimensions (a & b), all in meters.

        // THIS FUNCTION REQUIRES THE "Iterate_XYZ_to_Lat" FUNCTION
        // THIS FUNCTION IS CALLED BY THE "XYZ_to_H" FUNCTION

        var RootXYSqr = Math.sqrt(Math.pow(X,2) + Math.pow(Y,2));
        var e2 = (Math.pow(a,2) - Math.pow(b,2)) / Math.pow(a,2);
        var PHI1 = Math.atan2(Z , (RootXYSqr * (1 - e2)) );

        var PHI = Iterate_XYZ_to_Lat(a, e2, PHI1, Z, RootXYSqr);

        return PHI * (180 / Math.PI);
    }


    private double Iterate_XYZ_to_Lat(double a, double e2, double PHI1, double Z, double RootXYSqr) {
        // Iteratively computes Latitude (PHI).
        // Input: - _
        //    ellipsoid semi major axis (a) in meters; _
        //    eta squared (e2); _
        //    estimated value for latitude (PHI1) in radians; _
        //    cartesian Z coordinate (Z) in meters; _
        // RootXYSqr computed from X & Y in meters.

        // THIS FUNCTION IS CALLED BY THE "XYZ_to_PHI" FUNCTION
        // THIS FUNCTION IS ALSO USED ON IT'S OWN IN THE _
        // "Projection and Transformation Calculations.xls" SPREADSHEET


        var V = a / (Math.sqrt(1 - (e2 * Math.pow(Math.sin(PHI1),2))));
        var PHI2 = Math.atan2((Z + (e2 * V * (Math.sin(PHI1)))) , RootXYSqr);

        while (Math.abs(PHI1 - PHI2) > 0.000000001) {
            PHI1 = PHI2;
            V = a / (Math.sqrt(1 - (e2 * Math.pow(Math.sin(PHI1),2))));
            PHI2 = Math.atan2((Z + (e2 * V * (Math.sin(PHI1)))) , RootXYSqr);
        }

        return PHI2;
    }


    private double XYZ_to_Long(double x, double y)
    {
        // Convert XYZ to Longitude (LAM) in Dec Degrees.
        // Input: - _
        // X and Y cartesian coords in meters.
        return Math.atan2(y , x) * (180 / Math.PI);
    }

    private double Marc(double bf0, double n, double PHI0, double PHI)
    {
        //Compute meridional arc.
        //Input: - _
        // ellipsoid semi major axis multiplied by central meridian scale factor (bf0) in meters; _
        // n (computed from a, b and f0); _
        // lat of false origin (PHI0) and initial or final latitude of point (PHI) IN RADIANS.

        //THIS FUNCTION IS CALLED BY THE - _
        // "Lat_Long_to_North" and "InitialLat" FUNCTIONS
        // THIS FUNCTION IS ALSO USED ON IT'S OWN IN THE "Projection and Transformation Calculations.xls" SPREADSHEET

        return bf0 * (((1 + n + ((5.0 / 4.0) * Math.pow(n,2)) + ((5.0 / 4.0) * Math.pow(n,3))) * (PHI - PHI0)) - (((3 * n) + (3 * Math.pow(n,2)) + ((21.0 / 8.0) * Math.pow(n,3))) * (Math.sin(PHI - PHI0)) * (Math.cos(PHI + PHI0))) + ((((15.0 / 8.0
        ) * Math.pow(n,2)) + ((15.0 / 8.0) * Math.pow(n,3))) * (Math.sin(2 * (PHI - PHI0))) * (Math.cos(2 * (PHI + PHI0)))) - (((35.0 / 24.0) * Math.pow(n,3)) * (Math.sin(3 * (PHI - PHI0))) * (Math.cos(3 * (PHI + PHI0)))));
    }


    public double Lat_Long_to_East(double PHI, double LAM, double a, double b, double e0, double f0, double PHI0, double LAM0) {
        //Project Latitude and longitude to Transverse Mercator eastings.
        //Input: - _
        //    Latitude (PHI) and Longitude (LAM) in decimal degrees; _
        //    ellipsoid axis dimensions (a & b) in meters; _
        //    eastings of false origin (e0) in meters; _
        //    central meridian scale factor (f0); _
        // latitude (PHI0) and longitude (LAM0) of false origin in decimal degrees.

        // Convert angle measures to radians
        var RadPHI = PHI * (Math.PI / 180);
        var RadLAM = LAM * (Math.PI / 180);
        var RadPHI0 = PHI0 * (Math.PI / 180);
        var RadLAM0 = LAM0 * (Math.PI / 180);

        var af0 = a * f0;
        var bf0 = b * f0;
        var e2 = (Math.pow(af0,2) - Math.pow(bf0,2)) / Math.pow(af0,2);
        var n = (af0 - bf0) / (af0 + bf0);
        var nu = af0 / (Math.sqrt(1 - (e2 * Math.pow(Math.sin(RadPHI),2) )));
        var rho = (nu * (1 - e2)) / (1 - (e2 * Math.pow(Math.sin(RadPHI),2) ));
        var eta2 = (nu / rho) - 1;
        var p = RadLAM - RadLAM0;

        var IV = nu * (Math.cos(RadPHI));
        var V = (nu / 6) * ( Math.pow(Math.cos(RadPHI),3)) * ((nu / rho) - (Math.pow(Math.tan(RadPHI),2)));
        var VI = (nu / 120) * (Math.pow(Math.cos(RadPHI),5)) * (5 - (18 * (Math.pow(Math.tan(RadPHI),2))) + (Math.pow(Math.tan(RadPHI),4)) + (14 * eta2) - (58 * (Math.pow(Math.tan(RadPHI),2)) * eta2));

        return e0 + (p * IV) + (Math.pow(p,3) * V) + (Math.pow(p,5) * VI);
    }


    public double Lat_Long_to_North(double PHI, double LAM, double a, double b, double e0, double n0, double f0, double PHI0, double LAM0) {
        // Project Latitude and longitude to Transverse Mercator northings
        // Input: - _
        // Latitude (PHI) and Longitude (LAM) in decimal degrees; _
        // ellipsoid axis dimensions (a & b) in meters; _
        // eastings (e0) and northings (n0) of false origin in meters; _
        // central meridian scale factor (f0); _
        // latitude (PHI0) and longitude (LAM0) of false origin in decimal degrees.

        // REQUIRES THE "Marc" FUNCTION

        // Convert angle measures to radians
        var RadPHI = PHI * (Math.PI / 180);
        var RadLAM = LAM * (Math.PI / 180);
        var RadPHI0 = PHI0 * (Math.PI / 180);
        var RadLAM0 = LAM0 * (Math.PI / 180);

        var af0 = a * f0;
        var bf0 = b * f0;
        var e2 = (Math.pow(af0,2) - Math.pow(bf0,2)) / Math.pow(af0,2);
        var n = (af0 - bf0) / (af0 + bf0);
        var nu = af0 / (Math.sqrt(1 - (e2 * Math.pow(Math.sin(RadPHI),2))));
        var rho = (nu * (1 - e2)) / (1 - (e2 * Math.pow(Math.sin(RadPHI),2)));
        var eta2 = (nu / rho) - 1;
        var p = RadLAM - RadLAM0;
        var M = Marc(bf0, n, RadPHI0, RadPHI);

        var I = M + n0;
        var II = (nu / 2) * (Math.sin(RadPHI)) * (Math.cos(RadPHI));
        var III = ((nu / 24) * (Math.sin(RadPHI)) * (Math.pow(Math.cos(RadPHI),3))) * (5 - (Math.pow(Math.tan(RadPHI),2)) + (9 * eta2));
        var IIIA = ((nu / 720) * (Math.sin(RadPHI)) * (Math.pow(Math.cos(RadPHI),5))) * (61 - (58 * (Math.pow(Math.tan(RadPHI),2))) + (Math.pow(Math.tan(RadPHI),4)));

        return I + (Math.pow(p,2) * II) + (Math.pow(p,4) * III) + (Math.pow(p,6) * IIIA);
    }
}
