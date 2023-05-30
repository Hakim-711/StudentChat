package com.example.collegecommapp.auth

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.collegecommapp.MainActivity
import com.example.collegecommapp.R
import com.example.collegecommapp.viewmodels.RegisterActivityViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class Register : AppCompatActivity(), View.OnClickListener {
    private val TAG = "Register"
    private lateinit var firstName: TextInputEditText
    private lateinit var lastName: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var phone: TextInputEditText
    private lateinit var btn: MaterialButton
    private lateinit var back: ImageView
    private lateinit var registerActivityViewModel: RegisterActivityViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerActivityViewModel = ViewModelProvider(this).get(RegisterActivityViewModel::class.java)
        initViews()

    }

    private fun initViews() {
        phone = findViewById(R.id.phone)
        firstName = findViewById(R.id.firstname)
        lastName = findViewById(R.id.lastname)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        btn = findViewById(R.id.btnRegister)
        back = findViewById(R.id.registerBack)
        back.setOnClickListener(this)
        btn.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.btnRegister -> {
                registerUser()
            }
            R.id.registerBack -> {
                startActivity(Intent(this, Login::class.java))
            }
        }
    }

    private fun registerUser() {
        val first = firstName.text.toString().trim()
        val last = lastName.text.toString().trim()
        val em = email.text.toString().trim()
        val pass = password.text.toString().trim()
        val pn = phone.text.toString().trim()
        if (TextUtils.isEmpty(pn)){
            phone.error = "Phone Required"
            phone.requestFocus()
        }
        else if (TextUtils.isEmpty(first)){
            firstName.error = "First Name Required"
            firstName.requestFocus()
        }
        else if (TextUtils.isEmpty(last)){
            lastName.error = "Last Name Required"
            lastName.requestFocus()
        }
        else if (TextUtils.isEmpty(em)){
            email.error = "Email Required"
            email.requestFocus()
        }
        else if (TextUtils.isEmpty(pass)){
            password.error = "Password Required"
            password.requestFocus()
        }
        else if (pass.length < 8){
            password.error = "Password Should have eight or more characters"
            password.requestFocus()
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, Login::class.java))
        finish()
    }

}