package uk.m0nom.adif3;

import com.amihaiemil.eoyaml.YamlMapping;
import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;

public class LogHXAdifRecordTransformer implements Adif3RecordTransformer {
    @Override
    public void transform(Adif3Record rec) {
        // Transform notes into comment
        if (StringUtils.isNotEmpty(rec.getNotes())) {
            rec.setComment(rec.getNotes());
            rec.setNotes("");
        }
    }
}
