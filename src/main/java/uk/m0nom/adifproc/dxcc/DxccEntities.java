package uk.m0nom.adifproc.dxcc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.m0nom.adifproc.callsign.CallsignUtils;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class DxccEntities {

    private Collection<DxccEntity> dxccEntities = new ArrayList<>();

    private Map<Integer, DxccEntity> dxccEntityMap = new HashMap<>();

    public void setup(JsonDxccEntities rawDxccEntities) throws ParseException {
        for (JsonDxccEntity dxcc : rawDxccEntities.getRawDxccEntities()) {
            DxccEntity entity = new DxccEntity(dxcc);
            dxccEntities.add(entity);
            dxccEntityMap.put(entity.getEntityCode(), entity);
            entity.setPrefixes(getPrefixesForDxccEntity(entity));
        }
    }

    public DxccEntity getDxccEntity(int entityCode) {
        return dxccEntityMap.get(entityCode);
    }

    public Collection<DxccEntity> findEntitiesFromPrefix(String prefix, LocalDate qsoDate) {
        return dxccEntities
                .stream()
                .filter(entity -> entity.isValidForDate(qsoDate))
                .filter(entity -> Pattern.compile(entity.getPrefixRegex()).matcher(prefix.toUpperCase()).matches())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private DxccEntity tryFindEntitiesForSuffix(String callsign, LocalDate qsoDate) {
        String swappedCallsign = CallsignUtils.swapSuffixToPrefix(callsign);
        Collection<DxccEntity> matches = findEntitiesFromPrefix(swappedCallsign, qsoDate);
        switch (matches.size()) {
            case 1:
                return matches.iterator().next();
            case 2:
                return bestMatch(swappedCallsign, matches);
        }
        return null;
    }

    public DxccEntity findDxccEntityFromCallsign(String callsign, LocalDate qsoDate) {
        Collection<DxccEntity> matches;
        if (CallsignUtils.doesCallsignHaveNonStandardSuffix(callsign)) {
            return tryFindEntitiesForSuffix(callsign, qsoDate);
        } else {
            matches = findEntitiesFromPrefix(callsign, qsoDate);
        }

        switch (matches.size()) {
            case 0:
                return null;
            case 1:
                return matches.iterator().next();
            default:
                return bestMatch(callsign, matches);
        }
    }

    public Collection<String> getPrefixesForDxccEntity(DxccEntity entity) {
        Collection<String> prefixesToCheck = new ArrayList<>();

        String prefixList = entity.getPrefix();
        String[] prefixes = prefixList.split(",");
        for (String prefix : prefixes) {
            // See if we have any ranges
            if (prefix.contains("-")) {
                String[] rangePrefixes = prefix.split("-");
                if (rangePrefixes.length == 2) {
                    Collection<String> range = new ArrayList<>(DxccPermutations.generate(rangePrefixes[0], rangePrefixes[1]));
                    prefixesToCheck.addAll(range);
                } else if (rangePrefixes.length == 3) {
                    Collection<String> range = processComplexPrefixSpecification(rangePrefixes);
                    prefixesToCheck.addAll(range);
                }
            } else {
                prefixesToCheck.add(prefix);
            }
        }
        return prefixesToCheck;
    }

    private DxccEntity bestMatch(String callsign, Collection<DxccEntity> entities) {
        Map<String, DxccEntity> prefixesToCheck = new HashMap<>();

        for (DxccEntity entity : entities) {
            Collection<String> prefixes = getPrefixesForDxccEntity(entity);
            for (String prefix : prefixes) {
                prefixesToCheck.put(prefix, entity);
            }
        }

        return checkForLongestMatchingPrefix(callsign, prefixesToCheck);
    }

    public Collection<String> processComplexPrefixSpecification(String[] rangePrefixes) {
        Collection<String> prefixes = new ArrayList<>();
        if (rangePrefixes.length == 3) {
            // eg: UA-UI8-0 split into {'UA', 'UI8', '0'}.
            String outPrefixLeft = rangePrefixes[0];
            String outPrefixRight = rangePrefixes[1].substring(0, outPrefixLeft.length());
            Collection<String> outPrefixes = new ArrayList<>(DxccPermutations.generate(outPrefixLeft, outPrefixRight));
            String inPrefixRight = rangePrefixes[2];
            String inPrefixLeft = rangePrefixes[1].substring(rangePrefixes[1].length() - inPrefixRight.length());

            Collection<String> inPrefixes = new ArrayList<>();
            if (inPrefixRight.equals("0")) {
                inPrefixes.addAll(getSpecialRangEndingIn0(inPrefixLeft));
            } else {
                inPrefixes.addAll(DxccPermutations.generate(inPrefixLeft, inPrefixRight));
            }
            for (String outPrefix : outPrefixes) {
                for (String inPrefix : inPrefixes) {
                    prefixes.add(outPrefix.concat(inPrefix));
                }
            }
        }
        return prefixes;
    }

    private Collection<String> getSpecialRangEndingIn0(String inPrefixLeft) {
        Collection<String> prefixes = new ArrayList<>();
        int left = Integer.valueOf(inPrefixLeft);
        while (left < 10) {
            prefixes.add(Integer.toString(left++));
        }
        prefixes.add("0");
        return prefixes;
    }

    private Collection<String> getRangeOfPrefixes(String[] rangePrefixes) {
        Collection<String> prefixes = new ArrayList<>();
        Collection<String> range = new ArrayList<>(DxccPermutations.generate(rangePrefixes[0], rangePrefixes[1]));
        for (String rangePrefix : range) {
            prefixes.add(rangePrefix);
        }
        return prefixes;
    }

    private DxccEntity checkForLongestMatchingPrefix(String callsign, Map<String, DxccEntity> prefixesToCheck) {
        String longestPrefix = "";

        for (String prefix : prefixesToCheck.keySet()) {
            if (callsign.startsWith(prefix) && prefix.length() > longestPrefix.length()) {
                longestPrefix = prefix;
            }
        }
        return prefixesToCheck.get(longestPrefix);
    }
}
