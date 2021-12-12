package org.marsik.ham.adif.types;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.types.AdifType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Value
@AllArgsConstructor
public class Sota implements AdifType {
    String ituPrefix;
    String subdivision;
    Integer reference;

    @Override
    public String getValue() {
        String value = "";
        if (ituPrefix != null && subdivision != null && reference != null) {
            value = ituPrefix + (subdivision != null ? "/" + subdivision : "") + "-" + String.format("%03d", reference);
        }
        return value;
    }

    private static Pattern SOTA_RE = Pattern.compile("([0-9A-Z]+)/([A-Z][A-Z])-([0-9]+)", Pattern.CASE_INSENSITIVE);

    public static Sota valueOf(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        Matcher m = SOTA_RE.matcher(s);
        if (m.matches()) {
            return new Sota(m.group(1), m.group(2), Integer.parseInt(m.group(3)));
        } else {
            throw new IllegalArgumentException(String.format("'%s' is not a SOTA reference", s));
        }
    }
}
