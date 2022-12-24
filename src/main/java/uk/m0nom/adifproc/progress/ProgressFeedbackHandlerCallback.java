package uk.m0nom.adifproc.progress;

public interface ProgressFeedbackHandlerCallback {
    void sendProgressUpdate(String sessionId, String message);
}
