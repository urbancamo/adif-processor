package uk.m0nom.adifproc.adif3.xsdquery;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * An element in the ADIF3 record
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class Adif3Field implements Comparable<Adif3Field> {
    private final String name;
    private Adif3Type type;
    private Boolean nillable;

    public Adif3FieldValidationResult isValid(String fieldValue) {
        Adif3TypeValidationResult typeResult = type.isValid(fieldValue);

        Adif3FieldValidationResult result = new Adif3FieldValidationResult(typeResult);

        // Check for nillable
        result.setDefinedWhenRequired(!nillable || StringUtils.isNotEmpty(fieldValue));

        return result;
    }

    @Override
    public int compareTo(@NotNull Adif3Field o) {
        return getName().compareTo(o.getName());
    }
}
