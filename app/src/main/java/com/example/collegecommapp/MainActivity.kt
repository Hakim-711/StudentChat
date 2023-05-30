package com.example.collegecommapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.collegecommapp.auth.Login
import com.example.collegecommapp.chatclasses.ChatRoom
import com.example.collegecommapp.chatclasses.Dashboard
import com.example.collegecommapp.interfaces.Generalinterface
import com.example.collegecommapp.models.Group
import com.example.collegecommapp.sqlitedatabase.helpers.UsersSqliteDatabaseHelper
import com.example.collegecommapp.sqlitedatabase.tables.UserTable
import com.example.collegecommapp.viewmodels.ChattingViewModel

class MainActivity : AppCompatActivity(), Generalinterface {
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val TAG = "MainActivity"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var usersSqliteDatabaseHelper: UsersSqliteDatabaseHelper
    private lateinit var chattingViewModel: ChattingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.navFrag) as NavHostFragment
        navController = navHostFragment.navController
        sharedPreferences = this.getSharedPreferences(getString(R.string.User), Context.MODE_PRIVATE)
        usersSqliteDatabaseHelper = UsersSqliteDatabaseHelper(this)
        chattingViewModel = ViewModelProvider(this).get(ChattingViewModel::class.java)

        initFrag()
    }

    private fun initFrag() {
        navController.navigate(R.id.dashboard)
    }

    override fun logOut() {
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
        val db = usersSqliteDatabaseHelper.readableDatabase

        val projection = arrayOf(
            UserTable.COLUMN_NAME_FIRST_NAME,
            UserTable.COLUMN_NAME_LAST_NAME,
            UserTable.COLUMN_NAME_EMAIL
        )

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
            val userName =
                cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_FIRST_NAME)) +
                        " " +
                        cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_LAST_NAME))
            val userEmail =
                cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_EMAIL))

            "Name: $userName, Email: $userEmail"
        } else {
            "User not found."
        }

        cursor.close()

        return details
    }

    override fun goToNewChatRooms() {
        navController.navigate(R.id.newChatRooms)
    }

    override fun goToSearch() {
        navController.navigate(R.id.search)
    }

    override fun goToMainPage() {
        navController.navigate(R.id.dashboard)
    }

    override fun onBackPressed() {
        navHostFragment.childFragmentManager.primaryNavigationFragment?.let {
            if (it is ChatRoom) {
                navController.navigate(R.id.dashboard)
            } else if (it is Dashboard) {
                logOut()
            } else {
                super.onBackPressed()
            }
        }
    }
}
