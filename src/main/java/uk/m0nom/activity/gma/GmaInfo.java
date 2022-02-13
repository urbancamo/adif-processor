package uk.m0nom.activity.gma;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;

/**
 * Additional information for a Global Mountain Activity
 */
@Getter
@Setter
public class GmaInfo extends Activity {

    public GmaInfo() {
        super(ActivityType.GMA);
    }

    @Override
    public String getUrl() {
        return String.format("https://www.cqgma.org/zinfo.php?ref=%s", getRef());
    }
}
