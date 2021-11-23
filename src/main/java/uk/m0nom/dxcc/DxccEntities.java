package uk.m0nom.dxcc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class DxccEntities {
    @JsonProperty("dxcc")
    private Collection<DxccEntity> dxccEntities;

    private Map<Integer,DxccEntity> dxccEntityMap = new HashMap<>();

    public void setup() {
        for (DxccEntity dxcc : dxccEntities) {
            dxccEntityMap.put(dxcc.getEntityCode(), dxcc);
        }
    }

    public DxccEntity getEntity(int code) {
        return dxccEntityMap.get(code);
    }
}
