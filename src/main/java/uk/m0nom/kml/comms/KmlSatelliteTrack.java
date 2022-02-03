package uk.m0nom.kml.comms;

import de.micromata.opengis.kml.v_2_2_0.*;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.coords.GlobalCoords3D;
import uk.m0nom.kml.KmlLineStyle;
import uk.m0nom.kml.KmlStyling;
import uk.m0nom.kml.KmlUtils;
import uk.m0nom.satellite.ApSatellite;
import uk.m0nom.satellite.SatelliteActivity;
import uk.m0nom.satellite.SatellitePass;
import uk.m0nom.satellite.SatellitePassId;

import java.time.LocalDate;
import java.time.LocalTime;

public class KmlSatelliteTrack {
    private final static String SATELLITE_TRACK_ID = "satellite_track";

    public void addSatelliteTracks(TransformControl control, Document doc, SatelliteActivity activity,
                                   GlobalCoords3D groundStation) {
        String styleUrl = addSatelliteTrackStyle(control, doc);

        Folder folder = doc.createAndAddFolder();
        folder.withName("Satellite Tracks").withOpen(false);

        for (SatellitePass pass : activity.getPasses()) {
            SatellitePassId id = pass.getId();
            LocalDate passDate = id.getDate();
            String satName = id.getSatelliteName();
            ApSatellite satellite = activity.getSatellites().getSatellite(satName);

            // Create KML folder for the pass points
            Folder passFolder = folder.createAndAddFolder().withName(pass.getId().toString()).withOpen(false);

            LocalTime currentTime = pass.getFirstContact().minusMinutes(3);
            GlobalCoords3D lastPosition = null;
            while (currentTime.isBefore(pass.getLastContact().plusMinutes(2))) {
                // Calculate position of satellite at the time
                GlobalCoords3D currentPosition = satellite.getPosition(groundStation, passDate, currentTime);
                if (lastPosition != null) {
                    drawSatelliteTrack(passFolder, currentTime, lastPosition, currentPosition, styleUrl);
                }
                lastPosition = currentPosition;
                currentTime = currentTime.plusMinutes(1);
            }
        }


    }

    private void drawSatelliteTrack(Folder folder, LocalTime currentTime, GlobalCoords3D lastPosition,
                                    GlobalCoords3D currentPosition,
                                    String styleUrl) {
        Placemark placemark = folder.createAndAddPlacemark();
        String time = currentTime.toString();
        // use the style for each line type
        placemark.withName(time)
                .withId(time)
                .withStyleUrl(styleUrl);

        LineString trackLine = placemark.createAndSetLineString().withAltitudeMode(AltitudeMode.ABSOLUTE);
        trackLine.addToCoordinates(lastPosition.getLongitude(), lastPosition.getLatitude(), lastPosition.getAltitude());
        trackLine.addToCoordinates(currentPosition.getLongitude(), currentPosition.getLatitude(), currentPosition.getAltitude());
    }

    private String addSatelliteTrackStyle(TransformControl control,  Document doc) {
        String styleId = KmlUtils.getStyleId(SATELLITE_TRACK_ID);
        KmlLineStyle styling = KmlStyling.getKmlLineStyle(control.getKmlSatelliteTrackLineStyle());
        Style style = doc.createAndAddStyle()
                .withId(styleId);
        assert styling != null;
        style.createAndSetLineStyle().withColor(styling.getStringSpecifier()).withWidth(5);
        return KmlUtils.getStyleUrl(SATELLITE_TRACK_ID);
    }
}
