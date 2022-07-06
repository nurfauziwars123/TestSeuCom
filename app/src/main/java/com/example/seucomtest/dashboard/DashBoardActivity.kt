package com.example.seucomtest.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import com.example.seucomtest.R
import com.example.seucomtest.dashboard.model.Warung
import com.example.seucomtest.databinding.ActivityDashBoardBinding
import com.example.seucomtest.utils.Constant
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashBoardActivity : AppCompatActivity() {

    private var binding : ActivityDashBoardBinding ?= null
    private val myRef = FirebaseDatabase.getInstance().getReference("Warung")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        atttachView()
        attachListener()
    }

    private fun atttachView() {
        myRef.addValueEventListener(object : ValueEventListener{
            val warung =  mutableListOf<Warung>()
            override fun onDataChange(snapshot: DataSnapshot) {
                warung.clear()
                for(datas in snapshot.children){
                    val name = datas.child("nama_warung").value.toString()
                    val cordinate = datas.child("kordinate").value.toString()
                    val alamat = datas.child("alamat").value.toString()
                    val image = datas.child("downloadUri").value.toString()
                    val id = datas.child("id").value.toString()

                    val warung1 = Warung(name, cordinate, alamat, image, id)
                    Constant.warungNameList?.add(name)
                    warung.add(warung1)
                }
                binding?.progressBar2?.visibility = GONE
                binding?.rvListWarung?.adapter = ListWarungAdapter(this@DashBoardActivity, warung)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun attachListener() {
        binding?.btnFabAdd?.setOnClickListener {
            startActivity(Intent(this@DashBoardActivity, AddWarung::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}