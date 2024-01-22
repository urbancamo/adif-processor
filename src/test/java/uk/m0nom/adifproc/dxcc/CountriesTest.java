package uk.m0nom.adifproc.dxcc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;


public class CountriesTest {

    private static Countries countries;

    @BeforeAll
    public static void setup() throws ParseException {
        countries = new CountriesJsonReader().read();
        countries.setup();
    }

    @Test
    public void haveCountries() {
        assert countries != null;
        assert countries.getCountries().size() == 244;
    }

    @Test
    public void retrieveVe() {
        String code = "VE";
        Country country = countries.getCountry(code);
        assertThat(country).isNotNull();
        assertThat(country.getName()).isEqualTo("Venezuela");
    }

    @Test
    public void retrieveGb() {
        String code = "GB";
        Country country = countries.getCountry(code);
        assertThat(country).isNotNull();
        assertThat(country.getName()).isEqualTo("United Kingdom");
    }
}
