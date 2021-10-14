package uk.m0nom.kml.station;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.icons.IconResource;
import uk.m0nom.kml.info.KmlStationInfoPanel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import static uk.m0nom.kml.KmlUtils.*;

public class KmlStationUtils {
    public final static double DEFAULT_RANGE_METRES = 500.0;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm");
    private final Set<String> iconStyles = new HashSet<>();

    private final TransformControl control;

    public KmlStationUtils(TransformControl control) {
        this.control = control;
    }

    public String addMyStationToMap(Document doc, Folder folder, Qso qso) {
        return createMyStationMarker(doc, folder, qso);
    }

    public static String getQsoDateTimeAsString(Qso qso) {
        LocalDate date = qso.getRecord().getQsoDate();
        LocalTime time = qso.getRecord().getTimeOn();

        LocalDateTime contactDateTime = LocalDateTime.of(date, time);
        return formatter.format(contactDateTime);
    }


    public String createMyStationMarker(Document document, Folder folder, Qso qso) {
        // Create a folder for this information
        GlobalCoordinates coords = qso.getRecord().getMyCoordinates();
        if (qso.getFrom().getCoordinates() == null && coords == null) {
            return String.format("Cannot determine coordinates for station %s, please specify a location override", qso.getFrom().getCallsign());
        }
        double longitude = coords.getLongitude();
        double latitude = coords.getLatitude();
        String callsign = qso.getFrom().getCallsign();
        Folder myFolder = folder.createAndAddFolder().withName(callsign).withOpen(false);

        IconResource icon = IconResource.getIconFromStation(control, qso.getFrom());
        if (!iconStyles.contains(icon.getName())) {
            Icon kmlIcon = new Icon().withHref(icon.getUrl());
            Style style = document.createAndAddStyle()
                    .withId(getStyleId(icon.getName()));
            // set the stylename to use this style from the placemark
            style.createAndSetIconStyle().withScale(1.0).withIcon(kmlIcon); // set size and icon
            style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1.0); // set color and size of the continent name
            iconStyles.add(icon.getName());
        }

        Placemark placemark = myFolder.createAndAddPlacemark();
        String htmlPanelContent = new KmlStationInfoPanel().getPanelContent(qso.getFrom());
        // use the style for each continent
        placemark.withName(callsign)
                .withStyleUrl(getStyleUrl(icon.getName()))
                // 3D chart image
                .withDescription(htmlPanelContent)
                // coordinates and distance (zoom level) of the viewer
                .createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(0).withRange(DEFAULT_RANGE_METRES);

        placemark.createAndSetPoint().addToCoordinates(longitude, latitude); // set coordinates

        return null;
    }

    public String createStationMarker(TransformControl control, Document document, Folder folder, Qso qso) {
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

        IconResource icon = IconResource.getIconFromStation(control, qso.getTo());
        if (!iconStyles.contains(icon.getName())) {
            Icon kmlIcon = new Icon()
                    .withHref(icon.getUrl());

            Style style = document.createAndAddStyle()
                    .withId(getStyleId(icon.getName()));

            // set the stylename to use this style from the placemark
            style.createAndSetIconStyle().withScale(1.0).withIcon(kmlIcon); // set size and icon
            style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1.0); // set color and size of the station marker
            style.createAndSetLineStyle().withColor("ffb343ff").withWidth(5);
            iconStyles.add(icon.getName());
        }

        Placemark placemark = folder.createAndAddPlacemark();
        String htmlPanelContent = new KmlStationInfoPanel().getPanelContent(qso.getTo());
        // use the style for each continent
        placemark.withName(name)
                .withId(id)
                .withStyleUrl(getStyleUrl(icon.getName()))
                // 3D chart image
                .withDescription(htmlPanelContent)
                // coordinates and distance (zoom level) of the viewer
                .createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(0).withRange(DEFAULT_RANGE_METRES);

        placemark.createAndSetLineString().addToCoordinates(myLongitude, myLatitude).addToCoordinates(longitude, latitude).setExtrude(true);
        placemark.createAndSetPoint().addToCoordinates(longitude, latitude); // set coordinates

        if (control.isKmlShowStationSubLabel()) {
            icon = IconResource.getIconFromMode(control, qso.getRecord().getMode());
            String modeId = qso.getRecord().getMode().name();
            if (icon != null) {
                if (!iconStyles.contains(modeId)) {
                    Icon modeIcon = new Icon().withHref(icon.getUrl());
                    Style modeStyle = document.createAndAddStyle()
                            .withId(getModeStyleId(modeId));
                    modeStyle.createAndSetIconStyle()
                            .withScale(1.0)
                            .withIcon(modeIcon);
                    modeStyle.createAndSetLabelStyle().withColor("ff43b3ff").withScale(0.75); // set color and size of the station marker
                    modeStyle.createAndSetLineStyle().withColor("ffb343ff").withWidth(3);
                    iconStyles.add(modeId);
                }
                Placemark modePlaceMark = folder.createAndAddPlacemark();
                modePlaceMark.withId(getModeId(id))
                        .withName(getModeLabel(qso))
                        .withStyleUrl(getModeStyleUrl(modeId));
                modePlaceMark.createAndSetPoint().addToCoordinates(longitude, latitude); // set coordinates
            }
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
