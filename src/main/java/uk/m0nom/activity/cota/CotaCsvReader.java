package uk.m0nom.activity.cota;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.activity.*;
import uk.m0nom.coords.GlobalCoordinatesWithLocationSource;
import uk.m0nom.coords.LocationParsers;
import uk.m0nom.maidenheadlocator.MaidenheadLocatorConversion;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Getter
@Setter
public class CotaCsvReader extends ActivityReader {
    private static final Logger logger = Logger.getLogger(CotaCsvReader.class.getName());

    public CotaCsvReader(String sourceFile) {
        super(ActivityType.COTA, sourceFile);
     }


    public ActivityDatabase read(InputStream inputStream) throws IOException {
        Map<String, Activity> cotaInfo = new HashMap<>();

        final Reader reader = new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8);
        int line = 0;
        int foundLocationsCount = 0;
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            line++;
            //COTA WCA	CASTLES	PREFIX	NAME OF CASTLE	LOCATION	INFORMATION
            CotaInfo info = new CotaInfo();
            try {
                info.setRef(record.get("COTA WCA").trim());
                info.setNoCastles(record.get("CASTLES").trim());
                info.setPrefix(record.get("PREFIX").trim());
                info.setName(record.get("NAME OF CASTLE").trim());
                info.setLocation(record.get("LOCATION").trim());
                info.setInformation(record.get("INFORMATION").trim());
            } catch (IllegalArgumentException e) {
                logger.severe(String.format("Error reading line %d: %s", line, e.getMessage()));
            }
            cotaInfo.put(info.getRef(), info);
        }
        logger.info(String.format("Loaded %d castles", line));

        return new ActivityDatabase(ActivityType.COTA, cotaInfo);
    }
}
