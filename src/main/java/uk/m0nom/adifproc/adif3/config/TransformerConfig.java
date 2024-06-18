package uk.m0nom.adifproc.adif3.config;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Getter
@Configuration
public class TransformerConfig {
    private final static String configFilePath = "adif-processor.yaml";

    private final YamlMapping config;

    public TransformerConfig() throws IOException {
        InputStream configStream = TransformerConfig.class.getClassLoader().
                getResourceAsStream(configFilePath);

        config = Yaml.createYamlInput(configStream).readYamlMapping();
    }

}
