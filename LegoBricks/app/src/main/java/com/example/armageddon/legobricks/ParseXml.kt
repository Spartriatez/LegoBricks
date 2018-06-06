package com.example.armageddon.legobricks

import android.util.Log
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class ParseXml{
    public fun loadData(pathName:String):ArrayList<ArrayList<String>>{
        val file= File(pathName)
        var datas=ArrayList<ArrayList<String>>()
        if(file.exists()){
            val xmlDoc:Document= DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
            xmlDoc.documentElement.normalize()
            val items:NodeList=xmlDoc.getElementsByTagName("ITEM")
            for(i in 0..items.length-1) {
                val itemNode: Node = items.item(i)
                if (itemNode.nodeType == Node.ELEMENT_NODE) {
                    val elem = itemNode as Element
                    val children = elem.childNodes
                    var itemData= ArrayList<String>()
                    var itemType: String? = null
                    var itemId: String? = null
                    var qty: String? = null
                    var color: String? = null
                    var extra: String? = null

                    for (j in 0..children.length - 1) {
                        val node = children.item(j)
                        if (node is Element) {
                            when (node.nodeName) {
                                "ITEMTYPE" -> {
                                    itemType = node.textContent
                                }
                                "ITEMID" -> {
                                    itemId = node.textContent
                                }
                                "QTY" -> {
                                    qty = node.textContent
                                }
                                "COLOR" -> {
                                    color = node.textContent
                                }
                                "EXTRA" -> {
                                    extra = node.textContent

                                }
                                "ALTERNATE" -> {
                                        var tmp = node.textContent
                                        if (tmp != "N") {
                                            itemType = null
                                            itemId = null
                                            qty = null
                                            color = null
                                            extra = null
                                        }
                                    }
                            }
                            //Log.e("cosnie tak", itemType)
                        }

                    }

                    if (itemType != null && itemId != null && qty != null && color != null) {
                        itemData.add(itemType)
                        itemData.add(itemId)
                        itemData.add(qty)
                        itemData.add(color)
                        if (extra == null)
                            extra = "None"
                        itemData.add(extra)
                        datas.add(itemData)
                    }

                }

            }

        }
        return datas
    }
    public fun deleteFile(path:String){
        var dir=path
        val dirPath=File(dir)
        if(dirPath.isDirectory){
            dirPath.walk().forEach{
               if( it.delete()){
                   Log.e("deleteFile","Usunieto")
               }else
                   Log.e("deleteFile","Błąd usuwania")
            }
        }
        dirPath.delete()
    }
}