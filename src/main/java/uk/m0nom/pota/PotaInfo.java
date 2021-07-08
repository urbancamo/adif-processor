package uk.m0nom.pota;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class PotaInfo {
    String reference;
    String name;
    Boolean active;
    Integer entityId;
    String locationDesc;
    Double latitude;
    Double longitude;
    String grid;

    public boolean hasCoord() {
        return latitude != null && longitude != null;
    }

    public boolean hasGrid() {
        return StringUtils.isNotEmpty(grid);
    }
}
