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

        parser.addArgument("-l", "--lat").required(false).type(Double.class)
                .help("Specify override latitude in decimal format");
        parser.addArgument("-n", "--long").required(false).type(Double.class)
                .help("Specify override longitude in decimal format");
        parser.addArgument("-g", "--grid").required(false)
                .help("Specify override grid in 4/6/10 characters");
        parser.addArgument("-e", "--encoding").required(false).setDefault("windows-1251")
                .help("Specify encoding of input ADIF file");

        parser.addArgument("path").nargs("*")
                .help("Input ADIF file, and output file if specified, otherwise input filename is written with -fta before the extension");
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
            control.setGenerateKml(ns.getBoolean("kml"));
            control.setMyLatitude(ns.getDouble("lat"));
            control.setMyLongitude(ns.getDouble("long"));
            control.setMyGrid(ns.getString("grid"));
            control.setPathname(ns.getString("path").substring(1, ns.getString("path").length()-1));
            control.setEncoding(ns.getString("encoding"));
            control.setUseQrzDotCom(ns.getBoolean("qrz"));
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
        return control;
    }
}
