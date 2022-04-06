package uk.m0nom.adifproc.errors;

import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;

public class MessageFormatterTest {
    @Test
    public void test() {
        String pattern = "On {0, date}, {1} sent you "
                + "{2, choice, 0#no messages|1#a message|2#two messages|2<{2, number, integer} messages}.";
        MessageFormat formatter = new MessageFormat(pattern, Locale.UK);
        Date date = new Date();
        String message = formatter.format(new Object[] {date, "Alice", 2});
        System.out.println(message);
    }
}
