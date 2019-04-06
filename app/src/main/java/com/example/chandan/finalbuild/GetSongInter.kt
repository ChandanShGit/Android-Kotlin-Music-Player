package com.example.chandan.finalbuild

import android.graphics.Bitmap
import android.support.v4.app.Fragment

interface GetSongInter {

    fun setCurrentFragment(frag:Fragment)
    fun getAllSongList():ArrayList<SModel>
    fun getRandomSongs():ArrayList<SModel>
    fun getFavorite():ArrayList<SModel>
    fun getPlaylist():Array<String>
    fun getBitmapFromPath(path:String):Bitmap
    fun getAllPlaylist():ArrayList<SModel.PLModel>
    fun getMP_SER():MediaPlayerSv
    fun startCurSong(pos:Int,songTracks:ArrayList<SModel>)
    fun startTrackandTB()


}
