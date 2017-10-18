/** Created by Felcks on 23/09/2017. ...*/
package com.felcks.jogo_dama

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameManager(context: Context, val dificult: Int) : SurfaceView(context), Runnable{

    val surfaceHolder : SurfaceHolder by lazy<SurfaceHolder>{
        holder
    }
    val paint : Paint by lazy<Paint>{
        Paint()
    }

    var isPlaying : Boolean = true;
    var gameThread : Thread? = null
    var canvas: Canvas? = null
    val board : Board
    val player_1 : Player
    val player_2 : Player
    var player_turn : Player

    var currTimeLapse: Int = 10
    val delay: Int = 10;

    init {
        board = Board(8,8)
        player_1 = Player(board, PieceColor.WHITE, PlayerType.HUMAN, PlayerSide.BOTTOM, this)

        if(dificult == 0)
            player_2 = Player(board, PieceColor.BLACK, PlayerType.HUMAN, PlayerSide.TOP, this)
        else
            player_2 = Player(board, PieceColor.BLACK, PlayerType.MACHINE, PlayerSide.TOP, this)

        player_turn = player_1
    }

    override fun run() {
            while(isPlaying){

            if(currTimeLapse >= delay) {
                draw()
                update()
                currTimeLapse = 0
            }
            else{
                currTimeLapse++
            }
        }
    }

    fun draw(){

        if(surfaceHolder.surface.isValid){

            canvas = surfaceHolder.lockCanvas()

            canvas?.drawColor(Color.argb(255, 0, 0,0))

            board.draw(canvas!!)
            player_1.draw(canvas!!)
            player_2.draw(canvas!!)

            var textPlayer1 = ""
            var textPlayer2 = ""
            if(player_turn == player_1){
                textPlayer1 = "Seu turno!"
            }
            else{
                textPlayer2 = "Seu turno!"
            }

            val paint: Paint = Paint()
            paint.color = Color.argb(255, 255, 255, 255)
            paint.textSize = 20.toFloat()
            canvas?.drawText("Destruiu: ${player_1.eatenPieces}/12",  SCREEN_WIDTH/2.toFloat() - board.squareSize ,
                                            SCREEN_HEIGHT.toFloat() - 50, paint)
            canvas?.drawText(textPlayer1,  SCREEN_WIDTH/2.toFloat() - board.squareSize ,
                    SCREEN_HEIGHT.toFloat() - 100, paint)

            canvas?.save();
            canvas?.rotate(-180f, SCREEN_WIDTH/2.toFloat() - board.squareSize, 50f);
            canvas?.drawText("Destruiu: ${player_2.eatenPieces}/12",  SCREEN_WIDTH/2.toFloat() - 3*board.squareSize ,
                    50f, paint)
            canvas?.drawText(textPlayer2,  SCREEN_WIDTH/2.toFloat() - 3*board.squareSize ,
                    0f, paint)
            canvas?.restore()


            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    fun update(){
        if(player_turn.playerType == PlayerType.MACHINE && player_turn.playing == false){
            player_turn.alphaBeta(board.pieces, dificult, player_1)
            player_turn.playing = true
        }
    }

    fun changePlayerTurn(player: Player){
        if(player == player_1)
            player_turn = player_2
        else
            player_turn = player_1
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if(event.action == MotionEvent.ACTION_DOWN){

            val x = event.getX().toInt()
            val y = event.getY().toInt()

            player_turn.onTouch(x, y)

            return true
        }

        return false
    }

    public fun resume(){

        isPlaying = true
        gameThread = Thread(this)
        gameThread?.start()
    }

    public fun pause(){
        isPlaying = false;
        try{
            gameThread?.join()
        }
        catch(e : InterruptedException){
            Log.e("Error", "joining thread")
        }
    }

}


