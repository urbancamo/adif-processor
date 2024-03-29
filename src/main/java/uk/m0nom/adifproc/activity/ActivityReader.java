package uk.m0nom.adifproc.activity;

import lombok.Getter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.coords.LocationAccuracy;
import uk.m0nom.adifproc.coords.LocationSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Abstract class providing common methods for reading loading an activity and reading coordinate values
 */
@Getter
public abstract class ActivityReader {

    private static final Logger logger = Logger.getLogger(ActivityReader.class.getName());
    public ActivityType type;
    public String sourceFile;

    public ActivityReader(ActivityType type, String sourceFile) {
        this.type = type;
        this.sourceFile = sourceFile;
    }


    /**
     * Read lat/long from CSV file and convert to GlobalCoordinates. If neither is specified returns null
     * @param record CSVRecord to read
     * @param latColName name of Latitude column in CSV record
     * @param longColName name of Longitude column in CSV record
     * @return Global Coordinate for lat/long, or null if neither specified
     */
    protected GlobalCoords3D readCoords(CSVRecord record, String latColName, String longColName) {
        GlobalCoords3D location = null;

        String longitudeStr = record.get(latColName);

        String latitudeStr = record.get(longColName);
        if (!StringUtils.isEmpty(longitudeStr) && !StringUtils.isEmpty(latitudeStr)) {
            Double longitude = null;
            try {
                longitude = Double.parseDouble(longitudeStr);
            } catch (NumberFormatException e) {
                logger.severe(String.format("Error parsing longitude '%s' from CSV file for activity %s", longitudeStr, type.getActivityName()));
            }
            Double latitude = null;
            try {
                latitude = Double.parseDouble(latitudeStr);
            } catch (NumberFormatException ignored) {


            }
            if ((latitude != null && longitude != null) && (latitude != 0 && longitude != 0)) {
                location = new GlobalCoords3D(longitude, latitude, LocationSource.ACTIVITY, LocationAccuracy.LAT_LONG);
            }
        }
        return location;
    }

    public abstract ActivityDatabase read(InputStream inputStream) throws IOException;
}
