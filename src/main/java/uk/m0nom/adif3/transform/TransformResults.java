package uk.m0nom.adif3.transform;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class TransformResults {
    private boolean errors = false;
    private String adiFile;
    private String kmlFile;
    private String markdownFile;
    private String error = "";
    private Collection<String> contactsWithoutLocation;
    private Collection<String> contactsWithDubiousLocation;

    public TransformResults() {
        contactsWithoutLocation = new ArrayList<>();
        contactsWithDubiousLocation = new ArrayList<>();
    }

    public TransformResults(String errorMessage) {
        this.error = errorMessage;
        this.errors = true;
    }

    public void addContactWithoutLocation(String callsign) {
        contactsWithoutLocation.add(callsign);
    }

    public void addContactWithDubiousLocation(String callsign) {
        contactsWithDubiousLocation.add(callsign);
    }
}
