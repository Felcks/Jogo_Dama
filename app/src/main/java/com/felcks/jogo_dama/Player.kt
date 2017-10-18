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

    var possibilities = mutableListOf<Move>()
    var hasSelectedPiece: Boolean = false
    var clickedPiece: Piece? = null
    var eatenPieces = 0
    var playing = false

    init{
        for(i in 0..board.rows-1) {
            for (j in 0..board.columns - 1) {

                if((i + j) % 2 == 1) {
                    if(i >= 7 && playerSide == PlayerSide.BOTTOM){
                        board.pieces[i][j] = Piece(PieceType.PEAO, pieceColor)
                    }
                    else if(i <= 0 && playerSide == PlayerSide.TOP){
                        board.pieces[i][j] = Piece(PieceType.PEAO, pieceColor)
                    }
                }
            }
        }
    }

    fun draw(canvas: Canvas){

        if(playerType == PlayerType.MACHINE)
            return

        try {
            for(poss in possibilities){

                val rect = board.getRectBasedOnBoardPos(poss.x, poss.y)
                val paint = Paint()
                val alpha = getMoveAlpha(poss.order)
                paint.color = Color.argb(alpha, 255, 50, 50)

                canvas.drawRect(rect, paint)
            }
        }catch(e: ConcurrentModificationException){

        }
    }

    fun onTouch(x: Int, y: Int){

        if(playerType == PlayerType.HUMAN){

            if(hasSelectedPiece){
                for(move in possibilities) {
                    if (move.order == MoveOrder.PRIMARY) {
                        val rect = board.getRectBasedOnBoardPos(move.x, move.y)
                        if (x > rect.left && x < rect.right && y > rect.top && y < rect.bottom) {

                            var aux: Move? = move
                            while (aux?.prev != null) {
                                aux = aux.prev as Move
                            }


                            do {
                                board.pieces[aux!!.x][aux!!.y] = board.pieces[aux.oldX][aux.oldY]
                                board.pieces[aux!!.oldX][aux!!.oldY] = null
                                if(aux.type == MoveType.EAT){
                                    board.eatPiece(aux)
                                    eatenPieces++
                                }
                                aux = aux.next
                            } while (aux != null)

                            gameManager.changePlayerTurn(this)


                            if (isOnOpponentBoardEdge(move.x, move.y)) {
                                turnBoss(move.x, move.y)
                            }
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

                            this.possibilities = calculatePossibilities(i, j, board.pieces[i][j], 0, this) as MutableList<Move>
                            hasSelectedPiece = true
                            clickedPiece = board.pieces[i][j]
                            return
                        }
                    }
                }
            }
        }
    }

    private fun calculatePossibilities(i: Int, j: Int, piece: Piece?, blockQuad: Int, player: Player): List<Move>{

       // possibilities.clear()
        val possibilities: MutableList<Move> = mutableListOf()

        var possib_x = arrayOf(-1)
        var possib_y = arrayOf(-1)

        if(piece == null)
            return possibilities

        if(piece.type == PieceType.DAMA){
            var count: Int = 1
            var blockQuad1 = blockQuad == 1
            var blockQuad2 = blockQuad == 2
            var eatQuad1 = false
            var eatQuad2 = false

            for(x in i-1 downTo 0){

                if(board.isValidPos(x, j-count) == true && blockQuad1 == false) {
                    if (board.hasMyPieceOnPos(x, j - count, player) == true)
                        blockQuad1 = true
                    else if(board.hasPieceOnPos(x, j - count) && eatQuad1 == false)
                        eatQuad1 = true
                    else if(eatQuad1 && board.hasPieceOnPos(x, j - count) == false) {
                        blockQuad1 = true
                        val move = Move(i, j, x, j - count, MoveType.EAT, MoveOrder.PRIMARY)
                        possibilities.add(move)
                        possibilities.addAll(calculateMovePossib(x, j - count, move, player))
                    }
                    else if(board.hasPieceOnPos(x, j - count) == true && eatQuad1 == true)
                        blockQuad1 = true
                }
                else
                    blockQuad1 = true

                if(board.isValidPos(x, j + count) == true && blockQuad2 == false) {
                    if (board.hasMyPieceOnPos(x, j + count, player) == true)
                        blockQuad2 = true
                    else if(board.hasPieceOnPos(x, j + count) && eatQuad2 == false)
                        eatQuad2 = true
                    else if(eatQuad2 && board.hasPieceOnPos(x, j + count) == false) {
                        blockQuad2 = true
                        val move = Move(i, j, x, j + count, MoveType.EAT, MoveOrder.PRIMARY)
                        possibilities.add(move)
                        possibilities.addAll(calculateMovePossib(x, j + count, move, player))
                    }
                    else if(board.hasPieceOnPos(x, j + count) == true && eatQuad2 == true)
                        blockQuad2 = true
                }
                else
                    blockQuad2 = true


                if(blockQuad1 == false && eatQuad1 == false)
                    possibilities.add(Move(i, j, x, j - count, MoveType.MOVEMENT, MoveOrder.PRIMARY))

                if(blockQuad2 == false && eatQuad2 == false)
                    possibilities.add(Move(i, j, x, j + count, MoveType.MOVEMENT, MoveOrder.PRIMARY))

                count++
            }

            count = 1
            blockQuad1 = blockQuad == 3
            blockQuad2 = blockQuad == 4
            eatQuad1 = false
            eatQuad2 = false
            for(x in i+1..board.rows-1){

                if(board.isValidPos(x, j-count) == true && blockQuad1 == false) {
                    if (board.hasMyPieceOnPos(x, j - count, player) == true)
                        blockQuad1 = true
                    else if(board.hasPieceOnPos(x, j - count) && eatQuad1 == false)
                        eatQuad1 = true
                    else if(eatQuad1 && board.hasPieceOnPos(x, j - count) == false) {
                        blockQuad1 = true
                        val move = Move(i, j, x, j - count, MoveType.EAT, MoveOrder.PRIMARY)
                        possibilities.add(move)
                        possibilities.addAll(calculateMovePossib(x, j - count, move, player))
                    }
                    else if(board.hasPieceOnPos(x, j - count) == true && eatQuad1 == true)
                        blockQuad1 = true
                }
                else
                    blockQuad1 = true


                if(board.isValidPos(x, j + count) == true && blockQuad2 == false) {
                    if (board.hasMyPieceOnPos(x, j + count, player) == true)
                        blockQuad2 = true
                    else if(board.hasPieceOnPos(x, j + count) && eatQuad2 == false)
                        eatQuad2 = true
                    else if(eatQuad2 && board.hasPieceOnPos(x, j + count) == false) {
                        blockQuad2 = true
                        val move = Move(i, j, x, j + count, MoveType.EAT, MoveOrder.PRIMARY)
                        possibilities.add(move)
                        possibilities.addAll(calculateMovePossib(x, j + count, move, player))
                    }
                    else if(board.hasPieceOnPos(x, j + count) == true && eatQuad2 == true)
                        blockQuad2 = true
                }
                else
                    blockQuad2 = true


                if(blockQuad1 == false && eatQuad1 == false)
                    possibilities.add(Move(i, j, x, j - count, MoveType.MOVEMENT, MoveOrder.PRIMARY))

                if(blockQuad2 == false && eatQuad2 == false)
                    possibilities.add(Move(i, j, x, j + count, MoveType.MOVEMENT, MoveOrder.PRIMARY))
                count++
            }
        }
        else {
            if (player.playerSide == PlayerSide.BOTTOM) {
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
                            possibilities.add(Move(i, j, x, y, MoveType.MOVEMENT, MoveOrder.PRIMARY))
                        }
                    }
                }
            }

            possib_x = arrayOf(i - 1, i + 1)
            possib_y = arrayOf(j - 1, j + 1)
            for(x in possib_x) {
                for (y in possib_y) {
                    if (board.isValidPos(x, y)) {
                        if (board.hasEnemyPieceOnPos(x, y, player)) {
                            if (board.isValidPos(x + (x - i), y + (y - j)))
                                if (board.hasPieceOnPos(x + (x - i), y + (y - j)) == false) {
                                    possibilities.removeAll(removeMovementsFromPossibilities(possibilities))
                                    val move = Move(i, j, x + (x - i), y + (y - j), MoveType.EAT, MoveOrder.PRIMARY)
                                    possibilities.add(move)
                                    possibilities.addAll(calculateMovePossib(x + (x - i), y + (y - j), move, player))
                                }
                        }
                    }
                }
            }

        }

        return possibilities
    }

    private fun calculateMovePossib(i: Int, j: Int,
                                    antMove: Move,
                                    player: Player): List<Move>{

        val possibilities = mutableListOf<Move>()

        val possib_x = arrayOf(i - 1, i + 1)
        val possib_y = arrayOf(j - 1, j + 1)
        for(x in possib_x) {
            for (y in possib_y) {
                if (board.isValidPos(x, y)) {
                    if (board.hasEnemyPieceOnPos(x, y, player)) {
                        val nextX = x + (x - i)
                        val nextY = y + (y - j)
                        if (board.isValidPos(nextX, nextY)) {
                            if (board.hasPieceOnPos(nextX, nextY) == false) {
                                if(hasMoveOnPos(nextX, nextY) == false) {
                                    val move = Move(i, j, nextX, nextY, MoveType.EAT, MoveOrder.PRIMARY)
                                    move.prev = antMove
                                    antMove.next = move
                                    antMove.order = MoveOrder.SECUNDARY

                                    possibilities.add(move)
                                    possibilities.addAll(calculateMovePossib(nextX, nextY, move, player))
                                }
                            }
                        }
                    }
                }
            }
        }

        return possibilities;
    }

    fun alphaBeta(pieces:  Array<Array<Piece?>>, ply: Int, opponent: Player){

        //return score if game.isOver

        val moveScore = ab(pieces, ply, this, opponent, -999, 999)

        var aux: Move? = moveScore?.move
        while (aux?.prev != null) {
            aux = aux.prev as Move
        }

        if(aux != null) {
            do {
                board.pieces[aux!!.x][aux!!.y] = board.pieces[aux.oldX][aux.oldY]
                board.pieces[aux!!.oldX][aux!!.oldY] = null
                if (aux.type == MoveType.EAT) {
                    board.eatPiece(aux)
                    eatenPieces++
                }
                aux = aux.next
            } while (aux != null)

            gameManager.changePlayerTurn(this)

            if (moveScore?.move != null) {
                val move = moveScore.move
                if (move != null)
                    if (isOnOpponentBoardEdge(move.x, move.y)) {
                        turnBoss(move.x, move.y)
                    }
            }
        }

        Log.i("script", "made movement")

        //Pegar todas as possibilidades de movimento
    }

    fun score(player: Player, pieces: Array<Array<Piece?>>): Int{

        var scr : Int = 0
        var win : Int = 500
        for(i in 0..board.rows-1) {
            for (j in 0..board.columns - 1) {
                if (board.hasMyPieceOnPos2(i, j, player, pieces)) {
                    scr += 1
                }
                else if(board.hasEnemyPieceOnPos2(i, j, player, pieces)){
                    scr -= 1
                    win = 0
                }
            }
        }

        //Log.i("script", "$scr");
        scr += win
        //if(player.playerType == PlayerType.HUMAN)
          //  scr *= -1

        return scr
    }

    fun ab(pieces:  Array<Array<Piece?>>, ply: Int, player: Player, opponent: Player, low: Int, high: Int): MoveScore?{
        var bestScore = -999
        var bestMove : MoveScore? = MoveScore(null, -999)
        var mLow = low
        var mHigh = high

        if(ply == 0){
            val score: Int = score(player, pieces)
            bestMove = MoveScore(null, score)
            return bestMove
        }

        for(i in 0..board.rows-1){
            for(j in 0..board.columns-1){
                if(board.hasMyPieceOnPos2(i, j, player, pieces)) {
                    val possib = calculatePossibilities(i, j, pieces[i][j], 0, player)
                    //var possib = mutableListOf<Move>()
                    //possib = possibilities

                    try {
                        var m: Move
                        for (m in possib) {
                            if (m.order == MoveOrder.PRIMARY) {

                                val newPieces = executeFakeMove(m, pieces)
                                val oldPieces = board.pieces
                                board.pieces = newPieces
                                val moveScore = ab(newPieces, ply - 1, opponent, player, -high, -low)
                                board.pieces = oldPieces

                                //if(m == )
                                if(m.type == MoveType.EAT){
                                    moveScore!!.score *= 10 * ply
                                }

                                // / if(m.type == MoveType.EAT)
                                   // moveScore
                                //score(player, newPieces)
                                if (moveScore != null && bestMove != null) {

                                    //if(moveScore.move?.type == MoveType.EAT){
                                      //  moveScore.score *= 10 * ply
                                    //}

                                    if (-moveScore.score > bestMove.score) {
                                        mLow = -moveScore.score
                                        bestMove.move = m
                                        bestMove.score = mLow


                                    }
                                    //if (mLow >= mHigh)
                                      //  return bestMove
                                }
                            }
                        }
                    }catch(e: ConcurrentModificationException){
                        Log.i("script", "concurrent exception ${e.message}")
                    }
                }
            }
        }

        return bestMove
    }

    private fun hasMoveOnPos(x: Int, y: Int): Boolean{
        for(possib in possibilities){
            if(possib.x == x && possib.y == y)
                return true
        }

        return false
    }


    private fun removeMovementsFromPossibilities(possibilities: MutableList<Move>): List<Move> {

        val possib = mutableListOf<Move>()
        try {
            var count: Int = 0
            for (x in 0..possibilities.size-1) {
                    if (possibilities[x - count].type == MoveType.MOVEMENT) {
                        possib.add(possibilities.get(x - count))
                        //possibilities.removeAt(x - count)
                        //count++
                    }
            }
        }
        catch(e : ConcurrentModificationException){

        }
        finally {
            return possibilities
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

    private fun getMoveAlpha(moveOrder: MoveOrder) = when(moveOrder){
        MoveOrder.PRIMARY -> 255
        MoveOrder.SECUNDARY -> 150
    }

    private fun executeFakeMove(move: Move,  pieces: Array<Array<Piece?>>): Array<Array<Piece?>>{
        var aux: Move? = move
        var mPieces = com.felcks.jogo_dama.array2d<Piece?>(board.rows, board.columns) { null }
        for(i in 0..board.rows-1){
            for(j in 0..board.columns-1){
                mPieces[i][j] = pieces[i][j]
            }
        }
        while (aux?.prev != null) {
            aux = aux.prev as Move
        }


        do {
            mPieces[aux!!.x][aux!!.y] = mPieces[aux.oldX][aux.oldY]
            mPieces[aux!!.oldX][aux!!.oldY] = null
            if(aux.type == MoveType.EAT){
                mPieces = board.fakeEatPiece(aux, mPieces)
                eatenPieces++
            }
            aux = aux.next
        } while (aux != null)

        if (isOnOpponentBoardEdge(move.x, move.y)) {
            turnBoss(move.x, move.y)
        }

        return mPieces
    }

}