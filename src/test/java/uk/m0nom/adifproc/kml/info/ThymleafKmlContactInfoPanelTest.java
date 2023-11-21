package uk.m0nom.adifproc.kml.info;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Band;
import org.thymeleaf.TemplateEngine;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.contacts.Station;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.antenna.Antenna;
import uk.m0nom.adifproc.comms.CommsLinkResult;
import uk.m0nom.adifproc.qrz.QrzCallsign;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.*;

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
        Station fromStation = mock(Station.class);
        Station toStation = mock(Station.class);

        Antenna fromAntenna = mock(Antenna.class);
        CommsLinkResult clr = mock(CommsLinkResult.class);
        QrzCallsign fromInfo = mock(QrzCallsign.class);
        QrzCallsign toInfo = mock(QrzCallsign.class);

        ZonedDateTime qsoDate = ZonedDateTime.of(LocalDateTime.of(2021,11,10, 0, 0), ZoneId.of("UTC"));
        LocalTime qsoTime = LocalTime.of(21,24);

        when(fromInfo.getCall()).thenReturn("M0NOM/P");
        when(toInfo.getCall()).thenReturn("G8CPZ");
        when(qso.getFrom()).thenReturn(fromStation);
        when(qso.getTo()).thenReturn(toStation);

        when(fromAntenna.getName()).thenReturn("Vertical");
        when(fromStation.getAntenna()).thenReturn(fromAntenna);
        when(qso.getRecord()).thenReturn(rec);
        when(qso.getFrom()).thenReturn(fromStation);
        when(rec.getCall()).thenReturn("G8CPZ/P");
        when(rec.getStationCallsign()).thenReturn("EA8/M0NOM/P");
        when(rec.getQsoDate()).thenReturn(qsoDate);
        when(rec.getTimeOn()).thenReturn(qsoTime);
        when(rec.getBand()).thenReturn(Band.BAND_2m);
        when(rec.getFreq()).thenReturn(145.450);
        when(rec.getTxPwr()).thenReturn(50.0);
        when(clr.getDistanceInKm()).thenReturn(20.5);
        when(clr.getPropagation()).thenReturn(null);
        when(rec.getFreqRx()).thenReturn(null);

        String html = infoPanel.getPanelContentForCommsLink(qso, clr, TemplateEngineConstructor.create());
        FileUtils.writeStringToFile(new File("target/contact.html"), html, StandardCharsets.UTF_8);
    }
}
