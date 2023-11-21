package org.marsik.ham.adif;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdifReaderException extends RuntimeException {
    private int record;

    public AdifReaderException(String msg, int record, RuntimeException cause) {
        super(String.format("%s (check record: %d)", msg, record), cause);
        setRecord(record);
    }
}
