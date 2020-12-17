package com.engineersbox.injector;

import java.io.*;
import java.util.Properties;

public class ConfigurationProperties {
    public Properties properties;

    public ConfigurationProperties(final String filepath) throws FileNotFoundException {
        this.properties = new Properties();
        InputStream inputStream = new FileInputStream(filepath);
        try {
            this.properties.load(inputStream);
        } catch (IOException e) {
            throw new FileNotFoundException("property file '" + filepath + "' not found");
        }
    }
}
