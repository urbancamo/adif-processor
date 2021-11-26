package uk.m0nom.activity.wota;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;

/**
 * Additional information for a Wainwright on the Air summit
 * Cross-references to SOTA and HEMA summits exist in the WOTA database and have been retained here.
 */
@Getter
@Setter
public class WotaInfo extends Activity {
    private int internalId;
    private String sotaId;
    private String hemaId;

    private String book;
    private String reference;
    private String gridId;
    private int x, y;

    public WotaInfo() {
        super(ActivityType.WOTA);
    }

    @Override
    public String getUrl() {
        String lookupRef = getRef();
        if (StringUtils.equals(getBook(), "OF")) {
            // need to compensate for LDO weird numbering
            lookupRef = String.format("LDO-%03d", getInternalId());
        }
        return String.format("https://wota.org.uk/MM_%s", lookupRef);
    }
}


