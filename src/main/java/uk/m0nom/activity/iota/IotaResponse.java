package uk.m0nom.activity.iota;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class IotaResponse {
    @JsonProperty("status")
    private String status;

    @JsonProperty("content")
    private Collection<IotaInfo> content;
}
