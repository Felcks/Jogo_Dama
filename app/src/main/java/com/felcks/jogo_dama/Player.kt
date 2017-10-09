/** Created by Felcks on 01/10/2017. ...*/
package com.felcks.jogo_dama

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import java.util.*

class Player (val board: Board,
              val pieceColor: PieceColor,
              val playerType: PlayerType,
              val playerSide: PlayerSide,
              val gameManager: GameManager) {

    val possibilities = mutableListOf<Move>()
    var hasSelectedPiece: Boolean = false
    var clickedPiece: Piece? = null

    init{
        for(i in 0..board.rows-1) {
            for (j in 0..board.columns - 1) {

                if((i + j) % 2 == 1) {
                    if(i >= 5 && playerSide == PlayerSide.BOTTOM){
                        board.pieces[i][j] = Piece(PieceType.PEAO, pieceColor)
                    }
                    else if(i <= 2 && playerSide == PlayerSide.TOP){
                        board.pieces[i][j] = Piece(PieceType.PEAO, pieceColor)
                    }
                }
            }
        }
    }

    fun draw(canvas: Canvas){

        try {
            for(poss in possibilities){

                val rect = board.getRectBasedOnBoardPos(poss.x, poss.y)
                val paint = Paint()
                paint.color = Color.argb(255, 255, 50, 50)

                canvas.drawRect(rect, paint)
            }
        }catch(e: ConcurrentModificationException){

        }
    }

    fun onTouch(x: Int, y: Int){

        if(playerType == PlayerType.HUMAN){

            if(hasSelectedPiece){
                //Conferir se eu cliquei em alguma possibilidade!
                for(move in possibilities){
                    val rect = board.getRectBasedOnBoardPos(move.x, move.y)
                    if (x > rect.left && x < rect.right && y > rect.top && y < rect.bottom) {

                        board.pieces[move.x][move.y] = board.pieces[move.oldX][move.oldY]
                        board.pieces[move.oldX][move.oldY] = null
                        gameManager.changePlayerTurn(this)


                        if(isOnOpponentBoardEdge(move.x, move.y)){
                            turnBoss(move.x, move.y)
                        }
                    }
                }
            }


            hasSelectedPiece = false
            possibilities.clear()

            for(i in 0..board.rows-1) {
                for (j in 0..board.columns - 1) {

                    if(board.hasMyPieceOnPos(i, j, this)) {
                        val rect = board.getRectBasedOnBoardPos(i, j)
                        if (x > rect.left && x < rect.right && y > rect.top && y < rect.bottom) {

                            if(clickedPiece != null)
                                if(clickedPiece == board.pieces[i][j]) {
                                    clickedPiece = null
                                    possibilities.clear()
                                    return
                                }

                            calculatePossibilities(i, j, board.pieces[i][j])
                            hasSelectedPiece = true
                            clickedPiece = board.pieces[i][j]
                            return
                        }
                    }
                }
            }
        }
    }

    private fun calculatePossibilities(i: Int, j: Int, piece: Piece?){

        possibilities.clear()

        var possib_x = arrayOf(-1)
        var possib_y = arrayOf(-1)

        if(piece == null)
            return;

        if(piece.type == PieceType.DAMA){
            var count: Int = 1
            var blockQuad1 = false
            var blockQuad2 = false
            for(x in i-1 downTo 0){

                if(board.isValidPos(x, j-count) == true) {
                    if (board.hasMyPieceOnPos(x, j - count, this) == true)
                        blockQuad1 = true
                }
                else
                    blockQuad1 = true

                if(board.isValidPos(x, j+count) == true) {
                    if (board.hasMyPieceOnPos(x, j + count, this) == true)
                        blockQuad2 = true
                }
                else
                    blockQuad2 = true


                if(blockQuad1 == false)
                    possibilities.add(Move(i, j, x, j - count, MoveType.MOVEMENT))

                if(blockQuad2 == false)
                    possibilities.add(Move(i, j, x, j + count, MoveType.MOVEMENT))

                count++
            }

            count = 1
            blockQuad1 = false;
            blockQuad2 = false
            for(x in i+1..board.rows-1){

                if(board.isValidPos(x, j-count) == true) {
                    if (board.hasMyPieceOnPos(x, j - count, this) == true)
                        blockQuad1 = true
                }
                else
                    blockQuad1 = true

                if(board.isValidPos(x, j + count) == true) {
                    if (board.hasMyPieceOnPos(x, j + count, this) == true)
                        blockQuad2 = true
                }
                else
                    blockQuad2 = true


                if(blockQuad1 == false)
                    possibilities.add(Move(i, j, x, j - count, MoveType.MOVEMENT))

                if(blockQuad2 == false)
                    possibilities.add(Move(i, j, x, j + count, MoveType.MOVEMENT))
                count++
            }
        }
        else {
            if (playerSide == PlayerSide.BOTTOM) {
                possib_x = arrayOf(i - 1)
                possib_y = arrayOf(j - 1, j + 1)
            } else {
                possib_x = arrayOf(i + 1)
                possib_y = arrayOf(j - 1, j + 1)
            }

            for(x in possib_x){
                for(y in possib_y) {

                    if (board.isValidPos(x, y)) {
                        if (board.hasPieceOnPos(x,y) == false) {
                            possibilities.add(Move(i, j, x, y, MoveType.MOVEMENT))
                        }
                        else{
                            if(board.hasEnemyPieceOnPos(x, y, this)){
                                possibilities.add(Move(i, j, x + (x-i), y + (y-j), MoveType.MOVEMENT))
                            }
                        }
                    }
                }
            }
        }


    }

    private fun isOnOpponentBoardEdge(row: Int, column: Int): Boolean{
        if(pieceColor == PieceColor.WHITE){
            if(row == 0){
                return true
            }
        }
        else if(pieceColor == PieceColor.BLACK){
            if(row == board.rows - 1){
                return true
            }
        }

        return false
    }

    private fun turnBoss(row: Int, column: Int){
        board.pieces[row][column]?.turnBoss();
    }

}