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
    Boolean active;
    Integer entityId;
    String locationDesc;
    String grid;

    public PotaInfo() {
        super(ActivityType.POTA);
    }

    public boolean hasGrid() {
        return StringUtils.isNotEmpty(grid);
    }
}
