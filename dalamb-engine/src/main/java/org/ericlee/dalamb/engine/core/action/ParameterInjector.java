package org.ericlee.dalamb.engine.core.action;

import lombok.AllArgsConstructor;
import org.ericlee.dalamb.api.annotation.request.Body;
import org.ericlee.dalamb.api.annotation.request.PathVariable;
import org.ericlee.dalamb.api.annotation.request.QueryParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

@AllArgsConstructor
public class ParameterInjector {
    private Object instance;
    private Method action;
    private Map<String, String> pathVariable;
    private Map<String, String> queryParameter;
    private Object body;
    private enum ParameterType {
        QUERY_PARAMETER, PATH_VARIABLE, BODY
    }

    private static ParameterType getSupportedTypeOf(Parameter param) {
        Annotation[] annotations = param.getAnnotations();
        for(int i = 0; i < annotations.length; i++) {
            if(QueryParameter.class.equals(annotations[i].annotationType())) return ParameterType.QUERY_PARAMETER;
            else if(PathVariable.class.equals(annotations[i].annotationType())) return ParameterType.PATH_VARIABLE;
            else if(Body.class.equals(annotations[i].annotationType())) return ParameterType.BODY;
        }

        return null;
    }

    private String getValueOfAnnotation(ParameterType type, Parameter param) {
        Annotation[] annotations = param.getAnnotations();
        for(int i = 0; i < annotations.length; i++) {
            if(type == ParameterType.QUERY_PARAMETER && QueryParameter.class.equals(annotations[i].annotationType()))
                return ((QueryParameter)annotations[i]).value();

            if(type == ParameterType.PATH_VARIABLE && PathVariable.class.equals(annotations[i].annotationType()))
                return ((PathVariable)annotations[i]).value();
        }

        return null;
    }

    public static Class<?> getBodyType(Method method) {
        Parameter[] parameters = method.getParameters();

        for(int i = 0; i < parameters.length; i++) {
            if(getSupportedTypeOf(parameters[i]) == ParameterType.BODY)
                return parameters[i].getType();
        }

        return null;
    }

    public Object execute() throws InvocationTargetException, IllegalAccessException {
        Parameter[] parameters = action.getParameters();
        Object[] arguments = new Object[parameters.length];

        for(int i = 0; i < parameters.length; i++) {
            ParameterType type = getSupportedTypeOf(parameters[i]);
            if(type == null) continue;

            switch (type) {
                case QUERY_PARAMETER:
                    arguments[i] = queryParameter.get(getValueOfAnnotation(type, parameters[i]));
                    break;
                case PATH_VARIABLE:
                    arguments[i] = pathVariable.get(getValueOfAnnotation(type, parameters[i]));
                    break;
                case BODY:
                    arguments[i] = body;
                    break;
                default:
                    arguments[i] = null;
            }
        }

        return action.invoke(instance, arguments);
    }
}
