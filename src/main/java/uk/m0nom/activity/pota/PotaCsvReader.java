package uk.m0nom.activity.pota;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
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

public class PotaCsvReader extends ActivityReader {
    private static final Logger logger = Logger.getLogger(PotaCsvReader.class.getName());

    public PotaCsvReader() {
        super(ActivityType.POTA);
    }

    public PotaDatabase read(InputStream inputStream) throws IOException {
        Map<String, PotaInfo> potaInfo = new HashMap<>();

        final Reader reader = new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8);

        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            PotaInfo info = new PotaInfo();
            info.ref = record.get("reference");
            info.name = record.get("name");
            info.active = StringUtils.equals(record.get("active"), "1");
            info.entityId = Integer.parseInt(record.get("entityId"));
            info.locationDesc = record.get("locationDesc");

            info.coords = readCoords(record, "latitude", "longitude");
            potaInfo.put(info.ref, info);
        }

        return new PotaDatabase(potaInfo);
    }
}
