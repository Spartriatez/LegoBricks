package com.example.armageddon.legobricks

import android.content.Intent
import android.graphics.Color
import android.graphics.Color.*
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.View
import android.widget.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.os.AsyncTask
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class addProj : AppCompatActivity() {
    var dataProj=ArrayList<ArrayList<String>>()
    var bt1:Button?=null
    var lLayaout:LinearLayout?=null
    var sv:ScrollView?=null
    var errTx:TextView?=null
    var delData=ArrayList<String>()
    var errDatas=ArrayList<String>()
    var urlImgs=ArrayList<Bitmap>()
    var cbl= ArrayList<BrickContainer>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_proj)
        val myDB=DBSQLite(this)
        myDB.openDatabase()
        bt1=findViewById(R.id.save)
        lLayaout=findViewById(R.id.showhide)
        sv=findViewById(R.id.sV2)
        errTx=findViewById(R.id.errorName)
        errTx!!.setTextColor(Color.RED)
        bt1!!.isEnabled=false
        bt1!!.setTextColor(RED)
        bt1!!.setBackgroundColor(GRAY)
    }

    fun backMM(v: View){
        val intent= Intent(this,MainLegoMenu::class.java)
        startActivity(intent)
    }

    fun checkBricks(v:View){
        val ex1: EditText =findViewById(R.id.idUrl)
        val ex2:EditText=findViewById(R.id.nameProj)
        val url:String=ex1.text.toString()
        val projectName:String=ex2.text.toString()
        val parseData=ParseXml()
        var tmpPath:String?=null
        if(url=="" || projectName==""){
            Toast.makeText(this,"Uzupełnij wszystkie dane",Toast.LENGTH_LONG).show()
        }else {
            val myDB=DBSQLite(this)
            val count=myDB.checkExistProj(projectName)
            if(count==0){
                bt1!!.isEnabled=true
                bt1!!.setTextColor(BLACK)
                bt1!!.setBackgroundColor(parseColor("#ff33b5e5"))
                val urltmp = "http://fcds.cs.put.poznan.pl/MyWeb/BL/"
                val url2 = urltmp+url+".xml"
                val res = AsyncT()
                val path = this.cacheDir.canonicalPath
                res.setPathName(path, urltmp, url2,url)
                val rs=res.getResult()
                Toast.makeText(this, rs!!.get(1),Toast.LENGTH_LONG).show()
                val retPath=rs!!.get(0)
                tmpPath=rs!!.get(2)
                dataProj=parseData.loadData(retPath)
                parseData.deleteFile(tmpPath)
                val errorsDatas=ArrayList<String>()
                for(i in 0..dataProj.size-1){
                    /*if(myDB.checkExistBricksOnlyId(dataProj[i][1])==0){
                        errorsDatas.add(dataProj[i][1]+" brak klocka w bazie danych\n")
                    }*/
                    if(myDB.checkExistBricks(dataProj[i][1])==0){
                        delData.add(dataProj[i][1])
                        errorsDatas.add(dataProj[i][1]+" brak klocka w bazie danych\n")
                    }
                    if(existsUrl(i)==true){
                        val newVal=dataProj[i][1]
                        if(!delData.contains(newVal))
                            delData.add(dataProj[i][1])
                        errorsDatas.add(dataProj[i][1]+"  brak zdjęcia tego klocka\n")
                    }
                }
                errDatas=errorsDatas
                var countErr=errDatas.size
                if(countErr!=0 && !errDatas.isEmpty()){
                    //lLayaout!!.visibility=View.VISIBLE
                    errTx!!.append("Errors: "+countErr.toString()+"\n")
                    for(i in 0..countErr-1){
                        errTx!!.append(i.toString()+" "+errorsDatas[i])
                    }
                    sv!!.post(object :Runnable{
                        override fun run() {
                            sv!!.smoothScrollTo(0,errTx!!.bottom)
                        }
                    })
                }
            }else{
                Toast.makeText(this,"Projekt już istnieje",Toast.LENGTH_LONG).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public fun zapiszProj(v:View){
        val ex2:EditText=findViewById(R.id.nameProj)
        val projectName:String=ex2.text.toString()
        val myDB=DBSQLite(this)
        val count=errDatas.size
        var where=ArrayList<Int>()
        for(j in 0..delData.size-1) {
            for (i in 0..dataProj.size - 1) {
                if(delData[j]==dataProj[i][1]){
                   where.add(i)
                }
            }
        }
        delData.clear()
        for(i in 0..where.size-1){
            dataProj.remove(dataProj[where[i]])
        }
        where.clear()

        for(i in 0..dataProj.size-1){
             val bitmap=urlImgs[i]

            val bL=BrickContainer(dataProj[i][1],dataProj[i][0],dataProj[i][2].toInt(),dataProj[i][3].toInt(),dataProj[i][4])
            cbl.add(i,bL)
        }
        dataProj.clear()

        myDB.addProj(cbl,projectName,urlImgs)
        /*==dataProj.size)
            Toast.makeText(this,"Zapisano dane",Toast.LENGTH_LONG).show()
        else
            Toast.makeText(this,"Błąd zapisu do bazy danych",Toast.LENGTH_LONG).show()*/
    }

    public fun existsUrl(i:Int):Boolean{
        val urlImg1="https://www.lego.com/service/bricks/5/2/"
        val urlImg2="http://img.bricklink.com/P/"
        val urlImg3="https://www.bricklink.com/PL/"
        var brickCode:String?=null
        val myDB=DBSQLite(this)
        if(myDB.getIdBricks(dataProj[i][1],dataProj[i][3])!=""){
            brickCode=myDB.getIdBricks(dataProj[i][1],dataProj[i][3])
            //Log.e("Url",brickCode)
        }else if(myDB.getIdBricksWithoutColor(dataProj[i][1])!=""){
            brickCode=myDB.getIdBricksWithoutColor(dataProj[i][1])
        }else
            brickCode=""

        var errCURL = false
        val tmpUrl2=urlImg2+dataProj[i][3]+"/"+dataProj[i][1]+".gif"
        val tmpUrl3 = urlImg3 + dataProj[i][1] + ".jpg"
        if(brickCode!="") {
            val tmpUrl1=urlImg1+brickCode
            val res=DownloadData().execute(tmpUrl1)
            val res2=res.get()
            if (res2=="")
                errCURL =true
        }else {
            var errd=0
            val res=DownloadData().execute(tmpUrl2)
            val res2=res.get()
            if(res2=="")
                errd=1
            //Log.e("ddd","done")
            if(errd==1){
                val res=DownloadData().execute(tmpUrl3)
                val res2=res.get()
                //Log.e("ddd2","done2")
                if(res2=="")
                    errd=2
            }

            if(errd>1)
                errCURL=true
            else {
                errCURL = false
            }
        }

        return errCURL
    }


    public inner class DownloadData : AsyncTask<String, Int, String>() {

        override fun doInBackground(vararg params: String?): String? {
            val urlConnection:HttpURLConnection? = null
            var data = params[0]
            var rs:String=""
            try {

                val url = URL(data)
                val connection = url.openConnection() as HttpURLConnection
                var status= connection.responseCode
                if (status != HttpURLConnection.HTTP_OK)
                {
                    rs=""
                }else {
                    rs = "ddd"
                    val inSt=connection.inputStream
                    val bitmap=BitmapFactory.decodeStream(inSt)
                    urlImgs.add(bitmap)
                }
            }catch(e: FileNotFoundException){
                rs=""
            }catch (e:IOException){
                    rs=""
            }
            return rs
        }

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

    }


/* public fun searchAll(v:View){

        val myDB=DBSQLite(this)
        var countErr=0
        errTx!!.text=""
        val errorsDatas=ArrayList<Int>()
        if(!dataProj.isEmpty()){
            for(i in 0..dataProj.size-1){
                if(myDB.checkExistBricks(dataProj[i][1])==0){
                    errorsDatas.add(i)
                    countErr+=1
                }

            }
            errDatas=errorsDatas
            if(countErr!=0){
                lLayaout!!.visibility=View.VISIBLE
                errTx!!.append("Errors: "+countErr.toString()+"\n")
                for(i in 0..countErr-1){
                    val iter=i+1
                    if(errorsDatas.get(i)==i+1000000){
                        errTx!!.append(iter.toString()+" "+dataProj[errorsDatas.get(i)][1]+" brak zdjęcia tego klocka\n")
                    }else{
                        errTx!!.append(iter.toString()+" "+dataProj[errorsDatas.get(i)][1]+" brak klocka w bazie danych\n")
                    }
                }
                sv!!.post(object :Runnable{
                    override fun run() {
                        sv!!.smoothScrollTo(0,errTx!!.bottom)
                    }
                })
                lLayaout!!.visibility=View.INVISIBLE
            }
        }else{
            Toast.makeText(this,"Brak danych",Toast.LENGTH_LONG).show()
        }

    }*/
}


