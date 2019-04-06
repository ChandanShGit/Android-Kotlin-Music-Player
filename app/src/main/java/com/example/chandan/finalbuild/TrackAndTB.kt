package com.example.chandan.finalbuild

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_track_and_tb.*
import kotlinx.android.synthetic.main.tracks_layout.*

class TrackAndTB : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_and_tb)
        val action=intent.getStringExtra("TAG")
        val tracks=intent.getParcelableArrayListExtra<SModel>("SModelList")
        if(action.equals("all_tracks",true))
        {
            tb_text.text="ALL Tracks"
            Fun.setRecyclerTrackAdapter(this,tb_tracks_rv,TrackAdapter("SAT",this,tracks),
                LinearLayoutManager.VERTICAL,false)
        }
    }

}
