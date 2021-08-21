package com.mate_engine.engine.move;

import com.mate_engine.engine.piece.Piece;
import com.mate_engine.engine.role.Role;

public class Move {

    private String type;
    private int to, from;
    private Piece piece;
    private boolean capture;
    private Role promotion;

    public Move(String type, int to, int from, Piece piece, boolean capture, Role promotion) {
        this.type = type;
        this.to = to;
        this.from = from;
        this.piece = piece;
        this.capture = capture;
        this.promotion = promotion;
    }

    public int getTo() {
        return to;
    }

    public int getFrom() {
        return from;
    }

    public Piece getPiece() {
        return piece;
    }

    public boolean isCapture() {
        return capture;
    }

    public boolean isPromotion() {
        return promotion != null;
    }

    public Role getPromotion(){
        return  promotion;
    }

    public String getType() {
        return type;
    }
}
