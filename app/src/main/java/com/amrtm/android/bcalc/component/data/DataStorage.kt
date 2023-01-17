package com.amrtm.android.bcalc.component.data

import android.content.Context
import androidx.compose.runtime.remember
import com.amrtm.android.bcalc.component.DataItemManipulation
import com.amrtm.android.bcalc.component.DataManipulation
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

enum class Dir(val folder: String) {
    Note(folder = "notes"),
    Item(folder = "items")
}

class DataStorage (val context: Context) {
    var listItem: MutableSet<ItemDetail> = mutableSetOf()
    var listNote: MutableSet<Note> = mutableSetOf()
    var balance: Balance = Balance()

    fun init(): DataStorage {
        runBlocking {
            val items = getFiles(Dir.Item) as Collection<Item>
            val notes = getFiles(Dir.Note) as Collection<Note>
            if (!items.isEmpty()) listItem.addAll(DataItemManipulation(items as MutableSet<Item>))
            if (!notes.isEmpty()) listNote.addAll(notes)
        }
        if (!listItem.isEmpty() && !listNote.isEmpty()){
            balance = DataManipulation(listNote,listItem.size)
        }
        return this
    }

    fun refreshData():DataStorage {
        balance = DataManipulation(listNote,listItem.size)
        return this
    }

    suspend fun getFiles(folder: Dir): Collection<Any> = coroutineScope {
        val listdata = mutableSetOf<Any>()
        if (!context.getDir(folder.folder,Context.MODE_PRIVATE).exists()) {
            context.getDir(folder.folder,Context.MODE_PRIVATE).mkdir()
        }
        val files = context.getDir(folder.folder,Context.MODE_PRIVATE).listFiles()
        for (file in files) {
            val datas = async { getFile(file, folder == Dir.Note) }
            val data = datas.await()
            listdata.add(data)
        }
        listdata
    }

    private fun getFile(file: File, isNote: Boolean): Any {
        val classNote = when {
            isNote -> Note::class.java
            else -> Item::class.java
        }
        var item: Any
        if (isNote){
            item =  Gson().fromJson<Note>(BufferedReader(FileReader(file)), classNote)
        }
        else {
            item = Gson().fromJson<Item>(BufferedReader(FileReader(file)), classNote)
        }
        return item
    }

    suspend fun createFile(filename:String, folder: Dir, data: List<Any>) = coroutineScope {
        try {
            launch {
                val jsondata: String = Gson().toJson(data)
                if (!context.getDir(folder.folder,Context.MODE_PRIVATE).exists()) {
                    context.getDir(folder.folder,Context.MODE_PRIVATE).mkdir()
                }
                val dir = context.getDir(folder.folder,Context.MODE_PRIVATE)
                var file = File(dir, filename + ".json")
                file.createNewFile()
                file.writeBytes(jsondata.toByteArray())
            }
        } catch (e: Exception) {
            e.message
        }
    }

    suspend fun deleteFile(filename:String, folder: Dir) = coroutineScope {
        try {
            launch {
                val dir = context.getDir(folder.folder,Context.MODE_PRIVATE)
                val file = File(dir,filename+".json")
                if (file.exists())
                    file.delete()
            }
        } catch (e: Exception) {
            e.message
        }
    }
}