package uk.m0nom.adifproc.kml.comms;

import de.micromata.opengis.kml.v_2_2_0.*;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.icons.IconResource;
import uk.m0nom.adifproc.kml.KmlLineStyle;
import uk.m0nom.adifproc.kml.KmlStyling;
import uk.m0nom.adifproc.kml.KmlUtils;
import uk.m0nom.adifproc.kml.station.KmlStationUtils;
import uk.m0nom.adifproc.satellite.ApSatellite;
import uk.m0nom.adifproc.satellite.SatelliteActivity;
import uk.m0nom.adifproc.satellite.SatellitePass;
import uk.m0nom.adifproc.satellite.SatellitePassId;

import java.time.ZonedDateTime;

/**
 * Draws an arc representing the track of a worked satellite for one pass through the sky based
 * the contacts that were made. The track includes a lead/trail time based on the first/last contact times
 * during that pass
 */

public class KmlSatelliteTrack {
    private final static String SATELLITE_TRACK_LINE_ID = "satellite_track_line";
    private final static String EMPTY_SHADOW_MARKER = "empty_shadow_marker";

    private final static int TRACK_LEAD_LAG_TIME_MINS = 5;

    public void addSatelliteTracks(TransformControl control, Document doc, SatelliteActivity activity, GlobalCoords3D groundStation) {
        String styleUrl = addSatelliteTrackStyle(control, doc);

        // Add an icon to indicate the name and date of the satellite pass
        IconResource icon = IconResource.getSatelliteTrackResource(control);
        Icon kmlIcon = new Icon().withHref(icon.getUrl());
        Style style = doc.createAndAddStyle().withId(KmlUtils.getStyleId(icon.getName()));
        style.createAndSetIconStyle().withScale(1.0).withIcon(kmlIcon);
        style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1.0);

        Style shadowStyle = doc.createAndAddStyle().withId(KmlUtils.getStyleId(EMPTY_SHADOW_MARKER));
        shadowStyle.createAndSetIconStyle().withScale(0).withIcon(kmlIcon);
        shadowStyle.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1.0);

        Folder folder = doc.createAndAddFolder();
        folder.withName("Satellite Tracks").withOpen(false);

        drawSatelliteTracks(control, folder, activity, groundStation, styleUrl, false);
        drawSatelliteTracks(control, folder, activity, groundStation, KmlUtils.getStyleId(KmlCommsService.SHADOW_LINE), true);
    }

    private void drawSatelliteTracks(TransformControl control, Folder folder, SatelliteActivity activity,
                                     GlobalCoords3D groundStation, String styleUrl, boolean shadow) {
        for (SatellitePass pass : activity.getPasses()) {
            SatellitePassId id = pass.getId();
            ZonedDateTime passDate = id.getDate();
            String satName = id.getSatelliteName();
            ApSatellite satellite = activity.getSatellites().getSatellite(satName, passDate);

            // Create KML folder for the pass points
            String folderId = pass.getId().toString();
            if (shadow) {
                folderId = folderId + "_shadow";
            }
            Folder passFolder = folder.createAndAddFolder().withName(folderId).withOpen(false);
            ZonedDateTime currentContact = pass.getFirstContact().minusMinutes(TRACK_LEAD_LAG_TIME_MINS-1);
            GlobalCoords3D lastPosition = null;
            while (currentContact.isBefore(pass.getLastContact().plusMinutes(TRACK_LEAD_LAG_TIME_MINS))) {
                // Calculate position of satellite at the time
                GlobalCoords3D currentPosition = satellite.getPosition(groundStation, currentContact);
                if (lastPosition == null) {
                    addSatelliteMarker(control, passFolder, satName, passDate, currentPosition, shadow);
                } else {
                    drawSatelliteTrack(passFolder, currentContact, lastPosition, currentPosition, styleUrl, shadow);
                }
                lastPosition = currentPosition;
                currentContact = currentContact.plusMinutes(1);
            }
        }
    }

    private void addSatelliteMarker(TransformControl control,  Folder folder, String satName, ZonedDateTime passDate, GlobalCoords3D position, boolean shadow) {
        Placemark placemark = folder.createAndAddPlacemark();
        String date = passDate.toString();
        String id = String.format("%s %s%s", satName, date, shadow ? "_shadow" : "");
        IconResource icon = IconResource.getSatelliteTrackResource(control);
        // use the style for each line type
        if (shadow) {
            placemark.withId(id)
                    .withStyleUrl(KmlUtils.getStyleUrl(EMPTY_SHADOW_MARKER))
                     .createAndSetPoint()
                     .addToCoordinates(position.getLongitude(), position.getLatitude(), 0.0)
                     .setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
        } else {
            placemark
                    .withName(id)
                    .withId(id)
                    .withStyleUrl(KmlUtils.getStyleUrl(icon.getName()))
                    .createAndSetLookAt()
                    .withLongitude(position.getLongitude()).withLatitude(position.getLatitude())
                    .withAltitude(position.getAltitude())
                    .withRange(KmlStationUtils.DEFAULT_RANGE_METRES);
            placemark
                    .createAndSetPoint()
                    .addToCoordinates(position.getLongitude(), position.getLatitude(), position.getAltitude())
                    .setAltitudeMode(AltitudeMode.ABSOLUTE); // set coordinates
        }
    }

    private void drawSatelliteTrack(Folder folder, ZonedDateTime currentTime, GlobalCoords3D lastPosition,
                                    GlobalCoords3D currentPosition,
                                    String styleUrl, boolean shadow) {
        Placemark placemark = folder.createAndAddPlacemark();
        String time = currentTime.toString();
        // use the style for each line type
        placemark.withName(time)
                .withId(time)
                .withStyleUrl(styleUrl);

        AltitudeMode mode = AltitudeMode.ABSOLUTE;
        if (shadow) {
            mode = AltitudeMode.RELATIVE_TO_GROUND;
        }
        LineString trackLine = placemark.createAndSetLineString().withAltitudeMode(mode);
        trackLine.addToCoordinates(lastPosition.getLongitude(), lastPosition.getLatitude(), shadow ? 0.0 : lastPosition.getAltitude());
        trackLine.addToCoordinates(currentPosition.getLongitude(), currentPosition.getLatitude(), shadow ? 0.0 : currentPosition.getAltitude());
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
