package uk.m0nom.adifproc.adif3;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.contacts.Station;
import uk.m0nom.adifproc.adif3.io.Adif3FileReader;
import uk.m0nom.adifproc.dxcc.DxccEntities;
import uk.m0nom.adifproc.dxcc.DxccJsonReader;
import uk.m0nom.adifproc.dxcc.JsonDxccEntities;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import java.text.ParseException;

@SpringBootTest
@ActiveProfiles("test")
public class PreferAdifDxccEntityOverCalculatedDxccEntityFromCallsignTest {

    @Autowired
    private Adif3FileReader reader;

    @Test
    public void testCountryOverDxcc() {
        try {
            TransformControl transformControl = new TransformControl();
            JsonDxccEntities jsonDxccEntities = new DxccJsonReader().read();
            DxccEntities dxccEntities = new DxccEntities();
            try {
                dxccEntities.setup(jsonDxccEntities);
            } catch (ParseException p) {
                Assertions.fail(p);
            }

            transformControl.setDxccEntities(dxccEntities);
            Adif3 input = reader.read("./target/test-classes/adif/LU7HF.adi", "windows-1251", false);
            assert input != null;
            assert input.getRecords().size() == 1;
            Adif3Record record = input.getRecords().getFirst();
            assert record.getCall().equals("LU7HF");
            assert record.getCountry().equals("Argentina");
            assert record.getDxcc().equals(100);

            Qso qso = new Qso(record, 0);
            Station from = new Station(record.getStationCallsign(), qso);
            Station to = new Station(record.getStationCallsign(), qso);
            to.setDxccEntity(dxccEntities.getDxccEntity(100));

            qso.setTo(to);
            qso.setFrom(from);

            dxccEntities.setToDxccEntity(qso, transformControl);
            assert(qso.getTo().getDxccEntity().getEntityCode() == 100);

        } catch (Exception e) {
            Assertions.fail("testApp() threw exception while setting up the test. ", e);
        }
    }
}
