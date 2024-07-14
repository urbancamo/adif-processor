package uk.m0nom.adifproc.icons;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PortableIcon {
    public final static String DEFAULT_ICON_NAME = "DEFAULT";

    private final String BASE_URL = "http://maps.google.com/mapfiles/kml/shapes/";

    private String iconName;
    private String description;

    public String getIconUrl() {
        return String.format("%s%s.png", BASE_URL, iconName);
    }
}
