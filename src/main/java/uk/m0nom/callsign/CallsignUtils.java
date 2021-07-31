package uk.m0nom.callsign;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities to deal with callsigns
 */
public class CallsignUtils {

    public final static Collection<CallsignSuffix> SUFFIXES = new ArrayList<>(List.of(
            CallsignSuffix.PORTABLE,
            CallsignSuffix.MOBILE,
            CallsignSuffix.MARITIME_MOBILE,
            CallsignSuffix.PEDESTRIAN_MOBILE,
            CallsignSuffix.ALTERNATIVE_ADDRESS
    ));

    @AllArgsConstructor
    private static class CallsignRegexMap {
        CallsignVariant variant;
        Pattern pattern;
    }

    private final static List<CallsignRegexMap> UK_CALLSIGN_REGEXS = new ArrayList<>(List.of(
            new CallsignRegexMap(CallsignVariant.G_ALT, Pattern.compile("^[GM][0-9]", Pattern.CASE_INSENSITIVE)),
            new CallsignRegexMap(CallsignVariant.GM_ALT, Pattern.compile("^[GM]M[0-9]", Pattern.CASE_INSENSITIVE)),
            new CallsignRegexMap(CallsignVariant.GG_ALT, Pattern.compile("^[GM]G[0-9]", Pattern.CASE_INSENSITIVE)),
            new CallsignRegexMap(CallsignVariant.GI_ALT, Pattern.compile("^[GM]I[0-9]", Pattern.CASE_INSENSITIVE)),
            new CallsignRegexMap(CallsignVariant.GW_ALT, Pattern.compile("^[GM]W[0-9]", Pattern.CASE_INSENSITIVE))
    ));

    private static Callsign gToGxVariant(Callsign ukVariant, CallsignVariant variant) {
        String variantCallsign;
        String callsign = ukVariant.getCallsign();
        
        if (ukVariant.getVariant() == CallsignVariant.G_ALT) {
            variantCallsign = String.format("%s%s%s", callsign.substring(0, 1), variant.getModifier(), callsign.substring(1, callsign.length()));
        } else {
            variantCallsign = String.format("%s%s%s", callsign.substring(0, 1), variant.getModifier(), callsign.substring(2, callsign.length()));
        }
        return new Callsign(variantCallsign, variant);
    }

    public static List<Callsign> getUkCallsignVariants(String callsign) {
        List<Callsign> ukVariants = new ArrayList<>();

        Callsign ukVariant = null;
        // Determine if this is a UK callsign
        for (CallsignRegexMap callsignRegex : UK_CALLSIGN_REGEXS) {
            Matcher matcher = callsignRegex.pattern.matcher(callsign);
            if (matcher.find()) {
                ukVariant = new Callsign(callsign, callsignRegex.variant);
            }
        }

        if (ukVariant != null) {
            switch (ukVariant.getVariant()) {
                case G_ALT:
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GD_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GG_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GI_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GM_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GW_ALT));
                    break;
                case GM_ALT:
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.G_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GD_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GG_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GI_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GW_ALT));
                    break;
                case GW_ALT:
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.G_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GD_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GG_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GI_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GM_ALT));
                    break;
                case GG_ALT:
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.G_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GD_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GI_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GM_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GW_ALT));
                    break;
                case GI_ALT:
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.G_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GD_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GG_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GM_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GW_ALT));
                    break;
                case GD_ALT:
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.G_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GG_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GI_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GM_ALT));
                    ukVariants.add(gToGxVariant(ukVariant, CallsignVariant.GW_ALT));
                    break;
            }
        }
        return ukVariants;
    }

    /**
     * Determine all the variants that could possibly be the base callsign for the callsign supplied.
     * So this could be a /A, it could be an English operator working in Wales, it could be a Swiss operator
     * activating a summit in Iceland.
     * The list is ordered, so the 'best guess' callsigns are the top of the list, going back to the fixed station
     * data for the operator in their own country as the worst guess.
     *
     * @param callsign to check
     * @return variants
     */
    public static List<Callsign> getCallsignVariants(String callsign) {
        List<Callsign> variants = new ArrayList<>();
        Callsign portableInCountry = new Callsign(callsign, CallsignVariant.IN_COUNTRY);
        variants.add(portableInCountry);

        // Strip off any suffixes such as /P, /M, /MM etc.
        Callsign fixedInCountry = getOperatorWithoutSuffix(callsign);
        Callsign portableInHomeCountry = getOperatorAsIsInHomeCountry(callsign);
        Callsign fixedInHomeCountry = null;
        if (portableInHomeCountry != null && !portableInCountry.getCallsign().equals(portableInHomeCountry.getCallsign())) {
            variants.add(portableInHomeCountry);
        }
        if (fixedInCountry != null) {
            variants.add(fixedInCountry);
        }
        if (fixedInCountry != null) {
            fixedInHomeCountry = getOperatorWithoutCountryPrefix(fixedInCountry.getCallsign());
            if (fixedInHomeCountry != null && !fixedInCountry.getCallsign().equals(fixedInHomeCountry.getCallsign())) {
                variants.add(fixedInHomeCountry);
            }
        }
        variants.addAll(getUkCallsignVariants(callsign));
        if (portableInHomeCountry != null && !portableInCountry.getCallsign().equals(portableInHomeCountry.getCallsign())) {
            variants.addAll(getUkCallsignVariants(portableInHomeCountry.getCallsign()));
        }
        if (fixedInHomeCountry != null) {
            variants.addAll(getUkCallsignVariants(fixedInHomeCountry.getCallsign()));
        }
        return variants;
    }

    private static Callsign getOperatorAsIsInHomeCountry(String callsign) {
        CallsignSuffix suffix = getSuffix(callsign);
        if (suffix != null) {
            String portableHomeCountry = stripCountryPrefix(callsign);
            return new Callsign(portableHomeCountry, CallsignVariant.HOME_COUNTRY);
        }
        return null;
    }


    private static Callsign getOperatorWithoutCountryPrefix(String fixedInCountry) {
        if (isAbroad(fixedInCountry)) {
            String homeCallsign = stripCountryPrefix(fixedInCountry);
            return new Callsign(homeCallsign, CallsignVariant.HOME_COUNTRY);
        }
        return new Callsign(fixedInCountry, CallsignVariant.HOME_COUNTRY);
    }

    private static Callsign getOperatorWithoutSuffix(String callsign) {
        String fixedCallsign = stripSuffix(callsign);
        if (!StringUtils.equalsIgnoreCase(fixedCallsign, callsign)) {
            return new Callsign(fixedCallsign, CallsignVariant.IN_COUNTRY);
        }
        return null;
    }


    public static boolean containsCallsign(List<Callsign> variants, String callsign) {
        for (Callsign op : variants) {
            if (StringUtils.equalsIgnoreCase(op.getCallsign(), callsign)) {
                return true;
            }
        }
        return false;
    }

    public static String stripSuffix(String callsign) {
        // See if the suffix matches a standard one
        CallsignSuffix suffix = getSuffix(callsign);
        if (suffix != null) {
            return callsign.substring(0, callsign.length() - suffix.getSuffix().length());
        }
        return callsign;
    }

    public static CallsignSuffix getSuffix(String callsign) {
        int loc = callsign.lastIndexOf('/');
        if (loc != -1) {
            String suffix = callsign.substring(loc, callsign.length());
            for (CallsignSuffix match : SUFFIXES) {
                if (StringUtils.equalsIgnoreCase(match.getSuffix(), suffix)) {
                    return match;
                }
            }
        }
        return null;
    }

    public static boolean isValidSuffix(String suffix) {
        return SUFFIXES.contains(suffix.toUpperCase());
    }

    // TODO these methods are very crude and need refactoring to include cty.dat lookups
    public static boolean isAbroad(String callsign) {
        int loc = callsign.indexOf('/');
        if (loc != -1) {
            CallsignSuffix suffix = getSuffix(callsign);
            if (suffix != null) {
                return !StringUtils.equals(callsign.substring(loc, callsign.length()), suffix.getSuffix());
            }
            return true;
        }
        return false;
    }

    public static String stripCountryPrefix(String callsign) {
        if (isAbroad(callsign)) {
            int loc = callsign.indexOf('/');
            return callsign.substring(loc+1, callsign.length());
        }
        return callsign;
    }

}
