package uk.m0nom.adifproc.adif3.transform.comment;

import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.transform.TransformResults;
import uk.m0nom.adifproc.adif3.transform.tokenizer.ColonTokenizer;
import uk.m0nom.adifproc.adif3.transform.tokenizer.CommentTokenizer;
import uk.m0nom.adifproc.adif3.xsdquery.*;

import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class SchemaBasedCommentTransformer implements CommentTransformer {
    private static final Logger logger = Logger.getLogger(SchemaBasedCommentTransformer.class.getName());

    private final Adif3Schema schema;
    private final CommentTokenizer tokenizer;

    public SchemaBasedCommentTransformer(ColonTokenizer tokenizer) {
        this.tokenizer = tokenizer;
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("adif/adx314.xsd");
        schema = Adif3SchemaLoader.loadAdif3Schema(inputStream);
    }

    @Override
    public void transformComment(Qso qso, String comment, Map<String, String> unmapped, TransformResults results) {
        if (Strings.isNullOrEmpty(comment)) {
            return;
        }

        Adif3Record rec = qso.getRecord();
        // try and split the comment up into comma separated list
        Map<String, String> tokens = tokenizer.tokenize(comment);

        // If this is a 'regular comment' then don't tokenize
        if (tokens.size() == 0) {
            unmapped.put(comment, "");
            return;
        }

        for (String fieldName : tokens.keySet()) {
            String fieldValue = tokens.get(fieldName).trim();

            // Look up key which is the ADIF field name
            Adif3Field field = schema.getField(fieldName);
            if (field != null) {
                // Name matches an ADIF field, check the value is valid
                Adif3FieldValidationResult validationResult = field.isValid(fieldValue);
                if (validationResult.isValid()){
                    Adif3RecordPopulator.addFieldToRecord(rec, field, fieldValue);
                } else {
                    addWarningAboutValidationError(results, fieldName, fieldValue, validationResult);
                    unmapped.put(fieldName, fieldValue);
                }
            } else {
                unmapped.put(fieldName, fieldValue);
            }

            logger.info(String.format("Transforming comment: %s with value: %s", fieldName, fieldValue));
        }
    }

    private void addWarningAboutValidationError(TransformResults results,
                                                String fieldName,
                                                String fieldValue,
                                                Adif3FieldValidationResult validationResult) {
       StringBuilder sb = new StringBuilder(String.format("Validation of comment field '%s:%s' failed because",
               fieldName, fieldValue));
       if (!validationResult.isDefinedWhenRequired()) {
           sb.append(" a value is required, ");
       }
       Adif3TypeValidationResult typeValidationResult = validationResult.getTypeValidationResult();
       if (!typeValidationResult.isWithinMin()) {
           sb.append(" value is too low, ");
       }
       if (!typeValidationResult.isWithinMax()) {
           sb.append(" value is too high, ");
       }
       if (!typeValidationResult.isMatchingPattern()) {
           sb.append(" value is invalid, ");
       }
       String warning = sb.toString();
       if (warning.endsWith(", ")) {
           results.addWarning(StringUtils.left(sb.toString(), warning.length()-2));
       }
       results.addWarning(warning);
    }
}
