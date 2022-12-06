package uk.m0nom.adifproc.kml.activity;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.adif3.contacts.Station;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.kml.KmlUtils;
import uk.m0nom.adifproc.kml.info.KmlActivityInfoPanel;
import uk.m0nom.adifproc.kml.station.KmlStationUtils;

import java.time.LocalDate;
import java.util.Locale;

public class KmlLocalActivities {
    public final static String DEFAULT_RADIUS_IN_KM = "5";

    public void addLocalActivities(TransformControl control, Document doc, Folder folder, Station to, ActivityDatabaseService activities) {
        LocalDate onDate = to.getQsos().get(0).getRecord().getQsoDate();
        // Determine activities within the pre-defined radius
        to.getActivities()
                .values()
                .forEach(activity -> activities.getDatabase(activity.getType())
                        .findActivitiesInRadius(activity, control.getKmlLocalActivationSitesRadius(), onDate)
                .forEach(localActivity -> addActivityMarker(control, doc, folder, localActivity)));
    }

    public void addActivityMarker(TransformControl control,  Document document, Folder folder, Activity activity) {
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
                    .withId(KmlUtils.getStyleId(id));

            // set the stylename to use this style from the placemark
            style.createAndSetIconStyle().withScale(1.0).withIcon(icon); // set size and icon
            style.createAndSetLabelStyle().withColor("ffffffff").withScale(0.75); // set color and size of the station marker
            style.createAndSetLineStyle().withColor("000000ff").withWidth(3);

            String htmlPanelContent = new KmlActivityInfoPanel().getPanelContentForActivity(control, activity);
            Placemark placemark = folder.createAndAddPlacemark();
            // use the style for each continent
            placemark.withName(id)
                    .withId(id)
                    .withDescription(htmlPanelContent)
                    .withStyleUrl(KmlUtils.getStyleUrl(id))
                    // coordinates and distance (zoom level) of the viewer
                    .createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(altitude).withRange(KmlStationUtils.DEFAULT_RANGE_METRES);
           placemark.createAndSetPoint().addToCoordinates(longitude, latitude); // set coordinates
        }
    }
}