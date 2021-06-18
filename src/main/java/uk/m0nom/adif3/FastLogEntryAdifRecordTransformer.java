package uk.m0nom.adif3;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.QslSent;
import org.marsik.ham.adif.enums.QslVia;
import org.marsik.ham.adif.types.Iota;
import org.marsik.ham.adif.types.Sota;

import java.time.LocalDate;
import java.util.*;

public class FastLogEntryAdifRecordTransformer implements Adif3RecordTransformer {

    private YamlMapping fieldMap;

    public FastLogEntryAdifRecordTransformer(YamlMapping config) {
        fieldMap = config.asMapping();
    }

    @Override
    public void transform(Adif3Record rec) {
        Map<String, String> unmapped = new HashMap<>();
        // Duplicate references into the comment
        if (rec.getSotaRef() != null) {
            unmapped.put("SOTA", rec.getSotaRef().getValue());
        }
        if (StringUtils.isNotBlank(rec.getComment())) {
            transformComment(rec, rec.getComment(), unmapped);
        }
        if (!unmapped.isEmpty()) {
            addUnmappedToRecord(rec, unmapped);
        } else {
            // done a good job and slotted all the key/value pairs in the right place
            rec.setComment("");
        }
    }

    /**
     * Parse the Fast Log Entry comment string for pairs of key and values, for example
     * OP: John, QTH: Gatwick, PWR: 100W, ANT: Inv-V, WX: 4 degC, GRID: IO84io
     * In this case OP, QTH and PWR are transferred into their respective ADIF records,
     * and ANT/WX records are appended to the comment
     * @param rec
     * @param comment
     */
    private void transformComment(Adif3Record rec, String comment, Map<String, String> unmapped) {
        // try and split the comment up into comma separated list
        Map<String, String> tokens = tokenize(comment);

        for (String key : tokens.keySet()) {
            String value = tokens.get(key).trim();

            YamlNode keyNode = fieldMap.value(key);
            if (keyNode != null) {
                YamlNode adifFieldYn = keyNode.asScalar();
                String adifField = adifFieldYn.asScalar().value();

                switch (adifField) {
                    case "Name":
                        rec.setName(value);
                        break;
                    case "Qth":
                        rec.setQth(value);
                        break;
                    case "Rig":
                        rec.setRig(value);
                        break;
                    case "Age":
                        rec.setAge(Integer.parseInt(value));
                        break;
                    case "Iota":
                        Iota iota = Iota.findByCode(value);
                        rec.setIota(iota);
                        break;
                    case "GridSquare":
                        rec.setGridsquare(value);
                        break;
                    case "RxPwr":
                        String pwr = value.toLowerCase(Locale.ROOT).trim();
                        if (pwr.endsWith("w")) {
                            pwr = StringUtils.replace(pwr, "w", "");
                        } else if (pwr.endsWith(" w")) {
                            pwr = StringUtils.replace(pwr, " w", "");
                        } else if (pwr.endsWith(" watt")) {
                            pwr = StringUtils.replace(pwr, " watt", "");
                        } else if (pwr.endsWith(" watts")) {
                            pwr = StringUtils.replace(pwr, " watts", "");
                        }
                        if (pwr.endsWith("k")) {
                            pwr = StringUtils.replace(pwr, "k", "000");
                        }
                        try {
                            rec.setRxPwr(Double.parseDouble(pwr));
                        } catch (NumberFormatException nfe) {
                            System.err.println(String.format("Couldn't parse RxPwr field: %s, leaving it unmapped", value));
                            unmapped.put(key, value);
                        }
                        break;
                    case "SotaRef":
                        // Strip off any S2s reference
                        String sotaRef = StringUtils.split(value, ' ')[0];
                        try {
                            Sota sota = Sota.valueOf(sotaRef);
                            rec.setSotaRef(sota);
                        } catch (IllegalArgumentException iae) {
                            // something we can't work out about the reference, so put it in the unmapped list instead
                            System.err.println(String.format("Couldn't identify %s as a SOTA reference in field %s, leaving it unmapped", sotaRef, value));
                            unmapped.put(key, value);
                        }
                        // We also add the Sota reference as-is to the comment field
                        unmapped.put(key, value);
                        break;
                    case "SerialTx":
                        // Determine if this is a serial number of string based contest exchange
                        try {
                            rec.setStx(Integer.parseInt(value));
                        } catch (NumberFormatException nfe) {
                            // Not a simple number, so use the string ADIF field instead
                            rec.setStxString(value);
                        }
                        break;
                    case "SerialRx":
                        // Determine if this is a serial number of string based contest exchange
                        try {
                            rec.setSrx(Integer.parseInt(value));
                        } catch (NumberFormatException nfe) {
                            // Not a simple number, so use the string ADIF field instead
                            rec.setSrxString(value);
                        }
                        break;
                    case "Fists":
                        rec.setFists(value);
                        break;
                    case "Qsl":
                        rec.setQslSDate(LocalDate.now());
                        rec.setQslSent(QslSent.SENT);
                        // This could either be a bureau or direct QSL depending on value
                        switch (value) {
                            case "D":
                                rec.setQslSentVia(QslVia.DIRECT);
                                break;
                            case "B":
                                rec.setQslSentVia(QslVia.BUREAU);
                                break;
                        }
                        break;
                }
            } else {
                unmapped.put(key, value);
            }
        }
    }

    /**
     * Any key/value pairs in the fast log entry comment string that can't be mapped into a specific ADIF field
     * are added to the comment string in the ADIF file
     * @param rec ADIF record
     * @param unmapped unmapped parameters
     */
    private void addUnmappedToRecord(Adif3Record rec, Map<String, String> unmapped) {
        StringBuilder sb = new StringBuilder();
        Set<String> keySet = unmapped.keySet();
        int keySetLen = keySet.size();
        int i = 1;
        for (String key : unmapped.keySet()) {
            sb.append(String.format("%s: %s", key, unmapped.get(key)));
            if (i++ < keySetLen) {
                sb.append(", ");
            }
        }
        rec.setComment(sb.toString());
    }


    private Map<String, String> tokenize(String comment) {
        Map<String, String> tokens = new HashMap<>();
        StringTokenizer tokenizer = new StringTokenizer(comment, ",");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.contains(":")) {
                String pair[] = StringUtils.split(token, ":");
                tokens.put(pair[0].trim(), pair[1].trim());
            }
        }
        return tokens;
    }
}
