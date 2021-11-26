package uk.m0nom.activity.iota;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * This is the JSON response information returned as a result of a query sent to the IOTA Rest Interface.
 * If the query is sent without an island reference then the entire database is returned in JSON format.
 */
@Getter
@Setter
public class IotaResponse {
    @JsonProperty("status")
    private String status;

    @JsonProperty("content")
    private Collection<IotaInfo> content;
}
