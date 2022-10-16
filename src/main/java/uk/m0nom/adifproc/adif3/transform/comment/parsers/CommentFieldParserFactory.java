package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.coords.LocationParsingService;
import uk.m0nom.adifproc.location.FromLocationDeterminer;
import uk.m0nom.adifproc.location.ToLocationDeterminer;
import uk.m0nom.adifproc.satellite.ApSatelliteService;

import java.util.HashMap;
import java.util.Map;

@Service
public class CommentFieldParserFactory {
    private final Map<String, CommentFieldParser> parserMap;

    public CommentFieldParserFactory(ActivityDatabaseService activities,
                                     FromLocationDeterminer fromLocationDeterminer,
                                     ToLocationDeterminer toLocationDeterminer,
                                     LocationParsingService locationParsingService,
                                     ApSatelliteService apSatelliteService) {
        parserMap = new HashMap<>();
        parserMap.put("Alt", new AltFieldParser());
        parserMap.put("Ant", new AntFieldParser());
        parserMap.put("MyAlt", new MyAltFieldParser());
        parserMap.put("Name", new NameFieldParser());
        parserMap.put("Operator", new OperatorFieldParser());
        parserMap.put("Qth", new QthFieldParser());
        parserMap.put("Rig", new RigFieldParser());
        parserMap.put("GridSquare", new GridSquareFieldParser());
        parserMap.put("RxPwr", new RxPwrFieldParser());
        parserMap.put("BandRx", new BandRxFieldParser());
        parserMap.put("SotaRef", new SotaRefFieldParser(toLocationDeterminer, activities));
        parserMap.put("MySotaRef", new MySotaRefFieldParser(fromLocationDeterminer, activities));
        parserMap.put("WotaRef", new WotaFieldParser(toLocationDeterminer, activities));
        parserMap.put("WwffRef", new WwffFieldParser(toLocationDeterminer, activities));
        parserMap.put("Iota", new IotaFieldParser(toLocationDeterminer, activities, ActivityType.IOTA));
        parserMap.put("GmaRef", new ActivityFieldParser(toLocationDeterminer, activities, ActivityType.GMA));
        parserMap.put("HemaRef", new ActivityFieldParser(toLocationDeterminer, activities, ActivityType.HEMA));
        parserMap.put("PotaRef", new ActivityFieldParser(toLocationDeterminer, activities, ActivityType.POTA));
        parserMap.put("CotaRef", new ActivityFieldParser(toLocationDeterminer, activities, ActivityType.COTA));
        parserMap.put("LotaRef", new ActivityFieldParser(toLocationDeterminer, activities, ActivityType.LOTA));
        parserMap.put("SerialTx", new SerialTxFieldParser());
        parserMap.put("SerialRx", new SerialRxFieldParser());
        parserMap.put("Fists", new FistsFieldParser());
        parserMap.put("Skcc", new SkccFieldParser());
        parserMap.put("Qsl", new QslFieldParser());
        parserMap.put("Coordinates", new CoordinatesFieldParser(locationParsingService));
        parserMap.put("Latitude", new LatitudeParser());
        parserMap.put("Longitude", new LongitudeParser());
        parserMap.put("AntPath", new AntPathFieldParser());
        parserMap.put("Propagation", new PropagationFieldParser());
        parserMap.put("SatelliteName", new SatelliteNameFieldParser(apSatelliteService));
        parserMap.put("SatelliteMode", new SatelliteModeFieldParser());
        parserMap.put("Notes", new NotesFieldParser());
        parserMap.put("Web", new WebFieldParser());
    }

    public CommentFieldParser get(String name) {
        return parserMap.get(name);
    }
}
