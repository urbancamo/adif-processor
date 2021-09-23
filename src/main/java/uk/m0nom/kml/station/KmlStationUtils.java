package uk.m0nom.kml.station;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.kml.info.KmlStationIcon;
import uk.m0nom.kml.info.KmlStationInfoPanel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import static uk.m0nom.kml.KmlUtils.getStyleId;
import static uk.m0nom.kml.KmlUtils.getStyleUrl;

public class KmlStationUtils {
    public final static double DEFAULT_RANGE_METRES = 500.0;
    private static final Logger logger = Logger.getLogger(KmlStationUtils.class.getName());
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm");

    public static String addMyStationToMap(TransformControl control, Document doc, Folder folder, Qso qso) {
        return createMyStationMarker(control, doc, folder, qso);
    }

    public static String getQsoDateTimeAsString(Qso qso) {
        LocalDate date = qso.getRecord().getQsoDate();
        LocalTime time = qso.getRecord().getTimeOn();

        LocalDateTime contactDateTime = LocalDateTime.of(date, time);
        return formatter.format(contactDateTime);
    }


    public static String createMyStationMarker(TransformControl control, Document document, Folder folder, Qso qso) {
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


    public static String createStationMarker(TransformControl control, Document document, Folder folder, Qso qso) {
        String id = getStationMarkerId(qso);
        String name = getStationMarkerName(qso);
        Adif3Record rec = qso.getRecord();
        if (qso.getTo().getCoordinates() == null && rec.getCoordinates() == null) {
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

    public static String getModeLabel(Qso qso) {
        if (qso.getRecord().getBand() != null && qso.getRecord().getMode() != null) {
            return String.format("%s %s", qso.getRecord().getBand().adifCode(), qso.getRecord().getMode().adifCode());
        } else {
            return "";
        }
    }

    /** In order to be unique the station marker name must contain the date and time of the contact **/
    public static String getStationMarkerId(Qso qso) {
        String stationName = qso.getTo().getCallsign();
        String dateTime = getQsoDateTimeAsString(qso);
        String id = String.format("%s %s", dateTime, stationName);
        return id.replaceAll(" ", "_");
    }

    public static String getStationMarkerName(Qso qso) {
        return qso.getTo().getCallsign();
    }

}
