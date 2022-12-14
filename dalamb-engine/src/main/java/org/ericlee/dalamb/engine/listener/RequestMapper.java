package org.ericlee.dalamb.engine.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.ericlee.dalamb.engine.configuration.element.BindingConfiguration;
import org.ericlee.dalamb.engine.core.action.ActionManager;
import org.ericlee.dalamb.engine.listener.http.RequestPathPattern;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestMapper {
    private final ActionManager manager;
    private final List<RequestPathPattern> bindings = new ArrayList<>();

    public RequestMapper(ActionManager manager, List<BindingConfiguration> bindingConfiguration) {
        this.manager = manager;
        bindingConfiguration.forEach((binding) ->
            bindings.add(new RequestPathPattern(binding.getPath(), binding))
        );
    }

    @ToString
    @Getter
    @AllArgsConstructor
    public static class ActionAndParamEntry {
        private Object runtimeInstance;
        private Method action;
        private Map<String, String> pathVariable;
    }

    public ActionAndParamEntry getMappedAction(String method, String requestPath) {
        RequestPathPattern found = bindings.stream().filter(it -> it.matches(requestPath) &&
                        it.getConfiguration().getMethods().contains(method))
                .findFirst()
                .orElse(null);
        if (found == null) return null;

        Object instance = manager.getQualifiedInstance(found.getConfiguration().getAction())
                .orElseThrow();
        Method action = manager.getAction(found.getConfiguration().getAction())
                .orElseThrow();

        return new ActionAndParamEntry(
                instance, action, found.getPathVariables(requestPath)
        );
    }
}
