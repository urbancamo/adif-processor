package uk.m0nom.adifproc.kml.info;

import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.adif3.contacts.Station;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.coords.LocationInfo;
import uk.m0nom.adifproc.dxcc.DxccEntity;
import uk.m0nom.adifproc.qrz.QrzCallsign;

public class KmlStationInfoPanel {
    public String getPanelContentForStation(TransformControl control, Station station) {
        String callSign = station.getCallsign();

        final Context context = new Context();
        QrzCallsign qrzInfo = station.getQrzInfo();
        if (qrzInfo != null) {
            setVariable(context,"call", station.getQrzInfo().getCall());
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
            setVariable(context,"name", String.format("%s %s",
                    StringUtils.defaultIfBlank(qrzInfo.getFname(), ""),
                    StringUtils.defaultIfBlank(qrzInfo.getName(), "")));
            setVariable(context, "country", qrzInfo.getCountry());

            if (StringUtils.isNotEmpty(qrzInfo.getDxcc())) {
                setVariable(context, "dxcc", qrzInfo.getDxcc());
                int dxccCode = Integer.parseInt(qrzInfo.getDxcc());
                DxccEntity dxcc = control.getDxccEntities().getEntity(dxccCode);
                setVariable(context, "flag", dxcc.getFlag());
            }
            setVariable(context, "ituZone", qrzInfo.getItuzone());
            setVariable(context, "cqZone", qrzInfo.getCqzone());
        }

        if (station.getCoordinates().getAltitude() > 0.0) {
            setVariable(context,"altitude", String.format("%.0f m", station.getCoordinates().getAltitude()));
        }

        String grid = station.getGrid();
        if (grid == null && qrzInfo != null) {
            grid = qrzInfo.getGrid();
        }
        setVariable(context, "grid", grid);

        GlobalCoords3D coordinates = station.getCoordinates();
        if (coordinates == null && qrzInfo != null) {
            coordinates = new GlobalCoords3D(qrzInfo.getLat(), qrzInfo.getLon());
        }
        if (coordinates != null) {
            context.setVariable("lat", String.format("%.3f", coordinates.getLatitude()));
            context.setVariable("long", String.format("%.3f", coordinates.getLongitude()));

            LocationInfo info = coordinates.getLocationInfo();

            context.setVariable("locationSource", info.getSource().getDescription());
            context.setVariable("locationAccuracy", info.getAccuracy().getDescription());
        }

        String html = control.getTemplateEngine().process(new TemplateSpec("KmlStationInfo", TemplateMode.XML), context);
        return html.replace("\n", "");
    }

    private void setVariable(Context context, String key, String value) {
        if (StringUtils.isNotEmpty(value)) {
            context.setVariable(key, value);
        }
    }
}
