package uk.m0nom.activity.hema;

import lombok.Getter;
import lombok.Setter;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;

@Getter
@Setter
public class HemaSummitInfo extends Activity {
    int key;
    double altitude;
    boolean active;

    public HemaSummitInfo() {
        super(ActivityType.HEMA);
    }
}
