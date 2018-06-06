package com.example.armageddon.legobricks

import android.graphics.Bitmap
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class BrickContainer{
    var itemId=""
    var itemType=""
    var qty=0
    var colorId=0
    var extra=""

    constructor(itemId:String,itemType:String,qty:Int,colorId:Int,extra:String){
        this.itemId=itemId
        this.itemType=itemType
        this.qty=qty
        this.colorId=colorId
        this.extra=extra
    }
}