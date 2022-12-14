package org.ericlee.dalamb.engine.listener.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ericlee.dalamb.engine.configuration.element.BindingConfiguration;

import java.util.HashMap;

import java.util.Map;

public class RequestPathPattern {
    @Getter
    private BindingConfiguration configuration;
    private String[] patternTokens;

    private boolean isVariableSegment(String patternSegment) {
        return patternSegment.matches("\\{[a-z]([a-z]|[0-9]|_|-)*\\}");
    }

    private boolean segmentEquals(String patternSegment, String pathSegment) {
        return isVariableSegment(patternSegment) || patternSegment.equals(pathSegment);
    }

    public RequestPathPattern(String pattern, BindingConfiguration configuration) {
        this.patternTokens = pattern.split("/");
        this.configuration = configuration;
    }

    public boolean matches(String path) {
        String[] pathTokens = path.split("/");

        if(patternTokens.length != pathTokens.length) return false;
        for(int i = 0; i < patternTokens.length; i++) {
            if(!segmentEquals(patternTokens[i], pathTokens[i])) return false;
        }

        return true;
    }

    public Map<String, String> getPathVariables(String path) {
        String[] pathTokens = path.split("/");

        Map<String, String> variables = new HashMap<>();
        for(int i = 0; i < patternTokens.length; i++) {
            if(isVariableSegment(patternTokens[i])) {
                variables.put(patternTokens[i].replace("{", "")
                                .replace("}", ""),
                        pathTokens[i]);
            }
        }

        return variables;
    }
}
