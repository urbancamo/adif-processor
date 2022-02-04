package uk.m0nom.osgb36;

import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.GlobalCoordinates;

public class OsGb36Converter {
    private final OsGb36ConverterEngine engine;

    public OsGb36Converter() {
        engine = new OsGb36ConverterEngine();
    }

    public OsGb36ConverterResult convertOsGb36ToCoords(String osGb36Location) {
        OsGb36ConverterResult result = new OsGb36ConverterResult();
        result.setOsGb36(osGb36Location);

        engine.deletestringbuffers();
        engine.bngoldcoordinatesstringbuffer.append(osGb36Location);
        engine.bngoldheightstringbuffer.append("0.0");

        engine.convertoldbngtonewbng();
        if (engine.stopcalculating) {
            engine.showerrormessage("Calculation stopped!");
        } else {
            engine.convertnewbngtolonglatosgb36();
            engine.convertlonglatdecosgb36tolonglatosgb36();
            engine.convertlonglatosgb36cartesianosgb36();
            engine.helmerttransformation(false);
            engine.convertcartesianwgs84longlatwgs84();
            engine.convertlonglatdecwgs84tolonglatwgs84();
            engine.convertlatlongwgs84todbx();
            engine.calcconvandscale();
            engine.showresults();
        }

        result.setError(engine.messagestextareastringbuffer.toString().replace('\n', ' '));
        result.setSuccess(StringUtils.isEmpty(result.getError()));
        result.setCoords(new GlobalCoordinates(engine.latitudedecosgb36, engine.longitudedecosgb36));
        return result;
    }

    public OsGb36ConverterResult convertCoordsToOsGb36(GlobalCoordinates coords) {
        OsGb36ConverterResult result = new OsGb36ConverterResult();
        result.setCoords(coords);

        engine.deletestringbuffers();
        engine.latitudedecosgb36stringbuffer.append(coords.getLatitude());
        engine.longitudedecosgb36stringbuffer.append(coords.getLongitude());
        engine.bngoldheightstringbuffer.append("0.0");

        engine.convertlonglatosgb36tonewbng();
        if (engine.stopcalculating) {
            engine.showerrormessage("Calculation stopped!");
        } else {
            engine.convertlonglatdecosgb36tolonglatosgb36();
            engine.convertlonglatosgb36cartesianosgb36();
            engine.helmerttransformation(false);
            engine.convertcartesianwgs84longlatwgs84();
            engine.convertlonglatdecwgs84tolonglatwgs84();
            engine.convertlatlongwgs84todbx();
            engine.calcconvandscale();
            engine.convertnewbngtooldbng();
            engine.showresults();
        }
        result.setError(engine.messagestextareastringbuffer.toString().replace('\n', ' '));
        result.setSuccess(StringUtils.isEmpty(result.getError()));
        result.setOsGb36(engine.bngoldcoordinatesstringbuffer.toString());

        return result;
    }

    public OsGb36ConverterResult convertOsGb36EastingNorthingToCoords(String easting, String northing) {
        OsGb36ConverterResult result = new OsGb36ConverterResult();
        result.setOsGb36Easting(Double.parseDouble(easting));
        result.setOsGb36Northing(Double.parseDouble(northing));

        engine.deletestringbuffers();
        engine.bngeastingscoordinatesstringbuffer.append(easting);
        engine.bngnorthingscoordinatesstringbuffer.append(northing);
        engine.bngoldheightstringbuffer.append("0.0");

        engine.convertnewbngtolonglatosgb36();
        if (engine.stopcalculating) {
            engine.showerrormessage("Calculation stopped!");
        } else {
            engine.convertlonglatdecosgb36tolonglatosgb36();
            engine.convertlonglatosgb36cartesianosgb36();
            engine.helmerttransformation(false);
            engine.convertcartesianwgs84longlatwgs84();
            engine.convertlonglatdecwgs84tolonglatwgs84();
            engine.convertlatlongwgs84todbx();
            engine.calcconvandscale();
            engine.showresults();
        }
        result.setError(engine.messagestextareastringbuffer.toString().replace('\n', ' '));
        result.setSuccess(StringUtils.isEmpty(result.getError()));
        result.setCoords(new GlobalCoordinates(engine.latitudedecosgb36, engine.longitudedecosgb36));
        return result;
    }

    public OsGb36ConverterResult convertCoordsToOsGb36EastingNorthing(GlobalCoordinates coords) {
        OsGb36ConverterResult result = new OsGb36ConverterResult();
        result.setCoords(coords);

        engine.deletestringbuffers();
        engine.latitudedecosgb36stringbuffer.append(coords.getLatitude());
        engine.longitudedecosgb36stringbuffer.append(coords.getLongitude());
        engine.bngoldheightstringbuffer.append("0.0");

        engine.convertlonglatosgb36tonewbng();
        if (engine.stopcalculating) {
            engine.showerrormessage("Calculation stopped!");
        } else {
            engine.convertlonglatdecosgb36tolonglatosgb36();
            engine.convertlonglatosgb36cartesianosgb36();
            engine.helmerttransformation(false);
            engine.convertcartesianwgs84longlatwgs84();
            engine.convertlonglatdecwgs84tolonglatwgs84();
            engine.convertlatlongwgs84todbx();
            engine.calcconvandscale();
            engine.convertnewbngtooldbng();
            engine.showresults();
        }
        result.setError(engine.messagestextareastringbuffer.toString().replace('\n', ' '));
        result.setSuccess(StringUtils.isEmpty(result.getError()));
        result.setOsGb36Easting(Double.parseDouble(engine.bngeastingscoordinatesstringbuffer.toString()));
        result.setOsGb36Northing(Double.parseDouble(engine.bngnorthingscoordinatesstringbuffer.toString()));

        return result;
    }

}
