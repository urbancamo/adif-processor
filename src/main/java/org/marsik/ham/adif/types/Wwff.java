package org.marsik.ham.adif.types;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Value
@AllArgsConstructor
public class Wwff implements AdifType {
    String nation;
    Integer reference;

    @Override
    public String getValue() {
        String value = "";
        if (nation != null && reference != null) {
            value = nation + "FF-" + String.format("%04d", reference);
        }
        return value;
    }

    public static Pattern WWFF_RE = Pattern.compile("([\\dA-Z]+){1,4}FF-(\\d{4})", Pattern.CASE_INSENSITIVE);

    public static Wwff valueOf(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        Matcher m = WWFF_RE.matcher(s);
        if (m.matches()) {
            return new Wwff(m.group(1), Integer.parseInt(m.group(2)));
        } else {
            throw new IllegalArgumentException(String.format("'%s' is not a WWFF reference", s));
        }
    }
}
