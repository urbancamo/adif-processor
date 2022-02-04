package uk.m0nom.activity.iota;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityReader;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.maidenheadlocator.MaidenheadLocatorConversion;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Reader for the Islands on the Air (IOTA) JSON export file
 */
public class IotaJsonReader extends ActivityReader {
    private static final Logger logger = Logger.getLogger(IotaJsonReader.class.getName());

    public IotaJsonReader(String sourceFile) {
        super(ActivityType.IOTA, sourceFile);
    }

    public ActivityDatabase read(InputStream inputStream) throws IOException {
        Map<String, Activity> iotaInfo = new HashMap<>();

        try {
            ObjectMapper mapper = new ObjectMapper();
            IotaResponse response = mapper.readValue(inputStream, IotaResponse.class);
            if ("ok".equals(response.getStatus())) {
                int i = 0;
                for (IotaInfo info : response.getContent()) {
                    info.setType(ActivityType.IOTA);
                    info.setRef(info.getRefNo());
                    info.setName(info.getIotaName());
                    info.setCoords(info.getCoordsFromLatLongMaxMin());
                    info.setGrid(MaidenheadLocatorConversion.coordsToLocator(info.getCoords()));
                    info.setIndex(i-1);
                    iotaInfo.put(info.getRefNo(), info);
                    i++;
                }
            }
        } catch (Exception ex) {
            logger.severe(String.format("Error reading IOTA JSON data: %s", ex.getMessage()));
        }

        return new ActivityDatabase(ActivityType.IOTA, iotaInfo);
    }
}
