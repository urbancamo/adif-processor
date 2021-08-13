package uk.m0nom.adif3.transform;

import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.qrz.QrzCallsign;

import java.util.zip.Adler32;

/**
 * Populate our output ADIF record wih informatoin obtained from QRZ.COM
 */
public class AdifQrzEnricher {
    public void enrichAdifForMe(Adif3Record rec, QrzCallsign qrzData) {
        if (qrzData == null) {
            return;
        }

        if (rec.getMyCountry() == null) {
            rec.setMyCountry(qrzData.getCountry());
        }
        if (rec.getMyName() == null) {
            rec.setMyName(qrzData.getName());
        }
    }

    public void enrichAdifForThem(Adif3Record rec, QrzCallsign qrzData) {
        if (qrzData == null) {
            return;
        }

        if (rec.getCountry() == null) {
            rec.setCountry(qrzData.getCountry());
        }

        if (rec.getName() == null) {
            String name = "";
            if (StringUtils.isNotEmpty(qrzData.getFname())) {
                name = qrzData.getFname();
            }
            if (StringUtils.isNotEmpty(qrzData.getName())) {
                if (StringUtils.isNotEmpty(name)) {
                    name = name + " ";
                }
                name = name + qrzData.getName();
            }
            rec.setName(name);
        }
        /* deal with location separately.
        if (rec.getGridsquare() == null) {
            rec.setGridsquare(qrzData.getGrid());
        }*/
        if (rec.getQth() == null) {
            StringBuilder addr = new StringBuilder();
            if (qrzData.getAddr1() != null) {
                addr.append(qrzData.getAddr1());
                if (qrzData.getAddr2() != null) {
                    addr.append(", ");
                    addr.append(qrzData.getAddr2());
                }
            }
            rec.setQth(addr.toString());
        }
    }
}
