package uk.m0nom.adif3.transform.tokenizer;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Parses KEY1: VALUE1, KEY2: VALUE2, ...
 * into a map of key value pairs
 */
public class ColonCommaTokenizer implements CommentTokenizer {
    @Override
    public Map<String, String> tokenize(String comment) {
        Map<String, String> tokens = new LinkedHashMap<>();
        StringTokenizer tokenizer = new StringTokenizer(comment, ",");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.contains(":")) {
                String[] pair = StringUtils.split(token, ":");
                tokens.put(pair[0].trim().toUpperCase(), pair[1].trim());
            }
        }
        return tokens;
    }
}
