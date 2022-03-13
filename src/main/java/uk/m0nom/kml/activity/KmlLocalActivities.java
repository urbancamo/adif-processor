package uk.m0nom.kml.activity;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.adif3.contacts.Station;
import uk.m0nom.kml.station.KmlStationUtils;

import java.util.Locale;

import static uk.m0nom.kml.KmlUtils.getStyleId;
import static uk.m0nom.kml.KmlUtils.getStyleUrl;

public class KmlLocalActivities {
    public final static String DEFAULT_RADIUS = "5000";

    public void addLocalActivities(Document doc, Folder folder, Station to, double radius, ActivityDatabases activities) {
        // Determine activities within the pre-defined radius
        to.getActivities().values().forEach(activity -> {
            activities.getDatabase(activity.getType()).findActivitiesInRadius(activity, radius)
                    .forEach(localActivity -> addActivityMarker(doc, folder, localActivity));
        });
    }

    public void addActivityMarker(Document document, Folder folder, Activity activity) {
        String id = activity.getRef();

        GlobalCoordinates coords = activity.getCoords();
        if (coords != null) {
            double longitude = coords.getLongitude();
            double latitude = coords.getLatitude();
            double altitude = 0.0;

            if (activity.hasAltitude()) {
                altitude = activity.getAltitude() + 100.0;
            }
            Icon icon = new Icon()
                    .withHref(String.format("http://maps.google.com/mapfiles/kml/paddle/%c.png", activity.getType().name().toUpperCase(Locale.ROOT).charAt(0)));

            Style style = document.createAndAddStyle()
                    .withId(getStyleId(id));

            // set the stylename to use this style from the placemark
            style.createAndSetIconStyle().withScale(1.0).withIcon(icon); // set size and icon
            style.createAndSetLabelStyle().withColor("ffffffff").withScale(0.75); // set color and size of the station marker
            style.createAndSetLineStyle().withColor("000000ff").withWidth(3);

            String htmlPanelContent = ""; //infoMap.get(activity.getType()).getInfo(activity);
            Placemark placemark = folder.createAndAddPlacemark();
            // use the style for each continent
            placemark.withName(id)
                    .withId(id)
                    .withDescription(htmlPanelContent)
                    .withStyleUrl(getStyleUrl(id))
                    // coordinates and distance (zoom level) of the viewer
                    .createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(altitude).withRange(KmlStationUtils.DEFAULT_RANGE_METRES);
           placemark.createAndSetPoint().addToCoordinates(longitude, latitude); // set coordinates
        }
    }
}