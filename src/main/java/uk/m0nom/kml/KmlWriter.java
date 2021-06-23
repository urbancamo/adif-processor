package uk.m0nom.kml;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.FileTransformerApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.logging.Logger;

public class KmlWriter {
    private static final Logger logger = Logger.getLogger(KmlWriter.class.getName());

    public void write(String pathname, Adif3 log) {
        final Kml kml = new Kml();
        Document doc = kml.createAndSetDocument().withName(pathname).withOpen(true);

        // create a Folder
        Folder folder = doc.createAndAddFolder();
        folder.withName("Contacts").withOpen(true);

        // create Placemark elements
        boolean first = true;
        for (Adif3Record rec : log.getRecords()) {
            if (first) {
                addMyStationToMap(doc, folder, rec);
                first = false;
            }
            GlobalCoordinates coords = rec.getCoordinates();
            if (coords != null) {
                Double longitude = coords.getLongitude();
                Double latitude = coords.getLatitude();
                if (longitude != null && latitude != null) {
                    createStationMarker(doc, folder, rec);
                    createCommsLink(doc, folder, rec);
                }
            }
        }

        // print and save
        try {
            logger.info(String.format("Writing KML to: %s", pathname));
            kml.marshal(new File(pathname));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addMyStationToMap(Document doc, Folder folder, Adif3Record rec) {
        GlobalCoordinates coords = rec.getMyCoordinates();
        if (coords != null) {
            Double longitude = coords.getLongitude();
            Double latitude = coords.getLatitude();
            if (longitude != null && latitude != null) {
                createMyStationMarker(doc, folder, rec);
            }
        }
   }

    private void createMyStationMarker(Document document, Folder folder, Adif3Record rec) {
        GlobalCoordinates coords = rec.getMyCoordinates();
        Double longitude = coords.getLongitude();
        Double latitude = coords.getLatitude();
        String station = rec.getStationCallsign();

        Icon icon = new Icon()
                .withHref(getMyIconFromRec(rec));
        Style style = document.createAndAddStyle();
        style.withId("style_" + station) // set the stylename to use this style from the placemark
                .createAndSetIconStyle().withScale(1.0).withIcon(icon); // set size and icon
        style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1.0); // set color and size of the continent name

        Placemark placemark = folder.createAndAddPlacemark();
        // use the style for each continent
        placemark.withName(station)
                .withStyleUrl("#style_" + station)
                // 3D chart imgae
                .withDescription(
                        String.format("https://qrz.com/db/%s", station))
                // coordinates and distance (zoom level) of the viewer
                .createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(0).withRange(12000000);

        placemark.createAndSetPoint().addToCoordinates(longitude, latitude); // set coordinates
    }

    private void createStationMarker(Document document, Folder folder, Adif3Record rec) {
        GlobalCoordinates myCoords = rec.getMyCoordinates();
        Double myLatitude = myCoords.getLatitude();
        Double myLongitude = myCoords.getLongitude();

        GlobalCoordinates coords = rec.getCoordinates();
        Double longitude = coords.getLongitude();
        Double latitude = coords.getLatitude();
        String station = rec.getCall();

        Icon icon = new Icon()
                .withHref(getIconFromRec(rec));
        Style style = document.createAndAddStyle();
        style.withId("style_" + station) // set the stylename to use this style from the placemark
                .createAndSetIconStyle().withScale(1.0).withIcon(icon); // set size and icon
        style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1.0); // set color and size of the continent name
        style.createAndSetLineStyle().withColor("ffb343ff").withWidth(5);

        Placemark placemark = folder.createAndAddPlacemark();
        // use the style for each continent
        placemark.withName(station)
                .withStyleUrl("#style_" + station)
                // 3D chart imgae
                .withDescription(
                        String.format("https://qrz.com/db/%s", station))
                // coordinates and distance (zoom level) of the viewer
                .createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(0).withRange(12000000);

        placemark.createAndSetLineString().addToCoordinates(myLongitude, myLatitude).addToCoordinates(longitude, latitude).setExtrude(true);
        placemark.createAndSetPoint().addToCoordinates(longitude, latitude); // set coordinates
    }

    private void createCommsLink(Document document, Folder folder, Adif3Record rec) {
        GlobalCoordinates myCoords = rec.getMyCoordinates();
        Double myLatitude = myCoords.getLatitude();
        Double myLongitude = myCoords.getLongitude();

        GlobalCoordinates coords = rec.getCoordinates();
        Double longitude = coords.getLongitude();
        Double latitude = coords.getLatitude();
        String station = rec.getCall();

        Style style = document.createAndAddStyle();
        style.withId("style_line_to_" + station);
        style.createAndSetLineStyle().withColor("c0c0c000").withWidth(3);

        Placemark placemark = folder.createAndAddPlacemark();
        // use the style for each continent
        placemark.withName(station + "_comms")
                .withStyleUrl("#style_line_to_" + station);
        LineString commsLine = placemark.createAndSetLineString();
        commsLine.addToCoordinates(myLongitude, myLatitude, 8);
        commsLine.addToCoordinates(longitude, latitude, 8);
        commsLine.setAltitudeMode(AltitudeMode.CLAMP_TO_GROUND);
        commsLine.setExtrude(true);
    }

    private String getMyIconFromRec(Adif3Record rec) {
        String icon = "http://maps.google.com/mapfiles/kml/shapes/ranger_station.png";
        String cs = rec.getStationCallsign().toUpperCase();
        if (cs.endsWith("/P")) {
            return "http://maps.google.com/mapfiles/kml/shapes/hiker.png";
        }
        if (cs.endsWith("/M")) {
            return "http://maps.google.com/mapfiles/kml/shapes/cabs.png";
        }
        return icon;
    }

    private String getIconFromRec(Adif3Record rec) {
        String icon = "http://maps.google.com/mapfiles/kml/shapes/ranger_station.png";
        String cs = rec.getCall().toUpperCase();
        if (cs.endsWith("/P")) {
            return "http://maps.google.com/mapfiles/kml/shapes/hiker.png";
        }
        if (cs.endsWith("/M")) {
            return "http://maps.google.com/mapfiles/kml/shapes/cabs.png";
        }
        return icon;
    }
}
