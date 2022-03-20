package uk.m0nom.activity.sota;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;

import java.time.LocalDate;

/**
 * Additional information for a Summit on the Air
 */
@Getter
@Setter
public class SotaInfo extends Activity {
    private int points, bonusPoints;
    private LocalDate validFrom, validTo;

    public SotaInfo() {
        super(ActivityType.SOTA);
    }

    @Override
    public boolean isValid(LocalDate onDate) {
        return (onDate.isEqual(validFrom) || onDate.isAfter(validFrom)) && onDate.isBefore(validTo);
    }

    @Override
    public String getUrl() {
        return String.format("https://summits.sota.org.uk/summit/%s", getRef());
    }
}
