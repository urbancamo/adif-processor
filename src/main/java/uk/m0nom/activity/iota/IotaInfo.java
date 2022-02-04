package uk.m0nom.activity.iota;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.coords.GlobalCoords3D;
import uk.m0nom.coords.LocationAccuracy;
import uk.m0nom.coords.LocationSource;

import java.util.Collection;

/**
 * Top of the information hierarchy stored for any Island on the Air reference
 */
@Getter
@Setter
@NoArgsConstructor
public class IotaInfo extends Activity {

    private int index;

    @JsonProperty("refno")
    private String refNo;

    @JsonProperty("name")
    private String iotaName;

    @JsonProperty("dxcc_num")
    private String dxccNum;

    @JsonProperty("latitude_max")
    private double latitudeMax;

    @JsonProperty("latitude_min")
    private double latitudeMin;

    @JsonProperty("longitude_max")
    private double longitudeMax;

    @JsonProperty("longitude_min")
    private double longitudeMin;

    @JsonProperty("grp_region")
    private String groupRegion;

    @JsonProperty("whitelist")
    private int whitelist;

    @JsonProperty("pc_credited")
    private double pcCredited;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("sub_groups")
    private Collection<IotaSubGroup> subGroups;

    public IotaInfo(ActivityType type) {
        super(type);
    }

    /**
     * Each IOTA reference could be a group of islands so the location is defined as a latitude/longitude
     * maximum and minimum. This method calculates a central coordinate in this region
     * @return Central coordinate of an island (group) max/min lat/long
     */
    public GlobalCoords3D getCoordsFromLatLongMaxMin() {
        double latitudeCentre = latitudeMin + ((latitudeMax - latitudeMin) / 2.0);
        double longitudeCentre = longitudeMin + ((longitudeMax - longitudeMin) / 2.0);
        return new GlobalCoords3D(latitudeCentre, longitudeCentre, LocationSource.ACTIVITY, LocationAccuracy.LAT_LONG);
    }

    @Override
    public String getUrl() {
        return String.format("https://www.iota-world.org/islands-on-the-air/iota-groups-islands/group/%d.html", index);
    }
}
