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
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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
    private Map<ZonedDateTime, Satellite> satelliteTleDataForDate;
    private String name;
    private String designator;

    public static String getIdentifier(String name) {
        String identifier = name.trim();
        if (identifier.contains("(")) {
            identifier = StringUtils.substringBefore(identifier, "(").trim();
        }
        return identifier;
    }

    public NoradSatellite(ZonedDateTime date, Satellite satelliteTleData) {
        satelliteTleDataForDate = new HashMap<>();
        String identifier = satelliteTleData.getTLE().getName();
        // Name is either just a name, or a name and designator in brackets
        if (identifier.contains("(")) {
            name = StringUtils.substringBefore(identifier, "(").trim();
            designator = StringUtils.substringAfter(identifier, "(");
            designator = StringUtils.substringBefore(designator, ")");
        } else {
            name = identifier;
            designator = "";
        }
        satelliteTleDataForDate.put(date, satelliteTleData);
    }

    public void addTleData(ZonedDateTime date, Satellite satellite) {
        satelliteTleDataForDate.put(date.truncatedTo(ChronoUnit.DAYS), satellite);
    }

    @Override
    public String getIdentifier() {
        if (StringUtils.isNotBlank(getDesignator())) {
            return String.format("%s: %s", getDesignator(), getName());
        }
        return getName();
    }

    public Satellite getSatelliteTleDataForDate(ZonedDateTime date) {
        Satellite satellite = satelliteTleDataForDate.get(date.truncatedTo(ChronoUnit.DAYS));
        if (satellite == null) {
            satellite = satelliteTleDataForDate.get(ZonedDateTime.now());
        }
        return satellite;
    }

    @Override
    public GlobalCoords3D getPosition(GlobalCoords3D coords, ZonedDateTime dateTime) {
        Satellite satellite = getSatelliteTleDataForDate(dateTime);

        GroundStationPosition groundStationPosition = new GroundStationPosition(coords.getLatitude(), coords.getLongitude(), coords.getAltitude());
        Date utcDate = new Date(dateTime.toInstant().toEpochMilli());
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

    @Override
    public boolean isGeostationary() {
        return false;
    }
}
