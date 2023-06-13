package uk.m0nom.adifproc.kml;

import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import org.apache.commons.io.FileUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.contacts.Qsos;
import uk.m0nom.adifproc.adif3.contacts.Station;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.adif3.transform.TransformResults;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.coords.LatLongUtils;
import uk.m0nom.adifproc.kml.activity.KmlLocalActivities;
import uk.m0nom.adifproc.kml.comms.KmlCommsService;
import uk.m0nom.adifproc.kml.comms.KmlSatelliteTrack;
import uk.m0nom.adifproc.kml.info.TemplateEngineConstructor;
import uk.m0nom.adifproc.kml.station.KmlStationUtils;
import uk.m0nom.adifproc.maidenheadlocator.MaidenheadLocatorConversion;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Service
public class KmlWriter {
    private static final Logger logger = Logger.getLogger(KmlWriter.class.getName());

    private final KmlCommsService kmlCommsService;

    public KmlWriter(KmlCommsService kmlCommsService) {
        this.kmlCommsService = kmlCommsService;
    }

    public String write(TransformControl control, String pathname,
                        String name, ActivityDatabaseService activities, Qsos qsos,
                        TransformResults results) {
        control.setTemplateEngine(TemplateEngineConstructor.create());

        KmlLocalActivities kmlLocalActivities = new KmlLocalActivities();
        KmlStationUtils kmlStationUtils = new KmlStationUtils(control);
        KmlSatelliteTrack kmlSatelliteTrack = new KmlSatelliteTrack();
        Map<String,String> commsStyleMap = new HashMap<>();

        final Kml kml = new Kml();
        Document doc = kml.createAndSetDocument().withName(name).withOpen(true);

        // create a Folder
        Folder contactsFolder = doc.createAndAddFolder();
        contactsFolder.withName("Contacts").withOpen(true);

        if (results.getSatelliteActivity().hasActivity()) {
            results.getSatelliteActivity().spaceOutContactsInPasses();
        }

        Station myStation = null;
        Iterator<Qso> qsoIterator = qsos.getQsos().iterator();
        Folder folder = null;
        while (qsoIterator.hasNext()) {
            Qso qso = qsoIterator.next();
            if (!qso.getFrom().equals(myStation)) {
                folder = contactsFolder.createAndAddFolder().withName(qso.getFrom().getCallsign()).withOpen(true);

                String error = kmlStationUtils.addMyStationToMap(doc, folder, qso);
                if (error != null) {
                    results.setError(error);
                }
                if (qso.getFrom().hasActivity() && control.isKmlShowLocalActivationSites()) {
                    kmlLocalActivities.addLocalActivities(control, doc, folder, qso.getFrom(), activities);
                }
                myStation = qso.getFrom();
            }
            Folder contactFolder = folder.createAndAddFolder().withName(qso.getTo().getCallsign()).withOpen(false);
            GlobalCoordinates myCoords = qso.getRecord().getMyCoordinates();
            GlobalCoordinates theirCoords = qso.getRecord().getCoordinates();
            if (LatLongUtils.isCoordinateValid(myCoords) && LatLongUtils.isCoordinateValid(theirCoords)) {
                String error = kmlStationUtils.createStationMarker(control, doc, contactFolder, qso);
                if (error != null) {
                    results.setError(error);
                }

                if (qso.getTo().hasActivity() && control.isKmlShowLocalActivationSites()) {
                    Folder localActivityFolder = contactFolder.createAndAddFolder().withName("Local Activity").withOpen(false);
                    kmlLocalActivities.addLocalActivities(control, doc, localActivityFolder, qso.getTo(), activities);
                }
                error = kmlCommsService.createCommsLink(doc, contactFolder, commsStyleMap, qso, control, kmlStationUtils);
                if (error != null) {
                    results.setError(error);
                }
                if (MaidenheadLocatorConversion.isADubiousGridSquare(qso.getRecord().getGridsquare())) {
                    results.addContactWithDubiousLocation(qso.getTo().getCallsign());
                }
            } else {
                results.addContactWithoutLocation(qso.getTo().getCallsign());
                logger.warning(String.format("Cannot determine communication link, no location data for: %s", qso.getTo().getCallsign()));
            }
        }

        if (!results.hasErrors()) {
            if (results.getSatelliteActivity().hasActivity()) {
                GlobalCoordinates coords = qsos.getQsos().get(0).getRecord().getMyCoordinates();
                GlobalCoords3D coordinatesWithSourceAccuracy = new GlobalCoords3D(coords, 0.0);
                kmlSatelliteTrack.addSatelliteTracks(control, doc, results.getSatelliteActivity(), coordinatesWithSourceAccuracy);
            }
            try {
                String tmpPathname = pathname + ".tmp";
                logger.info(String.format("Writing KML to: %s", pathname));
                File file = new File(tmpPathname);
                kml.marshal(file);
                replaceNameSpaces(tmpPathname, pathname, results);
                //return ZipUtils.compress(pathname, "kmz");
                return pathname;
            } catch (IOException e) {
                results.setError(e.getMessage());
                return "";
            }
        }
        return "";
    }

    private void replaceNameSpaces(String inPath, String outPath, TransformResults results) {
        try (
                BufferedWriter writer = Files.newBufferedWriter(Path.of(outPath), StandardCharsets.UTF_8);
                BufferedReader reader = Files.newBufferedReader(Path.of(inPath), StandardCharsets.UTF_8);) {
            while (reader.ready()) {
                String line = reader.readLine();
                String newLine = line.replaceAll("ns2:", "").replace("<kml xmlns:ns2=\"http://www.opengis.net/kml/2.2\" xmlns:ns3=\"http://www.w3.org/2005/Atom\" xmlns:ns4=\"urn:oasis:names:tc:ciq:xsdschema:xAL:2.0\" xmlns:ns5=\"http://www.google.com/kml/ext/2.2\">", "<kml>");
                writer.write(newLine);
            }
        } catch (IOException e) {
            results.setError(e.getMessage());
            e.printStackTrace();
        }
    }
}
