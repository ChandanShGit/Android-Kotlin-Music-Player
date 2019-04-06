package com.example.chandan.finalbuild

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.tracks_layout.*
import kotlinx.android.synthetic.main.tracks_layout.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment(){




    lateinit var ctx:Context
    var itemView:View?=null
    var getSongInter:GetSongInter?=null
    var favAdapter:TrackAdapter?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        itemView= inflater.inflate(R.layout.fragment_home, container, false)
        getSongInter=ctx as GetSongInter

       itemView?.see_all_tv!!.setOnClickListener{
            getSongInter?.startTrackandTB()
        }


        return itemView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        week_random_txt.text="${Fun.today()} Music"

        if(getSongInter!!.getRandomSongs().size>0){
            go_ahead_tv.visibility=View.VISIBLE
            home_random_rv.visibility=View.VISIBLE
            Fun.setRecyclerTrackAdapter(ctx,home_random_rv,TrackAdapter("SR",ctx,getSongInter!!.getRandomSongs()),LinearLayoutManager.HORIZONTAL,false)
        }else{
            go_ahead_tv.visibility=View.GONE
            home_random_rv.visibility=View.GONE
        }
        val data:ArrayList<SModel>?=SaveMetaDB(ctx).getList()
        if(data!=null){
            fav_ll.visibility=View.VISIBLE
            favat_main_rv.visibility=View.VISIBLE
            Fun.setRecyclerTrackAdapter(ctx,favat_main_rv,TrackAdapter("SF",ctx,data),LinearLayoutManager.HORIZONTAL,false)
        }else{
            fav_ll.visibility=View.GONE
            favat_main_rv.visibility=View.GONE
        }
        Fun.setRecyclerTrackAdapter(ctx,track_list_rv,TrackAdapter("STT",ctx,getSongInter!!.getAllSongList()),LinearLayoutManager.VERTICAL,false)
        Fun.setRecyclerPlaylist(ctx,playlist_main_rv,PlaylistAdapter(ctx,getSongInter!!.getAllPlaylist()))

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        ctx=context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        ctx=activity as Activity
    }


}
