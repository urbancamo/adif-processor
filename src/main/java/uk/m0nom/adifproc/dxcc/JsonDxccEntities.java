package uk.m0nom.adifproc.dxcc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class JsonDxccEntities {
    @JsonProperty("dxcc")
    private Collection<JsonDxccEntity> rawDxccEntities;
}
