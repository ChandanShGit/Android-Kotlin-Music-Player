package com.example.chandan.finalbuild

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_play_screen.*
import java.util.*
import java.util.concurrent.TimeUnit

class PlayScreen : AppCompatActivity(),UpdateInter{


    private lateinit  var mService:MediaPlayerSv
    var rotateCircle: ObjectAnimator? = null
    var animationSet: AnimatorSet? = null
    val TAG="PlayScreen"
    private var mBound:Boolean=false
    private var sHelper:SHelper?=SHelper()
    var i = 0F
    var k = 0F
    var screenVisible=false
    var checkRun=false
    var rotAlive=false

    private val mConnection= object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, ser: IBinder?) {
            val sb=ser as MediaPlayerSv.SBinder
            Log.i(TAG,"Service,Connecting ")
            mService=sb.getMS()
            mService.registerActivity(this@PlayScreen)
            mBound=true
            updateUI(mService.getSHelper()!!)
           // updateSeekBarAndTime()
            Log.i(TAG,"Service,Connected "+mService.getSHelper()?.trackTitle)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i(TAG,"Service,DisCon ")
            mBound=false
        }
    }

    override fun updateUI(sHelper: SHelper) {
        Log.i("interPS","PS")
        this.sHelper=sHelper
        checkRun=sHelper.isPlaying
        updatePlayScreen(sHelper)
        updateControls()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_screen)
        initListeners()
        screenVisible=true
        rotateBack()
        updateSBandTime()
        Log.i(TAG,"onCreate")
    }



    private fun initListeners() {

        pm_play_pause.setOnClickListener(){
            if(mBound){
                if(mService.getSHelper()!!.isPlaying){
                    mService.doPause()
                    pm_play_pause.setImageResource(R.drawable.play)
                    checkRun=false
                } else {
                    mService.doPlay()
                    checkRun=true
                    pm_play_pause.setImageResource(R.drawable.pause)
                }
            }
        }

        cp_prev_ib.setOnClickListener{
            if(mBound){
                mService.playChange(Var.PREVIOUS)
                k=0F
            }

        }

        cp_next_ib.setOnClickListener{
            if(mBound){
                mService.playChange(Var.NEXT)
                k=0F
            }

        }

        pm_shuffle.setOnClickListener{
            if(mBound){
                if(mService.getSHelper()!!.isShuffle){
                    Log.i(TAG,"Shuffle"+sHelper!!.isShuffle)
                    pm_shuffle.setImageResource(R.drawable.shuffle_off)
                    mService.updateShuffle(false)
                }else{
                    Log.i(TAG,"Shuffle"+sHelper!!.isShuffle)
                    mService.updateShuffle(true)
                    pm_shuffle.setImageResource(R.drawable.shuffle_on)
                    pm_loop.setImageResource(R.drawable.loop_off)
                }

            }
        }

        pm_loop.setOnClickListener{
            if(mBound){
                if(mService.getSHelper()!!.isLoop){
                    Log.i(TAG,"llople"+sHelper!!.isLoop)
                    pm_loop.setImageResource(R.drawable.loop_off)
                    mService.updateLoop(false)
                }else{
                    Log.i(TAG,"llop"+sHelper!!.isLoop)
                    mService.updateLoop(true)
                    pm_shuffle.setImageResource(R.drawable.shuffle_off)
                    pm_loop.setImageResource(R.drawable.loop_on)
                }
            }
        }

        pm_addfav.setOnClickListener{
            val db=SaveMetaDB(this)
            val bool=db.isPresent(sHelper!!.trackId)
            Log.i(TAG,"FavOld:$bool")
            if(!bool){
                db.add(sHelper!!)
                pm_addfav.setImageResource(R.drawable.fav_filled)
            }else{
                if(bool){
                    Log.i(TAG,"Fav:$bool")
                    db.remove(sHelper!!.trackId)
                    pm_addfav.setImageResource(R.drawable.fav_unfilled)
                }
            }
            Log.i(TAG,"FavNew:${db.isPresent(sHelper!!.trackId)}")

        }


        pm_song_sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    if(mBound){
                        mService.seekTo(p1)
                    }
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

    }


    override fun onStart() {
        Intent(this,MediaPlayerSv::class.java).also{ intent->bindService(intent,mConnection, Context.BIND_AUTO_CREATE)}
        super.onStart()
        Log.i(TAG,"onStart")
    }

    override fun onPause() {
        super.onPause()
        checkRun=false
        mService.unregisterActivity()
    }

    override fun onResume() {
        super.onResume()
        if(mBound){
            mService.registerActivity(this@PlayScreen)
            checkRun=sHelper!!.isPlaying
        }

    }

    override fun onDestroy() {
        screenVisible=true
        super.onDestroy()
        mService.stopSelf()
        unbindService(mConnection)
        Log.i(TAG,"onDestroy")
    }

     private fun rotateBack(){
        val rotate = object:Runnable {
            override fun run() {
                if (mBound && checkRun) {
                    i = k
                    k += 0.8F
                    Log.i("InRotate", i.toString())
                    rotateCircle = ObjectAnimator.ofFloat(rot_iv, "rotation", i, k)
                    animationSet = AnimatorSet()
                    animationSet!!.playTogether(rotateCircle)
                    animationSet!!.start()
                    //updateSeekBarAndTime()
                    Log.i("rotatethread","Running $k")
                }
                rotateBack()
            }
        }
         if(screenVisible){
             Handler().postDelayed(rotate,10)
         }

    }

    private fun updateSBandTime(){
        val rotate = object:Runnable {
            override fun run() {
                if (mBound && checkRun) {
                    val curPos=mService.mediaCurPosition().toLong()
                    pm_song_sb?.progress=curPos.toInt()
                    pm_start_duration!!.text = String.format("%02d :%02d", TimeUnit.MILLISECONDS.toMinutes(curPos)
                        , TimeUnit.MILLISECONDS.toSeconds(curPos)
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(curPos)))

                    pm_end_duration!!.text = String.format("%02d :%02d", TimeUnit.MILLISECONDS.toMinutes(sHelper!!.trackDuration.toLong())
                        , (TimeUnit.MILLISECONDS.toSeconds(sHelper!!.trackDuration.toLong())
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(sHelper!!.trackDuration.toLong()))))
                }
                updateSBandTime()
            }
        }
        if(screenVisible){
            Handler().postDelayed(rotate,1000)
        }
    }




    private fun updateHelper(){
        if(mBound){
            sHelper=mService.getSHelper()
            Log.i(TAG,"updateHel: ${sHelper?.trackTitle}")
        }
    }


    private fun updatePlayScreen(helper: SHelper){

        pm_play_pause.setImageResource(R.drawable.pause)
        cp_artist.text=helper.trackArtist
        cp_title.text=helper.trackTitle
        cp_top_title.text=cp_title.text
        val bm=Fun.getBitmap(this,helper.trackPath!!)
        cp_music_art.setImageBitmap(bm)
        full_art.setImageBitmap(bm)
        pm_song_sb.max=helper.trackDuration
        Log.i(TAG,"updatePlayScreen ${helper.trackTitle}")
        if(helper.isPlaying){
            pm_play_pause.setImageResource(R.drawable.pause)
        }else{
            pm_play_pause.setImageResource(R.drawable.play)
        }
        k=0F

    }




    private fun updateControls(){
        if(PreConfig(this).readShuffle())
        {
            pm_shuffle.setImageResource(R.drawable.shuffle_on)
        }else{
            pm_shuffle.setImageResource(R.drawable.shuffle_off)
        }

        if(PreConfig(this).readLoop()){
            pm_loop.setImageResource(R.drawable.loop_on)
        }else{
            pm_loop.setImageResource(R.drawable.loop_off)
        }

        if(SaveMetaDB(this).isPresent(sHelper!!.trackId)){
            pm_addfav.setImageResource(R.drawable.fav_filled)
        }else{
            pm_addfav.setImageResource(R.drawable.fav_unfilled)
        }
    }

}
