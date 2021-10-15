package uk.m0nom.activity.lota;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
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

@Getter
@Setter
public class LotaCsvReader extends ActivityReader {
    private static final Logger logger = Logger.getLogger(LotaCsvReader.class.getName());

    public LotaCsvReader(String sourceFile) {
        super(ActivityType.LOTA, sourceFile);
     }


    public ActivityDatabase read(InputStream inputStream) throws IOException {
        Map<String, Activity> lotaInfo = new HashMap<>();

        final Reader reader = new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8);
        int line = 0;
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            line++;
            LotaInfo info = new LotaInfo();
            try {
                info.setCountry(record.get("Country").trim());
                info.setName(record.get("Lighthouse Name").trim());
                info.setStatus(record.get("Status").trim());

                if (info.getName().contains("Deleted.")) {
                    info.setStatus("D");
                    info.setName(info.getName().replace("Deleted.", ""));
                }
                info.setDxcc(record.get("DXCC").trim());
                info.setContinent(record.get("Continent").trim());
                info.setLocation(record.get("Location").trim());
                info.setRef(record.get("ILLW").trim());
                info.setCoords(readCoords(record, "Latitude", "Longitude"));
            } catch (IllegalArgumentException e) {
                logger.severe(String.format("Error reading line %d: %s", line, e.getMessage()));
            }
            lotaInfo.put(info.getRef(), info);
        }
        logger.info(String.format("Loaded %d Light Houses", line));

        return new ActivityDatabase(getType(), lotaInfo);
    }
}
