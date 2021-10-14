package uk.m0nom.adif3.transform.tokenizer;

import java.util.Map;

public interface CommentTokenizer {
    Map<String, String> tokenize(String comment);
}
