package uk.m0nom.adifproc.adif3.transform;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.adifproc.satellite.SatelliteActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class TransformResults {
    private String adiFile;
    private String kmlFile;
    private String formattedQsoFile;
    private String qslLabelsFile;
    private String error = "";
    private Collection<String> contactsWithoutLocation;
    private Collection<String> contactsWithDubiousLocation;
    private Collection<String> qslContacts;
    private Set<String> unknownSatellites;
    private Set<String> unknownSatellitePasses;
    private SatelliteActivity satelliteActivity;
    private Collection<String> warnings;

    public TransformResults() {
        contactsWithoutLocation = new ArrayList<>();
        contactsWithDubiousLocation = new ArrayList<>();
        unknownSatellites = new HashSet<>();
        unknownSatellitePasses = new HashSet<>();
        satelliteActivity = new SatelliteActivity();
        warnings = new ArrayList<>();
    }

    public TransformResults(String errorMessage) {
        this.error = errorMessage;
    }

    public boolean hasWarnings() { return !warnings.isEmpty(); }

    public boolean hasErrors() {
        return StringUtils.isNotBlank(error);
    }

    public void addContactWithoutLocation(String callsign) {
        contactsWithoutLocation.add(callsign);
    }

    public void addContactWithDubiousLocation(String callsign) {
        contactsWithDubiousLocation.add(callsign);
    }

    public void addUnknownSatellite(String satellite) {
        unknownSatellites.add(satellite);
    }

    public void addUnknownSatellitePass(String satellitePass) {
        unknownSatellitePasses.add(satellitePass);
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }
}
