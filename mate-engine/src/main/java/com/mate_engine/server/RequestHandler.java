package com.mate_engine.server;

import com.google.gson.Gson;
import com.mate_engine.engine.bitboard.Bitboard;
import com.mate_engine.engine.board.Board;
import com.mate_engine.engine.move.Move;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RequestHandler extends Thread {

    private final static Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
    HttpExchange request;
    private Gson g = new Gson();


    public RequestHandler(HttpExchange request) {
        this.request = request;
    }

    public void sendResponse(String response, Headers headers, int code, OutputStream responseStream) {
        headers.add("Access-Control-Allow-Origin", "http://localhost:4200");
        try {
            request.sendResponseHeaders(code, response.length());
        } catch (IOException e) {
            LOGGER.error("Error sending the response headers: " + e);
        }
        try {
            responseStream.write(response.getBytes());
        } catch (IOException e) {
            LOGGER.error("Error writing the response: " + e);
        }
        LOGGER.info("Request successfully handled. [RESPONSE]: " + response );
    }

    @Override
    public void run() {

        LOGGER.info(request.getRequestMethod() + " " + request.getRemoteAddress() + " " + request.getRequestURI());
        OutputStream responseStream = request.getResponseBody();
        InputStream requestStream = request.getRequestBody();
        String message = handleRequest(g.fromJson(readRequest(), Request.class));
        sendResponse(message, request.getResponseHeaders(), 200, responseStream);

        try {
            this.request.getResponseBody().close();
            this.request.getRequestBody().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleRequest(Request req) {
        switch (req.action){
            case "newConnection" :
                String uid = java.util.UUID.randomUUID().toString();
                Server.CONNECTIONS.put(uid, new BoardServer(uid));
                return uid;
            case "calculatePieceMoves" :
                return Bitboard.calculateActiveSquaresIndex(
                        Server.CONNECTIONS
                            .get(req.boardID).board.legalMovesAt(Integer.parseInt(req.data))
                );
            case "playMove" :
                Board a = Server.CONNECTIONS.get(req.boardID).board;
                a.play(g.fromJson(req.data, Move.class));
                System.out.println(a);
                return "OK";
            default:
                return "not a valid request";
        }
    }

    private String readRequest(){

        int bufferSize = 1024;
        char[] buffer = new char[bufferSize];
        StringBuilder out = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(request.getRequestBody(), StandardCharsets.UTF_8);
        try {
            for (int numRead; (numRead = reader.read(buffer, 0, buffer.length)) > 0; ) {
                out.append(buffer, 0, numRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }
}
