package uk.m0nom.activity.wota;

import lombok.Getter;
import lombok.Setter;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;

@Getter
@Setter
public class WotaSummitInfo extends Activity {
    private int internalId;
    private String sotaId;
    private String hemaId;

    private String book;
    private int height;
    private String reference;
    private String gridId;
    private int x, y;

    public WotaSummitInfo() {
        super(ActivityType.WOTA);
    }
}


