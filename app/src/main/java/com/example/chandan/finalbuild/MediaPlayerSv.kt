package com.example.chandan.finalbuild

import android.app.*
import android.content.BroadcastReceiver
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.content.Context
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import java.util.*


class MediaPlayerSv: Service(){

    var TAG="MediaPlayerSv:"

    private val sBinder=SBinder()
    private  var mediaPlayer: MediaPlayer?=null
    private  var sHelper:SHelper?=null
    private val  ctx: Context=this
    private lateinit var curTracks:ArrayList<SModel>
    private lateinit var audioManager: AudioManager

    var mPlaybackDelayed = false

    private val focusLock=Any()
    private lateinit var audioAttributes: AudioAttributes
    private lateinit var audioFocusRequest: AudioFocusRequest
    private lateinit var afChangeListener: AudioManager.OnAudioFocusChangeListener
    private var mAudioFocusGrant:Boolean=false
    private lateinit var mDelayedStopRunnable:Runnable


    private val noisyIntent = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val broadCast = BroadCastR()
    private var updateInter:UpdateInter?=null


    inner class BroadCastR: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!!.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                // Pause the playback
                doPause()
            }
        }
    }


    fun registerActivity(updateInter: UpdateInter){
        this.updateInter=updateInter
    }
    fun unregisterActivity(){
        this.updateInter=null
    }

    override fun onCreate() {
        super.onCreate()

        mediaPlayer= MediaPlayer()

        mDelayedStopRunnable = Runnable {
            doPause()
            sHelper?.isPlaying=false
            mediaPlayer?.release()
        }
        initListeners()
        Log.i(TAG,"Initialising mediaPlayer in oncreate")
    }



    inner class SBinder:Binder(){
        fun getMS():MediaPlayerSv{
            Log.i(TAG,"Service,SBinder ")
            return this@MediaPlayerSv
        }
    }



    fun getSHelper():SHelper?{
        return sHelper
    }


    fun mediaCurPosition():Int{
       return mediaPlayer!!.currentPosition
    }

    fun seekTo(p1:Int){
        mediaPlayer?.seekTo(p1)
    }

    fun updateShuffle(bool:Boolean) {
        val config = PreConfig(ctx)
        if (bool && sHelper!!.isLoop) {
            updateLoop(false)
        }
        config.writeShuffle(bool)
        sHelper?.isShuffle = config.readShuffle()
        Log.i(TAG, "Shuffle"+config.readShuffle().toString())
    }

    fun updateLoop(bool:Boolean){
        val config=PreConfig(ctx)
        if(bool && sHelper!!.isShuffle){
            updateShuffle(false)
        }
        config.writeLoop(bool)
        sHelper?.isLoop=config.readLoop()
        Log.i(TAG,"Loop"+config.readLoop().toString())
    }

    fun startPlayingSong(pos:Int,songTracks:ArrayList<SModel>){
        curTracks=songTracks
        sHelper=Fun.saveCurPlayingTrack(curTracks[pos],pos,ctx)

//        if(mediaPlayer!=null){mediaPlayer?.reset()}
//        else {
//            mediaPlayer = MediaPlayer()
//            mediaPlayer!!.setOnCompletionListener {
//                playChange(Var.NEXT)
//            }
//        }

        mediaPlayer?.apply {
            reset()
            setDataSource(curTracks[pos].path)
            prepare()
            doPlay() //call play
        }

        sHelper?.isPlaying=true
        sHelper?.trackDuration=mediaPlayer!!.duration
        updateInter?.updateUI(sHelper!!)
        updateNotification(curTracks[sHelper!!.trackPos])

        Log.i(TAG,"mediaPlaying: $mediaPlayer , size:${songTracks.size}")

    }

//
//     private fun updateControlState(){
//         val config=PreConfig(ctx)
//         sHelper?.isShuffle=config.readShuffle()
//         sHelper?.isLoop=config.readLoop()
//    }
//






    fun playChange(what:String){

        if(!sHelper!!.isShuffle){
            if(!sHelper!!.isLoop){
                when (what) {
                    Var.NEXT -> {

                        if (sHelper!!.trackPos == curTracks.size-1) {
                            sHelper!!.trackPos = 0
                        }else{sHelper!!.trackPos += 1}

                    }
                    Var.PREVIOUS -> {

                        if (sHelper!!.trackPos ==0) {
                            sHelper!!.trackPos = curTracks.size-1
                        }else{sHelper!!.trackPos -= 1}
                    }
                }
            }
        }else{
                sHelper?.trackPos = Random().nextInt(curTracks.size)
        }

        Log.i(TAG,"total:${curTracks.size-1} trackPos:${sHelper?.trackPos}")
        startPlayingSong(sHelper!!.trackPos,curTracks)
    }




    fun doPlay():Boolean{
        if(!mAudioFocusGrant)
        {
            requestAudioFocus()
            Log.i(TAG,"ASKING FOCUS REQ")
            //get REQ Granted
        }
        if(mediaPlayer!=null && mAudioFocusGrant){
            Log.i(TAG,"PLAYING FOCUS REQ")
            mediaPlayer?.start()
            ctx.registerReceiver(broadCast,noisyIntent)
            sHelper?.isPlaying=true
            return true
        }
        return false
    }

    fun doPause():Boolean{

        if( sHelper!!.isPlaying ){
            mediaPlayer?.pause()
            sHelper?.isPlaying=false
            mAudioFocusGrant=false
            ctx.unregisterReceiver(broadCast)
            abandonFocus()
            return true
        }
        return false
    }





    override fun onDestroy() {
        doPause()
        mediaPlayer?.release()
        super.onDestroy()
    }

    fun getStatus(){
        Log.i(TAG,"RUNNING")
    }


    override fun onBind(intent: Intent?): IBinder? {
        Log.i(TAG,"Service,onBindCalled")
       return sBinder
    }




    private fun requestAudioFocus() {

        audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            // Request audio focus for play back
            val result = audioManager.requestAudioFocus(
                afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN
            )

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mAudioFocusGrant = true
                Log.i(TAG, "SUCCESS TO REQUEST AUDIO FOCUS")
            } else {
                mAudioFocusGrant = false
                Log.i(TAG, "FAILED TO REQUEST AUDIO FOCUS")
            }
        }else{

            //For Orea And Above

            val mHandler= Handler()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                setAudioAttributes(AudioAttributes.Builder().run {
                    setUsage(AudioAttributes.USAGE_MEDIA)
                    setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    build()
                })
                setAcceptsDelayedFocusGain(true)
                setOnAudioFocusChangeListener(afChangeListener, mHandler)
                build()
            }

            val res = audioManager.requestAudioFocus(audioFocusRequest)

            synchronized(focusLock) {
                when (res) {
                    AudioManager.AUDIOFOCUS_REQUEST_FAILED -> false
                    AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                        mAudioFocusGrant=true
                        Log.i(TAG, "SUCCESS TO REQUEST AUDIO FOCUS P:$mAudioFocusGrant")
                        doPlay()

                    }
                    AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
                        mPlaybackDelayed = true
                        mAudioFocusGrant=false
                        Log.i(TAG, "DELAYED TO REQUEST AUDIO FOCUS D:$mAudioFocusGrant")

                    }
                    else ->  {Log.i(TAG, "ELSE TO REQUEST AUDIO FOCUS P:$mAudioFocusGrant")
                        mAudioFocusGrant=false}
                }
            }
        }
    }



    private fun initListeners(){

        mediaPlayer?.setOnCompletionListener {
            playChange(Var.NEXT)
        }

        afChangeListener=AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS -> {
                    // Permanent loss of audio focus
                    // Pause playback immediately
                    // Wait 10 seconds before stopping playback
                    Log.i(TAG,"AUDIOFOCUS_LOSS")
                    mediaPlayer?.apply {
                        stop()
                    }
                    mAudioFocusGrant=false
                    abandonFocus()
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    Log.i(TAG,"AUDIOFOCUS_LOSS_TRANSIENT")
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    //mediaPlayer?.setVolume(0.2f,0.2f)
                    Log.i(TAG,"AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK")
                }
                AudioManager.AUDIOFOCUS_GAIN -> {
                    // Your app has been granted audio focus again
                    // Raise volume to normal, restart playback if necessary
                    Log.i(TAG,"AUDIOFOCUS_GAIN")
                    doPlay()
                    mAudioFocusGrant=true
                }
            }
        }
    }








    private fun updateNotification(track:SModel){
        val intent = Intent(this, IntroActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)


        val mBuilder = NotificationCompat.Builder(this, Var.NOTIF_CHANNEL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(track.title)
            .setContentText(track.artist)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true).build()

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(Var.NOTIF_ID,mBuilder)
        startForeground(Var.NOTIF_ID,mBuilder)
    }

    private fun abandonFocus(){
        if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.O)
            audioManager.abandonAudioFocusRequest(audioFocusRequest)
        else{
            audioManager.abandonAudioFocus(afChangeListener)
        }
    }
}