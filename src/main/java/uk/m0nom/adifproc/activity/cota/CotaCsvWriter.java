package uk.m0nom.adifproc.activity.cota;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityDatabase;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Castles on the Air CSV Writer. Used when cleaning up the data set
 */
public class CotaCsvWriter {
    private static final Logger logger = Logger.getLogger(CotaCsvWriter.class.getName());

    public void write(ActivityDatabase cotaDatabase) throws FileNotFoundException {
        Map<String, List<CotaInfo>> cotaBasedOnCountry = new HashMap<>();

        // Add all Castles to a map indexed on country
        for (Activity activity : cotaDatabase.getValues()) {
            CotaInfo cota = (CotaInfo) activity;
            String country = cota.getRef().split("-")[0];

            if (cotaBasedOnCountry.get(country) == null) {
                List<CotaInfo> countryCota = new ArrayList<>();
                cotaBasedOnCountry.put(country, countryCota);
            }

            cotaBasedOnCountry.get(country).add(cota);
        }

        String[] HEADERS = { "COTA WCA", "CASTLES", "PREFIX", "NAME OF CASTLE", "LOCATION", "INFORMATION", "LAT LONG", "LOCATION SOURCE"};

        // Now process each country in turn
        for (String country : cotaBasedOnCountry.keySet()) {
            BufferedWriter out = null;
            String outPath = "target/cotaCsv";
            try {
                Files.createDirectories(Paths.get(outPath));
                File outFile = new File(String.format("%s/%s.csv", outPath, country.toUpperCase()));
                out = new BufferedWriter(new FileWriter(outFile, StandardCharsets.ISO_8859_1));
                try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                        .withHeader(HEADERS))) {
                    int record = 1;
                    for (CotaInfo info : cotaBasedOnCountry.get(country)) {
                        String coordsString ="";
                        String locationSource = "";
                        if (info.getCoords() != null) {
                            coordsString = String.format("%.5f, %.5f", info.getCoords().getLatitude(), info.getCoords().getLongitude());
                            locationSource = info.getCoords().getLocationInfo().toString();
                        }
                        printer.printRecord(info.getRef(), info.getNoCastles(), info.getPrefix(), info.getName(), info.getLocation(), info.getInformation(), coordsString, locationSource);
                        record++;
                    }
                    logger.info(String.format("Wrote %d records to %s", record, outFile.getAbsolutePath()));
                }
            } catch (IOException e) {
                    logger.severe(String.format("Exception writing COTA country file %s: %s", country, e.getMessage()));
            } finally {
                try {
                    assert out != null;
                    out.close();
                } catch (IOException ioe) {
                    logger.severe(String.format("Exception closing COTA country file %s: %s", country, ioe.getMessage()));
                }

            }
        }
    }
}
