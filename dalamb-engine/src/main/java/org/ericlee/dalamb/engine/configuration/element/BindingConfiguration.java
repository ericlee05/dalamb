package org.ericlee.dalamb.engine.configuration.element;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BindingConfiguration {
    private String path;
    private List<String> methods;
    private String action;
}
