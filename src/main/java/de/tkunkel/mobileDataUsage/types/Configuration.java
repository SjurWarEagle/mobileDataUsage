package de.tkunkel.mobileDataUsage.types;

import com.google.gson.Gson;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Component
public class Configuration {
    public String p12TokenStoreFile;
    public String simDeUser;
    public String simDePassword;
    public String outputFileName;
    public ConfigurationMqtt mqtt;

    @PostConstruct
    public void loadConfig() {
        Gson gson = new Gson();

        String configFile = System.getenv("CONFIG_FILE");
        if (Objects.isNull(configFile)) {
            throw new RuntimeException("Environment variable CONFIG_FILE not set");
        }

        try {
            Configuration configuration = gson.fromJson(Files.readString(Paths.get(configFile)), Configuration.class);

            this.p12TokenStoreFile = configuration.p12TokenStoreFile;
            this.simDePassword = configuration.simDePassword;
            this.simDeUser = configuration.simDeUser;
            this.outputFileName = configuration.outputFileName;
            this.mqtt =configuration.mqtt;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
