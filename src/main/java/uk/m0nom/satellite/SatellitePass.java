package uk.m0nom.satellite;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

/**
 * This class records a single satellite pass being worked with the first and last time of being worked to
 * allow a pass to be visualized.
 */
@Getter
@Setter
public class SatellitePass {
    private SatellitePassId id;
    private LocalTime firstContact;
    private LocalTime lastContact;

    public SatellitePass(SatellitePassId id) {
        this.id = id;
    }

    public void addContact(LocalTime time) {
        if (firstContact == null) {
            firstContact = time;
            lastContact = time;
        } else if (firstContact.isAfter(time)) {
            firstContact = time;
        } else if (lastContact.isBefore(time)) {
            lastContact = time;
        }
    }
}
