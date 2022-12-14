package org.ericlee.dalamb.engine.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.io.Receiver;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.ericlee.dalamb.engine.configuration.ConfigurationFormat;
import org.ericlee.dalamb.engine.core.action.ActionManager;
import org.ericlee.dalamb.engine.core.action.ParameterInjector;
import org.ericlee.dalamb.engine.listener.http.HttpException;

import java.util.HashMap;
import java.util.Map;

import static org.ericlee.dalamb.engine.core.action.ParameterInjector.getBodyType;

public class DalambHandler implements Receiver.FullBytesCallback {
    private final RequestMapper requestMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public DalambHandler(ConfigurationFormat configuration) {
        this.requestMapper = new RequestMapper(new ActionManager(configuration),
                configuration.getBindings());
    }

    @Override
    public void handle(HttpServerExchange exchange, byte[] message) {
        try {
            RequestMapper.ActionAndParamEntry entry = requestMapper.getMappedAction(exchange.getRequestMethod().toString(),
                    exchange.getRequestPath());

            if (entry == null) throw new HttpException(404, "Page not found :(");

            // query parser
            Map<String, String> queryParams = new HashMap<>();
            String[] queryTokens = exchange.getQueryString().split("&");
            for(String queryToken : queryTokens) {
                String[] tokens = queryToken.split("=");
                queryParams.put(tokens[0], tokens.length == 2 ? tokens[1] : "");
            }

            // body parser
            Class<?> bodyType = getBodyType(entry.getAction());
            Object body = null;
            if(bodyType != null) {
                try {
                    body = objectMapper.readValue(message, bodyType);
                } catch (Exception exception) {
                    throw new HttpException(400, "Bad request");
                }
            }

            System.out.printf("Qualified action found:\n * method: %s\n * path variables: %s\n", entry.getAction(), entry.getPathVariable());

            Object response = new ParameterInjector(entry.getRuntimeInstance(), entry.getAction(),
                    entry.getPathVariable(), queryParams, body).execute();

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(objectMapper.writeValueAsString(response));
        } catch (HttpException exception) {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.setStatusCode(exception.getStatus());
            exchange.getResponseSender().send(exception.getMessage());
        } catch (Exception exception) {
            System.out.printf("Uncaught error on %s %s:\n", exchange.getRequestMethod(), exchange.getRequestPath());
            exception.printStackTrace();

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.setStatusCode(500);
            exchange.getResponseSender().send("Internal Server Error");
        }
    }
}
