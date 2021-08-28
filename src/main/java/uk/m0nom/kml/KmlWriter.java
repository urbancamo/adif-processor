package uk.m0nom.kml;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.contacts.Qsos;
import uk.m0nom.adif3.transform.TransformResults;
import uk.m0nom.propagation.Ionosphere;
import uk.m0nom.activity.sota.SotaSummitInfo;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.kml.info.KmlContactInfoPanel;
import uk.m0nom.kml.info.KmlStationIcon;
import uk.m0nom.kml.info.KmlStationInfoPanel;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import static uk.m0nom.kml.KmlUtils.getStyleId;
import static uk.m0nom.kml.KmlUtils.getStyleUrl;

public class KmlWriter {
    public final static double DEFAULT_RANGE_METRES = 500.0;
    private static final Logger logger = Logger.getLogger(KmlWriter.class.getName());
    private ActivityDatabases activities;
    private Ionosphere ionosphere;
    private TransformControl control;
    private KmlBandLineStyles bandLineStyles;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm");

    public KmlWriter(TransformControl control) {
        this.control = control;
        this.ionosphere = new Ionosphere();
        bandLineStyles = new KmlBandLineStyles(control.getKmlContactWidth(), control.getKmlContactTransparency());
    }

    public void write(String pathname, String name, ActivityDatabases activities, Qsos qsos, TransformResults results) {
        KmlLocalActivities kmlLocalActivities = new KmlLocalActivities();

        this.activities = activities;

        final Kml kml = new Kml();
        Document doc = kml.createAndSetDocument().withName(name).withOpen(true);

        // create a Folder
        Folder folder = doc.createAndAddFolder();
        folder.withName("Contacts").withOpen(true);

        // create Placemark elements
        boolean first = true;
        for (Qso qso : qsos.getQsos()) {
            if (first) {
                String error = addMyStationToMap(doc, folder, qso);
                if (error != null) {
                    results.setError(error);
                }
                if (qso.getFrom().hasActivity() && control.getKmlShowLocalActivationSites()) {
                    kmlLocalActivities.addLocalActivities(doc, folder, qso.getFrom(), control.getKmlLocalActivationSitesRadius(), activities);
                }
                first = false;
            }
            Folder contactFolder = folder.createAndAddFolder().withName(qso.getTo().getCallsign()).withOpen(false);
            GlobalCoordinates coords = qso.getRecord().getCoordinates();
            if (coords != null) {
                String error = createStationMarker(doc, contactFolder, qso);
                if (error != null) {
                    results.setError(error);
                }

                if (qso.getTo().hasActivity() && control.getKmlShowLocalActivationSites()) {
                    Folder localActivityFolder = contactFolder.createAndAddFolder().withName("Local Activity").withOpen(false);
                    kmlLocalActivities.addLocalActivities(doc, localActivityFolder, qso.getTo(), control.getKmlLocalActivationSitesRadius(), activities);
                }
                error = createCommsLink(doc, contactFolder, qso, control);
                if (error != null) {
                    results.setError(error);
                }
            } else {
                results.addContactWithoutLocation(qso.getTo().getCallsign());
                logger.warning(String.format("Cannot determine communication link, no location data for: %s", qso.getTo().getCallsign()));
            }
        }

        try {
            logger.info(String.format("Writing KML to: %s", pathname));
            kml.marshal(new File(pathname));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String addMyStationToMap(Document doc, Folder folder, Qso qso) {
        return createMyStationMarker(doc, folder, qso);
   }

    private String createMyStationMarker(Document document, Folder folder, Qso qso) {
        // Create a folder for this information
        GlobalCoordinates coords = qso.getRecord().getMyCoordinates();
        if (qso.getFrom().getCoordinates() == null && coords == null) {
            return String.format("Cannot determine coordinates for station %s, please specify a location override", qso.getFrom().getCallsign());
        }
        double longitude = coords.getLongitude();
        double latitude = coords.getLatitude();
        String station = qso.getFrom().getCallsign();
        Folder myFolder = folder.createAndAddFolder().withName(station).withOpen(false);

        Icon icon = new Icon().withHref(new KmlStationIcon().getIconFromStation(control, qso.getFrom()));
        Style style = document.createAndAddStyle()
                .withId(getStyleId(station));

        // set the stylename to use this style from the placemark
        style.createAndSetIconStyle().withScale(1.0).withIcon(icon); // set size and icon
        style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1.0); // set color and size of the continent name

        Placemark placemark = myFolder.createAndAddPlacemark();
        String htmlPanelContent = new KmlStationInfoPanel().getPanelContent(qso.getFrom());
        // use the style for each continent
        placemark.withName(station)
                .withStyleUrl(getStyleUrl(station))
                // 3D chart image
                .withDescription(htmlPanelContent)
                // coordinates and distance (zoom level) of the viewer
                .createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(0).withRange(DEFAULT_RANGE_METRES);

        placemark.createAndSetPoint().addToCoordinates(longitude, latitude); // set coordinates

        return null;
    }


    private String createStationMarker(Document document, Folder folder, Qso qso) {
        String id = getStationMarkerId(qso);
        String name = getStationMarkerName(qso);
        Adif3Record rec = qso.getRecord();
        if (qso.getFrom().getCoordinates() == null && rec.getMyCoordinates() == null) {
            return String.format("Cannot determine coordinates for station %s, please specify a location override", qso.getFrom().getCallsign());
        }
        GlobalCoordinates myCoords = rec.getMyCoordinates();
        double myLatitude = myCoords.getLatitude();
        double myLongitude = myCoords.getLongitude();

        GlobalCoordinates coords = rec.getCoordinates();
        double longitude = coords.getLongitude();
        double latitude = coords.getLatitude();
        String station = rec.getCall();

        Icon icon = new Icon()
                .withHref(new KmlStationIcon().getIconFromStation(control, qso.getTo()));

        Style style = document.createAndAddStyle()
                .withId(getStyleId(id));

        // set the stylename to use this style from the placemark
        style.createAndSetIconStyle().withScale(1.0).withIcon(icon); // set size and icon
        style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1.0); // set color and size of the station marker
        style.createAndSetLineStyle().withColor("ffb343ff").withWidth(5);

        Placemark placemark = folder.createAndAddPlacemark();
        String htmlPanelContent = new KmlStationInfoPanel().getPanelContent(qso.getTo());
        // use the style for each continent
        placemark.withName(name)
                .withId(id)
                .withStyleUrl(getStyleUrl(id))
                // 3D chart image
                .withDescription(htmlPanelContent)
                // coordinates and distance (zoom level) of the viewer
                .createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(0).withRange(DEFAULT_RANGE_METRES);

        placemark.createAndSetLineString().addToCoordinates(myLongitude, myLatitude).addToCoordinates(longitude, latitude).setExtrude(true);
        placemark.createAndSetPoint().addToCoordinates(longitude, latitude); // set coordinates

        String modeIconUrl = new KmlStationIcon().getIconFromMode(control, qso.getRecord().getMode());
        if (modeIconUrl != null) {
            Icon modeIcon = new Icon().withHref(modeIconUrl);
            Placemark modePlaceMark = folder.createAndAddPlacemark();
            Style modeStyle = document.createAndAddStyle()
                    .withId(getStyleId(id + "_mode"));

            modeStyle.createAndSetIconStyle()
                    .withScale(1.0)
                    .withIcon(modeIcon);
            modeStyle.createAndSetLabelStyle().withColor("ff43b3ff").withScale(0.75); // set color and size of the station marker
            modeStyle.createAndSetLineStyle().withColor("ffb343ff").withWidth(3);
            modePlaceMark.withId(id + "_mode")
                    .withName(getModeLabel(qso))
                    .withStyleUrl(getStyleUrl(id + "_mode"));

            modePlaceMark.createAndSetPoint().addToCoordinates(longitude, latitude); // set coordinates
        }
        return null;
    }

    private String getModeLabel(Qso qso) {
        return String.format("%s %s", qso.getRecord().getBand().adifCode(), qso.getRecord().getMode().adifCode());
    }

    /** In order to be unique the station marker name must contain the date and time of the contact **/
    private String getStationMarkerId(Qso qso) {
        String stationName = qso.getTo().getCallsign();
        String dateTime = getQsoDateTimeAsString(qso);
        String id = String.format("%s %s", dateTime, stationName);
        return id.replaceAll(" ", "_");
    }

    private String getStationMarkerName(Qso qso) {
        return qso.getTo().getCallsign();
    }

    private String getCommsLinkId(Qso qso) {
        String fromName = qso.getFrom().getCallsign();
        String toName = qso.getTo().getCallsign();
        String dateTime = getQsoDateTimeAsString(qso);

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

    private String getQsoDateTimeAsString(Qso qso) {
        LocalDate date = qso.getRecord().getQsoDate();
        LocalTime time = qso.getRecord().getTimeOn();

        LocalDateTime contactDateTime = LocalDateTime.of(date, time);
        return formatter.format(contactDateTime);
    }


    private String createCommsLink(Document document, Folder folder, Qso qso, TransformControl control) {
        String commsLinkId = getCommsLinkId(qso);
        String commsLinkName = getCommsLinkName(qso);
        String commsLinkShadowId = getCommsLinkShadowId(qso);

        Adif3Record rec = qso.getRecord();

        GlobalCoordinates myCoords = rec.getMyCoordinates();
        if (qso.getFrom().getCoordinates() == null && rec.getMyCoordinates() == null) {
            return String.format("Cannot determine coordinates for station %s, please specify a location override", qso.getFrom().getCallsign());
        }

        GlobalCoordinates coords = rec.getCoordinates();

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

        LineString hfLine = placemark.createAndSetLineString();
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
        HfLineResult result = KmlGeodesicUtils.getHfLine(hfLine, myCoords, coords, ionosphere,
                rec.getFreq(), rec.getBand(), rec.getTimeOn(), myAltitude, theirAltitude, control.getHfAntennaTakeoffAngle());

        // Set the contact distance in the ADIF output file
        rec.setDistance(result.getDistance());

        placemark.withDescription(new KmlContactInfoPanel().getPanelContentForCommsLink(qso, result));
        if (control.getKmlContactShadow()) {
            placemark = folder.createAndAddPlacemark();
            // use the style for each line type
            placemark.withName("")
                    .withId(commsLinkShadowId)
                    .withStyleUrl(getStyleUrl(commsLinkShadowId));

            hfLine = placemark.createAndSetLineString();
            KmlGeodesicUtils.getSurfaceLine(hfLine, myCoords, coords);
        }
        return null;

    }


}
