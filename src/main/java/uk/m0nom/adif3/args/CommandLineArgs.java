package uk.m0nom.adif3.args;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class CommandLineArgs {
    public TransformControl parseArgs(String args[]) {
        TransformControl control = new TransformControl();
        ArgumentParser parser = ArgumentParsers.newFor("AdifFileTransformer").build()
                .defaultHelp(true)
                .description("Transform an ADIF file prior to storing or mapping it by enriching it with positional data.");

        parser.addArgument("-k", "--kml").required(false).action(Arguments.storeTrue())
                .help("Generate a KML output file for mapping direct to Google Earth");

        parser.addArgument("-q", "--qrz").required(false).action(Arguments.storeTrue())
                .help("Enable QRZ.COM lookup");
        parser.addArgument("-qu", "--qrz-username").required(false)
                .help("Username for the QRZ XML Service");
        parser.addArgument("-qp", "--qrz-password").required(false)
                .help("Password for the QRZ XML Service");

        parser.addArgument("-lat", "--latitude").required(false)
                .help("Specify override latitude in decimal format, enclose in single quotes");
        parser.addArgument("-long", "--longitude").required(false)
                .help("Specify override longitude in decimal format, enclose in single quotes");
        parser.addArgument("-g", "--grid").required(false)
                .help("Specify override grid in 4/6/10 characters");
        parser.addArgument("-e", "--encoding").required(false).setDefault("windows-1251")
                .help("Specify encoding of input ADIF file");
        parser.addArgument("-ks2s", "--kml-s2s").required(false).action(Arguments.storeTrue())
                .help("Highlight Summit to Summit Contacts in KML file");
        parser.addArgument("-kcs", "--kml-contact-shadow").required(false).action(Arguments.storeTrue()).setDefault(Boolean.TRUE)
                .help("Draw a shadow under the KML contact line");
        parser.addArgument("-ks2sls", "--kml-s2s-line-style").required(false).setDefault("dark_midnight_blue:50:3")
                .help("Set the colour of contact line for Summit to Summit contacts of the form html_color:transparency:width, default is: dark_midnight_blue:50:3");
        parser.addArgument("-kcls", "--kml-contact-line-style").required(false).setDefault("baby_blue:50:3")
                .help("Set the colour of contact line for contacts of the form html_color:transparency:width, default is: baby_blue:50:3");
        parser.addArgument("-kfi", "--kml-fixed-station").required(false).setDefault("http://maps.google.com/mapfiles/kml/shapes/ranger_station.png")
                .help("URL of the icon to use for fixed/home station locations");
        parser.addArgument("-kpi", "--kml-portable-station").required(false).setDefault("http://maps.google.com/mapfiles/kml/shapes/hiker.png")
                .help("URL of the icon to use for fixed/home station locations");
        parser.addArgument("-kmi", "--kml-mobile-station").required(false).setDefault("http://maps.google.com/mapfiles/kml/shapes/ranger_station.png")
                .help("URL of the icon to use for fixed/home station locations");
        parser.addArgument("-kmmi", "--kml-maritime-station").required(false).setDefault("http://maps.google.com/mapfiles/kml/shapes/ferry.png")
                .help("URL of the icon to use for maritime mobile station locations");
        parser.addArgument("path").nargs("*")
                .help("Input ADIF files");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
            control.setGenerateKml(ns.getBoolean("kml"));
            control.setMyLatitude(ns.getString("latitude"));
            control.setMyLongitude(ns.getString("longitude"));
            control.setMyGrid(ns.getString("grid"));
            control.setPathname(ns.getString("input")); //.substring(1, ns.getString("path").length()-1));
            control.setEncoding(ns.getString("encoding"));
            control.setUseQrzDotCom(ns.getBoolean("qrz"));
            control.setKmlS2s(ns.getBoolean("kml_s2s"));
            control.setKmlS2sContactLineStyle(ns.getString("kml_s2s_line_style"));
            control.setKmlContactLineStyle(ns.getString("kml_contact_line_style"));
            control.setQrzUsername(ns.getString("qrz_username"));
            control.setQrzPassword(ns.getString("qrz_password"));
            control.setKmlContactShadow(ns.getBoolean("kml_contact_shadow"));
            control.setKmlFixedIconUrl(ns.getString("kml_fixed_station"));
            control.setKmlMobileIconUrl(ns.getString("kml_mobile_station"));
            control.setKmlPortableIconUrl(ns.getString("kml_portable_station"));
            control.setKmlPortableIconUrl(ns.getString("kml_portable_station"));
            control.setKmlMaritimeIconUrl(ns.getString("kml_maritime_station"));
            control.setPathname(ns.getString("path").substring(1, ns.getString("path").length()-1));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        return control;
    }
}
