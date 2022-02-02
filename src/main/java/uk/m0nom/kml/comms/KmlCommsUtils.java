package uk.m0nom.kml.comms;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.sota.SotaInfo;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.CommsLinkResult;
import uk.m0nom.comms.CommsVisualizer;
import uk.m0nom.coords.GlobalCoordinatesWithSourceAccuracy;
import uk.m0nom.geodesic.GeodesicUtils;
import uk.m0nom.kml.KmlBandLineStyles;
import uk.m0nom.kml.KmlLineStyle;
import uk.m0nom.kml.KmlStyling;
import uk.m0nom.kml.KmlUtils;
import uk.m0nom.kml.info.KmlContactInfoPanel;
import uk.m0nom.kml.station.KmlStationUtils;

import java.util.HashMap;
import java.util.Map;

import static uk.m0nom.kml.KmlUtils.getStyleId;
import static uk.m0nom.kml.KmlUtils.getStyleUrl;

public class KmlCommsUtils {
    private final static String S2S_LINE = "s2S";
    private final static String COMM_LINE = "comm";
    private final static String SHADOW_LINE = "shadow";
    private final static String SATELLITE_ID = "satellite";

    private final ActivityDatabases activities;
    private final KmlBandLineStyles bandLineStyles;
    private final Map<String,String> commsStyleMap;

    public KmlCommsUtils(TransformControl control, ActivityDatabases activities) {
        this.activities = activities;
        bandLineStyles = new KmlBandLineStyles(control.getKmlContactWidth(), control.getKmlContactTransparency());
        commsStyleMap = new HashMap<>();
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

    public String createCommsLink(Document document, Folder folder, Qso qso, TransformControl control, KmlStationUtils stationUtils) {
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

        addStyleIfUsed(document, control, qso, commsStyleMap);

        String id = getStyleForQso(control, qso);

        Placemark placemark = folder.createAndAddPlacemark();
        // use the style for each line type
        placemark.withName(commsLinkName)
                .withId(commsLinkId)
                .withStyleUrl(commsStyleMap.get(id));

        LineString commsLine = placemark.createAndSetLineString();
        double myAltitude = 0.0;
        double theirAltitude = 0.0;
        if (qso.getRecord().getMySotaRef() != null) {
            SotaInfo summitInfo = (SotaInfo) activities.getDatabase(ActivityType.SOTA).get(qso.getRecord().getMySotaRef().getValue());
            if (summitInfo != null) {
                myAltitude = summitInfo.getAltitude();
            }
        }
        if (qso.getRecord().getSotaRef() != null) {
            SotaInfo summitInfo = (SotaInfo) activities.getDatabase(ActivityType.SOTA).get(qso.getRecord().getSotaRef().getValue());
            if (summitInfo != null) {
                theirAltitude = summitInfo.getAltitude();
            }
        }
        CommsLinkResult result = new CommsVisualizer().getCommunicationsLink(control, myCoords, coords,
                rec, myAltitude, theirAltitude);
        if (!result.isValid()) {
            return result.getError();
        }

        // Set the contact distance in the ADIF output file
        rec.setDistance(result.getDistanceInKm());
        String description = new KmlContactInfoPanel().getPanelContentForCommsLink(control, qso, result, control.getTemplateEngine());
        placemark.withDescription(description);
        commsLine.setAltitudeMode(AltitudeMode.ABSOLUTE);
        commsLine.setExtrude(false);

        for (GlobalCoordinatesWithSourceAccuracy coord : result.getPath()) {
            commsLine.addToCoordinates(coord.getLongitude(), coord.getLatitude(), coord.getAltitude() );
        }

        if (control.isKmlContactShadow() && !qso.isSatelliteContact()) {
            placemark = folder.createAndAddPlacemark();
            // use the style for each line type
            placemark.withName("(shadow)")
                    .withId(commsLinkShadowId)
                    .withDescription(description)
                    .withStyleUrl(commsStyleMap.get(SHADOW_LINE));
            LineString shadowLine = placemark.createAndSetLineString();
            shadowLine.setAltitudeMode(AltitudeMode.CLAMP_TO_GROUND);
            shadowLine.setExtrude(false);

            for (GlobalCoordinatesWithSourceAccuracy coord : result.getPath()) {
                shadowLine.addToCoordinates(coord.getLongitude(), coord.getLatitude());
            }

        }

        if (qso.isSatelliteContact()) {
            stationUtils.createSatelliteContactMarker(control, document, folder, qso, result.getSatellitePosition());
        }
        return null;

    }

    private String getStyleForQso(TransformControl control, Qso qso) {
        if (control.isKmlS2s() && qso.doingSameActivity()) {
            return S2S_LINE;
        }
        else if (control.isKmlContactColourByBand()) {
            KmlLineStyle styling = bandLineStyles.getLineStyle(qso.getRecord().getBand());
            return styling.getStringSpecifier();
        }
        return COMM_LINE;
    }


    /**
     * This method add styles to the map only if used. This ensures that we only create one style of each type
     * @param document Kml document
     * @param qso QSO to add appropriate style
     * @param control Controls the rendering of line styles
     * @return
     */
     private void addStyleIfUsed(Document document, TransformControl control, Qso qso, Map<String, String> commsStyleMap) {
        if (control.isKmlS2s() && qso.doingSameActivity()) {
            if (!commsStyleMap.containsKey(S2S_LINE)) {
                KmlLineStyle styling = KmlStyling.getKmlLineStyle(control.getKmlS2sContactLineStyle());
                Style style = document.createAndAddStyle()
                        .withId(getStyleId(S2S_LINE));
                assert styling != null;
                style.createAndSetLineStyle().withColor(styling.getStringSpecifier()).withWidth(styling.getWidth());
                commsStyleMap.put(S2S_LINE, getStyleUrl(S2S_LINE));
            }
        } else if (control.isKmlContactColourByBand()) {
            KmlLineStyle styling = bandLineStyles.getLineStyle(qso.getRecord().getBand());
            String styleId = styling.getStringSpecifier();
            if (!commsStyleMap.containsKey(styling.getStringSpecifier())) {
                Style style = document.createAndAddStyle()
                        .withId(getStyleId(styleId));
                style.createAndSetLineStyle().withColor(styling.getStringSpecifier()).withWidth(styling.getWidth());
                commsStyleMap.put(styling.getStringSpecifier(), getStyleUrl(styling.getStringSpecifier()));
            }
        }  else  {
            if (!commsStyleMap.containsKey(COMM_LINE)) {
                KmlLineStyle styling = KmlStyling.getKmlLineStyle(control.getKmlContactLineStyle());
                Style style = document.createAndAddStyle()
                        .withId(getStyleId(COMM_LINE));
                assert styling != null;
                style.createAndSetLineStyle().withColor(styling.getStringSpecifier()).withWidth(styling.getWidth());
                commsStyleMap.put(COMM_LINE, getStyleUrl(COMM_LINE));
            }
        }

         if (control.isKmlContactShadow()) {
             if (!commsStyleMap.containsKey(SHADOW_LINE)) {
                 String styleId = SHADOW_LINE;
                 Style style = document.createAndAddStyle()
                         .withId(getStyleId(styleId));
                 style.createAndSetLineStyle().withColor("40000000").withWidth(3);
                 commsStyleMap.put(SHADOW_LINE, getStyleUrl(SHADOW_LINE));
             }
         }
    }
}
