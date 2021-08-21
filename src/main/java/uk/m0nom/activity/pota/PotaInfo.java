package uk.m0nom.activity.pota;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;

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
