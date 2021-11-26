package uk.m0nom.activity.iota;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import uk.m0nom.activity.iota.IotaIsland;

import java.util.Collection;

/**
 * Sub-group of islands that share a common main IOTA reference
 */
@Getter
@Setter
public class IotaSubGroup {
    @JsonProperty("subref")
    private String ref;

    @JsonProperty("subname")
    private String name;

    @JsonProperty("status")
    private String status;

    @JsonProperty("islands")
    private Collection<IotaIsland> islands;

    public boolean isActive() {
        return "Active".equals(status);
    }
}
