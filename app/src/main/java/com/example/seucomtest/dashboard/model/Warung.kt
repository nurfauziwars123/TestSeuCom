package com.example.seucomtest.dashboard.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Warung(
    val warung_name : String,
    val cordinate : String,
    val alamat : String,
    val image : String ?= null,
    val id : String ?= null
    ) : Parcelable