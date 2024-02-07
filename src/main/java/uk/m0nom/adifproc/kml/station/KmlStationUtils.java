package uk.m0nom.adifproc.kml.station;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Band;
import org.marsik.ham.adif.enums.Mode;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.contacts.Station;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.comms.CommsLinkResult;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.icons.IconResource;
import uk.m0nom.adifproc.kml.KmlUtils;
import uk.m0nom.adifproc.kml.info.KmlStationInfoPanel;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class KmlStationUtils {
    public final static double DEFAULT_RANGE_METRES = 500.0;
    private static final DateTimeFormatter serialDateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter displayDateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter timeWithSecondsFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String UNKNOWN_DATE_TIME = "unknown";

    private final Set<String> iconStyles = new HashSet<>();

    private final TransformControl control;

    public KmlStationUtils(TransformControl control) {
        this.control = control;
    }

    public String addMyStationToMap(Document doc, Folder folder, Qso qso) {
        return createMyStationMarker(doc, folder, qso);
    }

    public static String getQsoDateTimeAsString(Qso qso) {
        ZonedDateTime date = qso.getRecord().getQsoDate();
        if (date != null) {
            LocalTime time = qso.getRecord().getTimeOn();
            ZonedDateTime utcDateTime = date;
            if (time != null) {
                utcDateTime = date.with(time);
            }
            return dateTimeFormatter.format(utcDateTime);
        } else {
            System.out.printf("ERROR: QSO with %s has no date%n", qso.getTo().getCallsign());
        }
        return UNKNOWN_DATE_TIME;
    }

    public static String getQsoDateAsSerialString(Qso qso) {
        ZonedDateTime date = qso.getRecord().getQsoDate();
        if (date != null) {
            return serialDateFormatter.format(date);
        }
        return UNKNOWN_DATE_TIME;
    }

    public static String getQsoDateAsDisplayString(Qso qso) {
        ZonedDateTime date = qso.getRecord().getQsoDate();
        if (date != null) {
            return displayDateFormatter.format(date);
        }
        return UNKNOWN_DATE_TIME;
    }

    public static String getQsoTimeOnAsString(Qso qso) {
        LocalTime time = qso.getRecord().getTimeOn();
        if (time != null) {
            return timeFormatter.format(time);
        } else {
            return null;
        }
    }

    public static String getQsoTimeOffAsString(Qso qso) {
        LocalTime time = qso.getRecord().getTimeOff();
        if (time != null) {
            return timeFormatter.format(time);
        }
        return UNKNOWN_DATE_TIME;
    }

    public String createMyStationMarker(Document document, Folder folder, Qso qso) {
        String id = getStationMarkerId(qso, qso.getFrom());
        Adif3Record rec = qso.getRecord();
        // Create a folder for this information
        GlobalCoordinates coords = rec.getMyCoordinates();
        if (qso.getFrom().getCoordinates() == null && coords == null) {
            return String.format("Cannot determine coordinates for station %s, please specify a location override", qso.getFrom().getCallsign());
        }
        double longitude = coords.getLongitude();
        double latitude = coords.getLatitude();
        double altitude = rec.getMyAltitude() != null ? rec.getMyAltitude() : 0.0;

        String callsign = qso.getFrom().getCallsign();
        Folder myFolder = folder.createAndAddFolder().withName(callsign).withOpen(false);

        IconResource icon = IconResource.getIconFromStation(control, qso.getFrom());
        if (!iconStyles.contains(icon.getName())) {
            Icon kmlIcon = new Icon().withHref(icon.getUrl());
            Style style = document.createAndAddStyle()
                    .withId(KmlUtils.getStyleId(icon.getName()));
            // set the stylename to use this style from the placemark
            style.createAndSetIconStyle().withScale(1.0).withIcon(kmlIcon); // set size and icon
            style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1.0); // set color and size of the continent name
            iconStyles.add(icon.getName());
        }

        Placemark placemark = myFolder.createAndAddPlacemark();
        String htmlPanelContent = new KmlStationInfoPanel().getPanelContentForStation(control, qso.getFrom(), null);
        // use the style for each continent
        placemark.withName(callsign)
                .withStyleUrl(KmlUtils.getStyleUrl(icon.getName()))
                // 3D chart image
                .withDescription(htmlPanelContent)
                // coordinates and distance (zoom level) of the viewer
                .createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(altitude).withRange(DEFAULT_RANGE_METRES);

        placemark.createAndSetPoint().addToCoordinates(longitude, latitude); // set coordinates
        if (control.isKmlShowActivitySubLabel()) {
            addActivitySubLabel(qso.getFrom(), document, folder, longitude, latitude, altitude, id);
        } else if (control.isKmlShowStationSubLabel()) {
            addStationSubLabel(qso.getRecord().getBand(), qso.getRecord().getMode(), document, folder, longitude, latitude, altitude, id);
        }
        return null;
    }

    public void createSatelliteContactMarker(TransformControl control, Document document, Folder folder, Qso qso, GlobalCoords3D position, String description) {
        String name = getSatelliteMarkerName(qso);
        String folderName = getSatelliteFolderName(qso);

        IconResource icon = IconResource.getSatelliteResource(control);

        if (!iconStyles.contains(icon.getName())) {
            Icon kmlIcon = new Icon().withHref(icon.getUrl());
            Style style = document.createAndAddStyle()
                    .withId(KmlUtils.getStyleId(icon.getName()));
            // set the stylename to use this style from the placemark
            style.createAndSetIconStyle().withScale(1.0).withIcon(kmlIcon); // set size and icon
            style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1.0); // set color and size of the continent name
            iconStyles.add(icon.getName());
        }

        Folder satFolder = folder.createAndAddFolder().withName(folderName).withOpen(false);
        Placemark placemark = satFolder.createAndAddPlacemark().withDescription(description);

        //
        // String htmlPanelContent = new KmlStationInfoPanel().getPanelContentForStation(control, qso.getFrom());
        // use the style for each continent
        placemark.withName(name)
                .withStyleUrl(KmlUtils.getStyleUrl(icon.getName()))
                .createAndSetLookAt()
                .withLongitude(position.getLongitude())
                .withLatitude(position.getLatitude())
                .withAltitude(position.getAltitude())
                .withRange(DEFAULT_RANGE_METRES);
                // 3D chart image
                //.withDescription(htmlPanelContent)
                // coordinates and distance (zoom level) of the viewer
        placemark.createAndSetPoint()
                .addToCoordinates(position.getLongitude(), position.getLatitude(), position.getAltitude())
                .setAltitudeMode(AltitudeMode.ABSOLUTE); // set coordinates

    }


    public String createStationMarker(TransformControl control, Document document, Folder folder, Qso qso, CommsLinkResult result) {
        String id = getStationMarkerId(qso, qso.getTo());
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
        double altitude = rec.getAltitude() != null ? rec.getAltitude() : 0.0;

        IconResource icon = IconResource.getIconFromStation(control, qso.getTo());
        if (!iconStyles.contains(icon.getName())) {
            Icon kmlIcon = new Icon()
                    .withHref(icon.getUrl());

            Style style = document.createAndAddStyle()
                    .withId(KmlUtils.getStyleId(icon.getName()));

            // set the stylename to use this style from the placemark
            style.createAndSetIconStyle().withScale(1.0).withIcon(kmlIcon); // set size and icon
            style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1.0); // set color and size of the station marker
            style.createAndSetLineStyle().withColor("ffb343ff").withWidth(5);

            iconStyles.add(icon.getName());
        }

        Placemark placemark = folder.createAndAddPlacemark();
        String htmlPanelContent = new KmlStationInfoPanel().getPanelContentForStation(control, qso.getTo(), result);
        // use the style for each continent
        placemark.withName(name)
                .withId(id)
                .withStyleUrl(KmlUtils.getStyleUrl(icon.getName()))
                // 3D chart image
                .withDescription(htmlPanelContent)
                // coordinates and distance (zoom level) of the viewer
                .createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(altitude).withRange(DEFAULT_RANGE_METRES);

        placemark.createAndSetLineString().addToCoordinates(myLongitude, myLatitude).addToCoordinates(longitude, latitude).setExtrude(true);
        placemark.createAndSetPoint().addToCoordinates(longitude, latitude, altitude); // set coordinates
        if (altitude > 0.0) {
            placemark.createAndSetPoint().addToCoordinates(longitude, latitude, altitude).setAltitudeMode(AltitudeMode.ABSOLUTE); // set coordinates
        } else {
            placemark.createAndSetPoint().addToCoordinates(longitude, latitude, altitude);
        }
        // Doesn't work having two sub-labels, so prefer the activity sub label if set
        if (control.isKmlShowActivitySubLabel()) {
            addActivitySubLabel(qso.getTo(), document, folder, longitude, latitude, altitude, id);
        } else if (control.isKmlShowStationSubLabel()) {
            addStationSubLabel(qso.getRecord().getBand(), qso.getRecord().getMode(), document, folder, longitude, latitude, altitude, id);
        }

        return null;
    }

    private void addStationSubLabel(Band band, Mode mode, Document document, Folder folder, double longitude, double latitude, double altitude, String id) {
        IconResource icon = IconResource.getIconFromMode(control, mode);
        String modeId = mode.name();
        if (icon != null) {
            if (!iconStyles.contains(modeId)) {
                Icon modeIcon = new Icon().withHref(icon.getUrl());
                Style modeStyle = document.createAndAddStyle()
                        .withId(KmlUtils.getModeStyleId(modeId));
                modeStyle.createAndSetIconStyle()
                        .withScale(1.0)
                        .withIcon(modeIcon);
                modeStyle.createAndSetLabelStyle().withColor("ff43b3ff").withScale(0.75); // set color and size of the station marker
                modeStyle.createAndSetLineStyle().withColor("ffb343ff").withWidth(3);
                iconStyles.add(modeId);
            }
            Placemark modePlaceMark = folder.createAndAddPlacemark();
            modePlaceMark.withId(KmlUtils.getModeId(id))
                    .withName(getModeLabel(band, mode))
                    .withStyleUrl(KmlUtils.getModeStyleUrl(modeId));
            if (altitude > 0.0) {
                modePlaceMark.createAndSetPoint().addToCoordinates(longitude, latitude, altitude).setAltitudeMode(AltitudeMode.ABSOLUTE); // set coordinates
            } else {
                modePlaceMark.createAndSetPoint().addToCoordinates(longitude, latitude, altitude);
            }
        }
    }

    private void addActivitySubLabel(Station station, Document document, Folder folder, double longitude, double latitude, double altitude, String id) {
        IconResource icon = IconResource.getActivityIcon();
        if (!iconStyles.contains(icon.getName())) {
            Icon modeIcon = new Icon().withHref(icon.getUrl());
            Style activityStyle = document.createAndAddStyle()
                    .withId(KmlUtils.getStyleId(icon.getName()));
            activityStyle.createAndSetIconStyle()
                    .withScale(1.0)
                    .withIcon(modeIcon);
            activityStyle.createAndSetLabelStyle().withColor("ff43b3ff").withScale(0.75); // set color and size of the station marker
            activityStyle.createAndSetLineStyle().withColor("ffb343ff").withWidth(3);
            iconStyles.add(icon.getName());
        }
        Placemark activityPlaceMark = folder.createAndAddPlacemark();
        activityPlaceMark.withId(KmlUtils.getModeId(id))
                .withName(getActivityLabel(station))
                .withStyleUrl(KmlUtils.getStyleUrl(icon.getName()));
        if (altitude > 0.0) {
            activityPlaceMark.createAndSetPoint().addToCoordinates(longitude, latitude, altitude).setAltitudeMode(AltitudeMode.ABSOLUTE); // set coordinates
        } else {
            activityPlaceMark.createAndSetPoint().addToCoordinates(longitude, latitude, altitude);
        }
    }

    public static String getModeLabel(Band band, Mode mode) {
        if (band != null && mode != null) {
            return String.format("%s %s", band.adifCode(), mode.adifCode());
        } else {
            return "";
        }
    }

    public static String getActivityLabel(Station station) {
        return station.getActivities().stream()
                .map(Activity::getRef)
                .collect(Collectors.joining(", "));
    }

    /* In order to be unique the station marker name must contain the date and time of the contact **/
    public static String getStationMarkerId(Qso qso, Station station) {
        String stationName = station.getCallsign();
        String dateTime = getQsoDateTimeAsString(qso);
        String id = String.format("%s %s", dateTime, stationName);
        return id.replaceAll(" ", "_");
    }

    public static String getStationMarkerName(Qso qso) {
        return qso.getTo().getCallsign();
    }

    public static String getSatelliteFolderName(Qso qso) {
        String date = getQsoDateAsSerialString(qso);
        String satelliteName = qso.getRecord().getSatName();
        return String.format("%s %s", date, satelliteName);
    }

    public static String getSatelliteMarkerName(Qso qso) {
        LocalTime time = qso.getRecord().getTimeOn();
        return timeWithSecondsFormatter.format(time);
    }

}
