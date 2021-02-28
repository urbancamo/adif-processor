package uk.m0nom.adif3;

import com.amihaiemil.eoyaml.YamlMapping;
import org.marsik.ham.adif.Adif3Record;

public interface Adif3RecordTransformer {
    void transform(Adif3Record rec);
}
