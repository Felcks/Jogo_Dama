/** Created by Felcks on 23/09/2017. ...*/
package com.felcks.jogo_dama
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.SurfaceHolder

class Board(val rows: Int, val columns: Int) {

    var colorWhiteSquare : Int = Color.argb(255, 255, 255, 255)
    var colorDarkSquare : Int = Color.argb(255, 66, 76, 88)
    val squareSize : Int = SCREEN_WIDTH / rows
    val startX : Int
    val startY : Int
    public val pieces = array2d<Piece?>(rows, columns) { null }

    init{
        startX = (SCREEN_WIDTH - (rows * squareSize)) / 2
        startY = (SCREEN_HEIGHT - (columns * squareSize)) / 2
    }

    fun draw(canvas : Canvas){

        val paint: Paint = Paint()
        var rect: Rect = Rect(0, 0, 0, 0)

        for(i in 0..rows-1){
            for(j in 0..columns-1){

                rect = getRectBasedOnBoardPos(i, j)

                if((i + j) % 2 == 0){
                    paint.color = colorWhiteSquare
                }
                else
                    paint.color = colorDarkSquare

                canvas.drawRect(rect, paint)


                drawPieces(canvas, paint, i, j)
            }
        }
    }

    private fun drawPieces(canvas: Canvas, paint: Paint, i: Int, j: Int){

        val posX: Float  = ((j * squareSize + startX) + squareSize/2).toFloat()
        val posY: Float  = ((i * squareSize + startY) + squareSize/2).toFloat()

        if((i + j) % 2 == 1 && pieces[i][j] != null) {
            paint.color = pieces[i][j]!!.color.rgb
            canvas.drawCircle(posX, posY, squareSize.toFloat()/2.5f, paint)

            if(pieces[i][j]?.type == PieceType.DAMA){
                paint.color = colorDarkSquare
                canvas.drawCircle(posX, posY, squareSize.toFloat()/3.5f, paint)
            }
        }
    }

    public fun getRectBasedOnBoardPos(i: Int, j: Int): Rect{
        val posX  = j * squareSize + startX
        val posY  = i * squareSize + startY

        return Rect(posX, posY, posX + squareSize, posY + squareSize)
    }

    fun hasMyPieceOnPos(i: Int, j: Int, player: Player) : Boolean{

        if(pieces[i][j] != null) {
            if (pieces[i][j]?.color == player.pieceColor)
                return true
        }

        return false
    }

    fun hasEnemyPieceOnPos(i: Int, j: Int, player: Player) : Boolean{

        if(pieces[i][j] != null) {
            if (pieces[i][j]?.color != player.pieceColor)
                return true
        }

        return false
    }

    fun hasPieceOnPos(i: Int, j: Int) : Boolean{
        if(pieces[i][j] != null) {
                return true
        }

        return false
    }

    public fun isValidPos(i: Int, j: Int): Boolean{
        if(i >=0 && i < rows && j >= 0 && j < columns)
            return true

        return false
    }
}