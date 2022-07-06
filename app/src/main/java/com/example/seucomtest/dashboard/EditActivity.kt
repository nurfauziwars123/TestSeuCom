package com.example.seucomtest.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.seucomtest.R
import com.example.seucomtest.dashboard.model.Warung
import com.example.seucomtest.databinding.ActivityEditBinding
import com.example.seucomtest.utils.UtilsFunction.setImage

class EditActivity : AppCompatActivity() {

    private var binding : ActivityEditBinding ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent.getParcelableExtra<Warung>("data")
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        initView(intent)
        attachBtn()
    }

    private fun attachBtn() {
        binding?.btnFabEdit?.setOnClickListener {
            activeListener()
        }
    }

    private fun activeListener() {
        binding?.etWarungName?.isEnabled = true
        binding?.etWarungName?.requestFocus()
        binding?.etAdress?.isEnabled = true
        binding?.etCordinate?.isEnabled = true

        binding?.ivAddWarung?.isClickable = true
    }

    private fun initView(item : Warung?) {
        binding?.etWarungName?.setText(item?.warung_name)
        binding?.etWarungName?.setText(item?.warung_name)
        binding?.etWarungName?.setText(item?.warung_name)

        binding?.ivAddWarung?.let { setImage(this@EditActivity, item?.image ?: "", it) }
    }
}