package uk.m0nom.activity.hema;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;

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
