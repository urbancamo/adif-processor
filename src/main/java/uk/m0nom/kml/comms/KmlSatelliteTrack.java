package uk.m0nom.kml.comms;

import de.micromata.opengis.kml.v_2_2_0.*;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.coords.GlobalCoords3D;
import uk.m0nom.icons.IconResource;
import uk.m0nom.kml.KmlLineStyle;
import uk.m0nom.kml.KmlStyling;
import uk.m0nom.kml.KmlUtils;
import uk.m0nom.satellite.ApSatellite;
import uk.m0nom.satellite.SatelliteActivity;
import uk.m0nom.satellite.SatellitePass;
import uk.m0nom.satellite.SatellitePassId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static uk.m0nom.kml.KmlUtils.getStyleId;
import static uk.m0nom.kml.KmlUtils.getStyleUrl;
import static uk.m0nom.kml.station.KmlStationUtils.DEFAULT_RANGE_METRES;

public class KmlSatelliteTrack {
    private final static String SATELLITE_TRACK_ID = "satellite_track";
    private final static String SATELLITE_TRACK_LINE_ID = "satellite_track_line";
    private final static int TRACK_LEAD_LAG_TIME_MINS = 5;

    public void addSatelliteTracks(TransformControl control, Document doc, SatelliteActivity activity,
                                   GlobalCoords3D groundStation) {
        String styleUrl = addSatelliteTrackStyle(control, doc);

        /** Add an icon to indicate the name and date of the satellite pass */
        IconResource icon = IconResource.getSatelliteTrackResource(control);
        Icon kmlIcon = new Icon().withHref(icon.getUrl());
        Style style = doc.createAndAddStyle()
                .withId(getStyleId(icon.getName()));
        style.createAndSetIconStyle().withScale(1.0).withIcon(kmlIcon);
        style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1.0);

        Folder folder = doc.createAndAddFolder();
        folder.withName("Satellite Tracks").withOpen(false);

        for (SatellitePass pass : activity.getPasses()) {
            SatellitePassId id = pass.getId();
            LocalDate passDate = id.getDate();
            String satName = id.getSatelliteName();
            ApSatellite satellite = activity.getSatellites().getSatellite(satName);

            // Create KML folder for the pass points
            Folder passFolder = folder.createAndAddFolder().withName(pass.getId().toString()).withOpen(false);
            LocalDateTime currentContact = pass.getFirstContact().minusMinutes(TRACK_LEAD_LAG_TIME_MINS-1);
            GlobalCoords3D lastPosition = null;
            while (currentContact.isBefore(pass.getLastContact().plusMinutes(TRACK_LEAD_LAG_TIME_MINS))) {
                // Calculate position of satellite at the time
                GlobalCoords3D currentPosition = satellite.getPosition(groundStation, currentContact.toLocalDate(), currentContact.toLocalTime());
                if (lastPosition == null) {
                    addSatelliteMarker(control, passFolder, satName, passDate, currentPosition);
                } else {
                    drawSatelliteTrack(passFolder, currentContact, lastPosition, currentPosition, styleUrl);
                }
                lastPosition = currentPosition;
                currentContact = currentContact.plusMinutes(1);
            }
        }
    }

    private void addSatelliteMarker(TransformControl control,  Folder folder, String satName, LocalDate passDate, GlobalCoords3D position) {
        Placemark placemark = folder.createAndAddPlacemark();
        String date = passDate.toString();
        String id = String.format("%s %s", satName, date);

        IconResource icon = IconResource.getSatelliteTrackResource(control);
        // use the style for each line type
        placemark.withName(id)
                .withId(id)
                .withStyleUrl(getStyleUrl(icon.getName()))
                .createAndSetLookAt()
                .withLongitude(position.getLongitude())
                .withLatitude(position.getLatitude())
                .withAltitude(position.getAltitude())
                .withRange(DEFAULT_RANGE_METRES);
        placemark.createAndSetPoint()
                .addToCoordinates(position.getLongitude(), position.getLatitude(), position.getAltitude())
                .setAltitudeMode(AltitudeMode.ABSOLUTE); // set coordinates
    }

    private void drawSatelliteTrack(Folder folder, LocalDateTime currentTime, GlobalCoords3D lastPosition,
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

    private String addSatelliteTrackStyle(TransformControl control, Document doc) {
        String styleId = KmlUtils.getStyleId(SATELLITE_TRACK_LINE_ID);
        KmlLineStyle styling = KmlStyling.getKmlLineStyle(control.getKmlSatelliteTrackLineStyle());
        Style style = doc.createAndAddStyle()
                .withId(styleId);
        assert styling != null;
        style.createAndSetLineStyle().withColor(styling.getStringSpecifier()).withWidth(5);
        return KmlUtils.getStyleUrl(SATELLITE_TRACK_LINE_ID);
    }
}
