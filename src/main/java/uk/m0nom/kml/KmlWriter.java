package uk.m0nom.kml;

import de.micromata.opengis.kml.v_2_2_0.*;
import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.contacts.Qsos;
import uk.m0nom.adif3.contacts.Station;
import uk.m0nom.activity.hema.HemaSummitInfo;
import uk.m0nom.ionosphere.Ionosphere;
import uk.m0nom.ionosphere.PropagationMode;
import uk.m0nom.activity.pota.PotaInfo;
import uk.m0nom.qrz.QrzCallsign;
import uk.m0nom.activity.sota.SotaSummitInfo;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.wota.WotaSummitInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.logging.Logger;

public class KmlWriter {
    private final static double DEFAULT_RANGE_METRES = 3000.0;
    private static final Logger logger = Logger.getLogger(KmlWriter.class.getName());
    private ActivityDatabase summits;
    private Ionosphere ionosphere;
    private TransformControl control;
    private KmlBandLineStyles bandLineStyles;

    public KmlWriter(TransformControl control) {
        this.control = control;
        this.ionosphere = new Ionosphere();
        bandLineStyles = new KmlBandLineStyles(control.getKmlContactWidth(), control.getKmlContactTransparency());
    }

    public void write(String pathname, ActivityDatabase summits, Qsos qsos) {
        this.summits = summits;

        final Kml kml = new Kml();
        Document doc = kml.createAndSetDocument().withName(pathname).withOpen(true);

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

        Icon icon = new Icon().withHref(getIconFromStation(qso.getFrom()));
        Style style = document.createAndAddStyle();
        style.withId("style_" + station) // set the stylename to use this style from the placemark
                .createAndSetIconStyle().withScale(1.0).withIcon(icon); // set size and icon
        style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1.0); // set color and size of the continent name

        Placemark placemark = folder.createAndAddPlacemark();
        String htmlPanelContent = getPanelContentForStation(qso.getFrom());
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
                .withHref(getIconFromStation(qso.getTo()));
        Style style = document.createAndAddStyle();
        style.withId("style_" + station) // set the stylename to use this style from the placemark
                .createAndSetIconStyle().withScale(1.0).withIcon(icon); // set size and icon
        style.createAndSetLabelStyle().withColor("ff43b3ff").withScale(1.0); // set color and size of the station marker
        style.createAndSetLineStyle().withColor("ffb343ff").withWidth(5);

        Placemark placemark = folder.createAndAddPlacemark();
        String htmlPanelContent = getPanelContentForStation(qso.getTo());
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

    private String getPanelContentForStation(Station station) {
        StringBuilder sb = new StringBuilder();
        String callsign = station.getCallsign();

        sb.append("<div style=\"width: 340px; height: 480px\">");
        QrzCallsign qrzInfo = station.getQrzInfo();
        if (qrzInfo != null) {
            if (qrzInfo.getImage() != null) {
                sb.append(String.format("<a href=\"https://qrz.com/db/%s\"><img src=\"%s\" width=\"300px\"/></a><br/>",
                        callsign, station.getQrzInfo().getImage()));
            } else {
                sb.append(String.format("<a href=\"https://qrz.com/db/%s\"><img src=\"%s\" width=\"300px\"/></a><br/>",
                        callsign, "http://i3.cpcache.com/product/178743690/ham_radio_operator_35_button.jpg?height=630&width=630&qv=90"));
            }
            sb.append(String.format("Callsign: <a href=\"https://qrz.com/db/%s\">%s</a><br/>",
                    callsign, callsign));
        } else {
            sb.append(String.format("Callsign: %s<br/>", callsign));
        }
        if (station.getSotaId() != null) {
            if (summits.getSota().get(station.getSotaId()) != null) {
                appendSotaInfo(station.getSotaId(), sb);
            } else {
                logger.warning(String.format("Suspicious SOTA reference: %s for %s", station.getSotaId(), station.getCallsign()));
            }
        }
        if (station.getHemaId() != null) {
            if (summits.getHema().get(station.getHemaId()) != null) {
                appendHemaInfo(station.getHemaId(), sb);
            } else {
                logger.warning(String.format("Suspicious HEMA reference: %s for %s", station.getHemaId(), station.getCallsign()));
            }
        }
        if (station.getWotaId() != null) {
            if (summits.getWota().get(station.getWotaId()) != null) {
                appendWotaInfo(station.getWotaId(), sb);
            } else {
                logger.warning(String.format("Suspicious WOTA reference: %s for %s", station.getWotaId(), station.getCallsign()));
            }
        }
        if (station.getPotaId() != null) {
            if (summits.getPota().get(station.getPotaId()) != null) {
                appendPotaInfo(station.getPotaId(), sb);
            } else {
                logger.warning(String.format("Suspicious Parks on the Air reference: %s for %s", station.getPotaId(), station.getCallsign()));
            }
        }

        if (qrzInfo != null) {
            sb.append(String.format("Name: %s %s<br/>", qrzInfo.getFname(), qrzInfo.getName()));
        }

        String grid = station.getGrid();
        if (grid == null && qrzInfo != null) {
            grid = qrzInfo.getGrid();
        }
        if (grid != null) {
            sb.append(String.format("Grid: %s<br/>", grid));
        }

        GlobalCoordinates coordinates = station.getCoordinates();
        if (coordinates == null && qrzInfo != null) {
            coordinates = new GlobalCoordinates(qrzInfo.getLat(), qrzInfo.getLon());
        }
        if (coordinates != null) {
            sb.append(String.format("Lat: %.3f, Long: %.3f<br/>", coordinates.getLatitude(), coordinates.getLongitude()));
        }
        sb.append("</div>");
        return sb.toString();
    }

    private String getPanelContentForCommsLink(Adif3Record rec, HfLineResult result) {
        StringBuilder sb=  new StringBuilder();
        sb.append("<b>Contact</b><br/><br/><br/>");
        sb.append(String.format("D: %s, T: %s<br/>", rec.getQsoDate().toString(), rec.getTimeOn().toString()));
        sb.append(String.format("<a href=\"https://qrz.com/db/%s\">%s</a> ⇋ ",
                rec.getStationCallsign(), rec.getStationCallsign()));
        sb.append(String.format("<a href=\"https://qrz.com/db/%s\">%s</a><br/>",
                rec.getCall(), rec.getCall()));
        sb.append(String.format("Band: %s<br/>", StringUtils.replace(rec.getBand().name(), "BAND_", "").toLowerCase(Locale.ROOT)));
        sb.append(String.format("Mode: %s<br/>", rec.getMode().toString()));
        if (rec.getFreq() != null) {
            sb.append(String.format("Freq: %.3f Mhz<br/>", rec.getFreq()));
        }
        if (rec.getTxPwr() != null) {
            sb.append(String.format("TX Pwr: %.1f Watts<br/>", rec.getTxPwr()));
        }
        sb.append(String.format("Gnd dist: %.0f km<br/>", result.getDistance()));
        if (result.getMode() == PropagationMode.SKY_WAVE) {
            sb.append(String.format("Sky dist: %.0f km<br/>", result.getSkyDistance()));
            sb.append(String.format("Bounces: %d<br/>", result.getBounces()));
            if (result.getAltitude() > 9999.99) {
                sb.append(String.format("Avg Alt: %.0f km<br/>", result.getAltitude() / 1000));
            } else {
                sb.append(String.format("Avg Alt: %.0f metres<br/>", result.getAltitude()));
            }
            sb.append(String.format("Avg Angle: %.0f°<br/>", result.getAngle()));
        }
        sb.append(String.format("Propagation Mode: %s", result.getMode().toString()));

        return sb.toString();
    }

    private void appendSotaInfo(String summitRef, StringBuilder sb) {
        SotaSummitInfo summitInfo = summits.getSota().get(summitRef);
        sb.append(String.format("SOTA: <a href=\"https://summits.sota.org.uk/summit/%s\">%s</a>, ", summitRef, summitRef));
        sb.append(String.format("%s, ", summitInfo.getName()));
        sb.append(String.format("%.0f metres, %d points<br/>", summitInfo.getAltitude(), summitInfo.getPoints()));
    }

    private void appendWotaInfo(String summitRef, StringBuilder sb) {
        String lookupRef = summitRef.toUpperCase();
        WotaSummitInfo summitInfo = summits.getWota().get(lookupRef);
        if (StringUtils.equals(summitInfo.getBook(), "OF")) {
            // need to compensate for LDO weird numbering
            lookupRef = String.format("LDO-%03d", summitInfo.getInternalId());
        }
        sb.append(String.format("WOTA: <a href=\"https://wota.org.uk/MM_%s\">%s</a>, ", lookupRef, summitRef.toUpperCase()));
        sb.append(String.format("%s<br/>", summitInfo.getName()));
    }

    private void appendHemaInfo(String summitRef, StringBuilder sb) {
        HemaSummitInfo summitInfo = summits.getHema().get(summitRef);
        sb.append(String.format("HEMA: <a href=\"http://hema.org.uk/fullSummit.jsp?summitKey=%d\">%s</a>, ",
                summitInfo.getKey(), summitRef));
        sb.append(String.format("%s<br/>", summitInfo.getName()));
    }

    private void appendPotaInfo(String parkRef, StringBuilder sb) {
        PotaInfo parkInfo = summits.getPota().get(parkRef);
        sb.append(String.format("POTA: <a href=\"https://pota.app/#/park/%s\">%s</a><br/>",parkRef, parkRef));
        sb.append(String.format("%s<br/>", parkInfo.getName()));
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

        if (control.getKmlS2s() && qso.isS2S()) {
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
            SotaSummitInfo summitInfo = summits.getSota().get(qso.getRecord().getMySotaRef().getValue());
            if (summitInfo != null) {
                myAltitude = summitInfo.getAltitude();
            }
        }
        if (qso.getRecord().getSotaRef() != null) {
            SotaSummitInfo summitInfo = summits.getSota().get(qso.getRecord().getSotaRef().getValue());
            if (summitInfo != null) {
                theirAltitude = summitInfo.getAltitude();
            }
        }
        HfLineResult result = KmlGeodesicUtils.getHfLine(hfLine, myCoords, coords, ionosphere, rec.getFreq(), rec.getBand(), rec.getTimeOn(), myAltitude, theirAltitude);
        placemark.withDescription(getPanelContentForCommsLink(rec, result));
        if (control.getKmlContactShadow()) {
            placemark = folder.createAndAddPlacemark();
            // use the style for each line type
            placemark.withName(station + "_comms_shadow")
                    .withStyleUrl("#style_line_to_" + station + "_shadow");

            hfLine = placemark.createAndSetLineString();
            KmlGeodesicUtils.getSurfaceLine(hfLine, myCoords, coords);
        }

    }

    private String getIconFromStation(Station station) {
        String cs = station.getCallsign();

        String icon = control.getKmlFixedIconUrl();

        // SOTA icon overrides WOTA, so is above it in this list
        if (station.isSota()) {
            return control.getKmlSotaIconUrl();
        }
        if (station.isPota()) {
            return control.getKmlParkIconUrl();
        }
        // HEMA icon overrides WOTA, so is above it in this list
        if (station.isHema()) {
            return control.getKmlHemaIconUrl();
        }
        if (station.isWota()) {
            return control.getKmlWotaIconUrl();
        }
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
