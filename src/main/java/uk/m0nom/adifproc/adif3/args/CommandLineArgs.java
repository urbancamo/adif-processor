package uk.m0nom.adifproc.adif3.args;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.antenna.AntennaService;
import uk.m0nom.adifproc.icons.IconResource;

import java.util.Set;

/**
 * Sets up a TransformControl instance based on command line arguments for the standalone processor
 */
public class CommandLineArgs {

    public TransformControl parseArgs(String[] args) {
        TransformControl control = new TransformControl();
        ArgumentParser parser = ArgumentParsers.newFor("AdifProcessor").build()
                .defaultHelp(true)
                .description("Process an ADIF file by locating stations and enriching output ADIF file, also optionally produces markdown and Google Earth KML files");

        parser.addArgument("-qu", "--qrz-username").required(false)
                .help("Username for the QRZ XML Service");
        parser.addArgument("-qp", "--qrz-password").required(false)
                .help("Password for the QRZ XML Service");
        parser.addArgument("-ql", "--qsl-labels").required(false).setDefault(Boolean.FALSE)
                .help("Generate QSL Card Labels");

        parser.addArgument("-l", "--location").required(false)
                .help("Specify override grid in 4/6/10 characters");
        parser.addArgument("-he", "--hema").required(false)
                .help("Specify override HEMA Id for your location");
        parser.addArgument("-w", "--wota").required(false)
                .help("Specify override WOTA Id for your location");
        parser.addArgument("-s", "--sota").required(false)
                .help("Specify override SOTA Id for your location");
        parser.addArgument("-p", "--pota").required(false)
                .help("Specify override POTA Id for your location");
        parser.addArgument("-o", "--output").required(false)
                .help("Write output files to this directory");

        parser.addArgument("-e", "--encoding").required(false).setDefault("windows-1251")
                .help("Specify encoding of input ADIF file");

        Set<String> antennaNames = new AntennaService().getAntennaNames();
        parser.addArgument("-a", "--antenna")
                .required(false)
                .setDefault("Vertical")
                .choices(antennaNames)
                .help(String.format("Antenna Type, one of: %s", String.join(", ", antennaNames)));

        parser.addArgument("-k", "--kml").required(false).action(Arguments.storeTrue())
                .help("Generate a KML output file for mapping direct to Google Earth");
        parser.addArgument("-kcw", "--kml-contact-width").required(false).setDefault(3)
                .help("Specify the width of contact lines");
        parser.addArgument("-kct", "--kml-contact-transparency").required(false).setDefault(20)
                .help("Specify the transparency of contact lines between 0% and 100%, 0% being solid");
        parser.addArgument("-kcband", "--kml-contact-colour-band").required(false).action(Arguments.storeTrue()).setDefault(Boolean.FALSE)
                .help("Colour QSOs based on the band used");
        parser.addArgument("-ks2s", "--kml-s2s").required(false).action(Arguments.storeTrue())
                .help("Highlight Summit to Summit Contacts in KML file");
        parser.addArgument("-kcs", "--kml-contact-shadow").required(false).action(Arguments.storeTrue()).setDefault(Boolean.TRUE)
                .help("Draw a shadow under the KML contact line");
        parser.addArgument("-ks2sls", "--kml-s2s-line-style").required(false).setDefault("brick_red:50:2")
                .help("Set the colour of contact line for Summit to Summit contacts of the form html_color:transparency:width, default is: brick_red:50:2");
        parser.addArgument("-kstls", "--kml-satellite-track-line-style").required(false).setDefault("brick_red:50:2")
                .help("Set the colour of contact line for Satellite tracks of the form html_color:transparency:width, default is: brick_red:50:2");
        parser.addArgument("-kcls", "--kml-contact-line-style").required(false).setDefault("baby_blue:50:2")
                .help("Set the colour of contact line for contacts of the form html_color:transparency:width, default is: baby_blue:50:2");
        parser.addArgument("-kfi", "--kml-fixed-station").required(false).setDefault("https://maps.google.com/mapfiles/kml/shapes/ranger_station.png")
                .help("URL of the icon to use for fixed/home station locations");
        parser.addArgument("-kpi", "--kml-portable-station").required(false).setDefault("https://maps.google.com/mapfiles/kml/shapes/hiker.png")
                .help("URL of the icon to use for fixed/home station locations");
        parser.addArgument("-kmi", "--kml-mobile-station").required(false).setDefault("https://maps.google.com/mapfiles/kml/shapes/cabs.png")
                .help("URL of the icon to use for fixed/home station locations");
        parser.addArgument("-kmmi", "--kml-maritime-station").required(false).setDefault("https://maps.google.com/mapfiles/kml/shapes/sailing.png")
                .help("URL of the icon to use for maritime mobile station locations");
        parser.addArgument("-kmai", "--kml-aeronautical-station").required(false).setDefault("http://maps.google.com/mapfiles/kml/shapes/airports.png")
                .help("URL of the icon to use for aeronautical mobile station locations");
        parser.addArgument("-kparki", "--kml-park-station").required(false).setDefault("https://maps.google.com/mapfiles/kml/shapes/picnic.png")
                .help("URL of the icon to use for Parks on the Air station locations");
        parser.addArgument("-ksotai", "--kml-sota-station").required(false).setDefault("https://maps.google.com/mapfiles/kml/shapes/mountains.png")
                .help("URL of the icon to use for SOTA station locations");
        parser.addArgument("-khemai", "--kml-hema-station").required(false).setDefault("https://maps.google.com/mapfiles/kml/shapes/hospitals.png")
                .help("URL of the icon to use for SOTA station locations");
        parser.addArgument("-kwotai", "--kml-wota-station").required(false).setDefault("https://maps.google.com/mapfiles/kml/shapes/trail.png")
                .help("URL of the icon to use for SOTA station locations");

        parser.addArgument("-md", "--markdown").required(false).action(Arguments.storeTrue())
                .help("Generate Markdown file containing the contacts");

        parser.addArgument("path").nargs("*")
                .help("Input ADIF files");

        Namespace ns;
        try {
            ns = parser.parseArgs(args);
            control.setGenerateKml(ns.getBoolean("kml"));
            control.setLocation(ns.getString("location"));
            control.setPathname(ns.getString("input")); //.substring(1, ns.getString("path").length()-1));
            control.setEncoding(ns.getString("encoding"));

            control.setQrzUsername(ns.getString("qrz_username"));
            control.setQrzPassword(ns.getString("qrz_password"));

            control.setKmlS2s(ns.getBoolean("kml_s2s"));
            control.setKmlS2sContactLineStyle(ns.getString("kml_s2s_line_style"));
            control.setKmlInternetContactLineStyle(ns.getString("kml_internet_line_style"));
            control.setKmlSatelliteTrackLineStyle(ns.getString("kml_satellite_track_line_style"));
            control.setKmlContactLineStyle(ns.getString("kml_contact_line_style"));
            control.setKmlContactShadow(ns.getBoolean("kml_contact_shadow"));

            control.setIcon(IconResource.FIXED_ICON_NAME, ns.getString("kml_fixed_station"));
            control.setIcon(IconResource.MOBILE_ICON_NAME, ns.getString("kml_mobile_station"));
            control.setIcon(IconResource.PORTABLE_ICON_NAME, ns.getString("kml_portable_station"));
            control.setIcon(IconResource.MARITIME_MOBILE_ICON_NAME, ns.getString("kml_maritime_station"));
            control.setIcon(IconResource.AERONAUTICAL_MOBILE_ICON_NAME, ns.getString("kml_aeronautical_station"));

            for (ActivityType activity : ActivityType.values()) {
                control.setIcon(activity.getActivityName(), String.format("kml_%s_station", activity.getActivityName().toLowerCase()));
                if (ns.getString(activity.getActivityName()) != null) {
                    control.setActivityRef(activity, ns.getString(activity.getActivityName()).toLowerCase());
                }
            }
            control.setKmlContactTransparency(100-ns.getInt("kml_contact_transparency"));
            control.setKmlContactWidth(ns.getInt("kml_contact_width"));
            control.setKmlContactColourByBand(ns.getBoolean("kml_contact_colour_band"));

            control.setFormattedOutput(ns.getBoolean("markdown"));
            control.setQslLabels(ns.getBoolean("qsl_labels"));
            control.setQslLabelsInitialPosition(ns.getInt("qsl_labels_initial_position"));
            control.setOutputPath(ns.getString("output"));
            control.setPathname(ns.getString("path").substring(1, ns.getString("path").length()-1));

            if (StringUtils.isNotEmpty(ns.getString("antenna"))) {
                control.setAntenna(new AntennaService().getAntenna(ns.getString("antenna")));
            }
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        return control;
    }
}
