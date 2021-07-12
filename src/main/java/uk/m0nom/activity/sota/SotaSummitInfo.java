package uk.m0nom.activity.sota;

import lombok.Getter;
import lombok.Setter;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.activity.Activity;

@Getter
@Setter
public class SotaSummitInfo extends Activity {
    double altitude;
    int points, bonusPoints;
}
