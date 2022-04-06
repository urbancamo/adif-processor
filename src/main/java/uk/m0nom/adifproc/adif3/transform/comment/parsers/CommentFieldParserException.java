package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import lombok.Getter;
import uk.m0nom.adifproc.adif3.contacts.Qso;

@Getter
public class CommentFieldParserException extends Exception {
    private final String className;
    private final String messageKey;
    private final boolean unmapped;
    private final Qso qso;
    private final Object[] args;

    public CommentFieldParserException(String className, String messageKey, Qso qso,  String... args) {
        super();
        this.className = className;
        this.messageKey = messageKey;
        this.unmapped = false;
        this.qso = qso;
        this.args = args;
    }

    public CommentFieldParserException(String className, String messageKey, Qso qso, Exception e, String... args) {
        super(e);
        this.className = className;
        this.qso = qso;
        this.messageKey = messageKey;
        this.unmapped = false;
        this.args = args;
    }

    public CommentFieldParserException(String className, String messageKey, Qso qso, boolean unmapped, String... args) {
        super();
        this.className = className;
        this.qso = qso;
        this.messageKey = messageKey;
        this.unmapped = unmapped;
        this.args = args;
    }

    public CommentFieldParserException(String className, String messageKey, Qso qso, Exception e, boolean unmapped, String... args) {
        super(e);
        this.className = className;
        this.qso = qso;
        this.messageKey = messageKey;
        this.unmapped = unmapped;
        this.args = args;
    }
}
