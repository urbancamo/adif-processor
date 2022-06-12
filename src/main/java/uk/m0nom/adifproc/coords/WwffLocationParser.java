package uk.m0nom.adifproc.coords;

import lombok.Getter;
import lombok.Setter;
import org.marsik.ham.adif.types.Wwff;
import uk.m0nom.adifproc.activity.ActivityDatabase;
import uk.m0nom.adifproc.activity.wwff.WwffInfo;

import java.util.regex.Pattern;

@Getter
@Setter
public class WwffLocationParser implements LocationParser {
    private ActivityDatabase wwffDatabase;
    private final static Pattern PATTERN = Wwff.WWFF_RE;

    public WwffLocationParser(ActivityDatabase wwffDatabase) {
        setWwffDatabase(wwffDatabase);
    }

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoords3D parse(LocationSource source, String location) {
        WwffInfo wwff = (WwffInfo) wwffDatabase.get(location);
        if (wwff != null) {
            if (wwff.hasCoords()) {
                return new GlobalCoords3D(wwff.getCoords(), LocationSource.ACTIVITY, LocationAccuracy.LAT_LONG);
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return "WWFF Location";
    }
}
