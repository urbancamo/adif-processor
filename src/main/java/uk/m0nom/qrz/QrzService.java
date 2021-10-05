package uk.m0nom.qrz;

public interface QrzService {
    QrzCallsign getCallsignData(String callsign);
    boolean getSessionKey();
    void enable();
    void disable();
}
