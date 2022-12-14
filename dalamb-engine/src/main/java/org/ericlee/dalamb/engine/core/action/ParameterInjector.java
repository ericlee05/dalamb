package org.ericlee.dalamb.engine.core.action;

import lombok.AllArgsConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

@AllArgsConstructor
public class ParameterInjector {
    private Method action;
    private Map<String, String> pathVariable;
    private Map<String, String> queryParameter;
    private Object body;

    public Object execute() {
        Parameter[] parameters = action.getParameters();
        for(int i = 0; i < parameters.length; i++) {
            System.out.println(parameters[i].getAnnotations());
        }

        return null;
    }
}
