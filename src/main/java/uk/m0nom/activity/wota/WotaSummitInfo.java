package uk.m0nom.activity.wota;

import lombok.Getter;
import lombok.Setter;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;

@Getter
@Setter
public class WotaSummitInfo extends Activity {
    int internalId;
    String sotaId;
    String hemaId;

    String book;
    int height;
    String reference;
    String gridId;
    int x,y;

    public WotaSummitInfo() {
        super(ActivityType.WOTA);
    }
}


