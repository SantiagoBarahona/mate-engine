package com.mate_engine.engine.piece;

import com.mate_engine.engine.role.Role;

public class Piece {

    private boolean white;
    private Role role;

    public Piece(Role role, boolean white){
        this.role = role;
        this.white = white;
    }

    public boolean isWhite() {
        return white;
    }

    public Role getRole() {
        return role;
    }
}
