package com.example.collegecommapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.collegecommapp.auth.Login
import com.example.collegecommapp.interfaces.Generalinterface
import com.example.collegecommapp.models.Group
import com.example.collegecommapp.sqlitedatabase.helpers.UsersSqliteDatabaseHelper
import com.example.collegecommapp.sqlitedatabase.tables.UserTable
import com.example.collegecommapp.viewmodels.ChattingViewModel

class MainActivity : AppCompatActivity(), Generalinterface {

    private val TAG = "MainActivity"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var chattingViewModel: ChattingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chattingViewModel = ViewModelProvider(this).get(ChattingViewModel::class.java)
    }

    override fun logOut() {
        sharedPreferences = this.getSharedPreferences(getString(R.string.User), Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        startActivity(Intent(this, Login::class.java))
        finish()
    }

    override fun addChatRoom(group: Group) {
        // TODO: Implement the functionality of adding a new chat room here
    }

    override fun goToChatPage(groupId: String) {
        val sharedPrefs: SharedPreferences = getSharedPreferences("GROUPID", Context.MODE_PRIVATE)
        val ed: SharedPreferences.Editor = sharedPrefs.edit()
        ed.putString("groupId", groupId)
        ed.apply()

        // TODO: Navigate to chat page
    }




    override fun getPhoneDetails(userPhone: String): String? {
        val db = UsersSqliteDatabaseHelper(this).readableDatabase

        val projection = arrayOf(UserTable.COLUMN_NAME_FIRST_NAME, UserTable.COLUMN_NAME_LAST_NAME, UserTable.COLUMN_NAME_EMAIL)

        val selection = "${UserTable.COLUMN_NAME_PHONE} = ?"
        val selectionArgs = arrayOf(userPhone)

        val cursor = db.query(
            UserTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val details = if (cursor.moveToFirst()) {
            val userName = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_FIRST_NAME)) +
                    " " +
                    cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_LAST_NAME))
            val userEmail = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_EMAIL))

            "Name: $userName, Email: $userEmail"
        } else {
            "User not found."
        }

        cursor.close()

        return details
    }

}
