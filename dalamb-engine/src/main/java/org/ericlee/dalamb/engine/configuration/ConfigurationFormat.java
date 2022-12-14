package org.ericlee.dalamb.engine.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ericlee.dalamb.engine.configuration.element.BindingConfiguration;
import org.ericlee.dalamb.engine.configuration.element.ServiceConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Getter
@NoArgsConstructor
public class ConfigurationFormat {
    private List<ServiceConfiguration> services;
    private List<BindingConfiguration> bindings;

    public static ConfigurationFormat from(File yaml) throws IOException {
        return ConfigurationParser.parseFrom(yaml);
    }
}
