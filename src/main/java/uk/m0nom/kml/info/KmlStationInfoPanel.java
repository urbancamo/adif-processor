package uk.m0nom.kml.info;

import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.adif3.contacts.Station;
import uk.m0nom.qrz.QrzCallsign;

import java.util.HashMap;
import java.util.Map;

public class KmlStationInfoPanel {
    private Map<ActivityType, KmlStationActivityInfo> infoMap;


    public KmlStationInfoPanel() {
        infoMap = new HashMap<>();
        infoMap.put(ActivityType.SOTA, new KmlStationSotaInfo());
        infoMap.put(ActivityType.WOTA, new KmlStationWotaInfo());
        infoMap.put(ActivityType.POTA, new KmlStationPotaInfo());
        infoMap.put(ActivityType.HEMA, new KmlStationHemaInfo());
    }

    public String getPanelContentForStation(Station station) {
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

        if (station.isDoing(ActivityType.SOTA)) {
            sb.append(infoMap.get(ActivityType.SOTA).getInfo(station));
        }
        if (station.isDoing(ActivityType.HEMA)) {
            sb.append(infoMap.get(ActivityType.HEMA).getInfo(station));
        }
        if (station.isDoing(ActivityType.WOTA)) {
            sb.append(infoMap.get(ActivityType.WOTA).getInfo(station));
        }
        if (station.isDoing(ActivityType.POTA)) {
            sb.append(infoMap.get(ActivityType.POTA).getInfo(station));
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

}
