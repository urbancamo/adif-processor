package uk.m0nom.activity.cota;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;

import java.util.Date;

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
