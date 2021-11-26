package uk.m0nom.activity.iota;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Information about an individual island which might be part of a group of islands captured using a single
 * IOTA reference.
 */
@Getter
@Setter
public class IotaIsland {
    @JsonProperty("id")
    private int id;

    @JsonProperty("island_name")
    private String name;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("excluded")
    private int excluded;

    public boolean isExcluded() {
        return excluded == 1;
    }
}
