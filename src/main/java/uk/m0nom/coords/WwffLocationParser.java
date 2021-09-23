package uk.m0nom.coords;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.wwff.WwffInfo;

import java.util.regex.Pattern;

@Getter
@Setter
public class WwffLocationParser implements LocationParser {
    private ActivityDatabase wwffDatabase;
    private final static Pattern PATTERN = Pattern.compile("([\\w\\d]+FF-\\d\\d\\d\\d)");

    public WwffLocationParser(ActivityDatabase wwffDatabase) {
        setWwffDatabase(wwffDatabase);
    }

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoordinatesWithLocationSource parse(String location) {
        WwffInfo wwff = (WwffInfo) wwffDatabase.get(location);
        if (wwff != null) {
            if (wwff.hasCoords()) {
                return new GlobalCoordinatesWithLocationSource(wwff.getCoords(), LocationSource.WWFF);
            }
        }
        return null;
    }
}
