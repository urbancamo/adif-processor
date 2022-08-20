package uk.m0nom.adifproc.dxcc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class DxccEntity {
    @JsonProperty("continent")
    private Collection<String> continent;

    @JsonProperty("countryCode")
    private String countryCode;

    @JsonProperty("cq")
    private Collection<Integer> cq;

    @JsonProperty("deleted")
    private boolean deleted;

    @JsonProperty("entityCode")
    private int entityCode;

    @JsonProperty("flag")
    private String flag;

    @JsonProperty("itu")
    private Collection<Integer> itu;

    @JsonProperty("name")
    private String name;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("outgoingQslService")
    private boolean outgoingQslService;

    @JsonProperty("prefix")
    private String prefix;

    @JsonProperty("prefixRegex")
    private String prefixRegex;

    @JsonProperty("thirdPartyTraffic")
    private boolean thirdPartyTraffic;

    @JsonProperty("validEnd")
    private String validEnd;

    @JsonProperty("validStart")
    private String validStart;

    private Collection<String> prefixes;
}
