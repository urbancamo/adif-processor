package uk.m0nom.adifproc.adif3;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.AdifHeader;
import org.marsik.ham.adif.enums.Mode;
import org.marsik.ham.adif.enums.Submode;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.adif3.contacts.Qsos;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.domain.Log;
import uk.m0nom.adifproc.db.LogRepository;
import uk.m0nom.adifproc.adif3.transform.CommentParsingAdifRecordTransformer;
import uk.m0nom.adifproc.adif3.transform.MyCallsignCheck;
import uk.m0nom.adifproc.adif3.transform.MyCallsignCheckResults;
import uk.m0nom.adifproc.adif3.transform.TransformResults;
import uk.m0nom.adifproc.progress.ProgressFeedbackHandlerCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main entry into the Adif3 Transformer functionality.
 */
@Service
@AllArgsConstructor
public class Adif3Transformer {
    private final CommentParsingAdifRecordTransformer transformer;
    private LogRepository logRepository;

    public Qsos transform(Adif3 log, TransformControl control, TransformResults results, ProgressFeedbackHandlerCallback progressFeedbackHandlerCallback, String sessionId) throws UnsupportedHeaderException {
        Qsos qsos = new Qsos(log);

        int firstError = 0;
        int index = 1;
        String additionalInfo = "";
        int myCallsignIssues = 0;
        int theirCallsignIssues = 0;

        MyCallsignCheckResults callsigns = MyCallsignCheck.checkForSingleMyCallsign(log);
        Log logRecord = new Log(callsigns.getCallsignsForUserLog());
        logRepository.save(logRecord);

        // LOTW export can contain a single APP field that needs stripping as the record isn't valid for processing
        log.setRecords(stripLotwEofRecordIfPresent(log.getRecords()));

        int totalRecords = log.getRecords().size();
        for (Adif3Record rec : log.getRecords()) {
            progressFeedbackHandlerCallback.sendProgressUpdate(sessionId, String.format("%d/%d Processing %s", index, totalRecords, rec.getCall()));
            migrateAdifImportOnlyFields(rec);
            if (StringUtils.isBlank(rec.getOperator()) && callsigns.isOneOperator()) {
                rec.setOperator(callsigns.getSingleOperator());
            }
            if (StringUtils.isBlank(rec.getStationCallsign()) && callsigns.isOneStationCallsign()) {
                rec.setStationCallsign(callsigns.getSingleStationCallsign());
            }
            boolean haveMyCallsign = rec.getStationCallsign() != null || rec.getOperator() != null;
            if (!haveMyCallsign && StringUtils.isNotBlank(control.getCallsign())) {
                rec.setStationCallsign(control.getCallsign());
                haveMyCallsign = true;
            }

            boolean haveTheirCallsign = rec.getCall() != null;
            if (haveMyCallsign && haveTheirCallsign) {
                transformer.transform(control, results, qsos, rec, index);
            } else {
                if (!haveMyCallsign) {
                    myCallsignIssues++;
                    if (firstError == 0) {
                        firstError = index;
                        additionalInfo = String.format("record %d%s", firstError, StringUtils.defaultIfEmpty(String.format(", their call: %s", rec.getCall()), ""));
                    }
                }
                if (!haveTheirCallsign) {
                    theirCallsignIssues++;
                    if (firstError == 0) {
                        firstError = index;
                        additionalInfo = String.format("record %d", firstError);
                    }
                }
            }
            index++;
        }

        if (theirCallsignIssues > 0) {
            results.setError(String.format("CALL not defined for %d record(s), first error on %s", theirCallsignIssues, additionalInfo));
        } else if (myCallsignIssues > 0) {
            results.setError("STATION_CALLSIGN or OPERATOR not defined, enter it on the form above as 'Your Callsign'");
        }

        AdifHeader header = new AdifHeader();
        header.setProgramId("M0NOM ADIF Processor");
        header.setProgramVersion("1.4.22");
        log.setHeader(header);

        return qsos;
    }

    @Data
    @AllArgsConstructor
    private static class ModeSubmode {
        Mode mode;
        Submode submode;
    }

    private final Map<Mode, ModeSubmode> modeImportOnlyMap = new HashMap<>() {
        {
            put(Mode.AMTORFEC, new ModeSubmode(Mode.TOR, Submode.AMTORFEC));
            put(Mode.ASCI, new ModeSubmode(Mode.RTTY, Submode.ASCI));
            put(Mode.CHIP64, new ModeSubmode(Mode.CHIP, Submode.CHIP64));
            put(Mode.CHIP128, new ModeSubmode(Mode.CHIP, Submode.CHIP128));
            put(Mode.C4FM, new ModeSubmode(Mode.DIGITALVOICE, Submode.C4FM));
            put(Mode.DSTAR, new ModeSubmode(Mode.DIGITALVOICE, Submode.DSTAR));
            put(Mode.DOMINOF, new ModeSubmode(Mode.DOMINO, Submode.DOMINOF));
            put(Mode.FMHELL, new ModeSubmode(Mode.HELL, Submode.FMHELL));
            put(Mode.FSK31, new ModeSubmode(Mode.PSK, Submode.FSK31));
            put(Mode.FT4, new ModeSubmode(Mode.MFSK, Submode.FT4));
            put(Mode.GTOR, new ModeSubmode(Mode.TOR, Submode.GTOR));
            put(Mode.HELL80, new ModeSubmode(Mode.HELL, Submode.HELL80));
            put(Mode.HFSK, new ModeSubmode(Mode.HELL, Submode.HFSK));
            put(Mode.JT4A, new ModeSubmode(Mode.JT4, Submode.JT4A));
            put(Mode.JT4B, new ModeSubmode(Mode.JT4, Submode.JT4B));
            put(Mode.JT4C, new ModeSubmode(Mode.JT4, Submode.JT4B));
            put(Mode.JT4D, new ModeSubmode(Mode.JT4, Submode.JT4D));
            put(Mode.JT4E, new ModeSubmode(Mode.JT4, Submode.JT4E));
            put(Mode.JT4F, new ModeSubmode(Mode.JT4, Submode.JT4F));
            put(Mode.JT4G, new ModeSubmode(Mode.JT4, Submode.JT4G));
            put(Mode.JT65A, new ModeSubmode(Mode.JT65, Submode.JT65A));
            put(Mode.JT65B, new ModeSubmode(Mode.JT65, Submode.JT65B));
            put(Mode.JT65C, new ModeSubmode(Mode.JT65, Submode.JT65C));
            put(Mode.MFSK8, new ModeSubmode(Mode.MFSK, Submode.MFSK8));
            put(Mode.MFSK16, new ModeSubmode(Mode.MFSK, Submode.MFSK16));
            put(Mode.PAC2, new ModeSubmode(Mode.PAC, Submode.PAC2));
            put(Mode.PAC3, new ModeSubmode(Mode.PAC, Submode.PAC3));
            put(Mode.PAX2, new ModeSubmode(Mode.PAX, Submode.PAX2));
            put(Mode.PCW, new ModeSubmode(Mode.CW, Submode.PCW));
            put(Mode.PSK10, new ModeSubmode(Mode.PSK, Submode.PSK10));
            put(Mode.PSK31, new ModeSubmode(Mode.PSK, Submode.PSK31));
            put(Mode.PSK63, new ModeSubmode(Mode.PSK, Submode.PSK63));
            put(Mode.PSK63F, new ModeSubmode(Mode.PSK, Submode.PSK63F));
            put(Mode.PSK125, new ModeSubmode(Mode.PSK, Submode.PSK125));
            put(Mode.PSKAM10, new ModeSubmode(Mode.PSK, Submode.PSKAM10));
            put(Mode.PSKAM31, new ModeSubmode(Mode.PSK, Submode.PSKAM31));
            put(Mode.PSKAM50, new ModeSubmode(Mode.PSK, Submode.PSKAM50));
            put(Mode.PSKFEC31, new ModeSubmode(Mode.PSK, Submode.PSKFEC31));
            put(Mode.PSKHELL, new ModeSubmode(Mode.HELL, Submode.PSKHELL));
            put(Mode.QPSK31, new ModeSubmode(Mode.PSK, Submode.QPSK31));
            put(Mode.QPSK63, new ModeSubmode(Mode.PSK, Submode.QPSK63));
            put(Mode.QPSK125, new ModeSubmode(Mode.PSK, Submode.QPSK125));
            put(Mode.THRBX, new ModeSubmode(Mode.THRB, Submode.THRBX));
            put(Mode.LSB, new ModeSubmode(Mode.SSB, Submode.LSB));
            put(Mode.USB, new ModeSubmode(Mode.SSB, Submode.USB));
            put(Mode.VARA_HF, new ModeSubmode(Mode.DYNAMIC, Submode.VARA_HF));
            put(Mode.JS8, new ModeSubmode(Mode.MFSK, Submode.JS8));
        }
    };

    /**
     * Some ADIF Fields are import only, so deal with them here by migrating to the correct
     * values
     *
     * @param rec check if mode exists in the import only map - if it does migrate to the correct
     *            mode/sub-mode combination
     */
    private void migrateAdifImportOnlyFields(Adif3Record rec) {
        if (modeImportOnlyMap.containsKey(rec.getMode())) {
            ModeSubmode migrationPath = modeImportOnlyMap.get(rec.getMode());
            rec.setMode(migrationPath.getMode());
            rec.setSubmode(migrationPath.getSubmode().adifCode());
        }
    }

    private List<Adif3Record> stripLotwEofRecordIfPresent(List<Adif3Record> records) {
        List<Adif3Record> processedRecords = new ArrayList<>(records.size());
        for (Adif3Record rec : records) {
            if (rec.getApplicationDefinedField("APP_LOTW_EOF") == null) {
                processedRecords.add(rec);
            }
        }
        return processedRecords;
    }
}
