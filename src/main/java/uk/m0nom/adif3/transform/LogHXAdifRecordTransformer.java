package uk.m0nom.adif3.transform;

import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.contacts.Qsos;
import uk.m0nom.adif3.transform.Adif3RecordTransformer;

public class LogHXAdifRecordTransformer implements Adif3RecordTransformer {
    @Override
    public void transform(Qsos qsos, Adif3Record rec) {
        // Transform notes into comment
        if (StringUtils.isNotEmpty(rec.getNotes())) {
            rec.setComment(rec.getNotes());
            rec.setNotes("");
        }
    }
}
