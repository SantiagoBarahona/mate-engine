package com.mate_engine.engine.board;

import com.mate_engine.engine.bitboard.Bitboard;
import com.mate_engine.engine.move.Move;
import com.mate_engine.engine.piece.Piece;
import com.mate_engine.engine.role.Role;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @org.junit.jupiter.api.Test
    void pawnAttacks() {
    }

    @org.junit.jupiter.api.Test
    void play() {
        Board board = new Board();
        toBinary(board.pawns * 0x2l);
        board.play(new Move("normal",27,11,new Piece(Role.PAWN, true), false, null));
        board.play(new Move("normal",36,52,new Piece(Role.PAWN, false), false, null));
    }

    private void toBinary(long n){
        String b =  Long.toBinaryString(n), aux = "\n- new board -\n";
        for (int i = 0; i < 64; i++) {
            if(i % 8 == 0 && i != 0)aux = "\n"+aux;
            if(i < b.length()){
                aux=b.charAt(b.length() - 1 - i)+aux;
            }else{
                aux="0"+aux;
            }
        }
        aux+= "\n- end -\n";
        System.out.println(aux);
    }
}