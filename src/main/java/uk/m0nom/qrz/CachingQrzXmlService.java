package uk.m0nom.qrz;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CachingQrzXmlService implements QrzService {
    private final Map<String, QrzCallsign> cache;
    private final QrzXmlService service;

    private static final Logger logger = Logger.getLogger(CachingQrzXmlService.class.getName());

    public CachingQrzXmlService(String username, String password) {
        cache = new HashMap<>();
        service = new QrzXmlService(username, password);
    }

    @Override
    public QrzCallsign getCallsignData(String callsign) {
        QrzCallsign callsignData = cache.get(callsign);
        if (callsignData == null) {
            callsignData = service.getCallsignData(callsign);
            if (callsign != null) {
                logger.info(String.format("Caching qrz info for %s", callsign));
                cache.put(callsign, callsignData);
            }
        } else {
            //logger.info(String.format("Cache hit on qrz info for %s", callsign));
        }
        return callsignData;
    }

    @Override
    public boolean getSessionKey() {
        return service.getSessionKey();
    }

    @Override
    public void enable() {
        service.enable();
    }

    @Override
    public void disable() {
        service.disable();
    }
}
