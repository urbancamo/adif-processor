package uk.m0nom.pota;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.kml.KmlWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PotaCsvReader {
    private static final Logger logger = Logger.getLogger(PotaCsvReader.class.getName());

    public static PotaDatabase read(InputStream inputStream) throws IOException {
        Map<String, PotaInfo> potaInfo = new HashMap<>();

        final Reader reader = new InputStreamReader(new BOMInputStream(inputStream), "UTF-8");

        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            PotaInfo info = new PotaInfo();
            info.reference = record.get("reference");
            info.name = record.get("name");
            info.active = StringUtils.equals(record.get("active"), "1");
            info.entityId = Integer.parseInt(record.get("entityId"));
            info.locationDesc = record.get("locationDesc");

            String latitude = record.get("latitude");
            if (!StringUtils.isEmpty(latitude)) {
                info.latitude = Double.parseDouble(latitude);
            }
            String longitude = record.get("longitude");
            if (!StringUtils.isEmpty(latitude)) {
                info.longitude = Double.parseDouble(longitude);
            }
            potaInfo.put(info.reference, info);
        }

        return new PotaDatabase(potaInfo);
    }
}
