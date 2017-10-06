/** Created by Felcks on 23/09/2017. ...*/
package com.felcks.jogo_dama

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameManager(context: Context) : SurfaceView(context), Runnable{

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
    val player_turn : Player

    var currTimeLapse: Int = 10
    val delay: Int = 10;

    init {
        board = Board(8,8)
        player_1 = Player(board, PieceColor.WHITE, PlayerType.HUMAN, PlayerSide.BOTTOM)
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

            canvas?.drawColor(Color.argb(255, 255, 0,0))

            board.draw(canvas!!)
            player_1.draw(canvas!!)

            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    fun update(){

    }

    public override fun onTouchEvent(event: MotionEvent): Boolean {

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


