package com.example.chandan.finalbuild

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_play_screen.*
import kotlinx.android.synthetic.main.bottom_play_bar.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.tracks_layout.*
import kotlinx.android.synthetic.main.tracks_layout.view.*
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity(),GetSongInter,UpdateInter{

    private var songList:ArrayList<SModel>?=null
    private var songCovers:ArrayList<Bitmap>?=null
    private var playlist:ArrayList<SModel.PLModel>?=null
    private  var mService:MediaPlayerSv?=null
    val TAG="HOME"
    var screenVisible=false
    var noService=false
    var checkRun=false
    private var mBound:Boolean=false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Fun.createNotificationChannel(this)
        songList=intent.getParcelableArrayListExtra<SModel>("SModelList")
        startService(Intent(this,MediaPlayerSv::class.java))
        if(songList!=null){
            no_song_rl.visibility=View.GONE
            //StartService with songlist
            setCurrentFragment(HomeFragment())
            initListeners()
            screenVisible=true
            updateBottomPB()
        }else{
            no_song_rl.visibility=View.VISIBLE
        }


        Log.i(TAG,"onCreate")
    }


    private val mConnection= object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, ser: IBinder?) {
            val sb=ser as MediaPlayerSv.SBinder
            Log.i(TAG,"Service,Connecting ")
            mService=sb.getMS()
            if(mService?.getSHelper()!=null) {
                checkRun=mService?.getSHelper()!!.isPlaying
                if(checkRun)
                updateUI(mService?.getSHelper()!!)
            }
            mService?.registerActivity(this@HomeActivity)
            Log.i(TAG,"Service,Connected "+mService?.getSHelper()?.trackTitle)
            mService?.registerActivity(this@HomeActivity)
            mBound=true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i(TAG,"Service,DisCon ")
            mBound=false
        }
    }


    override fun startCurSong(pos:Int,songTracks:ArrayList<SModel>){
        if(mBound){
            mService?.startPlayingSong(pos,songTracks)
            if(play_bar.visibility==View.GONE){
                play_bar.visibility=View.VISIBLE
                noService=true
            }
            Log.i(TAG,"isBind:$mBound  isPlaying:")
        }else{
            Log.i(TAG,"isBind:$mBound  isErrorPlaying:")
        }
    }

    override fun updateUI(helper: SHelper) {
        Log.i("Inter","HA")
        now_playing.text=helper.trackTitle
        artist.text=helper.trackArtist
        btm_bar_art.setImageBitmap(getBitmapFromPath(helper.trackPath!!))
        bottom_pb.max=helper.trackDuration
        if(helper.isPlaying){
            bb_playpause_iv.setImageResource(R.drawable.pause)
        }else{
            bb_playpause_iv.setImageResource(R.drawable.play)
        }

        checkRun=helper.isPlaying
    }

    override fun startTrackandTB() {
        val intent= Intent(this,TrackAndTB::class.java)
        intent.putParcelableArrayListExtra("SModelList",songList)
        intent.putExtra("TAG","all_tracks")
        startActivity(intent)
    }



    override fun onPause() {
        super.onPause()
        if(songList!=null){
        checkRun=false
        mBound=false
        mService?.unregisterActivity()
        }
        Log.i(TAG,"REsume UnREgistering")
        Log.i(TAG,"Pase")
    }


    override fun onResume() {
        super.onResume()
        Log.i(TAG,"REsume")

    }




    private fun initListeners(){

        bb_playpause_iv.setOnClickListener{
                if(mBound){
                    if(mService?.getSHelper()!!.isPlaying){
                        mService?.doPause()
                        bb_playpause_iv.setImageResource(R.drawable.play)
                        checkRun=false
                    } else {
                        mService?.doPlay()
                        checkRun=true
                        bb_playpause_iv.setImageResource(R.drawable.pause)
                    }
            }
        }

        play_bar.setOnClickListener{
            startActivity(Intent(this,PlayScreen::class.java))
        }

    }


    private fun updateBottomPB(){
        val rotate = object:Runnable {
            override fun run() {
                if (mBound && checkRun) {
                    bottom_pb.progress=mService?.mediaCurPosition()!!
                    Log.i("rotatethread","RunningHome")
                }
                updateBottomPB()
            }

        }
        if(screenVisible){
            Handler().postDelayed(rotate,1000)
        }
    }

    override fun getMP_SER(): MediaPlayerSv{
        return mService!!
    }

    override fun onStart() {
        Log.i(TAG,"onstart")
        super.onStart()
        if(songList!=null){
            Intent(this,MediaPlayerSv::class.java).also{ intent->bindService(intent,mConnection,Context.BIND_AUTO_CREATE)}
            if(mBound){
                mService?.registerActivity(this@HomeActivity)
                Log.i(TAG,"REsume REgistering")
                updateUI(mService?.getSHelper()!!)
            }else{

            }

        }
    }

    override fun onDestroy() {

        screenVisible=false
        if(noService){
            mService?.unregisterActivity()
            mService?.stopSelf()

        }
        unbindService(mConnection)
        mBound=false
        super.onDestroy()
    }




    override fun getAllPlaylist(): ArrayList<SModel.PLModel> {
        playlist= ArrayList()
        playlist?.add(SModel.PLModel(1,"Monsoon"))
        playlist?.add(SModel.PLModel(2,"Gym"))
        playlist?.add(SModel.PLModel(3,"Weekend"))
        return playlist!!
    }


    override fun getRandomSongs(): ArrayList<SModel> {
        return Fun.getRandomSongs(songList!!)
    }


    override fun getBitmapFromPath(path:String): Bitmap{
        return Fun.getBitmap(this,path)
    }

    override fun getFavorite(): ArrayList<SModel> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun getPlaylist(): Array<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun getAllSongList():ArrayList<SModel>{
        return songList!!
    }


    override fun setCurrentFragment(frag:Fragment){
        val ft = supportFragmentManager.beginTransaction()
        ft.add(R.id.home_frag,frag).commit()
    }




}
