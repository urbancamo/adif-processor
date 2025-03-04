package uk.m0nom.adifproc.satellite;

import lombok.Getter;
import lombok.Setter;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adifproc.adif3.contacts.Qso;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Container for all the satellite activity (QSOs) that are being processed in this run
 */
@Getter
@Setter
public class SatelliteActivity {
    private ApSatelliteService satellites;
    private Map<SatellitePassId, SatellitePass> satellitePasses;

    public SatelliteActivity() {
        satellitePasses = new HashMap<>();
    }

    public void recordSatelliteActivity(Qso qso) {
       addOrUpdateSatellitePass(qso);
    }

    private void addOrUpdateSatellitePass(Qso qso) {
        Adif3Record rec = qso.getRecord();
        SatellitePassId id = new SatellitePassId(rec.getSatName(), rec.getQsoDate());
        SatellitePass pass = satellitePasses.computeIfAbsent(id, SatellitePass::new);
        pass.addContact(qso);
    }

    public Collection<SatellitePass> getPasses() {
        return satellitePasses.values();
    }

    public void spaceOutContactsInPasses() {
        for (SatellitePass pass: getPasses()) {
            pass.spaceOutContacts();
        }
    }

    public boolean hasActivity() {
        return !satellitePasses.isEmpty();
    }
}
