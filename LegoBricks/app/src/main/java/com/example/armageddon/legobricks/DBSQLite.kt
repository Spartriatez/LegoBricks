package com.example.armageddon.legobricks

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


public class DBSQLite(private val context: Context) {
    public var myDB:SQLiteDatabase?=null
    companion object {

        private val DB_NAME = "BrickList.db"
    }

    fun openDatabase(): SQLiteDatabase {
        val dbFile = context.getDatabasePath(DB_NAME)


        if (!dbFile.exists()) {
            try {
                val checkDB = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null)
                myDB=checkDB
                checkDB?.close()
                copyDatabase(dbFile)
            } catch (e: IOException) {
                throw RuntimeException("Error creating source database", e)
            }

        }
        return SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
    }

    @SuppressLint("WrongConstant")
    private fun copyDatabase(dbFile: File) {
        val ins = context.assets.open(DB_NAME)
        val os = FileOutputStream(dbFile)

        val buffer = ByteArray(1024)
        while (ins.read(buffer) > 0) {
            os.write(buffer)
            Log.d("#DB", "writing>>")
        }

        os.flush()
        os.close()
        ins.close()
        Log.d("#DB", "completed..")
    }

    public fun getAllProj() {
        val query="SELECT Name FROM 'Inventories'"
        val db=context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null)
        val curs= db.rawQuery(query,null)
        Log.e("data",curs.count.toString())
        db.close()
    }

    public fun checkExistProj(name:String):Int{
        val query="SELECT Name FROM 'Inventories' WHERE Name='$name';"
        val db=context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null)
        val curs= db.rawQuery(query,null)
        return curs.count
    }
    public fun checkExistBricksOnlyId(id:String):Int{
        val query="SELECT * FROM 'Codes' WHERE ItemId=(SELECT id FROM 'Parts' WHERE Code='$id');"
        val db=context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null)
        val curs= db.rawQuery(query,null)
        return curs.count
    }
    public fun checkExistBricks(id:String):Int{
        val query="SELECT id FROM 'Parts' WHERE Code='$id';"
        val db=context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null)
        val curs= db.rawQuery(query,null)
        return curs.count
    }

    public fun getIdBricks(id:String,color:String): String {
        val c=color.toInt()
        val arr=ArrayList<String>()
        val query="SELECT Code FROM 'Codes' WHERE ItemId=(SELECT id FROM 'Parts' WHERE Code='$id') AND ColorId=$c;"
        val db=context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null)
        val curs= db.rawQuery(query,null)
        var codeBrick:String=""
        while(curs.moveToNext()){
            var tmp=curs.getInt(0)
            codeBrick=tmp.toString()
        }
        return codeBrick
    }
    public fun getIdBricksWithoutColor(id:String): String {
        val arr=ArrayList<String>()
        val query="SELECT Code FROM 'Codes' WHERE ItemId=(SELECT id FROM 'Parts' WHERE Code='$id');"
        val db=context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null)
        val curs= db.rawQuery(query,null)
        var codeBrick:String=""
        while(curs.moveToNext()){
            var tmp=curs.getInt(0)
            codeBrick=tmp.toString()
            break;
        }
        return codeBrick
    }
    /*----------------------------------------------------------*/
    public fun getDataBricksWithColor(id:String,color:Int): ArrayList<Int> {
        val query="SELECT ItemID,ColorID FROM 'Codes' WHERE ItemID=(SELECT id FROM 'Parts' WHERE Code='$id') AND ColorID=$color and Image is null;"
        Log.e("query",query)
        val db=context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null)
        val curs= db.rawQuery(query,null)
        var codeBrick=ArrayList<Int>()
        while(curs.moveToNext()){
            var tmp=curs.getInt(0)
            codeBrick.add(tmp)
            var tmp2=curs.getInt(1)
            codeBrick.add(tmp2)
        }
        return codeBrick
    }

    public fun getDataBricksWithoutColor(id:String): ArrayList<Int> {
        val arr=ArrayList<String>()
        val query="SELECT ItemID,ColorID FROM 'Codes' WHERE ItemId=(SELECT id FROM 'Parts' WHERE Code='$id') AND Image is null;"
        val db=context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null)
        val curs= db.rawQuery(query,null)
        var codeBrick=ArrayList<Int>()
        while(curs.moveToNext()){
            var tmp=curs.getInt(0)
            codeBrick.add(tmp)
            var tmp2=curs.getInt(1)
            codeBrick.add(tmp2)
            break;
        }
        return codeBrick
    }
    public fun returnType(type:String):Int{
        val query="SELECT id FROM 'ItemTypes' WHERE Code='$type';"
        val db=context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null)
        val curs= db.rawQuery(query,null)
        var codeBrick=0
        while(curs.moveToNext()){
            codeBrick=curs.getInt(0)
        }
        return codeBrick
    }

    public fun returnColor(color:Int):Int{
        val query="SELECT id FROM 'Colors' WHERE Code='$color';"
        val db=context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null)
        val curs= db.rawQuery(query,null)
        var codeBrick=0
        while(curs.moveToNext()){
            codeBrick=curs.getInt(0)
        }
        return codeBrick
    }

    public fun getWithoutAll(id:String):Int{
        val query="SELECT id FROM 'Parts' WHERE Code='$id';"
        val db=context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null)
        val curs= db.rawQuery(query,null)
        var codeBrick=0
        while(curs.moveToNext()){
            codeBrick=curs.getInt(0)
        }
        return codeBrick
    }
    @RequiresApi(Build.VERSION_CODES.O)
    public fun addProj(bL: ArrayList<BrickContainer>, nameProj:String, images: ArrayList<Bitmap>):Int{
        var count=0
        try
        {
            val db=context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,null)
            val values=ContentValues()
            values.put("Name",nameProj)
            values.put("Active",1)
            val current=Date()
            val res=current.time/1000+(2*3600)
            val local=res.toInt()
            values.put("LastAccessed",local)
            if(db.insert("Inventories",null,values)>0) {
                val query = "SELECT id FROM 'Inventories' WHERE Name='$nameProj'"
                val db = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null)
                val curs = db.rawQuery(query, null)
                var id: Int = 0
                while (curs.moveToNext()) {
                    id = curs.getInt(0)
                }
                val iter = bL.size - 1
                for (q in 0..iter) {
                    val brick = bL[q]
                    val d = getDataBricksWithColor(brick.itemId, brick.colorId)
                    if (d.size > 0) {
                        val values2 = ContentValues()
                        values2.put("InventoryID",id)
                        var type=returnType(brick.itemType)
                        values2.put("TypeID",type)
                        values2.put("ItemID",d[0])
                        values2.put("QuantityInSet",0)
                        values2.put("QuantityInStore",brick.qty)
                        values2.put("ColorID",d[1])
                        var extra=0
                        if(brick.extra=="N")
                            extra=1
                        else
                            extra=0
                        values2.put("Extra",extra)
                        //Log.e("arrays222222222222", brick.itemId)
                        if(db.insert("InventoriesParts",null,values2)>0) {
                            //Log.e("arrays222222222222", brick.itemId)
                            val values3 = ContentValues()
                            val bmp=images[q]
                            val baos = ByteArrayOutputStream()
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                            val b = baos.toByteArray()
                            values3.put("Image",b)
                            var queue="ColorID="+d[1]+" and ItemID="+d[0]
                            if(db.update("Codes",values3,queue,null)>0){
                                count+=1
                                Log.e("dsddddd",count.toString())
                            }else
                                Log.e("ERRNO","Błąd zapisu obrazka")
                        }else{
                            Log.e("ERRNO","Błąd ładowania klocków")
                        }
                    } else {
                        val d2 = getDataBricksWithoutColor(brick.itemId)
                        if (d2.size > 0) {
                            //count+=saveParam(brick,id,d,db,images,q)
                            Log.e("databaess2", d2.toString())
                            Log.e("who", brick.itemId)
                            val values2 = ContentValues()
                            values2.put("InventoryID",id)
                            var type=returnType(brick.itemType)
                            values2.put("TypeID",type)
                            values2.put("ItemID",d2[0])
                            values2.put("QuantityInSet",0)
                            values2.put("QuantityInStore",brick.qty)
                            values2.put("ColorID",d2[1])
                            var extra=0
                            if(brick.extra=="N")
                                extra=1
                            else
                                extra=0
                            values2.put("Extra",extra)
                            ///Log.e("arrays222222222222", brick.itemId)
                            if(db.insert("InventoriesParts",null,values2)>0) {
                                //Log.e("arrays222222222222", brick.itemId)
                                val values3 = ContentValues()
                                val bmp=images[q]
                                val baos = ByteArrayOutputStream()
                                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                                val b = baos.toByteArray()
                                values3.put("Image",b)
                                var queue="ColorID="+d2[1]+" and ItemID="+d2[0]
                                if(db.update("Codes",values3,queue,null)>0){
                                    count+=1
                                    Log.e("dsddddd",count.toString())
                                }else
                                    Log.e("ERRNO","Błąd zapisu obrazka")
                            }else{
                                Log.e("ERRNO","Błąd ładowania klocków")
                            }
                        } else {
                            var d3 = getWithoutAll(brick.itemId)
                            if (d3 > 0) {
                                Log.e("databaess3", d3.toString())
                                Log.e("who", brick.itemId)
                            }
                        }
                    }
                }
            }
               /* for(i in 0..iter) {
                    val brick=bL[i]
                    Log.e("size bircks",iter.toString())
                    val d = getDataBricksWithColor(brick.itemId, brick.colorId)
                    Log.e("zzzzddddddddddddzzzzz",d.toString())
                    Log.e("zzzzdddddd",url[i].toString())
                    if (d.size > 0 ) {
                        //count+=saveParam(brick,1,d,db)
                        Log.e("databaess",count.toString())
                    }*/

                    /*val d2 = getDataBricksWithoutColor(arr[i][1])
                    Log.e("zzzzzzzzzzzzzzzzzzz",d2.toString())
                    if (d2.size > 0) {
                        count+=saveParam(arr,i,id,d2,img,db)
                        Log.e("databaess",count.toString())
                    }*/


           /* }else{
                Log.e("ERRNO","Błąd zapisu projektu ")
            }*/
        }catch(e:Exception){
            Log.w("Exception",e)
        }
        Log.e("how add",count.toString())
        return count
    }

    public fun saveParam(brick:BrickContainer,id:Int,d:ArrayList<Int>,db:SQLiteDatabase,img:ArrayList<Bitmap>,who:Int):Int {
        val values2 = ContentValues()
        var count=0
        values2.put("InventoryID",id)
        var type=returnType(brick.itemType)
        values2.put("TypeID",type)
        values2.put("ItemID",d[0])
        values2.put("QuantityInSet",0)
        values2.put("QuantityInStore",brick.qty)
        values2.put("ColorID",d[1])
        var extra=0
        if(brick.extra=="N")
            extra=1
        else
            extra=0
        values2.put("Extra",extra)
        Log.e("arrays222222222222", brick.itemId)
        if(db.insert("InventoriesParts",null,values2)>0) {
            Log.e("arrays222222222222", brick.itemId)
            val values3 = ContentValues()
            val bmp=img[who]
            val baos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            values3.put("Image",b)
            var queue="ColorID="+d[1]+" and ItemID="+d[0]
            if(db.update("Codes",values3,queue,null)>0){
                count=1
                Log.e("dsddddd",count.toString())
            }else
                Log.e("ERRNO","Błąd zapisu obrazka")
            }else{
                Log.e("ERRNO","Błąd ładowania klocków")
            }
        return count
        }
}


