package uk.m0nom.adifproc.satellite.norad;

import com.github.amsacode.predict4java.GroundStationPosition;
import com.github.amsacode.predict4java.SatPos;
import com.github.amsacode.predict4java.Satellite;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.coords.LocationAccuracy;
import uk.m0nom.adifproc.coords.LocationSource;
import uk.m0nom.adifproc.satellite.ApSatellite;

import java.sql.Date;
import java.time.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Definition for a satellite. Data is derived from a NASA TLE file entry.
 * Note that information is only accurate for the day of the pass, this class allows
 * multiple day entries to be associated with a satellite.
 */
@Getter
@Setter
public class NoradSatellite implements ApSatellite {
    private Map<LocalDate, Satellite> satelliteInfoForDate;
    private String name;
    private String designator;

    public NoradSatellite(Satellite satellite) {
        satelliteInfoForDate = new HashMap<>();
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

    public void addTleData(LocalDate date, Satellite satellite) {
        satelliteInfoForDate.put(date, satellite);
    }

    @Override
    public String getIdentifier() {
        if (StringUtils.isNotBlank(getDesignator())) {
            return String.format("%s: %s", getDesignator(), getName());
        }
        return getName();
    }

    private Satellite getSatelliteInfoForDate(LocalDate date) {
        Satellite satellite = satelliteInfoForDate.get(date);
        if (satellite == null) {
            satellite = satelliteInfoForDate.get(LocalDate.now());
        }
        return satellite;
    }

    @Override
    public GlobalCoords3D getPosition(GlobalCoords3D coords, LocalDate date, LocalTime time) {
        Satellite satellite = getSatelliteInfoForDate(date);

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