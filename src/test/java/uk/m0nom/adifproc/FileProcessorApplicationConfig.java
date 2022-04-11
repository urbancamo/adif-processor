package uk.m0nom.adifproc;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan("uk.m0nom")
public class FileProcessorApplicationConfig {

    public FileProcessorApplicationConfig(ConfigurableEnvironment configurableEnvironment, Environment environment) {
        String activeProfile = environment.getProperty("spring_profile_active");
        if (activeProfile != null) {
            configurableEnvironment.setActiveProfiles(activeProfile);
        }
    }
}
