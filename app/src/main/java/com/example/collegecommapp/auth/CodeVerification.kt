package com.example.collegecommapp.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.collegecommapp.R
import com.example.collegecommapp.MainActivity

class CodeVerification : AppCompatActivity(), View.OnClickListener {
    private lateinit var btn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_codeverification)
        initViews()
    }

    private fun initViews() {
        btn = findViewById(R.id.btnVerify)
        btn.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.btnVerify -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }
}