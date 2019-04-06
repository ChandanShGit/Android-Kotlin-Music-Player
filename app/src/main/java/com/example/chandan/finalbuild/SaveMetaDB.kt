package com.example.chandan.finalbuild

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.ArrayList


class SaveMetaDB : SQLiteOpenHelper {

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(
        context,
        name,
        factory,
        version
    )

    constructor(context: Context?) : super(context, Var.DB_NAME, null, Var.VERSION)

    @SuppressLint("SQLiteString")
    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(
            "CREATE TABLE "
                    + Var.TABLE_NAME + " ("
                    + Var.COLUMN_ID + " INTEGER,"
                    + Var.COLUMN_TITLE + " STRING,"
                    + Var.COLUMN_ARTIST + " STRING,"
                    + Var.COLUMN_PATH + " STRING );"
        )

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE +${Var.TABLE_NAME}")
        onCreate(p0)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun add(sHelper: SHelper) {
        try {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(Var.COLUMN_ID, sHelper.trackId.toInt())
            contentValues.put(Var.COLUMN_TITLE, sHelper.trackTitle)
            contentValues.put(Var.COLUMN_ARTIST, sHelper.trackArtist)
            contentValues.put(Var.COLUMN_PATH, sHelper.trackPath)
            db.insert(Var.TABLE_NAME, null, contentValues)
            db.close()
        } catch (e: Exception) {

        }
    }

    fun remove(id: Long) {
        val db = this.writableDatabase
        db.delete(Var.TABLE_NAME, Var.COLUMN_ID + "=" + id, null)
        db.close()
    }

    fun getList(): ArrayList<SModel>? {

        var allSongs: ArrayList<SModel>? = null
        val db = this.readableDatabase

        try {
            val query = "SELECT * FROM ${Var.TABLE_NAME}"
            val cursor = db.rawQuery(query, null)
            Log.i("favlist","allsongsRun")
            if (cursor.moveToFirst()) {
                allSongs=ArrayList()
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(Var.COLUMN_ID))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(Var.COLUMN_TITLE))
                    val artist = cursor.getString(cursor.getColumnIndexOrThrow(Var.COLUMN_ARTIST))
                    val path = cursor.getString(cursor.getColumnIndexOrThrow(Var.COLUMN_PATH))
                    allSongs?.add(SModel(id.toLong(), title, artist, path, 0))
                    Log.i("favlist","allsongsInside:${allSongs}")
                } while (cursor.moveToNext())
            db.close()
            } else {
                Log.i("favlist","allsongsNull:${allSongs}")
                db.close()
                return null
            }

        } catch (e: Exception) {
            db.close()
            Log.i("favlist","allsongsException:${allSongs}")
        }
        return allSongs
    }


    fun isPresent(ids: Long): Boolean {
        var id=-1090
        val db = this.readableDatabase
        val query = "SELECT * FROM " + Var.TABLE_NAME + " WHERE songID='$ids'"
        var cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(Var.COLUMN_ID))
            } while (cursor.moveToNext())
        } else {
            return false
        }
        db.close()
        return id!= -1090  //return true and false
    }


}