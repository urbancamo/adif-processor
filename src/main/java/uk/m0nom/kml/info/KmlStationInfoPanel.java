package uk.m0nom.kml.info;

import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.adif3.contacts.Station;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.coords.GlobalCoordinatesWithSourceAccuracy;
import uk.m0nom.coords.LocationInfo;
import uk.m0nom.qrz.QrzCallsign;

import java.util.ArrayList;
import java.util.List;

public class KmlStationInfoPanel {
    public String getPanelContentForStation(TransformControl control, Station station) {
        String callSign = station.getCallsign();

        final Context context = new Context();
        QrzCallsign qrzInfo = station.getQrzInfo();
        if (qrzInfo != null) {
            context.setVariable("call", station.getQrzInfo().getCall());
            if (qrzInfo.getImage() != null) {
                context.setVariable("image", station.getQrzInfo().getImage());
            }
        } else {
            context.setVariable("call", callSign);
        }

        for (ActivityType activityType : ActivityType.values()) {
            if (station.isDoing(activityType)) {
                context.setVariable(activityType.getActivityName().toLowerCase(), station.getActivity(activityType));
            }
        }

        if (qrzInfo != null) {
            context.setVariable("name", String.format("%s %s",
                    StringUtils.defaultIfBlank(qrzInfo.getFname(), ""),
                    StringUtils.defaultIfBlank(qrzInfo.getName(), "")));
        }

        String grid = station.getGrid();
        if (grid == null && qrzInfo != null) {
            grid = qrzInfo.getGrid();
        }
        if (grid != null) {
            context.setVariable("grid", grid);
        }

        GlobalCoordinatesWithSourceAccuracy coordinates = station.getCoordinates();
        if (coordinates == null && qrzInfo != null) {
            coordinates = new GlobalCoordinatesWithSourceAccuracy(qrzInfo.getLat(), qrzInfo.getLon());
        }
        if (coordinates != null) {
            context.setVariable("lat", String.format("%.3f", coordinates.getLatitude()));
            context.setVariable("long", String.format("%.3f", coordinates.getLongitude()));

            LocationInfo info = coordinates.getLocationInfo();

            context.setVariable("locationSource", info.getSource().getDescription());
            context.setVariable("locationAccuracy", info.getAccuracy().getDescription());
        }

        return control.getTemplateEngine().process("KmlStationInfo", context);
    }
}
