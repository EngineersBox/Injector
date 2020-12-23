package com.engineersbox.injector;

import com.engineersbox.injector.exceptions.MissingPropertyFile;

import java.io.*;
import java.util.Properties;

public class ConfigurationProperties {
    public Properties properties;

    public ConfigurationProperties(final String filepath) {
        this.properties = new Properties();
        try (InputStream inputStream = new FileInputStream(filepath)) {
            this.properties.load(inputStream);
        } catch (IOException e) {
            throw new MissingPropertyFile(filepath);
        }
    }
}
