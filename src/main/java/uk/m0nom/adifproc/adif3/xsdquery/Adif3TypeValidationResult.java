package uk.m0nom.adifproc.adif3.xsdquery;

import lombok.Data;

@Data
public class Adif3TypeValidationResult implements Adif3ValidationResult {
    private boolean isBaseType = true;
    private boolean isMatchingPattern = true;
    private boolean isWithinMin = true;
    private boolean isWithinMax = true;
    private boolean isValid = true;
    private boolean isParsable = true;

    @Override
    public boolean isValid() {
        return isBaseType && isMatchingPattern && isWithinMin && isWithinMax && isParsable;
    }
}


