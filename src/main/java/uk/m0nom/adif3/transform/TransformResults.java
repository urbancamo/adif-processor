package uk.m0nom.adif3.transform;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.satellite.SatelliteActivity;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class TransformResults {
    private String adiFile;
    private String kmlFile;
    private String formattedQsoFile;
    private String error = "";
    private Collection<String> contactsWithoutLocation;
    private Collection<String> contactsWithDubiousLocation;
    private SatelliteActivity satelliteActivity;

    public TransformResults() {
        contactsWithoutLocation = new ArrayList<>();
        contactsWithDubiousLocation = new ArrayList<>();
        satelliteActivity = new SatelliteActivity();
    }

    public TransformResults(String errorMessage) {
        this.error = errorMessage;
    }

    public boolean hasErrors() {
        return StringUtils.isNotBlank(error);
    }

    public void addContactWithoutLocation(String callsign) {
        contactsWithoutLocation.add(callsign);
    }

    public void addContactWithDubiousLocation(String callsign) {
        contactsWithDubiousLocation.add(callsign);
    }
}
