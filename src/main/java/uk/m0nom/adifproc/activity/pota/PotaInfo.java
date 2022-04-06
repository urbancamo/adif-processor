package uk.m0nom.adifproc.activity.pota;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityType;

/**
 * Additional information for a Park on the Air
 */
@Getter
@Setter
public class PotaInfo extends Activity {
    private Boolean active;
    private Integer entityId;
    private String locationDesc;

    public PotaInfo() {
        super(ActivityType.POTA);
    }

    @Override
    public String getUrl() {
        return String.format("https://pota.app/#/park/%s", getRef());
    }
}
