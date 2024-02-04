package uk.m0nom.adifproc.adif3.xsdquery;

import lombok.Data;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Data
@ToString
public class Adif3Type implements Comparable<Adif3Type> {
    private String name;
    private String baseType;
    private Pattern regex;
    private boolean preserveWhiteSpace;
    private Integer minInclusive;
    private Integer maxInclusive;

    public Adif3TypeValidationResult isValid(String value) {
        Adif3TypeValidationResult result = new Adif3TypeValidationResult();

        result.setBaseType(checkBaseType(value));

        if (regex != null) {
            result.setMatchingPattern(regex.matcher(value).matches());
        }

        try {
            if (minInclusive != null || maxInclusive != null) {
                int intVal = Integer.parseInt(value);
                if (minInclusive != null) {
                    result.setWithinMin(intVal >= minInclusive);
                }
                if (maxInclusive != null) {
                    result.setWithinMax(intVal <= maxInclusive);
                }
            }
        } catch (NumberFormatException nfe) {
            result.setBaseType(false);
        }

        return result;
    }

    private List<String> POSITIVE_BASE_TYPES = Arrays.asList("PositiveInteger", "IntegerGE0", "UnsignedInt", "NumberGE0");

    private boolean checkBaseType(String value) {
        boolean valid = true;

        switch (baseType) {
            case "xs:decimal":
            case "PositiveInteger":
            case "UnsignedInt":
            case "IntegerGE0":
                int iVal = 0;
                try {
                    iVal = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    valid = false;
                }
                if (POSITIVE_BASE_TYPES.contains(baseType) && iVal < 0) {
                    valid = false;
                }
                break;
            case "NumberGE0":
            case "Number":
                double dVal = 0.0;
                try {
                    dVal = Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    valid = false;
                }
                if (POSITIVE_BASE_TYPES.contains(baseType) && dVal < 0) {
                    valid = false;
                }
                break;
        }
        return valid;
    }

    @Override
    public int compareTo(@NotNull Adif3Type o) {
        return getName().compareTo(o.getName());
    }
}
