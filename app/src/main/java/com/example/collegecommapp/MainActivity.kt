package com.example.collegecommapp

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.collegecommapp.apputils.AppUtils
import com.example.collegecommapp.auth.Login
import com.example.collegecommapp.chatclasses.ChatRoom
import com.example.collegecommapp.chatclasses.Dashboard
import com.example.collegecommapp.interfaces.Generalinterface
import com.example.collegecommapp.models.Group
import com.example.collegecommapp.sqlitedatabase.helpers.UsersSqliteDatabaseHelper
import com.example.collegecommapp.sqlitedatabase.tables.GroupTable
import com.example.collegecommapp.sqlitedatabase.tables.UserTable
import com.example.collegecommapp.viewmodels.ChattingViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), Generalinterface {
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val TAG = "MainActivity"
    private lateinit var mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var chattingViewModel: ChattingViewModel
    private lateinit var appUtils: AppUtils
    private lateinit var usersSqliteDatabaseHelper: UsersSqliteDatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.navFrag) as NavHostFragment
        navController = navHostFragment.navController
        usersSqliteDatabaseHelper = UsersSqliteDatabaseHelper(this)

        chattingViewModel = ViewModelProvider(this).get(ChattingViewModel::class.java)

        appUtils = AppUtils(this)
        initFrag()
    }

    private fun initFrag() {
        navController.navigate(R.id.dashboard)
    }

    //get phone details
    override fun getPhoneDetails(phone: String): String {
        var resultCode: String? = null

        mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                var code = p0.smsCode

                if (code != null){
                    resultCode = code
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.i(TAG, "onVerificationFailed: $p0")

            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                Log.i(TAG, "onCodeSent: $p0")

            }
        }
        val db = usersSqliteDatabaseHelper.readableDatabase

        val projection = arrayOf(
            UserTable.COLUMN_NAME_FIRST_NAME,
            UserTable.COLUMN_NAME_LAST_NAME,
            UserTable.COLUMN_NAME_EMAIL
        )


        val selection = "${UserTable.COLUMN_NAME_PHONE} = ?"
        val selectionArgs = arrayOf(phone)

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






        return resultCode!!
    }

    override fun goToProfile() {
        navController.navigate(R.id.profile)
    }

    override fun goToNewChatRooms() {
        navController.navigate(R.id.newChatRooms)
    }

    override fun goToSearch() {
        navController.navigate(R.id.search)
    }

    override fun logOut() {
        sharedPreferences = this.getSharedPreferences(getString(R.string.User), MODE_PRIVATE)
        var editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.clear()
        editor.apply()
        startActivity(Intent(this, Login::class.java))
        finish()
    }

    override fun addChatRoom(group: Group) {
        if (appUtils.checkWiFi()){
            var sharedPreferences2 = getSharedPreferences("USER", Context.MODE_PRIVATE)
            var userPhone = sharedPreferences2.getString(getString(R.string.phone), "")

            mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    var code = p0.smsCode

                    if (code != null){
                        var newSharedPreferences: SharedPreferences = getSharedPreferences("CODE", MODE_PRIVATE)
                        var editor2: SharedPreferences.Editor = newSharedPreferences.edit()
                        editor2.putString("NUMBER", code)
                        editor2.putString("GroupId", group.group_id)
                        editor2.apply()

                        navController.navigate(R.id.verification)
                    }

                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Log.i(TAG, "onVerificationFailed: $p0")

                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    Log.i(TAG, "onCodeSent: $p0")

                }
            }


        }
        else{
            Toast.makeText(this, "Connect to the internet for code generation", Toast.LENGTH_LONG).show()
        }

    }

    override fun goToMainPage() {
        navController.navigate(R.id.dashboard)
    }

    override fun goToChatPage(groupId: String) {
        var sharedPrefs: SharedPreferences = getSharedPreferences("GROUPID", MODE_PRIVATE)
        var ed: SharedPreferences.Editor = sharedPrefs.edit()
        ed.putString("groupId", groupId)
        ed.apply()

        navController.navigate(R.id.chatRoom)
    }

    override fun onBackPressed() {
        navHostFragment.childFragmentManager.primaryNavigationFragment?.let {
            if (it is ChatRoom){
                navController.navigate(R.id.dashboard)
            }
            if (it is Dashboard){
                logOut()
            }
            else{
                super.onBackPressed()
            }
        }
    }
}
/*
class MainActivity : AppCompatActivity(), Generalinterface {
private lateinit var navHostFragment: NavHostFragment
private lateinit var navController: NavController
private lateinit var sharedPreferences: SharedPreferences
private lateinit var usersSqliteDatabaseHelper: UsersSqliteDatabaseHelper
private lateinit var chattingViewModel: ChattingViewModel

override fun onCreate(savedInstanceState: Bundle?) {
super.onCreate(savedInstanceState)
setContentView(R.layout.activity_main)
navHostFragment = supportFragmentManager.findFragmentById(R.id.navFrag) as NavHostFragment
navController = navHostFragment.navController
chattingViewModel = ViewModelProvider(this).get(ChattingViewModel::class.java)
sharedPreferences = this.getSharedPreferences(getString(R.string.User), Context.MODE_PRIVATE)
usersSqliteDatabaseHelper = UsersSqliteDatabaseHelper(this)

initFrag()
}

private fun initFrag() {
navController.navigate(R.id.dashboard)
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
override fun goToProfile() {

navController.navigate(R.id.profile)

}

override fun goToNewChatRooms() {
navController.navigate(R.id.newChatRooms)
}

override fun goToSearch() {
navController.navigate(R.id.search)
}


override fun logOut() {
val editor: SharedPreferences.Editor = sharedPreferences.edit()
editor.clear()
editor.apply()
startActivity(Intent(this, Login::class.java))
finish()
}

override fun addChatRoom(group: Group) {
// Get a writable database instance
val db = usersSqliteDatabaseHelper.writableDatabase

// Prepare the values to insert into the database
val values = ContentValues().apply {
put(GroupTable.COLUMN_NAME_GROUP_ID, group.group_id)
put(GroupTable.COLUMN_NAME_GROUP_NAME, group.group_name)
put(GroupTable.COLUMN_NAME_DESCRIPTION, group.group_description)
put(GroupTable.COLUMN_NAME_CAPACITY, group.group_capacity)
put(GroupTable.COLUMN_NAME_IMAGE, group.group_image)
put(GroupTable.COLUMN_NAME_CREATEDBY, group.group_created_by)
put(GroupTable.COLUMN_NAME_DATE_CREATED, group.group_date_created)
}

// Insert the new chat room into the database
val newRowId = db.insert(GroupTable.TABLE_NAME, null, values)

if (newRowId != -1L) {
// New chat room added successfully
// You can perform any additional actions here, such as updating the UI or showing a success message
} else {
// Failed to add the new chat room
// You can handle the error or show an error message
}
}


override fun goToChatPage(groupId: String) {
val sharedPrefs: SharedPreferences = getSharedPreferences("GROUPID", Context.MODE_PRIVATE)
val ed: SharedPreferences.Editor = sharedPrefs.edit()
ed.putString("groupId", groupId)
ed.apply()

navController.navigate(R.id.chatRoom)
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
*/