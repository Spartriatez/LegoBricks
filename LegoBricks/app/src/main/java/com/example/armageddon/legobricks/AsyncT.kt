package com.example.armageddon.legobricks

import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.support.annotation.RequiresApi
import android.util.Log
import java.io.*
import java.net.MalformedURLException
import java.net.URL
import java.nio.file.Paths
public class AsyncT {
    public var path:String?=null
    public var xmlPath:String?=null
    public var fileName:String?=null
    private var result:String?=null
    private var url_tmp:String?=null
    public var nr_l=0
    public fun setPathName(pathT:String, urlTmp:String,url:String,fN:String){
        val chars = ArrayList<Char>()
        for (i in 0..43) {
            chars.add(pathT!![i])
        }
        this.url_tmp=urlTmp
        path=chars.joinToString(separator = "")
        fileName=fN
        val downloadData=DownloadData().execute(url)
        result=downloadData.get()
        if(nr_l==1){
            Log.e("dane",downloadData.get())
        }
    }
    public fun getResult(): ArrayList<String>? {
        val res=ArrayList<String>()
        res.add(path.toString())
        res.add(result.toString())
        res.add(xmlPath.toString())
        return res
    }
    public fun returnTmpUrl():String{
        return url_tmp.toString()
    }
    public inner class DownloadData : AsyncTask<String, Int, String>() {

        override fun doInBackground(vararg params: String?): String? {
            var data = params[0]
            val count = 37
            val chars = ArrayList<Char>()
            for (i in 0..count) {
                chars.add(data!![i])
            }
            var rs:String?=null
            val checkWho = returnTmpUrl()
            if ("http://fcds.cs.put.poznan.pl/MyWeb/BL/" == checkWho) {
                try {

                    val url = URL(data)
                    val connection = url.openConnection()
                    connection.connect()
                    val lengthOfFile = connection.contentLength
                    val isStream = url.openStream()
                    val testDirectory = File("$path/XML")
                    xmlPath=path+"/XML"
                    if (!testDirectory.exists()) {
                        testDirectory.mkdir()
                        try {
                            Runtime.getRuntime().exec("chmod 774 $testDirectory")
                        }catch (e:Exception){
                            Log.e("Error",e.toString())
                        }
                    }
                    val fos = FileOutputStream("$testDirectory/$fileName.xml")
                    path="$testDirectory/$fileName.xml"
                    val data = ByteArray(1024)
                    var count = 0
                    var total: Long = 0
                    var progress = 0
                    count = isStream.read(data)
                    while (count != -1) {
                        total += count.toLong()
                        val progress_tmp = total.toInt() * 100 / lengthOfFile
                        if (progress_tmp % 10 == 0 && progress != progress_tmp) {
                            progress = progress_tmp
                        }
                        fos.write(data, 0, count)
                        count = isStream.read(data)
                    }
                    isStream.close()
                    fos.close()
                    Runtime.getRuntime().exec("chmod 774 $path")
                    rs="Pobrano dane"
                    nr_l=1

                }catch(e:FileNotFoundException){
                    rs="File not found"
                }catch (e:IOException){
                    rs="IO Exception"
                }
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


}