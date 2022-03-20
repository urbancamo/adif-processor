package uk.m0nom.adif3.transform.comment;

import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.transform.TransformResults;

import java.util.Map;

public interface CommentTransformer {
    void transformComment(Qso qso, String comment, Map<String, String> unmapped, TransformResults results);
}