package com.example.chandan.finalbuild

import android.content.Context
import android.content.SharedPreferences

class PreConfig(private val ctx: Context) {

    private val sp: SharedPreferences = ctx.getSharedPreferences(ctx.getString(R.string.pref_file), Context.MODE_PRIVATE)
    lateinit var editor: SharedPreferences.Editor

    fun readShuffle(): Boolean {
        return sp.getBoolean(ctx.getString(R.string.pref_shuffle), false)
    }

    fun writeShuffle(shuf: Boolean) {
        editor = sp.edit()
        editor.putBoolean(ctx.getString(R.string.pref_shuffle), shuf)
        editor.commit()
    }

    fun readLoop(): Boolean {
        return sp.getBoolean(ctx.getString(R.string.pref_loop), false)
    }

    fun writeLoop(loop: Boolean) {
        editor = sp.edit()
        editor.putBoolean(ctx.getString(R.string.pref_loop), loop)
        editor.commit()
    }



    fun isFirstTimeLaunched():Boolean{
        return sp.getBoolean(ctx.getString(R.string.pref_launch_status),true)
    }

    fun falseFirstTimeLaunch(changed:Boolean){
        editor=sp.edit()
        editor.putBoolean(ctx.getString((R.string.pref_launch_status)),changed)
        editor.commit()
    }


}
