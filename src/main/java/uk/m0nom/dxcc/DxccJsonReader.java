package uk.m0nom.dxcc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class DxccJsonReader {
    private static final Logger logger = Logger.getLogger(DxccJsonReader.class.getName());

    public DxccEntities read() {
        DxccEntities entities = null;
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dxcc/dxcc.json");
            ObjectMapper mapper = new ObjectMapper();
            entities = mapper.readValue(inputStream, DxccEntities.class);
            entities.setup();
        } catch (Exception ex) {
            logger.severe(String.format("Error reading DXCC JSON data: %s", ex.getMessage()));
        }

        return entities;
    }
}
