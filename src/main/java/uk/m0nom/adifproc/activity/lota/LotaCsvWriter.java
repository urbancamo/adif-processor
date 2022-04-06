package uk.m0nom.adifproc.activity.lota;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityDatabase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * A writer used to standardize the various coordinate systems used in the original Lighthouses on the Air extract
 */
public class LotaCsvWriter {
    private static final Logger logger = Logger.getLogger(LotaCsvWriter.class.getName());

    public void write(ActivityDatabase lotaDatabase) {
        String[] HEADERS = { "Country","Lighthouse Name","DXCC","Continent","Location","ILLW","Status","Latitude","Longitude","Location Source" };

        // Now process each country in turn
        BufferedWriter out = null;
        String outPath = "target";
        String filePath = String.format("%s/%s.csv", outPath, "lighthouses");
        File outFile = null;

        SortedSet<Activity> activities = new TreeSet<>(lotaDatabase.getValues());

        try {
            outFile = new File(filePath);
            out = new BufferedWriter(new FileWriter(outFile, StandardCharsets.UTF_8));

            try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                    .withHeader(HEADERS))) {
                int record = 1;
                for (Activity activity : activities) {
                    LotaInfo info = (LotaInfo) activity;

                    String latitude = "";
                    String longitude = "";
                    String locationSource = "";
                    if (info.getCoords() != null) {
                        latitude = String.format("%.5f", info.getCoords().getLatitude());
                        longitude = String.format("%.5f", info.getCoords().getLongitude());
                        locationSource = info.getCoords().getLocationInfo().toString();
                    }
                    printer.printRecord(info.getCountry(), info.getName(), info.getDxcc(), info.getContinent(), info.getLocation(), info.getRef(), info.getStatus(),
                            latitude, longitude, locationSource);
                    record++;
                }
                logger.info(String.format("Wrote %d records to %s", record, outFile.getAbsolutePath()));
            }
        } catch (IOException e) {
                logger.severe(String.format("Exception writing LOTA file %s: %s", outFile.getAbsolutePath(), e.getMessage()));
        } finally {
            try {
                assert out != null;
                out.close();
            } catch (IOException ioe) {
                logger.severe(String.format("Exception closing LOTA file %s: %s", outFile.getAbsolutePath(), ioe.getMessage()));
            }
        }
    }
}
