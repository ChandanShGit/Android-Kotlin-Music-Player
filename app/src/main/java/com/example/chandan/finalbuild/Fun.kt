package com.example.chandan.finalbuild

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.widget.ImageButton
import java.text.SimpleDateFormat
import java.util.*


object Fun{

    fun today():String{
        return SimpleDateFormat("EEEE").format(Date())
    }

    fun setRecyclerTrackAdapter(ctx: Context, v: RecyclerView?, adapter:TrackAdapter?, layStyle:Int, reverse:Boolean) {
        v?.layoutManager = LinearLayoutManager(ctx,layStyle,reverse)
        v?.itemAnimator = DefaultItemAnimator()
        v?.adapter = adapter
        v?.adapter?.notifyDataSetChanged()
    }


    fun setRecyclerPlaylist(ctx: Context, v: RecyclerView?, adapter:PlaylistAdapter?){
        v?.layoutManager = LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false)
        v?.itemAnimator = DefaultItemAnimator()
        v?.adapter = adapter
        v?.adapter?.notifyDataSetChanged()
    }


    fun getBitmap(ctx: Context, path:String): Bitmap {

        val mmr = MediaMetadataRetriever()
        val rawArt: ByteArray?
        var art: Bitmap = BitmapFactory.decodeResource(
            ctx.resources,
            R.drawable.default_art
        )
        val bfo = BitmapFactory.Options()
        mmr.setDataSource(path)
        rawArt = mmr.embeddedPicture

        if (null != rawArt) {
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.size, bfo)
        }
        return art
    }


    fun saveCurPlayingTrack(track:SModel,pos:Int,ctx:Context):SHelper {
        val sHelper= SHelper()
        sHelper.trackId=track.songId
        sHelper.trackTitle=track.title
        sHelper.trackPath=track.path
        sHelper.trackArtist=track.artist
        sHelper.trackPos=pos
        sHelper.isLoop=PreConfig(ctx).readLoop()
        sHelper.isShuffle=PreConfig(ctx).readShuffle()
        sHelper.trackAddedDate=track.dateAdded
        return sHelper
    }

    fun createNotificationChannel(ctx:Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MusicX!"
            val descriptionText = "Show MusicX1 notification"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(Var.NOTIF_CHANNEL, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }



    fun getHeight(windowManager: WindowManager){
        var dm= DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        var height=dm.heightPixels
        Log.i("HeightPx",height.toString())
    }


    fun getRandomSongs(songList: ArrayList<SModel>): ArrayList<SModel> {
        var songRandomList:ArrayList<SModel>?=null
        var select:Int
        var allSelect=IntArray(10)
        if(songList.size>=10) {
            var total=0
            songRandomList=ArrayList()
            while(total!=10) {
                select = Random().nextInt(songList.size)
                if (!allSelect.contains(select)){
                    allSelect[total] = select
                    Log.i("printing","pos:$total , song no:$select")
                    songRandomList?.add(songList!![select])
                    total++
                }
            }
        }
        return songRandomList!!
    }


}