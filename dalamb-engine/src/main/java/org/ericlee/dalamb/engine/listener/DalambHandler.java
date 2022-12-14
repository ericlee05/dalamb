package org.ericlee.dalamb.engine.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.ericlee.dalamb.engine.configuration.ConfigurationFormat;
import org.ericlee.dalamb.engine.core.action.ActionManager;
import org.ericlee.dalamb.engine.listener.http.HttpException;

public class DalambHandler implements HttpHandler {
    private final ConfigurationFormat configuration;
    private final ActionManager actionManager;
    private final RequestMapper requestMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    public DalambHandler(ConfigurationFormat configuration) {
        this.configuration = configuration;
        this.actionManager = new ActionManager(configuration);
        this.requestMapper = new RequestMapper(actionManager, configuration.getBindings());
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        try {
            RequestMapper.ActionAndParamEntry entry = requestMapper.getMappedAction(exchange.getRequestMethod().toString(),
                    exchange.getRequestPath());

            if (entry == null) throw new HttpException(404, "Page not found :(");

            // query parser here

            // body parser here

            System.out.printf("Qualified action found:\n * method: %s\n * parameters: %s\n", entry.getAction(), entry.getParams());

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(objectMapper.writeValueAsString(entry.getAction().invoke(entry.getRuntimeInstance())));
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
