package uk.m0nom.adifproc.kml.info;

import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.adif3.contacts.Station;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.coords.LocationInfo;
import uk.m0nom.adifproc.dxcc.Country;
import uk.m0nom.adifproc.dxcc.DxccEntity;
import uk.m0nom.adifproc.qrz.QrzCallsign;

import java.util.Collection;
import java.util.stream.Collectors;

public class KmlStationInfoPanel {
    public String getPanelContentForStation(TransformControl control, Station station) {
        final Context context = new Context();

        String callSign = station.getCallsign();
        context.setVariable("call", callSign);

        QrzCallsign qrzInfo = station.getQrzInfo();
        if (qrzInfo != null) {
            setVariable(context, "callForQrz", station.getQrzInfo().getCall());
            if (qrzInfo.getImage() != null) {
                context.setVariable("image", station.getQrzInfo().getImage());
            }
        } else {
            context.setVariable("callForQrz", callSign);
        }

        for (ActivityType activityType : ActivityType.values()) {
            if (station.isDoing(activityType)) {
                Collection<Activity> activitiesOfType = station.getActivity(activityType);
                // TODO only displaying first activity
                Activity activity = activitiesOfType.iterator().next();
                context.setVariable(activityType.getActivityName().toLowerCase(), activity);
            }
        }

        if (qrzInfo != null) {
            setVariable(context, "name", String.format("%s %s",
                    StringUtils.defaultIfBlank(qrzInfo.getFname(), ""),
                    StringUtils.defaultIfBlank(qrzInfo.getName(), "")));
        }

        if (station.getDxccEntity() != null) {
            DxccEntity dxcc = station.getDxccEntity();
            Country country = control.getCountries().getCountry(dxcc.getCountryCode());
            String name = dxcc.getName();
            if (country != null) {
                name = country.getName();
            }
            setVariable(context, "country", name);
            if ("United states".equalsIgnoreCase(name) &&
                    qrzInfo != null &&
                    qrzInfo.getCountry() != null &&
                    name.equalsIgnoreCase(qrzInfo.getCountry())) {
                setVariable(context, "state", qrzInfo.getState());
            }
            setVariable(context, "dxcc", dxcc.getName());
            setVariable(context, "flag", dxcc.getFlag());
            setVariable(context, "ituZone", formatIntList(dxcc.getItu()));
            setVariable(context, "cqZone", formatIntList(dxcc.getCq()));
        }

        if (station.getCoordinates() != null && station.getCoordinates().getAltitude() > 0.0) {
            setVariable(context, "altitude", String.format("%.0f m", station.getCoordinates().getAltitude()));
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

    private String formatIntList(Collection<Integer> intList) {
        return intList.stream().map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    private void setVariable(Context context, String key, String value) {
        if (StringUtils.isNotEmpty(value)) {
            context.setVariable(key, value);
        }
    }
}
