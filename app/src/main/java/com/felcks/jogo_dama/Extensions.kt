/** Created by Felcks on 01/10/2017. ...*/
package com.felcks.jogo_dama

public inline fun <reified INNER> array2d(sizeOuter: Int, sizeInner: Int, noinline innerInit: (Int)->INNER): Array<Array<INNER>>
        = Array(sizeOuter) { Array<INNER>(sizeInner, innerInit) }

