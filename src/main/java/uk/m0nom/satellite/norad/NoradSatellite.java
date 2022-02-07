package uk.m0nom.satellite.norad;

import com.github.amsacode.predict4java.GroundStationPosition;
import com.github.amsacode.predict4java.SatPos;
import com.github.amsacode.predict4java.Satellite;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.coords.GlobalCoords3D;
import uk.m0nom.coords.LocationAccuracy;
import uk.m0nom.coords.LocationSource;
import uk.m0nom.satellite.ApSatellite;

import java.sql.Date;
import java.time.*;

/**
 * Definition for a satellite derived from a NASA TLE file entry.
 */
@Getter
@Setter
public class NoradSatellite implements ApSatellite {
    private Satellite satellite;
    private String name;
    private String designator;

    public NoradSatellite(Satellite satellite) {
        this.satellite = satellite;
        String identifier = satellite.getTLE().getName();
        // Name is either just a name, or a name and designator in brackets
        if (identifier.contains("(")) {
            name = StringUtils.substringBefore(identifier, "(");
            designator = StringUtils.substringAfter(identifier, "(");
            designator = StringUtils.substringBefore(designator, ")");
        } else {
            name = identifier;
            designator = "";
        }
    }

    @Override
    public String getIdentifier() {
        if (StringUtils.isNotBlank(getDesignator())) {
            return String.format("%s: %s", getDesignator(), getName());
        }
        return getName();
    }

    @Override
    public GlobalCoords3D getPosition(GlobalCoords3D coords, LocalDate date, LocalTime time) {
        GroundStationPosition groundStationPosition = new GroundStationPosition(coords.getLatitude(), coords.getLongitude(), coords.getAltitude());
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        ZonedDateTime utcDateTime = dateTime.atZone(ZoneId.of("UTC"));
        Date utcDate = new Date(utcDateTime.toInstant().toEpochMilli());
        SatPos satPos = satellite.getPosition(groundStationPosition, utcDate);

        double latitude = satPos.getLatitude() / (Math.PI * 2.0) * 360;
        double longitude = satPos.getLongitude() / (Math.PI * 2.0) * 360;
        double altitudeInMetres = satPos.getAltitude() * 1000.0;
        return new GlobalCoords3D(latitude,
                longitude, altitudeInMetres,
                LocationSource.SATELLITE, LocationAccuracy.LAT_LONG);

    }

    @Override
    public void updateAdifRec(TransformControl control, Adif3Record rec) {
    }
}
