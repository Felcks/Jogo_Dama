package com.felcks.jogo_dama

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun player_player(view: View){
        val intent = Intent(this, SimpleGameEngine::class.java)
        startActivity(intent)
        finish()
    }

    fun player_IA_easy(view: View){
        val intent = Intent(this, SimpleGameEngine::class.java)
        val b: Bundle = Bundle()
        b.putInt("dificult", 4)
        intent.putExtras(b)
        startActivity(intent)
        finish()
    }

    fun player_IA_medium(view: View){
        val intent = Intent(this, SimpleGameEngine::class.java)
        val b: Bundle = Bundle()
        b.putInt("dificult", 8)
        intent.putExtras(b)
        startActivity(intent)
        finish()
    }

    fun player_IA_hard(view: View){
        val intent = Intent(this, SimpleGameEngine::class.java)
        val b: Bundle = Bundle()
        b.putInt("dificult", 12)
        intent.putExtras(b)
        startActivity(intent)
        finish()
    }

}
