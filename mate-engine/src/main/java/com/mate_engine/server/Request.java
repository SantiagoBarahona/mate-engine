package com.mate_engine.server;

public class Request {

    public final String action, data, boardID;

    public Request(String action, String data, String boardID) {
        this.action = action;
        this.data = data;
        this.boardID = boardID;
    }
}
