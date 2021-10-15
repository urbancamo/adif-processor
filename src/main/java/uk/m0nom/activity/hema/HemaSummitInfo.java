package uk.m0nom.activity.hema;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;

@Getter
@Setter
public class HemaSummitInfo extends Activity {

    private int key;
    private boolean active;

    public HemaSummitInfo() {
        super(ActivityType.HEMA);
    }

    @Override
    public String getUrl() {
        return null;
    }
}
