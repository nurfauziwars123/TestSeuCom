package com.example.seucomtest.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.textfield.TextInputEditText

object UtilsFunction {

     fun showEdtError(editText: TextInputEditText?, errorMessages : String) {
        editText?.error = errorMessages
        editText?.requestFocus()
    }

    fun setImage(context: Context, urlImage: String, imageView: ImageView){
        Glide.with(context)
            .load(urlImage)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)

    }
}