package uk.m0nom.adifproc.adif3.transform.tokenizer;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses KEY1: VALUE2 KEY2: VALUE 2 strings
 * note the lack of the comma. The string is split at the colon and the word before taken as the key
 * and everything after as the value. This allows for us to specify COORD: lat, long so that Google
 * Maps LAT/LONG can be pasted as is into the string
 */
@Service
public class ColonTokenizer implements CommentTokenizer {
    @Override
    public Map<String, String> tokenize(String comment) {
        Map<String, String> tokens = new LinkedHashMap<>();
        StringTokenizer tokenizer = new StringTokenizer(comment, ":");

        String previousToken = "";
        while (tokenizer.hasMoreTokens()) {
            String key;
            String token = tokenizer.nextToken().trim();
            if (previousToken.isEmpty()) {
                key = token;
                if (tokenizer.hasMoreTokens()) {
                    token = tokenizer.nextToken().trim();
                }
            } else {
                key = getKeyFromPrevious(previousToken);
            }
            String value = token;
            if (tokenizer.hasMoreTokens()) {
                value = getValueFromCurrent(token);
            }
            previousToken = token;
            tokens.put(key.trim().toUpperCase(), value.trim());
        }
        return tokens;
    }

    private String getValueFromCurrent(String token) {
        // Looking for everything up to the last word before the colon
        Pattern pattern = Pattern.compile("(.*)[,\\s]+\\S+");
        Matcher matcher = pattern.matcher(token);
        String value = token;
        if (matcher.matches()) {
            value = matcher.group(1);
            if (value.endsWith(",")) {
                value = value.substring(0, value.length()-1);
            }
        }
        return value;
    }

    private String getKeyFromPrevious(String previous) {
        // We are looking for the last whole word here which is the key
        Pattern pattern = Pattern.compile(".*[,\\s](\\S+)");
        Matcher matcher = pattern.matcher(previous);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return "";
    }
}
