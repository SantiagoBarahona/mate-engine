package com.mate_engine.engine.board;

import com.mate_engine.engine.move.Move;
import com.mate_engine.engine.piece.Piece;
import com.mate_engine.engine.bitboard.Bitboard;
import com.mate_engine.engine.role.Role;
import com.mate_engine.engine.square.Square;

import java.util.HashMap;
import java.util.Locale;

public class Board {

    long pawns;
    long knights;
    long bishops;
    long rooks;
    long queens;
    long kings;

    long white;
    long black;
    long occupied;

    boolean turn;
    int epSquare;
    long castlingRights;

    int incrementalHash;

    public Board(){
        this.pawns = 0xff00000000ff00L;
        this.knights = 0x4200000000000042L;
        this.bishops = 0x2400000000000024L;
        this.rooks = 0x8100000000000081L;
        this.queens = 0x800000000000008L;
        this.kings = 0x1000000000000010L;

        this.white = 0xffffL;
        this.black = 0xffff000000000000L;
        this.occupied = 0xffff00000000ffffL;
    }

    @Override
    public String toString() {
        String board = "";
        for (int i = 0; i < 64; i++) {
            if(i % 8 == 0 && i != 64) board += "\n";
            int square = Square.mirror(i);
            if(roleAt(square) != null){
                String role = roleAt(square).toString();
                board += whiteAt(square) ? "[" + role.toUpperCase(Locale.ROOT) + "]," : "[" + role.toLowerCase(Locale.ROOT) + "],";
            }else{
                board += "[ ],";
            }
        }
        return board;
    }

    final static HashMap<Character, Role> ROLE_FROM_SYMBOL = new HashMap<>() {
        {put('k', Role.KING);}
        {put('q', Role.QUEEN);}
        {put('r', Role.ROOK);}
        {put('b', Role.BISHOP);}
        {put('n', Role.KNIGHT);}
        {put('p', Role.PAWN);}
    };

    public Role roleAt(int square) {
        if (Bitboard.contains(this.pawns, square)) return Role.PAWN;
        if (Bitboard.contains(this.knights, square)) return Role.KNIGHT;
        if (Bitboard.contains(this.bishops, square)) return Role.BISHOP;
        if (Bitboard.contains(this.rooks, square)) return Role.ROOK;
        if (Bitboard.contains(this.queens, square)) return Role.QUEEN;
        if (Bitboard.contains(this.kings, square)) return Role.KING;
        return null;
    }

    public boolean whiteAt(int square) {
        return Bitboard.contains(this.white, square);
    }

    public void loadPositionFromFEN(final String FEN){

        char [] fen = FEN.toCharArray();

        int file = 7, rank = 0;

        for(char symbol : fen){
            if(symbol == '/'){
                rank = 0;
                file--;
            }else{
                if(Character.isDigit(symbol)){
                    rank += Character.getNumericValue(symbol);
                }else{
                    boolean white = Character.isUpperCase(symbol);
                    Role role  = ROLE_FROM_SYMBOL.get(symbol);
                    Piece pieza = new Piece(role, white);
                    //TODO GUARDAR PIEZA EN CASILLA

                    rank++;
                }
            }
        }
    }

    public long legalMovesAt(int i){
        long index = 1L << i;
        int sq = Bitboard.msb(index);
        long them = (whiteAt(sq) ? ~this.white : ~this.black);
        switch (roleAt(i)){
            case PAWN:
                if(whiteAt(i)){
                    if(wPawnsAble2Push(index) == index){
                        if(wPawnsAble2DblPush(index) == index) {
                            return wSglPawnPushes(index) | wDblPawnPushes(index) | pawnAttacks(true, index);
                        }
                        return wSglPawnPushes(index) | pawnAttacks(true, index);
                    }
                }else{
                    if(bPawnsAble2Push(index) == index){
                        if(bPawnsAble2DblPush(index) == index) {
                            return  bSglPawnPushes(index) | bDblPawnPushes(index) | pawnAttacks(false, index);
                        }
                        return bSglPawnPushes(index) | pawnAttacks(false, index);
                    }
                }
                return pawnAttacks(whiteAt(i), index);
            case KNIGHT:
                return Bitboard.KNIGHT_MOVES[sq] & them;
            case ROOK:
                return  Bitboard.rookAttacks(sq, this.occupied) & them;
            case QUEEN:
                return  Bitboard.queenAttacks(sq, this.occupied) ;
            case KING:
                return Bitboard.KING_MOVES[sq] & them;
            case BISHOP:
                return  Bitboard.bishopAttacks(sq, this.occupied) & them;

        }
        return 0;
    }


    //PAWNS

    public long wSglPawnPushes(long wPawns){return Bitboard.north(wPawns) & ~occupied;}
    public long wDblPawnPushes(long wPawns){
        final long sglPushes = wSglPawnPushes(wPawns);
        return Bitboard.north(sglPushes) & ~occupied & Bitboard.RANK_4;
    }

    public long bSglPawnPushes(long bPawns){return Bitboard.south(bPawns) & ~occupied;}
    public long bDblPawnPushes(long bPawns){
        final long sglPushes = bSglPawnPushes(bPawns);
        return Bitboard.south(sglPushes) & ~occupied & Bitboard.RANK_5;
    }

    public long wPawnsAble2Push(long wPawns){return Bitboard.south(~occupied) & wPawns;}
    public long wPawnsAble2DblPush(long wPawns){
        long emptyRank3 = Bitboard.south(~occupied & Bitboard.RANK_4) & ~occupied;
        return Bitboard.south(emptyRank3) & wPawns;
    }

    public long bPawnsAble2Push(long bPawns){return Bitboard.north(~occupied) & bPawns;}
    public long bPawnsAble2DblPush(long bPawns){
        long emptyRank4 = Bitboard.north(~occupied & Bitboard.RANK_5) & ~occupied;
        return Bitboard.north(emptyRank4) & bPawns;
    }

    long pawnAttacks(boolean white, long b){
        return (white ? this.black : this.white) & (white ? Bitboard.WHITE_PAWN_ATTACKS : Bitboard.BLACK_PAWN_ATTACKS)[Bitboard.msb(b)];
    }

    private boolean isOccupied(int square) {
        return Bitboard.contains(this.occupied, square);
    }

    private void put(int square, boolean color, Role role) {
        discard(square);

        long mask = 1L << square;

        switch (role) {
            case PAWN: this.pawns ^= mask; break;
            case KNIGHT: this.knights ^= mask; break;
            case BISHOP: this.bishops ^= mask; break;
            case ROOK: this.rooks ^= mask; break;
            case QUEEN: this.queens ^= mask; break;
            case KING: this.kings ^= mask; break;
        }

        if (color) this.white ^= mask;
        else this.black ^= mask;

        this.occupied ^= mask;
    }

    private void discard(int square) {
        if (!isOccupied(square)) return;
        Role role = roleAt(square);
        long mask = 1L << square;

        switch (role) {
            case PAWN: this.pawns ^= mask; break;
            case KNIGHT: this.knights ^= mask; break;
            case BISHOP: this.bishops ^= mask; break;
            case ROOK: this.rooks ^= mask; break;
            case QUEEN: this.queens ^= mask; break;
            case KING: this.kings ^= mask; break;
        }

        boolean color = whiteAt(square);
        if (color) this.white ^= mask;
        else this.black ^= mask;

        this.occupied ^= mask;
    }

    public void play(Move move) {
        switch (move.getType()){
            case "normal" :
                    discard(move.getFrom());
                    put(
                        move.getTo(),
                        move.getPiece().isWhite(),
                        move.isPromotion() ? move.getPromotion() : move.getPiece().getRole());
                break;

        }
    }
}
