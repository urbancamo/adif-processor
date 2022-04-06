package uk.m0nom.adifproc.sotacsv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.AdifHeader;
import org.marsik.ham.adif.enums.Band;
import org.marsik.ham.adif.enums.Mode;
import org.marsik.ham.adif.types.Sota;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.adif3.io.Adif3FileReader;
import uk.m0nom.adifproc.qsofile.QsoFileReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class SotaCsvFileReader implements QsoFileReader {
    private static final Logger logger = Logger.getLogger(Adif3FileReader.class.getName());
    private final DateFormat internationalDateFormat =  new SimpleDateFormat("dd/MM/yy");
    private final DateFormat internationalTimeFormat = new SimpleDateFormat("hh:mm");

    private final Map<String, String> sotaBandFreqMap = new HashMap<>();

    public SotaCsvFileReader() {
        sotaBandFreqMap.put("1.8mhz", "160m");
        sotaBandFreqMap.put("3.5mhz", "80m");
        sotaBandFreqMap.put("5mhz", "60m");
        sotaBandFreqMap.put("7mhz", "40m");
        sotaBandFreqMap.put("10mhz", "30m");
        sotaBandFreqMap.put("14mhz", "20m");
        sotaBandFreqMap.put("18mhz", "17m");
        sotaBandFreqMap.put("21mhz", "15m");
        sotaBandFreqMap.put("24mhz", "12m");
        sotaBandFreqMap.put("28mhz", "10m");
        sotaBandFreqMap.put("50mhz", "6m");
        sotaBandFreqMap.put("144mhz", "2m");
        sotaBandFreqMap.put("432mhz", "70cm");
        sotaBandFreqMap.put("1240mhz", "23cm");
    }

    @Override
    public Adif3 read(String filename, String encoding, boolean sort) throws IOException {
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filename));

        final Reader reader = new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8);
        int line = 0;
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(reader);

        // [V2] [My Callsign][My Summit] [Date] [Time] [Band] [Mode] [His Callsign]
        //or
        //[V2] [My Callsign][My Summit] [Date] [Time] [Band] [Mode] [His Callsign][His Summit]
        //or
        //[V2] [My Callsign][My Summit] [Date] [Time] [Band] [Mode] [His Callsign][His Summit] [Notes or Comments]

        Adif3 log = new Adif3();
        AdifHeader header = new AdifHeader();
        header.setProgramId("ADIF Processor SOTA V2 Reader");
        header.setVersion("1.0");
        log.setHeader(header);

        List<Adif3Record> recs = new ArrayList<>();

        for (CSVRecord record : records) {
            try {
                line++;
                recs.add(readRecord(record));
            } catch (IllegalArgumentException | ParseException e) {
                logger.severe(String.format("Error reading line %d: %s", line, e.getMessage()));
            }
        }
        log.setRecords(recs);
        return log;
    }

    private Adif3Record readRecord(CSVRecord record) throws IllegalArgumentException, ParseException {
        Adif3Record rec = new Adif3Record();
        rec.setStationCallsign(toUpperAndTrim(record.get(1)));
        rec.setMySotaRef(parseSotaRef(record.get(2)));
        rec.setQsoDate(parseSotaDate(record.get(3)));
        rec.setTimeOn(parseSotaTime(record.get(4)));
        parseSotaBand(rec, record.get(5));
        rec.setMode(parseSotaMode(record.get(6)));
        rec.setCall(toUpperAndTrim(record.get(7)));
        if (record.size() > 7) {
            rec.setSotaRef(parseSotaRef(record.get(8)));
        }
        if (record.size() > 8) {
            rec.setComment(toUpperAndTrim(record.get(9)));
        }
        return rec;
    }

    private Sota parseSotaRef(String ref) {
        if (ref != null) {
            return Sota.valueOf(ref.trim().toUpperCase());
        }
        return null;
    }

    private Mode parseSotaMode(String sotaMode) {
        if (sotaMode != null) {
            return Mode.valueOf(sotaMode.trim().toUpperCase());
        }
        return null;
    }

    private void parseSotaBand(Adif3Record rec, String sotaBand) {
        if (sotaBand != null) {
            String bandOrFreq = sotaBand.toLowerCase();
            if (bandOrFreq.endsWith("m")) {
                setBandFromSotaBand(rec, bandOrFreq);
            } else if (bandOrFreq.endsWith("mhz")) {
                setBandFromSotaFreq(rec, bandOrFreq);
            }
        }
    }

    private void setBandFromSotaBand(Adif3Record rec, String bandOrFreq) {
        // Assume meters
        parseFrequency(rec, bandOrFreq);
        rec.setBand(Band.valueOf(bandOrFreq));
    }


    private void setBandFromSotaFreq(Adif3Record rec, String bandOrFreq) {
        // Check if this is a 'generic band'
        String possibleBand = sotaBandFreqMap.get(bandOrFreq);
        if (possibleBand != null) {
            try {
                Band band = Band.valueOf(String.format("BAND_%s", possibleBand));
                rec.setBand(band);
            } catch (IllegalArgumentException e) {
                rec.setBand(null);
            }
        }

        if (rec.getBand() == null) {
            String freqValue = StringUtils.removeEnd(bandOrFreq, "mhz");
            parseFrequency(rec, freqValue);
        }
    }

    private void parseFrequency(Adif3Record rec, String freqValue) {
        try {
            double freq = Double.parseDouble(freqValue);
            rec.setFreq(freq);
            for (Band toCheck : Band.values()) {
                if (freq >= toCheck.getLowerFrequency() && freq <= toCheck.getUpperFrequency()) {
                    rec.setBand(toCheck);
                }
            }
        } catch (NumberFormatException nfe) {
            logger.severe(String.format("Unable to parse frequency value of: %s", freqValue));
        }
    }

    private LocalTime parseSotaTime(String sotaTime) throws ParseException {
        return new java.sql.Time(internationalTimeFormat.parse(sotaTime).getTime()).toLocalTime();
    }

    private String toUpperAndTrim(String value) {
        String rtn = null;
        if (value != null) {
            rtn = value.toUpperCase().trim();
        }
        return rtn;
    }

    private LocalDate parseSotaDate(String sotaDate) throws ParseException {
        return new java.sql.Date(internationalDateFormat.parse(sotaDate).getTime()).toLocalDate();
    }
}
