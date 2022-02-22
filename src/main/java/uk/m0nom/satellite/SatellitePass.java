package uk.m0nom.satellite;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.adif3.contacts.Qso;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * This class records a single satellite pass being worked with the first and last contact date/time to
 * allow a pass to be visualized, as well as the list of QSOs that went through that satellite during the pass.
 */
@Getter
@Setter
public class SatellitePass {
    private SatellitePassId id;
    private LocalDateTime firstContact;
    private LocalDateTime lastContact;
    private Collection<Qso> contacts;

    public SatellitePass(SatellitePassId id) {
        this.id = id;
        contacts = new LinkedList<>();
    }

    public void addContact(Qso qso) {
        LocalDate date = qso.getRecord().getQsoDate();
        LocalTime time = qso.getRecord().getTimeOn();

        LocalDateTime contact = LocalDateTime.of(date, time);

        contacts.add(qso);

        if (firstContact == null) {
            firstContact = contact;
            lastContact = contact;
        } else if (firstContact.isAfter(contact)) {
            firstContact = contact;
        } else if (lastContact.isBefore(contact)) {
            lastContact = contact;
        }
    }

    /**
     * For satellite QSOs often the recorded time is the same for multiple contacts.
     * This isn't realistic and doesn't look good on the satellite path, so we artificially space out contacts
     * (in both time and therefore along the satellite path) based on the order in which they are added to the pass,
     * which will generally be the correct chronological order.
     */
    public void spaceOutContacts() {
        // Determine the number of contacts per time on the pass
        Map<LocalTime, Integer> qsoCountPerTime = createQsoCountPerTime();

        Iterator<Qso> qsoIterator = getContacts().iterator();
        while (qsoIterator.hasNext()) {
            Qso qso = qsoIterator.next();
            LocalTime qsoTime = qso.getRecord().getTimeOn();
            Integer numberOfQsosSharingTime = qsoCountPerTime.get(qsoTime);

            // Only need to adjust times if there are more than one sharing a time
            if (numberOfQsosSharingTime > 1) {
                for (int i = 1; i < numberOfQsosSharingTime; i++) {
                    // No need to adjust the first one which will be on 0 seconds
                    qso = qsoIterator.next();
                    LocalTime adjustedTime = qsoTime.plusSeconds((long) 60 / numberOfQsosSharingTime * i);
                    qso.getRecord().setTimeOn(adjustedTime);
                }
            }
        }
    }

    /**
     * For each time recorded for a contact determine the number of contacts that share that time
     * @return map of contact times with number of QSOs
     */
    private Map<LocalTime, Integer> createQsoCountPerTime() {

        Map<LocalTime, Integer> qsoCountPerTime = new HashMap<>();
        for (Qso qso : contacts) {
            LocalTime time = qso.getRecord().getTimeOn();
            Integer count = qsoCountPerTime.get(time);
            if (count != null) {
                count = count + 1;
            } else {
                count = 1;
            }
            qsoCountPerTime.put(time, count);
        }
        return qsoCountPerTime;
    }
}
