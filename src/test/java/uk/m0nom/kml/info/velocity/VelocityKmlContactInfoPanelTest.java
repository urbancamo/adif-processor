package uk.m0nom.kml.info.velocity;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Band;
import uk.m0nom.adif3.FileTransformerApp;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.CommsLinkResult;
import uk.m0nom.kml.info.IKmlContactInfoPanel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Properties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VelocityKmlContactInfoPanelTest {

    private final Properties velocityProperties = new Properties();

    @Before
    public void setup() {
        InputStream stream = VelocityKmlContactInfoPanelTest.class.getClassLoader().
                getResourceAsStream("./velocity.properties");
        try {
            velocityProperties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(stream).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testVmContactPanel() throws IOException {
        IKmlContactInfoPanel infoPanel = new VelocityKmlContactInfoPanel();

        TransformControl control = mock(TransformControl.class);
        when(control.getVelocityProperties()).thenReturn(velocityProperties);

        Adif3Record rec = mock(Adif3Record.class);
        Qso qso = mock(Qso.class);
        CommsLinkResult clr = mock(CommsLinkResult.class);

        LocalDate qsoDate = LocalDate.of(2021,11,10);
        LocalTime qsoTime = LocalTime.of(21,24);

        when(qso.getRecord()).thenReturn(rec);
        when(rec.getCall()).thenReturn("G8CPZ");
        when(rec.getStationCallsign()).thenReturn("M0NOM");
        when(rec.getQsoDate()).thenReturn(qsoDate);
        when(rec.getTimeOn()).thenReturn(qsoTime);
        when(rec.getBand()).thenReturn(Band.BAND_2m);
        when(rec.getFreq()).thenReturn(145.450);
        when(rec.getTxPwr()).thenReturn(50.0);
        when(clr.getDistance()).thenReturn(20.5);
        when(clr.getMode()).thenReturn(null);
        when(rec.getFreqRx()).thenReturn(null);

        String content = infoPanel.getPanelContentForCommsLink(control, qso, clr);
        String html = wrapContent("Contact Info", content);
        FileUtils.writeStringToFile(new File("../contact.html"), html, StandardCharsets.UTF_8);
        //System.out.println(html);
    }

    /**
     * This method wraps the panel content in an html context so when written to a file it can be viewed as html
     * @param title title of the panel
     * @param content html content of the panel
     * @return wrapped content suitable for viewing in a browser
     */
    private String wrapContent(String title, String content) {
        VelocityEngine ve = new VelocityEngine("target/classes/velocity.properties");
        ve.init();
        Template t = ve.getTemplate("velocity/panelWrapper.vm", StandardCharsets.UTF_8.name());
        VelocityContext context = new VelocityContext();
        context.put("content", content);
        context.put("title", title);
        StringWriter sw = new StringWriter();
        t.merge( context, sw );
        return sw.toString();
    }
}
