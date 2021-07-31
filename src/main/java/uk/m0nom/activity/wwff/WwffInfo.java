package uk.m0nom.activity.wwff;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;

import java.util.Date;

@Getter
@Setter
public class WwffInfo extends Activity {
    private Boolean active;
    private String program, dxcc, state, county, continent;
    private String iota, iaruLocator;

    private String IUCNcat;
    private Date validFrom,validTo;
    private String notes;
    private String lastMod;
    private String changeLog,reviewFlag,specialFlags,website,country,region;

    public WwffInfo() {
        super(ActivityType.WWFF);
    }
}
