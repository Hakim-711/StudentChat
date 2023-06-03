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
import com.example.collegecommapp.models.User
import com.example.collegecommapp.viewmodels.RegisterActivityViewModel
import java.util.*

class Register : AppCompatActivity(), View.OnClickListener {
    private val TAG = "Register"
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var phone: EditText
    private lateinit var btn: Button
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
        else{
            btn.isEnabled = false
            var user: User = User()
            user.firstName = first
            user.lastName = last
            user.email = em
            user.password = pass
            user.phone = pn
            user.userId = Random().nextInt(100).toString()

            if (user != null){
                val response = registerActivityViewModel.createAccount(user)

                if (response != null){
                    btn.isEnabled = true
                    if (response >= 0){
                        goToLogin()
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(this, "Registration Not Successful", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    btn.isEnabled = true
                    Toast.makeText(this, "Null Returned", Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this, "No User Details", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, Login::class.java))
        finish()
    }

}