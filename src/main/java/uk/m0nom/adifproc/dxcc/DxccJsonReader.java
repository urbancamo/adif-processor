package uk.m0nom.adifproc.dxcc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.logging.Logger;

public class DxccJsonReader {
    private static final Logger logger = Logger.getLogger(DxccJsonReader.class.getName());

    public JsonDxccEntities read() {
        JsonDxccEntities entities = null;
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dxcc/dxcc.json");
            ObjectMapper mapper = new ObjectMapper();
            entities = mapper.readValue(inputStream, JsonDxccEntities.class);
        } catch (Exception ex) {
            logger.severe(String.format("Error reading DXCC JSON data: %s", ex.getMessage()));
        }

        return entities;
    }
}
