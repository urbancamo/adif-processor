package uk.m0nom.activity.pota;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityReader;
import uk.m0nom.activity.ActivityType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Reader for the Parks on the Air CSV extract
 */
public class PotaCsvReader extends ActivityReader {
    private static final Logger logger = Logger.getLogger(PotaCsvReader.class.getName());

    public PotaCsvReader(String sourceFile) {
        super(ActivityType.POTA, sourceFile);
    }

    public ActivityDatabase read(InputStream inputStream) throws IOException {
        Map<String, Activity> potaInfo = new HashMap<>();

        final Reader reader = new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8);

        int i = 1;
        try {
            Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader);
            for (CSVRecord record : records) {
                PotaInfo info = new PotaInfo();
                info.setRef(record.get("reference"));
                info.setName(record.get("name"));
                info.setActive(StringUtils.equals(record.get("active"), "1"));
                String entityId = record.get("entityId");
                if (StringUtils.isNotBlank(entityId)) {
                    info.setEntityId(Integer.parseInt(entityId));
                }
                info.setLocationDesc(record.get("locationDesc"));

                info.setCoords(readCoords(record, "latitude", "longitude"));
                info.setGrid(record.get("grid"));
                potaInfo.put(info.getRef(), info);
                i++;
            }
        } catch (NumberFormatException numberFormatException) {
            logger.severe(String.format("Problem with number in record %d: %s", i, numberFormatException.getMessage()));
        }
        return new ActivityDatabase(ActivityType.POTA, potaInfo);
    }
}
