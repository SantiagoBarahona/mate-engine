package com.mate_engine.server;

import com.mate_engine.engine.bitboard.Bitboard;
import com.mate_engine.engine.board.Board;
import com.mate_engine.engine.move.Move;
import com.mate_engine.engine.role.Role;

public class BoardServer {

    public final Board board;
    public final String id;

    public BoardServer(String boardId) {
        this.id = boardId;
        this.board = new Board();
    }

    private String calculatePieceMoves(int pieceAt){
        return Bitboard.calculateActiveSquaresIndex(this.board.legalMovesAt(pieceAt));
    }

    private void playMove(Move move){
        this.board.play(move);
    }
}
