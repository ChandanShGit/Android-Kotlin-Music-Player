package com.example.chandan.finalbuild

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class SModel(var songId:Long,var title:String,var artist:String ,var path:String,var dateAdded:Long):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(songId)
        parcel.writeString(title)
        parcel.writeString(artist)
        parcel.writeString(path)
        parcel.writeLong(dateAdded)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SModel> {
        override fun createFromParcel(parcel: Parcel): SModel {
            return SModel(parcel)
        }

        override fun newArray(size: Int): Array<SModel?> {
            return arrayOfNulls(size)
        }
    }



    class PLModel(var plId:Long,var plname:String):Parcelable{
        constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeLong(plId)
            parcel.writeString(plname)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<PLModel> {
            override fun createFromParcel(parcel: Parcel): PLModel {
                return PLModel(parcel)
            }

            override fun newArray(size: Int): Array<PLModel?> {
                return arrayOfNulls(size)
            }
        }
    }

}
