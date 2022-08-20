package uk.m0nom.adifproc.dxcc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public Collection<DxccEntity> findEntitiesFromPrefix(String prefix) {
        return dxccEntities
                .stream()
                .filter(entity -> Pattern.compile(entity.getPrefixRegex()).matcher(prefix.toUpperCase()).matches())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public DxccEntity findDxccEntityFromCallsign(String callsign) {
        Collection<DxccEntity> matches = findEntitiesFromPrefix(callsign);
        switch (matches.size()) {
            case 0:
                return null;
            case 1:
                return matches.iterator().next();
            default:
                return bestMatch(callsign, matches);
        }
    }

    private DxccEntity bestMatch(String callsign, Collection<DxccEntity> entities) {
        int len = 0;
        DxccEntity bestMatch = null;
        Map<String, DxccEntity> prefixesToCheck = new HashMap<>();

        for (DxccEntity entity : entities) {
            String prefixList = entity.getPrefix();
            String[] prefixes = prefixList.split(",");
            for (String prefix : prefixes) {
                // See if we have any ranges
                if (prefix.contains("-")) {
                    String[] rangePrefixes = prefix.split("-");
                    Collection<String> range = new ArrayList<>();
                    range.addAll(DxccPermutations.generate(rangePrefixes[0], rangePrefixes[1]));
                    for (String rangePrefix : range) {
                        prefixesToCheck.put(rangePrefix, entity);
                    }
                } else {
                    prefixesToCheck.put(prefix, entity);
                }
            }
        }

        return checkForLongestMatchingPrefix(callsign, prefixesToCheck);
    }

    private DxccEntity checkForLongestMatchingPrefix(String callsign, Map<String, DxccEntity> prefixesToCheck) {
        String longestPrefix = "";

        for (String prefix: prefixesToCheck.keySet()) {
            if (callsign.startsWith(prefix) && prefix.length() > longestPrefix.length()) {
                longestPrefix = prefix;
            }
        }
        return prefixesToCheck.get(longestPrefix);
    }
}
