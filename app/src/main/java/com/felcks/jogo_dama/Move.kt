/** Created by Felcks on 01/10/2017. ...*/
package com.felcks.jogo_dama

class Move (val oldX: Int,
            val oldY: Int,
            val x: Int,
            val y: Int,
            val type: MoveType,
            var order: MoveOrder) {

    var prev: Move? = null
    var next: Move? = null
}