package uk.m0nom.wota;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WotaSummitInfo {
    String wotaId;
    String sotaId;
    String hemaId;

    String book;
    String name;
    int height;
    String reference;
    String gridId;
    int x,y;
    double latitude, longitude;
}
