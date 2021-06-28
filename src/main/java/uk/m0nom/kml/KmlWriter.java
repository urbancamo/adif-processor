package uk.m0nom.kml;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.ionosphere.Ionosphere;
import uk.m0nom.sota.SotaSummitInfo;
import uk.m0nom.summits.SummitsDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.logging.Logger;

public class KmlWriter {
    private final static double DEFAULT_RANGE_METRES = 3000.0;
    private static final Logger logger = Logger.getLogger(KmlWriter.class.getName());
    private SummitsDatabase summits;
    private Ionosphere ionosphere;
    private TransformControl control;

    public KmlWriter(TransformControl control) {
        this.control = control;
        this.ionosphere = new Ionosphere();
    }

    public void write(String pathname, Adif3 log, SummitsDatabase summits) {
        this.summits = summits;

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
            } else {
                logger.warning(String.format("Could not determine location for: %s", rec.getCall()));
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
        String htmlPanelContent = createMyPanelContent(rec);
        // use the style for each continent
        placemark.withName(station)
                .withStyleUrl("#style_" + station)
                // 3D chart image
                .withDescription(htmlPanelContent)
                // coordinates and distance (zoom level) of the viewer
                .createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(0).withRange(DEFAULT_RANGE_METRES);

        placemark.createAndSetPoint().addToCoordinates(longitude, latitude); // set coordinates
    }

    private String createMyPanelContent(Adif3Record rec) {
        StringBuilder sb = new StringBuilder();
        String station = rec.getStationCallsign();
        sb.append(String.format("<a href=\"https://qrz.com/db/%s\">%s</a><br/>", station, station));
        if (rec.getMySotaRef() != null) {
            appendSotaInfo(rec.getMySotaRef().getValue(), sb);
        }
        return sb.toString();
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
        style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1.0); // set color and size of the station marker
        style.createAndSetLineStyle().withColor("ffb343ff").withWidth(5);

        Placemark placemark = folder.createAndAddPlacemark();
        String htmlPanelContent = getPanelContentForStation(rec);
        // use the style for each continent
        placemark.withName(station)
                .withStyleUrl("#style_" + station)
                // 3D chart imgae
                .withDescription(htmlPanelContent)
                // coordinates and distance (zoom level) of the viewer
                .createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(0).withRange(DEFAULT_RANGE_METRES);

        placemark.createAndSetLineString().addToCoordinates(myLongitude, myLatitude).addToCoordinates(longitude, latitude).setExtrude(true);
        placemark.createAndSetPoint().addToCoordinates(longitude, latitude); // set coordinates
    }

    private String getPanelContentForStation(Adif3Record rec) {
        StringBuilder sb = new StringBuilder();
        String station = rec.getCall();
        sb.append(String.format("<a href=\"https://qrz.com/db/%s\">%s</a><br/>", station, station));
        if (rec.getSotaRef() != null) {
            appendSotaInfo(rec.getSotaRef().getValue(), sb);
        }
        sb.append(String.format("%s %.4f Mhz %s", rec.getTimeOn().toString(), rec.getFreq(), rec.getMode().toString()));
        return sb.toString();
    }

    private String getPanelContentForCommsLink(Adif3Record rec, HfLineResult result) {
        StringBuilder sb=  new StringBuilder();
        sb.append(String.format("<b>Contact</b><br/><br/><br/>"));
        sb.append(String.format("%s %s<br/>", rec.getQsoDate().toString(), rec.getTimeOn().toString()));
        sb.append(String.format("<a href=\"https://qrz.com/db/%s\">%s</a><br/>",
                rec.getStationCallsign(), rec.getStationCallsign()));
        sb.append(String.format("<a href=\"https://qrz.com/db/%s\">%s</a><br/>",
                rec.getCall(), rec.getCall()));
        sb.append(String.format("Band: %s<br/>", StringUtils.replace(rec.getBand().name(), "BAND_", "").toLowerCase(Locale.ROOT)));
        sb.append(String.format("Mode: %s<br/>", rec.getMode().toString()));
        if (rec.getFreq() != null) {
            sb.append(String.format("Freq: %.3f Mhz<br/>", rec.getFreq()));
        }
        sb.append(String.format("Gnd dist: %.0f km<br/>", result.getDistance()));
        sb.append(String.format("Sky dist: %.0f km<br/>", result.getSkyDistance()));
        sb.append(String.format("Bounces: %d<br/>", result.getBounces()));
        if (result.getAltitude() > 9999.99) {
            sb.append(String.format("Bounce Alt: %.0f km<br/>", result.getAltitude() / 1000));
        } else {
            sb.append(String.format("Bounce Alt: %.0f metres<br/>", result.getAltitude()));
        }
        sb.append(String.format("Propagation Mode: %s", result.getMode().toString()));

        return sb.toString();
    }

    private void appendSotaInfo(String summitRef, StringBuilder sb) {
        SotaSummitInfo summitInfo = summits.getSota().get(summitRef);
        sb.append(String.format("SOTA: <a href=\"https://summits.sota.org.uk/summit/%s\">%s</a><br/>", summitRef, summitRef));
        sb.append(String.format("%.0f metres, %d points<br/>", summitInfo.getAltitude(), summitInfo.getPoints()));
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
        style.withId("style_line_to_" + station + "_propagation");
        if (rec.getMySotaRef() != null && rec.getSotaRef() != null) {
            KmlLineStyle styling = KmlStyling.getKmlLineStyle(control.getKmlS2sContactLineStyle());
            style.createAndSetLineStyle().withColor(styling.getStringSpecifier()).withWidth(styling.getWidth());

        } else {
            KmlLineStyle styling = KmlStyling.getKmlLineStyle(control.getKmlContactLineStyle());
            style.createAndSetLineStyle().withColor(styling.getStringSpecifier()).withWidth(styling.getWidth());
        }

        if (control.getKmlContactShadow()) {
            style = document.createAndAddStyle();
            style.withId("style_line_to_" + station + "_surface");
            style.createAndSetLineStyle().withColor("40000000").withWidth(3);
        }

        Placemark placemark = folder.createAndAddPlacemark();
        // use the style for each line type
        placemark.withName(station + "_comms_propagation")
                .withStyleUrl("#style_line_to_" + station + "_propagation");

        LineString hfLine = placemark.createAndSetLineString();
        HfLineResult result = KmlGeodesicUtils.getHfLine(hfLine, myCoords, coords, ionosphere, rec.getFreq(), rec.getBand(), rec.getTimeOn());
        placemark.withDescription(getPanelContentForCommsLink(rec, result));
        if (control.getKmlContactShadow()) {
            placemark = folder.createAndAddPlacemark();
            // use the style for each line type
            placemark.withName(station + "_comms_surface")
                    .withStyleUrl("#style_line_to_" + station + "_surface");

            hfLine = placemark.createAndSetLineString();
            KmlGeodesicUtils.getSurfaceLine(hfLine, myCoords, coords);
        }

    }

    private String getMyIconFromRec(Adif3Record rec) {
        String cs = rec.getStationCallsign().toUpperCase();
        return getIconFromCallsign(cs);
    }

    private String getIconFromRec(Adif3Record rec) {
        String cs = rec.getCall().toUpperCase();
        return getIconFromCallsign(cs);
    }

    private String getIconFromCallsign(String cs) {
        String icon = control.getKmlFixedIconUrl();
        if (cs.endsWith("/P")) {
            return control.getKmlPortableIconUrl();
        }
        if (cs.endsWith("/M")) {
            return control.getKmlMobileIconUrl();
        }
        if (cs.endsWith("/MM")) {
            return control.getKmlMaritimeIconUrl();
        }
        return icon;
    }

}
