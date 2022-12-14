package org.ericlee.dalamb.engine;

import io.undertow.Undertow;
import org.ericlee.dalamb.engine.configuration.ConfigurationFormat;
import org.ericlee.dalamb.engine.listener.DalambHandler;

import java.io.File;
import java.io.IOException;

public class Dalamb {
    public static void main(String[] args) {
        ConfigurationFormat configuration;
        try {
            configuration = ConfigurationFormat.from(new File(args[0]));
        } catch (IOException exception) {
            System.out.println("Configuration file not found.");
            return;
        }

        Undertow server = Undertow.builder()
                .addHttpListener(Integer.parseInt(args[1]), "127.0.0.1")
                .setHandler((exchange) ->
                        exchange.getRequestReceiver().receiveFullBytes(new DalambHandler(configuration)))
                .build();

        server.start();

        System.out.println("Dalamb has started.");
    }
}
