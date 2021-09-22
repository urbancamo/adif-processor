package uk.m0nom.activity.lota;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;

@Getter
@Setter
public class LotaInfo extends Activity {
    private String country;
    private String dxcc;
    private String continent;
    private String status;
    private String location;

    public LotaInfo() {
        super(ActivityType.LOTA);
    }

    @Override
    public String getUrl() {
        return null;
    }
}
