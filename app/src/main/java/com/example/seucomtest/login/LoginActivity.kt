package com.example.seucomtest.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View.GONE
import android.view.View.VISIBLE
import com.example.seucomtest.dashboard.DashBoardActivity
import com.example.seucomtest.databinding.ActivityLoginBinding
import com.example.seucomtest.utils.UtilsFunction
import com.example.seucomtest.utils.UtilsFunction.showEdtError
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private var binding : ActivityLoginBinding ?= null
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        attachListner()
    }

    private fun attachListner() {

        binding?.tvRegister?.setOnClickListener {startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))}
        binding?.btnLogin?.setOnClickListener {
            val email = binding?.etEmail
            val password = binding?.etPassword

            when {
                email?.text.isNullOrEmpty() -> showEdtError(email, "Email Tidak Boleh Kosong")
                password?.text.isNullOrEmpty() ->showEdtError(password, "Passsword Tidak Boleh Kosong")
                else -> login(email?.text.toString(), password?.text.toString())
            }
        }
    }

    private fun login(email: String, password: String) {
        binding?.progressBar?.visibility = VISIBLE
        binding?.btnLogin?.visibility = GONE
        mAuth?.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it?.isSuccessful) {
                    binding?.progressBar?.visibility = GONE
                    startActivity(Intent(this@LoginActivity, DashBoardActivity::class.java))
                    finish()
                }else{

                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}