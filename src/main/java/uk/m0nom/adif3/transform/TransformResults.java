package uk.m0nom.adif3.transform;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
public class TransformResults {
    private String adiFile;
    private String kmlFile;
    private String markdownFile;
    private String error = "";
    private Collection<String> contactsWithoutLocation;

    public TransformResults() {
        contactsWithoutLocation = new ArrayList<>();
    }

    public void addContactWithoutLocation(String callsign) {
        contactsWithoutLocation.add(callsign);
    }
}
