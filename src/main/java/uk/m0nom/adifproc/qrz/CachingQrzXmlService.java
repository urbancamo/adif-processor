package uk.m0nom.adifproc.qrz;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("cachingQrzXmlService")
public class CachingQrzXmlService implements QrzService {
    private final Map<String, QrzCallsign> cache;
    private final QrzXmlService service;

    //private static final Logger logger = Logger.getLogger(CachingQrzXmlService.class.getName());

    public CachingQrzXmlService(QrzXmlService service) {
        cache = new HashMap<>();
        this.service = service;
    }

    @Override
    public void setCredentials(String username, String password) {
        service.setCredentials(username, password);
    }

    @Override
    public QrzCallsign getCallsignData(String callsign) {
        QrzCallsign callsignData = cache.get(callsign);
        if (callsignData == null) {
            callsignData = service.getCallsignData(callsign);
            if (callsign != null) {
                //logger.info(String.format("Caching qrz info for %s", callsign));
                cache.put(callsign, callsignData);
            }
        }  //logger.info(String.format("Cache hit on qrz info for %s", callsign));

        return callsignData;
    }

    @Override
    public boolean refreshSessionKey() {
        return service.refreshSessionKey();
    }

    @Override
    public boolean hasCredentials() {
        return service.hasCredentials();
    }
}
