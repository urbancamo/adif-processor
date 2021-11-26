package uk.m0nom.kml.info;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Band;
import org.thymeleaf.TemplateEngine;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.CommsLinkResult;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThymleafKmlContactInfoPanelTest {
    @Test
    public void testThymeleafContactPanel() throws IOException {
        KmlContactInfoPanel infoPanel = new KmlContactInfoPanel();

        TemplateEngine templateEngine = TemplateEngineConstructor.create();
        TransformControl control = mock(TransformControl.class);
        when(control.getTemplateEngine()).thenReturn(templateEngine);
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
        when(clr.getPropagation()).thenReturn(null);
        when(rec.getFreqRx()).thenReturn(null);

        String html = infoPanel.getPanelContentForCommsLink(control, qso, clr, TemplateEngineConstructor.create());
        FileUtils.writeStringToFile(new File("target/contact.html"), html, StandardCharsets.UTF_8);
        //System.out.println(html);
    }
}
