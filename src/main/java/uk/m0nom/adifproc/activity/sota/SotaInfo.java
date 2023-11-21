package uk.m0nom.adifproc.activity.sota;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityType;

import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * Additional information for a Summit on the Air
 */
@Getter
@Setter
public class SotaInfo extends Activity {
    private int points, bonusPoints;
    private ZonedDateTime validFrom, validTo;

    public SotaInfo() {
        super(ActivityType.SOTA);
    }

    @Override
    public boolean isValid(ZonedDateTime onDate) {
        return (onDate.isEqual(validFrom) || onDate.isAfter(validFrom)) && onDate.isBefore(validTo);
    }

    @Override
    public String getUrl() {
        return String.format("https://summits.sota.org.uk/summit/%s", getRef());
    }
}
