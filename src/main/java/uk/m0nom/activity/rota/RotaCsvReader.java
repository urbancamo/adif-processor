package uk.m0nom.activity.rota;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityReader;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.maidenheadlocator.MaidenheadLocatorConversion;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class RotaCsvReader extends ActivityReader {
    private static final Logger logger = Logger.getLogger(RotaCsvReader.class.getName());

    public RotaCsvReader(String sourceFile) {
        super(ActivityType.ROTA, sourceFile);
    }

    public ActivityDatabase read(InputStream inputStream) throws IOException {
        Map<String, Activity> rotaInfo = new HashMap<>();

        final Reader reader = new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8);

        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            RotaInfo info = new RotaInfo();
            info.setRef(record.get("Callsign"));
            info.setName(record.get("Railway"));
            info.setClub(record.get("Club"));
            info.setWab(record.get("WAB"));
            info.setGrid(record.get("Grid"));
            info.setCoords(readCoords(record, "Latitude", "Longitude"));
            rotaInfo.put(info.getRef(), info);
        }

        return new ActivityDatabase(ActivityType.ROTA, rotaInfo);
    }
}
