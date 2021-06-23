package uk.m0nom.qrz;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.adif3.FileTransformerApp;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * https://www.qrz.com/XML/current_spec.html
 */
public class QrzXmlService {
    private static final Logger logger = Logger.getLogger(FileTransformerApp.class.getName());

    private final static String QRZ_XML_SERVICE_BASE_URL = " https://xmldata.qrz.com/xml";
    private final static String QRZ_XML_SERVICE_VERSION = "1.34";
    private final static String USERNAME = "M0NOM";
    private final static String PASSWORD = "WindermereIsMyQTH";

    private OkHttpClient client;
    private String sessionKey;
    private boolean enabled;

    public QrzXmlService() {
        client = new OkHttpClient();
        sessionKey = null;
        enabled = true;
    }

    public void enable() {
        enabled = true;
        logger.info("QRZ.COM lookup has been enabled");
    }
    public void disable() {
        enabled = false;
        logger.info("QRZ.COM lookup has been disabled");
    }

    public boolean isDisabled() {
        return !enabled;
    }
    public boolean isEnabled() {
        return enabled;
    }

    public boolean getSessionKey()  {
        if (sessionKey == null && enabled) {
            String url = String.format("%s/%s/?username=%s&password=%s", QRZ_XML_SERVICE_BASE_URL, QRZ_XML_SERVICE_VERSION, USERNAME, PASSWORD);
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
        if (sessionKey != null) {
            String url = String.format("%s/%s/?s=%s;callsign=%s", QRZ_XML_SERVICE_BASE_URL, QRZ_XML_SERVICE_VERSION, sessionKey, callsign);
            logger.info(String.format("Querying QRZ.COM for info on: %s", callsign));
            QrzDatabase database = runQuery(url);
            if (database != null) {
                return database.getCallsign();
            }
        }
        return null;
    }

    private QrzDatabase runQuery(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String xmlBody = response.body().string();
            xmlBody = StringUtils.remove(xmlBody, '\n');
            InputStream stream = new ByteArrayInputStream(xmlBody.getBytes(StandardCharsets.UTF_8));

            JAXBContext context = JAXBContext.newInstance(QrzDatabase.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            QrzDatabase database = (QrzDatabase) unmarshaller.unmarshal(stream);
            return database;
        }    catch (Exception e) {
            logger.warning(String.format("QRZ.COM XML query failed: %s, error is: %s", url, e.getMessage()));
        }
        return null;
    }
}
