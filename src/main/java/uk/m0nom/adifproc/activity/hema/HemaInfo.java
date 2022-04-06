package uk.m0nom.adifproc.activity.hema;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityType;

/**
 * Encapsulates the additional information stored about a HEMA summit
 */
@Getter
@Setter
public class HemaInfo extends Activity {

    private int key;
    private boolean active;

    public HemaInfo() {
        super(ActivityType.HEMA);
    }

    @Override
    public String getUrl() {
        return null;
    }
}
