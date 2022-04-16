package uk.m0nom.adifproc.kml.comms;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.marsik.ham.adif.Adif3Record;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.activity.sota.SotaInfo;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.comms.CommsLinkResult;
import uk.m0nom.adifproc.comms.CommsVisualizationService;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.geodesic.GeodesicUtils;
import uk.m0nom.adifproc.kml.KmlBandLineStyles;
import uk.m0nom.adifproc.kml.KmlLineStyle;
import uk.m0nom.adifproc.kml.KmlStyling;
import uk.m0nom.adifproc.kml.KmlUtils;
import uk.m0nom.adifproc.kml.info.KmlContactInfoPanel;
import uk.m0nom.adifproc.kml.station.KmlStationUtils;

import java.util.Map;

@Service
public class KmlCommsService {
    public final static String S2S_LINE = "s2S";
    public final static String COMM_LINE = "comm";
    public final static String SHADOW_LINE = "shadow";

    private final ActivityDatabaseService activityDatabaseService;
    private final CommsVisualizationService commsVisualizationService;
    private KmlBandLineStyles bandLineStyles;

    public KmlCommsService(ActivityDatabaseService activities, CommsVisualizationService commsVisualizationService) {
        this.activityDatabaseService = activities;
        this.commsVisualizationService = commsVisualizationService;
    }

    public static String getCommsLinkId(Qso qso) {
        String fromName = qso.getFrom().getCallsign();
        String toName = qso.getTo().getCallsign();
        String dateTime = KmlStationUtils.getQsoDateTimeAsString(qso);

        String id = String.format("%s %s %s", dateTime, fromName, toName);
        return id.replaceAll(" ", "_");
    }

    public static String getCommsLinkName(Qso qso) {
        String fromName = qso.getFrom().getCallsign();
        String toName = qso.getTo().getCallsign();

        return String.format("%s ⇋ %s", fromName, toName);
    }

    public static String getCommsLinkShadowId(Qso qso) {
        String commsLinkLabel = getCommsLinkId(qso);
        String id = String.format("%s Shadow", commsLinkLabel);
        return id.replaceAll(" ", "_");
    }

    public String createCommsLink(Document document, Folder folder, Map<String, String> commsStyleMap, Qso qso, TransformControl control, KmlStationUtils stationUtils) {
        bandLineStyles = new KmlBandLineStyles(control.getKmlContactWidth(), control.getKmlContactTransparency());
        String commsLinkId = getCommsLinkId(qso);
        String commsLinkName = getCommsLinkName(qso);
        String commsLinkShadowId = getCommsLinkShadowId(qso);

        Adif3Record rec = qso.getRecord();

        if (qso.getFrom().getCoordinates() == null && rec.getMyCoordinates() == null) {
            return String.format("Cannot determine coordinates for station %s, please specify a location override", qso.getFrom().getCallsign());
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
            SotaInfo summitInfo = (SotaInfo) activityDatabaseService.getDatabase(ActivityType.SOTA).get(qso.getRecord().getMySotaRef().getValue());
            if (summitInfo != null) {
                myAltitude = summitInfo.getAltitude();
            }
        }
        if (qso.getRecord().getSotaRef() != null) {
            SotaInfo summitInfo = (SotaInfo) activityDatabaseService.getDatabase(ActivityType.SOTA).get(qso.getRecord().getSotaRef().getValue());
            if (summitInfo != null) {
                theirAltitude = summitInfo.getAltitude();
            }
        }

        GlobalCoords3D myCoords = new GlobalCoords3D(rec.getMyCoordinates(), myAltitude);
        GlobalCoords3D coords = new GlobalCoords3D(rec.getCoordinates(), theirAltitude);

        // Sanity check - if their coords and our coords are the same then the geodesic calculations are going to stall
        if (GeodesicUtils.areCoordsEqual(myCoords, coords)) {
            return String.format("Your location and the location of station %s at %.3f, %.3f are equal - check the log!", qso.getTo().getCallsign(), coords.getLatitude(), coords.getLongitude());
        }

        CommsLinkResult result = commsVisualizationService.getCommunicationsLink(control, myCoords, coords, rec);
        if (!result.isValid()) {
            return result.getError();
        }

        // Set the contact distance in the ADIF output file
        rec.setDistance(result.getDistanceInKm());
        String description = new KmlContactInfoPanel().getPanelContentForCommsLink(qso, result, control.getTemplateEngine());
        placemark.withDescription(description);
        commsLine.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
        commsLine.setExtrude(false);

        for (GlobalCoords3D coord : result.getPath()) {
            commsLine.addToCoordinates(coord.getLongitude(), coord.getLatitude(), coord.getAltitude());
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

            for (GlobalCoords3D coord : result. getPath()) {
                shadowLine.addToCoordinates(coord.getLongitude(), coord.getLatitude());
            }

        }

        if (qso.isSatelliteContact() && result.isValid()) {
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
     */
     private void addStyleIfUsed(Document document, TransformControl control, Qso qso, Map<String, String> commsStyleMap) {
        if (control.isKmlS2s() && qso.doingSameActivity()) {
            if (!commsStyleMap.containsKey(S2S_LINE)) {
                KmlLineStyle styling = KmlStyling.getKmlLineStyle(control.getKmlS2sContactLineStyle());
                Style style = document.createAndAddStyle()
                        .withId(KmlUtils.getStyleId(S2S_LINE));
                assert styling != null;
                style.createAndSetLineStyle().withColor(styling.getStringSpecifier()).withWidth(styling.getWidth());
                commsStyleMap.put(S2S_LINE, KmlUtils.getStyleUrl(S2S_LINE));
            }
        } else if (control.isKmlContactColourByBand()) {
            KmlLineStyle styling = bandLineStyles.getLineStyle(qso.getRecord().getBand());
            String styleId = styling.getStringSpecifier();
            if (!commsStyleMap.containsKey(styling.getStringSpecifier())) {
                Style style = document.createAndAddStyle()
                        .withId(KmlUtils.getStyleId(styleId));
                style.createAndSetLineStyle().withColor(styling.getStringSpecifier()).withWidth(styling.getWidth());
                commsStyleMap.put(styling.getStringSpecifier(), KmlUtils.getStyleUrl(styling.getStringSpecifier()));
            }
        }  else  {
            if (!commsStyleMap.containsKey(COMM_LINE)) {
                KmlLineStyle styling = KmlStyling.getKmlLineStyle(control.getKmlContactLineStyle());
                Style style = document.createAndAddStyle()
                        .withId(KmlUtils.getStyleId(COMM_LINE));
                assert styling != null;
                style.createAndSetLineStyle().withColor(styling.getStringSpecifier()).withWidth(styling.getWidth());
                commsStyleMap.put(COMM_LINE, KmlUtils.getStyleUrl(COMM_LINE));
            }
        }

         if (control.isKmlContactShadow()) {
             if (!commsStyleMap.containsKey(SHADOW_LINE)) {
                 Style style = document.createAndAddStyle()
                         .withId(KmlUtils.getStyleId(SHADOW_LINE));
                 style.createAndSetLineStyle().withColor("40000000").withWidth(3);
                 commsStyleMap.put(SHADOW_LINE, KmlUtils.getStyleUrl(SHADOW_LINE));
             }
         }
    }
}
