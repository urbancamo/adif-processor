package uk.m0nom.activity.sota;

import lombok.Getter;
import lombok.Setter;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;

@Getter
@Setter
public class SotaSummitInfo extends Activity {
    private int points, bonusPoints;

    public SotaSummitInfo() {
        super(ActivityType.SOTA);
    }

    @Override
    public String getUrl() {
        return String.format("https://summits.sota.org.uk/summit/%s", getRef());
    }
}
