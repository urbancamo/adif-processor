package uk.m0nom.adifproc.label;

import org.junit.jupiter.api.Test;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Band;
import org.marsik.ham.adif.enums.Mode;
import org.marsik.ham.adif.types.Sota;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.label.Adif3LabelFormatter;
import uk.m0nom.adifproc.adif3.label.Page;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Adif3LabelFormatterTest {

    @Test
    public void testSingleFormat() {
        Adif3LabelFormatter f = new Adif3LabelFormatter();
        List<Qso> qsoList = new ArrayList<>(1);
        for (int i=0; i < 48; i++) {
            Qso qso = new Qso();
            Adif3Record rec = new Adif3Record();
            rec.setCall("EA2IF");
            rec.setQslVia("EA2 Bureau");
            rec.setQsoDate(LocalDate.of(2021, 9, 11));
            rec.setTimeOn(LocalTime.of(11, 12));
            rec.setBand(Band.BAND_20m);
            rec.setMySotaRef(Sota.valueOf("G/LD-050"));
            rec.setRstSent("59");
            rec.setQslMsg("Thx for QSO!");
            rec.setMode(Mode.SSB);
            qso.setRecord(rec);
            qsoList.add(qso);
        }

        List<Page> pages = f.formatQsos(qsoList, 1);
        assertThat(pages.size()).isEqualTo(2);
        System.out.println("%<--------%<--------%<--------%<--------%<--------%<--------%<--------%<--------%<--------");
        Page.dump(pages);
    }
}
