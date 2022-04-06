package uk.m0nom.adifproc.adif3.config;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import org.springframework.context.annotation.Configuration;
import uk.m0nom.FileProcessorApplication;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class TransformerConfig {
    private final static String configFilePath = "adif-processor.yaml";

    private final YamlMapping config;

    public TransformerConfig() throws IOException {
        InputStream configStream = FileProcessorApplication.class.getClassLoader().
                getResourceAsStream(configFilePath);

        config = Yaml.createYamlInput(configStream).readYamlMapping();
    }

    public YamlMapping getConfig() { return config; }
}
