package com.example.chandan.finalbuild

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.util.ArrayList


class TrackAdapter(whichLay:String, private val ctx:Context, private val songData:ArrayList<SModel>):RecyclerView.Adapter<TrackAdapter.ViewHolder>(){

    private var getSongInter:GetSongInter?=null
    private var whichLay:String?=null

        init {
           this.whichLay = whichLay
            getSongInter=ctx as GetSongInter
        }



    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        when(whichLay){
            "SR" -> {
                return ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.home_radom_card,p0,false) as View)
            }
            "SF" -> {
              return  ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.recent_and_fav_layout,p0,false) as View)
            }
            "SAT"->{
                return ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.single_track_view,p0,false) as View)
            }
            "STT"->{
                return ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.single_track_view,p0,false) as View)
            }
        }
        return null!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i("AB:", "done for $songData!![position].title")
        holder.t_cover!!.setImageBitmap(getSongInter!!.getBitmapFromPath(songData[position].path))
        holder.t_title!!.text = songData[position].title
        if(whichLay.equals("SAT") || whichLay.equals("STT") ){
            holder.t_artist!!.text=songData[position].artist
        }
    }

    override fun getItemCount(): Int {
        if(whichLay.equals("STT") && songData.size>8){
            return 8
        }
        return songData.size
    }

    inner class ViewHolder(v:View):RecyclerView.ViewHolder(v){
        var t_cover:ImageView? =null
        var t_title:TextView?=null
        var t_artist:TextView?=null

        init{
            when(whichLay){
                "SR"->{
                    t_cover=v.findViewById(R.id.random_art_cover)
                    t_title=v.findViewById(R.id.random_card_title)
                }
                "SF"->{
                    t_cover=v.findViewById(R.id.ivOf_rf)
                    t_title=v.findViewById(R.id.title_rf)
                }
                "SAT"->{
                    t_cover=v.findViewById(R.id.st_img)
                    t_title=v.findViewById(R.id.st_name)
                    t_artist=v.findViewById(R.id.st_artist)
                }
                "STT"->{
                    t_cover=v.findViewById(R.id.st_img)
                    t_title=v.findViewById(R.id.st_name)
                    t_artist=v.findViewById(R.id.st_artist)
                }
            }

            v.setOnClickListener{
                getSongInter!!.startCurSong(adapterPosition,songData)
            }
            Log.i("AV:","done")
        }

    }

}