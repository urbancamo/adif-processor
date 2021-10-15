package uk.m0nom.kml.info;

import org.apache.commons.lang3.StringUtils;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.adif3.contacts.Station;
import uk.m0nom.coords.GlobalCoordinatesWithSourceAccuracy;
import uk.m0nom.coords.LocationInfo;
import uk.m0nom.qrz.QrzCallsign;

public class KmlStationInfoPanel implements KmlInfoPanel {
    private final KmlInfoMap infoMap;

    public KmlStationInfoPanel() {
        infoMap = new KmlInfoMap();
    }

    public String getPanelContent(Station station) {
        StringBuilder sb = new StringBuilder();
        String callsign = station.getCallsign();

        sb.append("<div style=\"width: 340px; height: 480px\">");
        QrzCallsign qrzInfo = station.getQrzInfo();
        if (qrzInfo != null) {
            if (qrzInfo.getImage() != null) {
                sb.append(String.format("<a href=\"https://qrz.com/db/%s\"><img src=\"%s\" width=\"300px\"/></a><br/>",
                        station.getQrzInfo().getCall(), station.getQrzInfo().getImage()));
            } else {
                sb.append(String.format("<a href=\"https://qrz.com/db/%s\"><img src=\"%s\" width=\"300px\"/></a><br/>",
                        station.getQrzInfo().getCall(), "http://i3.cpcache.com/product/178743690/ham_radio_operator_35_button.jpg?height=630&width=630&qv=90"));
            }
            sb.append(String.format("Callsign: <a href=\"https://qrz.com/db/%s\">%s</a><br/>",
                    station.getQrzInfo().getCall(), callsign));
        } else {
            sb.append(String.format("Callsign: %s<br/>", callsign));
        }

        for (ActivityType activityType : ActivityType.values()) {
            if (station.isDoing(activityType)) {
                sb.append(infoMap.get(activityType).getInfo(station.getActivity(activityType)));
            }
        }

        if (qrzInfo != null) {
            sb.append(String.format("Name: %s %s<br/>",
                    StringUtils.defaultIfBlank(qrzInfo.getFname(), ""),
                    StringUtils.defaultIfBlank(qrzInfo.getName(), "")));
        }

        String grid = station.getGrid();
        if (grid == null && qrzInfo != null) {
            grid = qrzInfo.getGrid();
        }
        if (grid != null) {
            sb.append(String.format("Grid: %s<br/>", grid));
        }

        GlobalCoordinatesWithSourceAccuracy coordinates = station.getCoordinates();
        if (coordinates == null && qrzInfo != null) {
            coordinates = new GlobalCoordinatesWithSourceAccuracy(qrzInfo.getLat(), qrzInfo.getLon());
        }
        if (coordinates != null) {
            sb.append(String.format("Lat: %.3f, Long: %.3f<br/>", coordinates.getLatitude(), coordinates.getLongitude()));
            LocationInfo info = coordinates.getLocationInfo();
            String sourceString = "";
            switch (info.getSource()) {
                case ACTIVITY:
                    sourceString = "Activity";
                    break;
                case OVERRIDE:
                    sourceString = "Overridden";
                    break;
                case QRZ:
                    sourceString = "QRZ.COM";
                    break;
                case GEOCODING:
                    sourceString = "Geocoding";
                    break;
                case UNDEFINED:
                    break;
            }
            String accuracyString = "";
            switch (info.getAccuracy()) {
                case LAT_LONG:
                    accuracyString = "Latitude/Longitude";
                    break;
                case MHL10:
                    accuracyString = "10-CHAR Maidenhead";
                    break;
                case MHL8:
                    accuracyString = "8-CHAR Maidenhead";
                    break;
                case MHL6:
                    accuracyString = "6-CHAR Maidenhead";
                    break;
                case MHL4:
                    accuracyString = "4-CHAR Maidenhead";
                    break;
                case GEOLOCATION_VERY_GOOD:
                    accuracyString = "Geolocation - Very Good";
                    break;
                case GEOLOCATION_GOOD:
                    accuracyString = "Geolocation - Good";
                    break;
                case GEOLOCATION_POOR:
                    accuracyString = "Geolocation - Poor";
                    break;
                case GEOLOCATION_VERY_POOR:
                    accuracyString = "Geolocation - Very Poor";
                    break;
            }
            sb.append(String.format("Location Source: %s<br/>", sourceString));
            sb.append(String.format("Location Accuracy: %s<br/>", accuracyString));
        }
        sb.append("</div>");
        return sb.toString();
    }
}
