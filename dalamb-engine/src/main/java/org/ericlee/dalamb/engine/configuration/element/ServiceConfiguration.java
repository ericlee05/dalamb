package org.ericlee.dalamb.engine.configuration.element;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ServiceConfiguration {
    @JsonProperty("package")
    private String packageName;

    private String name;

    private String jar;
}
