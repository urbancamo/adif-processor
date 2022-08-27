package uk.m0nom.adifproc.dxcc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.logging.Logger;

public class CountriesJsonReader {
    private static final Logger logger = Logger.getLogger(CountriesJsonReader.class.getName());

    public Countries read() {
        Countries countries = null;
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dxcc/countries.json");
            ObjectMapper mapper = new ObjectMapper();
            countries = mapper.readValue(inputStream, Countries.class);
        } catch (Exception ex) {
            logger.severe(String.format("Error reading Countries JSON data: %s", ex.getMessage()));
        }

        return countries;
    }
}
