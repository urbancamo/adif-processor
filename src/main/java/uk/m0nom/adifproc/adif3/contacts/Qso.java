package uk.m0nom.adifproc.adif3.contacts;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adifproc.qrz.QrzCallsign;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures the information relating to a single contact. Each station in the QSO is recorded as a single
 * instance of Station which allows some more OO-centric processing compared to the raw data in the Adif3Record
 */
@Data
@NoArgsConstructor
public class Qso {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm");

    private int index;
    private Station from;
    private Station to;
    private Adif3Record record;

    public Qso(Adif3Record rec, int index) {
        setIndex(index);
        setRecord(rec);
    }

    public boolean doingSameActivity() {
       return from.doingSameActivityAs(to);
    }

    public boolean isSatelliteContact() {
        return (record != null) && (record.getSatName() != null);
    }

    public boolean isQslViaBureau() {
        boolean viaBureau = false;
        if (to.getQrzInfo() != null) {
            QrzCallsign qrzInfo = to.getQrzInfo();
            if (qrzInfo.getQslmgr() != null) {
                String qslMgr = qrzInfo.getQslmgr().toUpperCase();
                viaBureau |= qslMgr.contains("BUREAU") || qslMgr.contains("BURO");
            }
        }
        if (record != null) {
            if (record.getQslVia() != null) {
                viaBureau |= record.getQslVia().toUpperCase().contains("BUREAU");
            }
        }
        return viaBureau;
    }

    @Override
    public String toString() {
        ZonedDateTime contactDateTime = record.getQsoDate().with(record.getTimeOn());
        return String.format("%s %s %s", dateTimeFormatter.format(contactDateTime), record.getStationCallsign(), record.getCall());
    }
}
