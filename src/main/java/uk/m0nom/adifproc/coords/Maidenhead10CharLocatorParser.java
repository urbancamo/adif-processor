package uk.m0nom.adifproc.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.adifproc.maidenheadlocator.MaidenheadLocatorConversion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Maidenhead10CharLocatorParser implements LocationParser, LocationFormatter {
    private final static Pattern PATTERN = Pattern.compile("^([A-Ra-r]{2}[0-9]{2}[A-Xa-x]{2}[0-9]{2}[A-Xa-x]{2})$");
    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoords3D parse(LocationSource source, String locationString) {
        Matcher matcher = getPattern().matcher(locationString);
        if (matcher.find()) {
            String locator = matcher.group(1);
            return MaidenheadLocatorConversion.locatorToCoords(source, locator);
        }
        return null;
    }

    @Override
    public String format(GlobalCoordinates coords) {
        return MaidenheadLocatorConversion.coordsToLocator(coords, 10);
    }

    @Override
    public String getName() {
        return "Maidenhead Locator 10 Char";
    }

}
