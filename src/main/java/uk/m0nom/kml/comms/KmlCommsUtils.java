package uk.m0nom.kml.comms;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.sota.SotaSummitInfo;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.comms.CommsLinkResult;
import uk.m0nom.comms.CommsVisualizer;
import uk.m0nom.geodesic.GeodesicUtils;
import uk.m0nom.kml.KmlBandLineStyles;
import uk.m0nom.kml.KmlLineStyle;
import uk.m0nom.kml.KmlStyling;
import uk.m0nom.kml.KmlWriter;
import uk.m0nom.kml.info.KmlContactInfoPanel;
import uk.m0nom.kml.station.KmlStationUtils;

import java.util.logging.Logger;

import static uk.m0nom.kml.KmlUtils.getStyleId;
import static uk.m0nom.kml.KmlUtils.getStyleUrl;

public class KmlCommsUtils {
    private static final Logger logger = Logger.getLogger(KmlCommsUtils.class.getName());
    private ActivityDatabases activities;
    private TransformControl control;
    private KmlBandLineStyles bandLineStyles;

    public KmlCommsUtils(TransformControl control, ActivityDatabases activities) {
        this.control = control;
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

    public String createCommsLink(KmlWriter kmlWriter, Document document, Folder folder, Qso qso, TransformControl control) {
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
        Style style = document.createAndAddStyle()
                .withId(getStyleId(commsLinkId));

        if (control.getKmlS2s() && qso.doingSameActivity()) {
            KmlLineStyle styling = KmlStyling.getKmlLineStyle(control.getKmlS2sContactLineStyle());
            assert styling != null;
            style.createAndSetLineStyle().withColor(styling.getStringSpecifier()).withWidth(styling.getWidth());
        } else if (control.getKmlContactColourByBand()) {
            KmlLineStyle styling = bandLineStyles.getLineStyle(qso.getRecord().getBand());
            style.createAndSetLineStyle().withColor(styling.getStringSpecifier()).withWidth(styling.getWidth());
        } else  {
            KmlLineStyle styling = KmlStyling.getKmlLineStyle(control.getKmlContactLineStyle());
            assert styling != null;
            style.createAndSetLineStyle().withColor(styling.getStringSpecifier()).withWidth(styling.getWidth());
        }

        if (control.getKmlContactShadow()) {
            style = document.createAndAddStyle()
                    .withId(getStyleId(commsLinkShadowId));

            style.createAndSetLineStyle().withColor("40000000").withWidth(3);
        }

        Placemark placemark = folder.createAndAddPlacemark();
        // use the style for each line type
        placemark.withName(commsLinkName)
                .withId(commsLinkId)
                .withStyleUrl(getStyleUrl(commsLinkId));

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

        placemark.withDescription(new KmlContactInfoPanel().getPanelContentForCommsLink(qso, result));
        if (control.getKmlContactShadow()) {
            placemark = folder.createAndAddPlacemark();
            // use the style for each line type
            placemark.withName("")
                    .withId(commsLinkShadowId)
                    .withStyleUrl(getStyleUrl(commsLinkShadowId));

            commsLine = placemark.createAndSetLineString();
            new GeodesicUtils().getSurfaceLine(commsLine, myCoords, coords);
        }
        return null;

    }
}
