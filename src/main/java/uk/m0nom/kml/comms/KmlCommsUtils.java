package uk.m0nom.kml.comms;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.sota.SotaSummitInfo;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.CommsLinkResult;
import uk.m0nom.comms.CommsVisualizer;
import uk.m0nom.geodesic.GeodesicUtils;
import uk.m0nom.kml.KmlBandLineStyles;
import uk.m0nom.kml.KmlLineStyle;
import uk.m0nom.kml.KmlStyling;
import uk.m0nom.kml.info.KmlContactInfoPanel;
import uk.m0nom.kml.info.velocity.VelocityKmlContactInfoPanel;
import uk.m0nom.kml.station.KmlStationUtils;

import java.util.HashSet;
import java.util.Set;

import static uk.m0nom.kml.KmlUtils.getStyleId;
import static uk.m0nom.kml.KmlUtils.getStyleUrl;

public class KmlCommsUtils {
    private final static String S2S_LINE_ID = "s2S";
    private final static String COMM_LINE_ID = "comm";
    private final static String SHADOW_LINE_ID = "shadow";

    private final ActivityDatabases activities;
    private final KmlBandLineStyles bandLineStyles;
    private final Set<String> commStyles;

    public KmlCommsUtils(TransformControl control, ActivityDatabases activities) {
        commStyles = new HashSet<>();
        this.activities = activities;
        bandLineStyles = new KmlBandLineStyles(control.getKmlContactWidth(), control.getKmlContactTransparency());
    }

    private String getCommsLinkId(Qso qso) {
        String fromName = qso.getFrom().getCallsign();
        String toName = qso.getTo().getCallsign();
        String dateTime = KmlStationUtils.getQsoDateTimeAsString(qso);

        String id = String.format("%s %s %s", dateTime, fromName, toName);
        return id.replaceAll(" ", "_");
    }

    private String getCommsLinkName(Qso qso) {
        String fromName = qso.getFrom().getCallsign();
        String toName = qso.getTo().getCallsign();

        return String.format("%s ⇋ %s", fromName, toName);
    }

    private String getCommsLinkShadowId(Qso qso) {
        String commsLinkLabel = getCommsLinkId(qso);
        String id = String.format("%s Shadow", commsLinkLabel);
        return id.replaceAll(" ", "_");
    }

    public String createCommsLink(Document document, Folder folder, Qso qso, TransformControl control) {
        String commsLinkId = getCommsLinkId(qso);
        String commsLinkName = getCommsLinkName(qso);
        String commsLinkShadowId = getCommsLinkShadowId(qso);

        Adif3Record rec = qso.getRecord();

        GlobalCoordinates myCoords = rec.getMyCoordinates();
        if (qso.getFrom().getCoordinates() == null && rec.getMyCoordinates() == null) {
            return String.format("Cannot determine coordinates for station %s, please specify a location override", qso.getFrom().getCallsign());
        }

        GlobalCoordinates coords = rec.getCoordinates();

        // Sanity check - if their coords and our coords are the same then the geodesic calculations are going to stall
        if (GeodesicUtils.areCoordsEqual(myCoords, coords)) {
            return String.format("Your location and the location of station %s at %.3f, %.3f are equal - check the log!", qso.getTo().getCallsign(), coords.getLatitude(), coords.getLongitude());
        }

        String commsStyleUrl;
        String shadowStyleUrl = null;
        if (control.isKmlS2s() && qso.doingSameActivity()) {
            if (!commStyles.contains(S2S_LINE_ID)) {
                String styleId = getStyleId(S2S_LINE_ID);
                KmlLineStyle styling = KmlStyling.getKmlLineStyle(control.getKmlS2sContactLineStyle());
                Style style = document.createAndAddStyle()
                        .withId(styleId);
                assert styling != null;
                style.createAndSetLineStyle().withColor(styling.getStringSpecifier()).withWidth(styling.getWidth());
                commStyles.add(S2S_LINE_ID);
            }
            commsStyleUrl = getStyleUrl(S2S_LINE_ID);
        } else if (control.isKmlContactColourByBand()) {
            KmlLineStyle styling = bandLineStyles.getLineStyle(qso.getRecord().getBand());
            String styleId = getStyleId(styling.getStringSpecifier());
            if (!commStyles.contains(styling.getStringSpecifier())) {
                Style style = document.createAndAddStyle()
                        .withId(styleId);
                style.createAndSetLineStyle().withColor(styling.getStringSpecifier()).withWidth(styling.getWidth());
                commStyles.add(styling.getStringSpecifier());
            }
            commsStyleUrl = getStyleUrl(styling.getStringSpecifier());
        } else  {
            if (!commStyles.contains(COMM_LINE_ID)) {
                String styleId = getStyleId(COMM_LINE_ID);
                KmlLineStyle styling = KmlStyling.getKmlLineStyle(control.getKmlContactLineStyle());
                Style style = document.createAndAddStyle()
                        .withId(styleId);
                assert styling != null;
                style.createAndSetLineStyle().withColor(styling.getStringSpecifier()).withWidth(styling.getWidth());
                commStyles.add(COMM_LINE_ID);
            }
            commsStyleUrl = getStyleUrl(COMM_LINE_ID);
        }

        if (control.isKmlContactShadow()) {
            if (!commStyles.contains(SHADOW_LINE_ID)) {
                String styleId = getStyleId(SHADOW_LINE_ID);
                Style style = document.createAndAddStyle()
                        .withId(styleId);
                style.createAndSetLineStyle().withColor("40000000").withWidth(3);
                commStyles.add(SHADOW_LINE_ID);
            }
            shadowStyleUrl = getStyleUrl(SHADOW_LINE_ID);
        }

        Placemark placemark = folder.createAndAddPlacemark();
        // use the style for each line type
        placemark.withName(commsLinkName)
                .withId(commsLinkId)
                .withStyleUrl(commsStyleUrl);

        LineString commsLine = placemark.createAndSetLineString();
        double myAltitude = 0.0;
        double theirAltitude = 0.0;
        if (qso.getRecord().getMySotaRef() != null) {
            SotaSummitInfo summitInfo = (SotaSummitInfo) activities.getDatabase(ActivityType.SOTA).get(qso.getRecord().getMySotaRef().getValue());
            if (summitInfo != null) {
                myAltitude = summitInfo.getAltitude();
            }
        }
        if (qso.getRecord().getSotaRef() != null) {
            SotaSummitInfo summitInfo = (SotaSummitInfo) activities.getDatabase(ActivityType.SOTA).get(qso.getRecord().getSotaRef().getValue());
            if (summitInfo != null) {
                theirAltitude = summitInfo.getAltitude();
            }
        }
        CommsLinkResult result = new CommsVisualizer().getCommsLink(control, commsLine, myCoords, coords,
                rec, myAltitude, theirAltitude);

        // Set the contact distance in the ADIF output file
        rec.setDistance(result.getDistance());
        String description = new VelocityKmlContactInfoPanel().getPanelContentForCommsLink(control, qso, result);
        placemark.withDescription(description);
        if (control.isKmlContactShadow()) {
            placemark = folder.createAndAddPlacemark();
            // use the style for each line type
            placemark.withName("(shadow)")
                    .withId(commsLinkShadowId)
                    .withDescription(description)
                    .withStyleUrl(shadowStyleUrl);

            commsLine = placemark.createAndSetLineString();
            GeodesicUtils.getSurfaceLine(commsLine, myCoords, coords);
        }
        return null;

    }
}
