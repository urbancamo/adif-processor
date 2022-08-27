package uk.m0nom.adifproc.dxcc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class Countries {
    @JsonProperty("countries")
    private Collection<Country> countries;

    private Map<String, Country> countriesMap;

    public void setup() {
        countriesMap = new HashMap<>();
        for (Country country : countries) {
            countriesMap.put(country.getCode(), country);
        }
    }

    public Country getCountry(String code) {
        return countriesMap.get(code);
    }
}
