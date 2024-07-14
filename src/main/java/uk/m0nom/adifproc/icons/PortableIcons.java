package uk.m0nom.adifproc.icons;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static uk.m0nom.adifproc.icons.PortableIcon.DEFAULT_ICON_NAME;

public class PortableIcons {
    private static final Logger logger = Logger.getLogger(PortableIcons.class.getName());

    private final List<PortableIcon> icons;

    public PortableIcons() {
        icons = new ArrayList<>(16);
        addIcon(DEFAULT_ICON_NAME, "Default (Activity Specific)");
        addIcon("hiker", "On Foot");
        addIcon("bus", "Bus/Public Transport");
        addIcon("cabs", "Car/Van");
        addIcon("ferry", "Ferry");
        addIcon("heliport", "Helicopter");
        addIcon("campground", "Camping");
        addIcon("cycling", "Cycling");
        addIcon("motorcycling", "Motorcycle");
        addIcon("horsebackriding", "Horse Riding");
        addIcon("rail", "Rail");
        addIcon("sailing", "Sailing");
        addIcon("ski","Skiing/Snowboarding");
        addIcon("swimming", "Swimming");
        addIcon("truck", "Truck");
        addIcon("tram", "Tram");
    }

    private void addIcon(String filename, String description) {
        var icon = new PortableIcon(filename, description);
        icons.add(icon);
    }

    public List<PortableIcon> getIcons() {
        return icons;
    }

    public List<String> getIconNames() {
        return icons.stream().map(PortableIcon::getIconName).toList();
    }

    public PortableIcon getIcon(String iconName) {
        return icons.stream().filter(icon -> icon.getIconName().equals(iconName)).findFirst().orElse(null);
    }
}
