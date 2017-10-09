/** Created by Felcks on 01/10/2017. ...*/
package com.felcks.jogo_dama

class Piece(var type: PieceType, val color: PieceColor) {

    public fun turnBoss(){
        this.type = PieceType.DAMA
    }
}