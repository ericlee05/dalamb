package org.ericlee.dalamb.engine.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

class ConfigurationParser {
    private static ObjectMapper yamlParser = new ObjectMapper(new YAMLFactory());

    static ConfigurationFormat parseFrom(File file) throws IOException {
        return yamlParser.readValue(file, ConfigurationFormat.class);
    }
}
