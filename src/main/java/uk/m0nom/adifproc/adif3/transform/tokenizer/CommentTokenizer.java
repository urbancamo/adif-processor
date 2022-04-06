package uk.m0nom.adifproc.adif3.transform.tokenizer;

import java.util.Map;

/**
 * Defines the interface that comment tokenizers implement to break up a comment into key/value pairs
 */
public interface CommentTokenizer {
    Map<String, String> tokenize(String comment);
}
