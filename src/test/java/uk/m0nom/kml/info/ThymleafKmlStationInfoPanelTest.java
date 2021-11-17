package uk.m0nom.kml.info;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.cota.CotaInfo;
import uk.m0nom.adif3.contacts.Station;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.coords.GlobalCoordinatesWithSourceAccuracy;
import uk.m0nom.coords.LocationAccuracy;
import uk.m0nom.coords.LocationSource;
import uk.m0nom.qrz.QrzCallsign;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ThymleafKmlStationInfoPanelTest {
    @Test
    public void testThymeleafStationPanel() throws IOException {
        KmlStationInfoPanel infoPanel = new KmlStationInfoPanel();

        TransformControl control = mock(TransformControl.class);
        TemplateEngine templateEngine = TemplateEngineConstructor.create();
        when(control.getTemplateEngine()).thenReturn(templateEngine);
        Station station = mock(Station.class);
        QrzCallsign qrzInfo = mock(QrzCallsign.class);

        when(station.getCallsign()).thenReturn("M5TUE");
        when(station.getQrzInfo()).thenReturn(qrzInfo);
        when(qrzInfo.getName()).thenReturn("Wadsworth");
        when(qrzInfo.getFname()).thenReturn("Nigel");

        when(station.getGrid()).thenReturn("IO84qi");

        GlobalCoordinatesWithSourceAccuracy coords =
                new GlobalCoordinatesWithSourceAccuracy(54.344710, -2.663091, LocationSource.QRZ, LocationAccuracy.LAT_LONG);

        when(station.getCoordinates()).thenReturn(coords);
        when(qrzInfo.getCall()).thenReturn("M5TUE");
        when(qrzInfo.getImage()).thenReturn("https://cdn-bio.qrz.com/e/m5tue/photo0014.jpg");

        CotaInfo castle = new CotaInfo();
        castle.setPrefix("C");
        castle.setActive(true);
        castle.setLocation("Docker");
        castle.setName("Castle Wadsworth");
        castle.setRef("C-99999");
        castle.setGrid("IO84qi");
        when(station.isDoing(ActivityType.COTA)).thenReturn(true);
        when(station.getActivity(ActivityType.COTA)).thenReturn(castle);

        Map<ActivityType, Activity> activities = new HashMap<>();
        activities.put(ActivityType.COTA, new CotaInfo());

        when(station.getActivities()).thenReturn(activities);
        String html = infoPanel.getPanelContentForStation(control, station);
        FileUtils.writeStringToFile(new File("target/station.html"), html, StandardCharsets.UTF_8);
    }
}
