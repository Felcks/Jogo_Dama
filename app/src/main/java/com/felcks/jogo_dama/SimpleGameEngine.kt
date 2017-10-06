package com.felcks.jogo_dama

import android.app.Activity
import android.graphics.Point
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Display

class SimpleGameEngine : Activity() {

    var gameManager : GameManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val display : Display = windowManager.defaultDisplay
        val size : Point = Point()
        display.getSize(size)
        SCREEN_WIDTH = size.x
        SCREEN_HEIGHT = size.y

        gameManager = GameManager(this)
        setContentView(gameManager)
    }

    override fun onResume(){
        super.onResume()

        gameManager?.resume()
    }

    override fun onPause(){
        super.onPause()

        gameManager?.pause()
    }
}
