package uk.m0nom.coords;

import uk.m0nom.maidenheadlocator.MaidenheadLocatorConversion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Maidenhead8CharLocatorParser implements LocationParser {
    private final static Pattern PATTERN = Pattern.compile("([A-R]{2}[0-9]{2}[A-X]{2}[0-9]{2})");
    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoordinatesWithSourceAccuracy parse(LocationSource source, String locationString) {
        Matcher matcher = getPattern().matcher(locationString);
        if (matcher.find()) {
            String locator = matcher.group(1);
            return MaidenheadLocatorConversion.locatorToCoords(source, locator);
        }
        return null;
    }
}
