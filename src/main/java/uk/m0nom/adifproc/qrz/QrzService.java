package uk.m0nom.adifproc.qrz;

import org.springframework.stereotype.Service;

@Service
public interface QrzService {
    void setCredentials(String username, String password);
    QrzCallsign getCallsignData(String callsign);
    boolean refreshSessionKey();
    boolean hasCredentials();
}
