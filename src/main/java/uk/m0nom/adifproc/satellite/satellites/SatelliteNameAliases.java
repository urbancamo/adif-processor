package uk.m0nom.adifproc.satellite.satellites;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:satellite/satellite-name-aliases.properties")
public class SatelliteNameAliases {

    private final Environment env;

    public SatelliteNameAliases(Environment env) {
        this.env = env;
    }

    public String getSatelliteName(String alias) {
        return env.getProperty(alias);
    }
}
