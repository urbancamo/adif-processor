package uk.m0nom.adifproc.activity.cota;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityType;

/**
 * Encapsulates the information available about any location described in the Castles on the Air activity programme.
 */
@Getter
@Setter
public class CotaInfo extends Activity {
    private Boolean active;
    private String noCastles, prefix;
    private String location, information;

    public CotaInfo() {
        super(ActivityType.COTA);
    }

    @Override
    public String getUrl() {
        return null;
    }
}
