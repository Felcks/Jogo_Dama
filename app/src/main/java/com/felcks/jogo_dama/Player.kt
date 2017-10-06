/** Created by Felcks on 01/10/2017. ...*/
package com.felcks.jogo_dama

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log

class Player (val board: Board,
              val pieceColor: PieceColor,
              val playerType: PlayerType,
              val playerSide: PlayerSide) {

    val possibilities = mutableListOf<Move>()
    var hasSelectedPiece: Boolean = false

    init{
        for(i in 0..board.rows-1) {
            for (j in 0..board.columns - 1) {

                if((i + j) % 2 == 1) {
                    if(i >= 6 && playerSide == PlayerSide.BOTTOM){
                        board.pieces[i][j] = Piece(PieceType.PEAO, pieceColor)
                    }
                    else if(i <= 1 && playerSide == PlayerSide.TOP){
                        board.pieces[i][j] = Piece(PieceType.PEAO, pieceColor)
                    }
                }
            }
        }
    }

    public fun draw(canvas: Canvas){

        for(poss in possibilities){

            val rect = board.getRectBasedOnBoardPos(poss.x, poss.y)
            val paint = Paint()
            paint.color = Color.argb(255, 255, 50, 50)

            canvas.drawRect(rect, paint)
        }
    }

    public fun onTouch(x: Int, y: Int){

        if(playerType == PlayerType.HUMAN){

            if(hasSelectedPiece){
                //Conferir se eu cliquei em alguma possibilidade!
                for(move in possibilities){
                    val rect = board.getRectBasedOnBoardPos(move.x, move.y)
                    if (x > rect.left && x < rect.right && y > rect.top && y < rect.bottom) {

                        board.pieces[move.x][move.y] = board.pieces[move.oldX][move.oldY]
                        board.pieces[move.oldX][move.oldY] = null
                    }
                }
            }


            hasSelectedPiece = false
            for(i in 0..board.rows-1) {
                for (j in 0..board.columns - 1) {

                    if(board.hasMyPieceOnPos(i, j, this)) {
                        val rect = board.getRectBasedOnBoardPos(i, j)
                        if (x > rect.left && x < rect.right && y > rect.top && y < rect.bottom) {

                            calculatePossibilities(i, j)
                            hasSelectedPiece = true
                            return
                        }
                    }
                }
            }
        }
    }

    private fun calculatePossibilities(i: Int, j: Int){

        possibilities.clear()

        var possib_x = arrayOf(-1)
        var possib_y = arrayOf(-1)

        Log.i("script", "$i $j")

        if(playerSide == PlayerSide.BOTTOM) {
            possib_x = arrayOf(i-1)
            possib_y = arrayOf(j-1, j+1)
        }
        else{
            possib_x = arrayOf(i+1)
            possib_y = arrayOf(j-1, j+1)
        }

        for(x in possib_x){
            for(y in possib_y) {

                if (board.isValidPos(x, y)) {
                    if (board.pieces[x][y] == null) {
                        possibilities.add(Move(i, j, x, y, MoveType.MOVEMENT))
                    }
                }
            }
        }
        /*if(i - 1 >= 0 && j - 1 >= 0){

            Log.i("script", "caaado")
            if(board.pieces[i-1][j-1] == null){
                //Nao tem peça nessa posição
                possibilities.add(Move(i-1, j-1, MoveType.MOVEMENT))
                Log.i("script", "calculou um movemento valido")
            }
        }*/
    }

}