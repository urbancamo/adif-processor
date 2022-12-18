package org.marsik.ham.adif.types;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Value
@AllArgsConstructor
public class Pota implements AdifType {
    String nation;
    Integer reference;
    String iso31662code;

    @Override
    public String getValue() {
        String value = "";
        String refStr;
        if (nation != null && reference != null) {
            if (reference < 9999) {
                refStr = String.format("%04d", reference);
            } else {
                refStr = String.format("%d", reference);
            }
            value = nation + "-" + refStr;
            if (iso31662code != null) {
                value += "@" + iso31662code;
            }
        }
        return value;
    }

    public static Pattern POTA_RE = Pattern.compile("([\\dA-Za-z]{1,4})-(\\d{4,5})", Pattern.CASE_INSENSITIVE);
    public static Pattern POTA_ISO_RE = Pattern.compile("([\\dA-Za-z]{1,4})-(\\d{4,5})@([A-Z]{2})", Pattern.CASE_INSENSITIVE);
    public static Pattern POTA_ISO_SUB_RE = Pattern.compile("([\\dA-Z]{1,4})-(\\d{4,5})@([A-Z]{2})-([\\dA-Z]{2,3})", Pattern.CASE_INSENSITIVE);

    public static Pota valueOf(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        Matcher potaMatcher = POTA_RE.matcher(s);
        Matcher potaIsoMatcher = POTA_ISO_RE.matcher(s);
        Matcher potaIsoSubMatcher = POTA_ISO_SUB_RE.matcher(s);

        if (potaIsoSubMatcher.matches()) {
          return new Pota(potaIsoSubMatcher.group(1), Integer.parseInt(potaIsoSubMatcher.group(2)), potaIsoSubMatcher.group(3) + "-" + potaIsoSubMatcher.group(4));
        } else if (potaIsoMatcher.matches()) {
            return new Pota(potaIsoMatcher.group(1), Integer.parseInt(potaIsoMatcher.group(2)), potaIsoMatcher.group(3));
        } else if (potaMatcher.matches()) {
            return new Pota(potaMatcher.group(1), Integer.parseInt(potaMatcher.group(2)), null);
        }else {
            throw new IllegalArgumentException(String.format("'%s' is not a POTA reference", s));
        }
    }
}
