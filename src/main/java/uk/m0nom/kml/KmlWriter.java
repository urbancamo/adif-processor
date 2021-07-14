package uk.m0nom.kml;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.apache.commons.io.FilenameUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.contacts.Qsos;
import uk.m0nom.propagation.Ionosphere;
import uk.m0nom.activity.sota.SotaSummitInfo;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.kml.info.KmlContactInfoPanel;
import uk.m0nom.kml.info.KmlIcon;
import uk.m0nom.kml.info.KmlStationInfoPanel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

public class KmlWriter {
    private final static double DEFAULT_RANGE_METRES = 3000.0;
    private static final Logger logger = Logger.getLogger(KmlWriter.class.getName());
    private ActivityDatabases activities;
    private Ionosphere ionosphere;
    private TransformControl control;
    private KmlBandLineStyles bandLineStyles;

    public KmlWriter(TransformControl control) {
        this.control = control;
        this.ionosphere = new Ionosphere();
        bandLineStyles = new KmlBandLineStyles(control.getKmlContactWidth(), control.getKmlContactTransparency());
    }

    public void write(String pathname, String name, ActivityDatabases activities, Qsos qsos) {
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
                addMyStationToMap(doc, folder, qso);
                first = false;
            }
            GlobalCoordinates coords = qso.getRecord().getCoordinates();
            if (coords != null) {
                createStationMarker(doc, folder, qso);
                createCommsLink(doc, folder, qso);
            } else {
                logger.warning(String.format("Cannot determine communication link, no location data for: %s", qso.getTo().getCallsign()));
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

    private void addMyStationToMap(Document doc, Folder folder, Qso qso) {
        GlobalCoordinates coords = qso.getRecord().getMyCoordinates();
        if (coords != null) {
            createMyStationMarker(doc, folder, qso);
        }
   }

    private void createMyStationMarker(Document document, Folder folder, Qso qso) {
        GlobalCoordinates coords = qso.getRecord().getMyCoordinates();
        double longitude = coords.getLongitude();
        double latitude = coords.getLatitude();
        String station = qso.getFrom().getCallsign();

        Icon icon = new Icon().withHref(new KmlIcon().getIconFromStation(control, qso.getFrom()));
        Style style = document.createAndAddStyle();
        style.withId("style_" + station) // set the stylename to use this style from the placemark
                .createAndSetIconStyle().withScale(1.0).withIcon(icon); // set size and icon
        style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1.0); // set color and size of the continent name

        Placemark placemark = folder.createAndAddPlacemark();
        String htmlPanelContent = new KmlStationInfoPanel().getPanelContentForStation(qso.getFrom());
        // use the style for each continent
        placemark.withName(station)
                .withStyleUrl("#style_" + station)
                // 3D chart image
                .withDescription(htmlPanelContent)
                // coordinates and distance (zoom level) of the viewer
                .createAndSetLookAt().withLongitude(longitude).withLatitude(latitude).withAltitude(0).withRange(DEFAULT_RANGE_METRES);

        placemark.createAndSetPoint().addToCoordinates(longitude, latitude); // set coordinates
    }


    private void createStationMarker(Document document, Folder folder, Qso qso) {
        Adif3Record rec = qso.getRecord();
        GlobalCoordinates myCoords = rec.getMyCoordinates();
        double myLatitude = myCoords.getLatitude();
        double myLongitude = myCoords.getLongitude();

        GlobalCoordinates coords = rec.getCoordinates();
        double longitude = coords.getLongitude();
        double latitude = coords.getLatitude();
        String station = rec.getCall();

        Icon icon = new Icon()
                .withHref(new KmlIcon().getIconFromStation(control, qso.getTo()));

        Style style = document.createAndAddStyle();
        style.withId("style_" + station) // set the stylename to use this style from the placemark
                .createAndSetIconStyle().withScale(1.0).withIcon(icon); // set size and icon
        style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1.0); // set color and size of the station marker
        style.createAndSetLineStyle().withColor("ffb343ff").withWidth(5);

        Placemark placemark = folder.createAndAddPlacemark();
        String htmlPanelContent = new KmlStationInfoPanel().getPanelContentForStation(qso.getTo());
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

    private void createCommsLink(Document document, Folder folder, Qso qso) {
        Adif3Record rec = qso.getRecord();

        GlobalCoordinates myCoords = rec.getMyCoordinates();
        Double myLatitude = myCoords.getLatitude();
        Double myLongitude = myCoords.getLongitude();

        GlobalCoordinates coords = rec.getCoordinates();
        Double longitude = coords.getLongitude();
        Double latitude = coords.getLatitude();
        String station = rec.getCall();

        Style style = document.createAndAddStyle();
        style.withId("style_line_to_" + station + "_path");

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
            style = document.createAndAddStyle();
            style.withId("style_line_to_" + station + "_shadow");
            style.createAndSetLineStyle().withColor("40000000").withWidth(3);
        }

        Placemark placemark = folder.createAndAddPlacemark();
        // use the style for each line type
        placemark.withName(station + "_comms_path")
                .withStyleUrl("#style_line_to_" + station + "_path");

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
        HfLineResult result = KmlGeodesicUtils.getHfLine(hfLine, myCoords, coords, ionosphere, rec.getFreq(), rec.getBand(), rec.getTimeOn(), myAltitude, theirAltitude);
        placemark.withDescription(new KmlContactInfoPanel().getPanelContentForCommsLink(qso, result));
        if (control.getKmlContactShadow()) {
            placemark = folder.createAndAddPlacemark();
            // use the style for each line type
            placemark.withName(station + "_comms_shadow")
                    .withStyleUrl("#style_line_to_" + station + "_shadow");

            hfLine = placemark.createAndSetLineString();
            KmlGeodesicUtils.getSurfaceLine(hfLine, myCoords, coords);
        }

    }


}
