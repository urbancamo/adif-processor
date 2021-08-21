package uk.m0nom.kml.info;

import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.activity.Activity;

public class KmlActivityInfoPanel {
    private KmlInfoMap infoMap;

    public KmlActivityInfoPanel() {
        infoMap = new KmlInfoMap();
    }

    public String getPanelContentForActivity(Activity activity) {
        StringBuilder sb = new StringBuilder();

        sb.append("<div style=\"width: 340px; height: 480px\">");
        sb.append(infoMap.get(activity.getType()).getInfo(activity));

        GlobalCoordinates coordinates = activity.getCoords();
        if (coordinates != null) {
            sb.append(String.format("Lat: %.3f, Long: %.3f<br/>", coordinates.getLatitude(), coordinates.getLongitude()));
        }
        sb.append("</div>");
        return sb.toString();
    }
}
