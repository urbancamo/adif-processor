package uk.m0nom.adifproc.kml.info;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.activity.cota.CotaInfo;
import uk.m0nom.adifproc.adif3.contacts.Station;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.coords.LocationAccuracy;
import uk.m0nom.adifproc.coords.LocationSource;
import uk.m0nom.adifproc.qrz.QrzCallsign;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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

        GlobalCoords3D coords =
                new GlobalCoords3D(54.344710, -2.663091, LocationSource.QRZ, LocationAccuracy.LAT_LONG);

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
        ArrayList<Activity> activities = new ArrayList<>();
        activities.add(castle);
        when(station.getActivity(ActivityType.COTA)).thenReturn(activities);
        when(station.getActivities()).thenReturn(activities);
        String html = infoPanel.getPanelContentForStation(control, station);
        FileUtils.writeStringToFile(new File("target/station.html"), html, StandardCharsets.UTF_8);
    }
}
