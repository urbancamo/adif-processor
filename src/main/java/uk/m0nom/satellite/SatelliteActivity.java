package uk.m0nom.satellite;

import lombok.Getter;
import lombok.Setter;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.contacts.Qso;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SatelliteActivity {
    private ApSatellites satellites;
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
        SatellitePass pass = satellitePasses.get(id);
        if (pass == null) {
            pass = new SatellitePass(id);
            satellitePasses.put(id, pass);
        }
        pass.addContact(qso.getRecord().getTimeOn());
    }

    public Collection<SatellitePass> getPasses() {
        return satellitePasses.values();
    }

    public boolean hasActivity() {
        return !satellitePasses.isEmpty();
    }
}
