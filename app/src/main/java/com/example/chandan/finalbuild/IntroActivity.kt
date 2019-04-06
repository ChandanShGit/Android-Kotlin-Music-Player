package com.example.chandan.finalbuild

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import kotlin.collections.ArrayList

class IntroActivity : AppCompatActivity() {


    private lateinit var permissionString:Array<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.P) {
            permissionString=arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            )

        }else{
            permissionString=arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.FOREGROUND_SERVICE

            )
        }

        if(!hasPermissions(this@IntroActivity,*permissionString)){
            //Ask For Permissions
            ActivityCompat.requestPermissions(this@IntroActivity,permissionString,131)

        }else{
            jump()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            131->{
                if(grantResults.isNotEmpty()) {
                    for(grant in grantResults){
                        if(grantResults[grant]!= PackageManager.PERMISSION_GRANTED)
                            return
                    }
                    jump()
                }else{
                    Toast.makeText(this,"Please Grant All Permissions", Toast.LENGTH_SHORT).show()
                }
            }

            else->{
                Toast.makeText(this,"Something Went Wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun jump(){
        Handler().postDelayed(
            {
                LoadData().execute()
            },0
        )
    }

    private fun hasPermissions(context : Context, vararg permission:String):Boolean{
        var hasAllPermissions=true
        var res:Int
        for(per in permission){
            res=context.checkCallingOrSelfPermission(per)
            if(res!= PackageManager.PERMISSION_GRANTED){
                hasAllPermissions=false
            }
        }
        Toast.makeText(this,"sda",Toast.LENGTH_LONG).show()

        return hasAllPermissions

    }

    inner class LoadData :AsyncTask<Void,Void,Boolean>(){

        private var arrayListOfSongs:ArrayList<SModel>?=null

        override fun onPreExecute() {
            super.onPreExecute()
            Log.i("prog","PreLoading")
        }

        override fun doInBackground(vararg params: Void?):Boolean {
            Log.i("prog","Loading Start")
            arrayListOfSongs=getSongsFromDevice()
            return true
        }


        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            Log.i("prog","Loaded")
            if(result){
                val intent= Intent(this@IntroActivity,HomeActivity::class.java)
                intent.putParcelableArrayListExtra("SModelList",arrayListOfSongs)
                startActivity(intent)
                this@IntroActivity.finish()
            }else{
                //Show No Song Present Activity
            }
        }


        private fun getSongsFromDevice():ArrayList<SModel>?{
            val arrayListOfSongs=ArrayList<SModel>()
            val contentResolver= contentResolver
            val songUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val albumUri=MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
            val songCursor =contentResolver?.query(songUri,null,null,null,null)
            if(songCursor!=null && songCursor.moveToFirst()){
                val songId=songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
                val songTitle=songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val songArtist=songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val songData=songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
                val songDateAdded=songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
                while (songCursor.moveToNext()) {
                    val currentId = songCursor.getLong(songId)
                    val currentTitle = songCursor.getString(songTitle)
                    val currentArtist = songCursor.getString(songArtist)
                    val currentPath = songCursor.getString(songData)
                    val currentDateAdded = songCursor.getLong(songDateAdded)
                    arrayListOfSongs.add(SModel(currentId, currentTitle, currentArtist, currentPath, currentDateAdded))
                    Log.i("title", currentTitle)
                }
            }else{
                Log.i("Home","No SOngs")
                return null
            }

            return arrayListOfSongs
        }


    }




}
