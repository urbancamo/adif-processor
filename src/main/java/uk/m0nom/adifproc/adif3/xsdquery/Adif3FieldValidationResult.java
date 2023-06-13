package uk.m0nom.adifproc.adif3.xsdquery;

import lombok.Data;

@Data
public class Adif3FieldValidationResult implements Adif3ValidationResult {
    private boolean isDefinedWhenRequired = true;
    private Adif3TypeValidationResult typeValidationResult;

    public Adif3FieldValidationResult(Adif3TypeValidationResult typeResult) {
        setTypeValidationResult(typeResult);
    }

    @Override
    public boolean isValid() {
        return typeValidationResult.isValid() && isDefinedWhenRequired;
    }
}
