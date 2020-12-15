package com.engineersbox.injector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationProperties {
    public Properties properties;

    public ConfigurationProperties(final String filepath) throws FileNotFoundException {
        this.properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filepath);
        try {
            if (inputStream != null) {
                this.properties.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + filepath + "' not found");
            }
        } catch (IOException e) {
            throw new FileNotFoundException("property file '" + filepath + "' not found");
        }
    }
}
