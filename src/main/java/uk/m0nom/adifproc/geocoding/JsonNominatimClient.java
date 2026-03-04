package uk.m0nom.adifproc.geocoding;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Client for the OpenStreetMap Nominatim search API.
 * Replaces the unmaintained fr.dudie.nominatim library.
 *
 * @see <a href="https://nominatim.org/release-docs/develop/api/Search/">Nominatim Search API</a>
 */
public class JsonNominatimClient {

    private static final Logger logger = LoggerFactory.getLogger(JsonNominatimClient.class);
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient;
    private final String email;

    public JsonNominatimClient(HttpClient httpClient, String email) {
        this.httpClient = httpClient;
        this.email = email;
    }

    public List<Address> search(String query) throws IOException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
        String url = String.format("%s?q=%s&format=jsonv2&email=%s", NOMINATIM_URL, encodedQuery, encodedEmail);

        logger.debug("Nominatim request URL: {}", url);

        HttpGet request = new HttpGet(url);
        request.setHeader("User-Agent", "adif-processor (contact: " + email + ")");
        request.setHeader("Accept", "application/json");

        List<Address> results = new ArrayList<>();

        httpClient.execute(request, response -> {
            int statusCode = response.getStatusLine().getStatusCode();
            String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            if (statusCode != 200) {
                logger.warn("Nominatim returned HTTP {}: {}", statusCode, body);
                return null;
            }

            logger.debug("Nominatim raw response: {}", body);

            JsonNode arrayNode = objectMapper.readTree(body);
            for (JsonNode node : arrayNode) {
                double lat = node.get("lat").asDouble();
                double lon = node.get("lon").asDouble();
                String displayName = node.has("display_name") ? node.get("display_name").asText() : "";
                results.add(new Address(lat, lon, displayName));
            }
            return null;
        });

        return results;
    }
}
