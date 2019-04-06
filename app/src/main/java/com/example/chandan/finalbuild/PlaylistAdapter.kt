package com.example.chandan.finalbuild

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.ArrayList

class PlaylistAdapter(private val ctx:Context,private val playlist:ArrayList<SModel.PLModel>):RecyclerView.Adapter<PlaylistAdapter.Holder>(){

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Holder {
      return Holder(LayoutInflater.from(ctx).inflate(R.layout.playlist_layout,p0,false))
    }

    override fun onBindViewHolder(p0: Holder, p1: Int) {
      p0.title.text=playlist[p1].plname
    }

    override fun getItemCount(): Int {
       return playlist.size
    }

    inner class Holder(v:View):RecyclerView.ViewHolder(v){
        val title:TextView = v.findViewById(R.id.pl_name)
    }
}