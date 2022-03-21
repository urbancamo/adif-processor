package uk.m0nom.errors;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class ErrorReporter {
    private static final ResourceBundle errors = ResourceBundle.getBundle("errors", Locale.UK);

    public static String formatError(String className, String key, Object[] args) {
        String lookup = String.format("%s.%s", className, key);
        String pattern = errors.getString(lookup);
        return MessageFormat.format(pattern, args);
    }
}
