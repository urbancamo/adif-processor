package uk.m0nom.adifproc.activity.bota;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityType;

/**
 * Additional information for a Global Mountain Activity
 */
@Getter
@Setter
public class BotaInfo extends Activity {
    private String bunkerType;
    private String area;
    private String osgr;
    private String wab;
    private String nearestPostcode;

    public BotaInfo() {
        super(ActivityType.BOTA);
    }

    @Override
    public String getUrl() {
        return "https://ukbota.net/";
    }
}
