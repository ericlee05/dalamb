package org.ericlee.dalamb.engine.core.action;

import org.ericlee.dalamb.api.annotation.Action;
import org.ericlee.dalamb.api.annotation.Topic;
import org.ericlee.dalamb.engine.configuration.ConfigurationFormat;
import org.ericlee.dalamb.engine.configuration.element.ServiceConfiguration;
import org.ericlee.dalamb.engine.core.JarClassLoader;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ActionManager {
    private final ConcurrentHashMap<String, Method> actions = new ConcurrentHashMap<String, Method>();
    private final Map<String, List<Class<?>>> classes = new HashMap<String, List<Class<?>>>();
    private final Map<String, Object> instances = new HashMap<>();

    public ActionManager(ConfigurationFormat configuration) {
        for(ServiceConfiguration service : configuration.getServices()) {
            try {
                classes.put(service.getName(), List.of(new JarClassLoader(service.getJar()).getClasses()));
            } catch (Exception exception) {}
        }

        System.out.printf("Loaded %,d services: ", classes.size());
        System.out.println(classes);
    }

    public Object getQualifiedInstance(String actionIdentifier) {
        String[] tokens = actionIdentifier.split("\\.");
        String serviceName = tokens[0];
        String topicName = tokens[1];

        String instanceId = String.format("%s.%s", serviceName, topicName);
        if(instances.containsKey(instanceId)) return instances.get(instanceId);

        try {
            Object instance = classes.get(serviceName).stream().filter(it -> it.isAnnotationPresent(Topic.class) &&
                            topicName.equals(it.getAnnotation(Topic.class).value()))
                    .findFirst()
                    .orElse(null)
                    .newInstance();

            instances.put(instanceId, instance);
            return instance;
        } catch (Exception exception) {
            return null;
        }
    }

    public Method getAction(String actionIdentifier) {
        if(actions.containsKey(actionIdentifier)) return actions.get(actionIdentifier);

        String[] tokens = actionIdentifier.split("\\.");
        String service = tokens[0];
        String topic = tokens[1];
        String action = tokens[2];

        for(Class clazz : classes.get(service)) {
            Topic topicAnnotation = (Topic)clazz.getAnnotation(Topic.class);
            if(topicAnnotation == null) continue;

            if(topic.equals(topicAnnotation.value())) {
                Method method = Arrays.stream(clazz.getMethods()).filter(it -> it.isAnnotationPresent(Action.class) && action.equals(it.getAnnotation(Action.class).value()))
                        .findFirst()
                        .orElse(null);
                if(method != null) actions.put(actionIdentifier, method);

                return method;
            }
        }

        return null;
    }
}
