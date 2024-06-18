package uk.m0nom.adifproc.activity.pota;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.activity.CsvActivityReader;
import uk.m0nom.adifproc.activity.RemoteActivitySource;

/**
 * Reader for the Parks on the Air CSV extract
 */
public class PotaCsvReader extends CsvActivityReader implements RemoteActivitySource {

    public PotaCsvReader(String sourceFile) {
        super(ActivityType.POTA, sourceFile);
    }


    @Override
    protected Activity readRecord(CSVRecord record) throws IllegalArgumentException {
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
        return info;
    }

    @Override
    public String getRemoteUrl() {
        return "https://pota.app/all_parks_ext.csv";
    }
}
