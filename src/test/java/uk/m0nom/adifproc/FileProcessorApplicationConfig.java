package uk.m0nom.adifproc;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("uk.m0nom.adifproc")
@EnableJpaRepositories(basePackages = "uk.m0nom.adifproc.db")
@EntityScan("uk.m0nom.adifproc.domain")
public class FileProcessorApplicationConfig {

    public FileProcessorApplicationConfig(ConfigurableEnvironment configurableEnvironment, Environment environment) {
        String activeProfile = environment.getProperty("spring_profile_active");
        if (activeProfile != null) {
            configurableEnvironment.setActiveProfiles(activeProfile);
        }
    }
}
