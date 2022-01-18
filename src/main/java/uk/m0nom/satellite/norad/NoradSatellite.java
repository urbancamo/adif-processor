package uk.m0nom.satellite.norad;

import com.github.amsacode.predict4java.GroundStationPosition;
import com.github.amsacode.predict4java.SatPos;
import com.github.amsacode.predict4java.Satellite;
import lombok.Getter;
import lombok.Setter;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.coords.GlobalCoordinatesWithSourceAccuracy;
import uk.m0nom.coords.LocationAccuracy;
import uk.m0nom.coords.LocationSource;
import uk.m0nom.satellite.ApSatellite;

import java.sql.Date;
import java.time.*;

@Getter
@Setter
public class NoradSatellite implements ApSatellite {
    private Satellite satellite;

    public NoradSatellite(Satellite satellite) {
        this.satellite = satellite;
    }

    @Override
    public String getName() {
        return satellite.getTLE().getName();
    }

    @Override
    public GlobalCoordinatesWithSourceAccuracy getPosition(GlobalCoordinatesWithSourceAccuracy coords, LocalDate date, LocalTime time) {
        GroundStationPosition groundStationPosition = new GroundStationPosition(coords.getLatitude(), coords.getLongitude(), coords.getAltitude());
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        ZonedDateTime utcDateTime = dateTime.atZone(ZoneId.of("UTC"));
        Date utcDate = new Date(utcDateTime.toInstant().toEpochMilli());
        SatPos satPos = satellite.getPosition(groundStationPosition, utcDate);

        double latitude = satPos.getLatitude() / (Math.PI * 2.0) * 360;
        double longitude = satPos.getLongitude() / (Math.PI * 2.0) * 360;
        double altitudeInMetres = satPos.getAltitude() * 1000.0;
        GlobalCoordinatesWithSourceAccuracy position =
                new GlobalCoordinatesWithSourceAccuracy(latitude,
                        longitude, altitudeInMetres,
                        LocationSource.SATELLITE, LocationAccuracy.LAT_LONG);
        return position;

    }

    @Override
    public void updateAdifRec(TransformControl control, Adif3Record rec) {
    }
}
