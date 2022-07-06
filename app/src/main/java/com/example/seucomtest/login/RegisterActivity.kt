package com.example.seucomtest.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.example.seucomtest.R
import com.example.seucomtest.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private var binding : ActivityRegisterBinding ?= null
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        attachListener()
    }

    private fun attachListener() {

        binding?.etEmail?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(binding?.etEmail?.text.toString()).matches()){
                    binding?.btnRegister?.isEnabled = true
                }
                else{
                    binding?.btnRegister?.isEnabled = false
                    binding?.etEmail?.error = "Email Tidak Valid"
                }
            }
        })
        binding?.btnRegister?.setOnClickListener {
            val email = binding?.etEmail?.text.toString()
            val password = binding?.etPassword?.text.toString()
            val confirmPassword = binding?.etConfirmPassword?.text.toString()

            if (email?.isNotEmpty() && password?.isNotEmpty() && confirmPassword.isNotEmpty()){
                if (password == confirmPassword) {
                    if (password.length > 5) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                                if (it?.isSuccessful){
                                    showToast("Berhasil Register Account")
                                    finish()
                                }else{
                                    showToast("User Sudah Terdaftar")
                                }
                            }
                    }else{
                        showToast("Password Tidak Boleh Kurang dari 6 karakter")
                    }
                }else{
                   showToast("Password Tidak Sama")
                }

            }else{
                showToast("Harap Isi Semua Form")
            }
        }
    }

    private fun showToast(message : String ){
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding == null
    }
}