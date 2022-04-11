package uk.m0nom.adifproc.qrz;

public interface QrzService {
    void setCredentials(String username, String password);
    QrzCallsign getCallsignData(String callsign);
    boolean refreshSessionKey();
    boolean hasCredentials();
}
