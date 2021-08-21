package com.mate_engine.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {

    private final static Logger LOGGER = LoggerFactory.getLogger(Server.class);
    public final static HashMap<String, BoardServer> CONNECTIONS = new HashMap<>();

    public static void main(String[] args) {

        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress("localhost",7000),0);
            httpServer.createContext("/api/mate-engine", (exchange) -> {
                RequestHandler requestHandler = new RequestHandler(exchange);
                requestHandler.start();
            });
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
