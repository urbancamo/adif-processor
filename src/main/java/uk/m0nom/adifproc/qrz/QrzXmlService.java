package uk.m0nom.adifproc.qrz;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.callsign.Callsign;
import uk.m0nom.adifproc.callsign.CallsignUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * <a href="https://www.qrz.com/XML/current_spec.html">QRZ XML Spec</a>
 */
@Service("qrzXmlService")
public class
QrzXmlService implements QrzService {
    private static final Logger logger = Logger.getLogger(QrzXmlService.class.getName());

    private final static String QRZ_XML_SERVICE_BASE_URL = " https://xmldata.qrz.com/xml";
    private final static String QRZ_XML_SERVICE_VERSION = "current";
    private final static long DELAY = 100L;

    private final OkHttpClient client;
    private String sessionKey;
    private String username;
    private String password;
    private long lastTimestamp;

    public QrzXmlService() {
        client = new OkHttpClient();
        sessionKey = null;
    }

    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean hasCredentials() {
        return !Strings.isEmpty(username) && !Strings.isEmpty(password);
    }

    public boolean refreshSessionKey()  {
        if (hasCredentials()) {
            String url = String.format("%s/%s/?username=%s&password=%s&agent=adifproc1.1", QRZ_XML_SERVICE_BASE_URL, QRZ_XML_SERVICE_VERSION, username, password);
            logger.info("Obtaining QRZ.COM session key");
            QrzDatabase database = runQuery(url);
            if (database != null) {
                sessionKey = database.getSession().getKey();
                return sessionKey != null;
            }
        }
        return false;
    }

    public QrzCallsign getCallsignData(String callsign) {
        List<Callsign> alternatives = CallsignUtils.getCallsignVariants(callsign);
        for (Callsign alternative : alternatives) {
            long timeDiff = new Date().getTime() - lastTimestamp;
            if (timeDiff < DELAY) {
                long pause = DELAY - timeDiff;
                // Ensure at least a second between calls to comply with fair usage policy
                //logger.info(String.format("Pausing for %d ms to throttle qrz.com usage", pause));
                try {
                    Thread.sleep(pause);
                } catch (Exception e) {
                    // discard
                }
            }
            QrzCallsign data = getCallsignDataInternal(alternative.getCallsign());
            lastTimestamp = java.time.Instant.now().toEpochMilli();
            if (data != null) {
                return data;
            }
        }
        return null;
    }

    public QrzCallsign getCallsignDataInternal(String callsign) {
        if (sessionKey != null) {
            String url = String.format("%s/%s/?s=%s;callsign=%s", QRZ_XML_SERVICE_BASE_URL, QRZ_XML_SERVICE_VERSION, sessionKey, callsign);
            //logger.info(String.format("Querying QRZ.COM for info on: %s", callsign));
            QrzDatabase database = runQuery(url);
            if (database != null) {
                return database.getCallsign();
            } else {
                logger.warning(String.format("Nothing found on QRZ.COM for: %s", callsign));
            }
        }
        return null;
    }

    private QrzDatabase runQuery(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String xmlBody = Objects.requireNonNull(response.body()).string();
            xmlBody = StringUtils.remove(xmlBody, '\n');
            InputStream stream = new ByteArrayInputStream(xmlBody.getBytes(StandardCharsets.UTF_8));

            JAXBContext context = JAXBContext.newInstance(QrzDatabase.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (QrzDatabase) unmarshaller.unmarshal(stream);
        }    catch (Exception e) {
            logger.warning(String.format("QRZ.COM XML query failed: %s, error is: %s", url, e.getMessage()));
        }
        return null;
    }
}
