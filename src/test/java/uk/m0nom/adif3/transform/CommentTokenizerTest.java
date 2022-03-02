package uk.m0nom.adif3.transform;


import org.junit.jupiter.api.Test;
import uk.m0nom.adif3.transform.tokenizer.ColonCommaTokenizer;
import uk.m0nom.adif3.transform.tokenizer.ColonTokenizer;
import uk.m0nom.adif3.transform.tokenizer.CommentTokenizer;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/** Check that each tokenizer does the right thing for comment strings */
public class CommentTokenizerTest {

    private final static String TEST1_INPUT = "OP: Mark";
    private final static String[] TEST1_EXPECTED = {"OP", "Mark"};

    private final static String TEST2_INPUT = "OP: Matthias, QTH: Ravenna, WX: 30 degC, RIG: Yaesu FT-891, PWR: 100, ANT: EFHW 13m Long";
    private final static String TEST2_EXPECTED[] = {"OP", "Matthias", "QTH", "Ravenna", "WX", "30 degC", "RIG", "Yaesu FT-891", "PWR", "100", "ANT", "EFHW 13m Long"};

    private final static String TEST3_INPUT = "OP: Matthias QTH: Ravenna WX: 30 degC RIG: Yaesu FT-891 PWR: 100 ANT: EFHW 13m Long";
    private final static String TEST3_EXPECTED[] = {"OP", "Matthias", "QTH", "Ravenna", "WX", "30 degC", "RIG", "Yaesu FT-891", "PWR", "100", "ANT", "EFHW 13m Long"};

    private final static String TEST4_INPUT = "OP: Matthias,QTH: Ravenna,WX: 30 degC,RIG: Yaesu FT-891,PWR: 100,ANT: EFHW 13m Long";
    private final static String TEST4_EXPECTED[] = {"OP", "Matthias", "QTH", "Ravenna", "WX", "30 degC", "RIG", "Yaesu FT-891", "PWR", "100", "ANT", "EFHW 13m Long"};

    private final static String TEST5_INPUT = "OP:Mark";
    private final static String[] TEST5_EXPECTED = {"OP", "Mark"};

    private final static String TEST6_INPUT = "";
    private final static String[] TEST6_EXPECTED = {};

    private final static String TEST7_INPUT = "COORD: 54.37037777557802, -2.922647707561104";
    private final static String[] TEST7_EXPECTED = {"COORD", "54.37037777557802, -2.922647707561104"};

    private final static String TEST8_INPUT = "COORD: 54.37037777557802, -2.922647707561104 OP: Matthias,QTH: Ravenna,WX: 30 degC,RIG: Yaesu FT-891,PWR: 100,ANT: EFHW 13m Long";
    private final static String TEST8_EXPECTED[] = {"COORD", "54.37037777557802, -2.922647707561104", "OP", "Matthias", "QTH", "Ravenna", "WX", "30 degC", "RIG", "Yaesu FT-891", "PWR", "100", "ANT", "EFHW 13m Long"};

    private final static String TEST9_INPUT = "COORD: 54.37037777557802, -2.922647707561104, OP: Matthias,QTH: Ravenna,WX: 30 degC,RIG: Yaesu FT-891,PWR: 100,ANT: EFHW 13m Long";
    private final static String TEST9_EXPECTED[] = {"COORD", "54.37037777557802, -2.922647707561104", "OP", "Matthias", "QTH", "Ravenna", "WX", "30 degC", "RIG", "Yaesu FT-891", "PWR", "100", "ANT", "EFHW 13m Long"};

    @Test
    public final void colonCommaTokenizerTest() {
        CommentTokenizer tokenizer = new ColonCommaTokenizer();
        check(tokenizer, TEST1_INPUT, TEST1_EXPECTED);
        check(tokenizer, TEST2_INPUT, TEST2_EXPECTED);
        check(tokenizer, TEST5_INPUT, TEST5_EXPECTED);
        check(tokenizer, TEST6_INPUT, TEST6_EXPECTED);
    }

    @Test
    public final void colonTokenizerTest() {
        CommentTokenizer tokenizer = new ColonTokenizer();
        check(tokenizer, TEST1_INPUT, TEST1_EXPECTED);
        check(tokenizer, TEST2_INPUT, TEST2_EXPECTED);
        check(tokenizer, TEST3_INPUT, TEST3_EXPECTED);
        check(tokenizer, TEST4_INPUT, TEST4_EXPECTED);
        check(tokenizer, TEST5_INPUT, TEST5_EXPECTED);
        check(tokenizer, TEST6_INPUT, TEST6_EXPECTED);
        check(tokenizer, TEST7_INPUT, TEST7_EXPECTED);
        check(tokenizer, TEST8_INPUT, TEST8_EXPECTED);
        check(tokenizer, TEST9_INPUT, TEST9_EXPECTED);
    }

    private void check(CommentTokenizer tokenizer, String input, String[] expected) {
        Map<String, String> tokens = tokenizer.tokenize(input);
        int i = 0;
        for (String tokenKey : tokens.keySet()) {
            String value = tokens.get(tokenKey);
            assertThat(tokenKey).isEqualTo(expected[i]);
            assertThat(value).isEqualTo(expected[i+1]);
            i+=2;
        }
    }
}
