package com.example.collegecommapp.reporitories

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.collegecommapp.models.User
import com.example.collegecommapp.sqlitedatabase.helpers.UsersSqliteDatabaseHelper
import com.example.collegecommapp.sqlitedatabase.tables.UserTable


class AuthRepository(context: Context) {
    private var usersSqliteDatabaseHelper: UsersSqliteDatabaseHelper? = null

    init {
        usersSqliteDatabaseHelper = UsersSqliteDatabaseHelper(context)
    }

    companion object{
        private var INSTANCE: AuthRepository? = null

        fun setInstance(context: Context): AuthRepository{
            if (INSTANCE == null){
                INSTANCE = AuthRepository(context)
            }

            return INSTANCE!!
        }
    }

    //create account
    suspend fun createAccount(user: User): Long {
        var result: Long? = null

        val db: SQLiteDatabase = usersSqliteDatabaseHelper!!.writableDatabase
        val contentValues: ContentValues = ContentValues().apply {
            put(UserTable.COLUMN_NAME_EMAIL, user.email)
            put(UserTable.COLUMN_NAME_FIRST_NAME, user.firstName)
            put(UserTable.COLUMN_NAME_LAST_NAME, user.lastName)
            put(UserTable.COLUMN_NAME_PHONE, user.phone)
            put(UserTable.COLUMN_NAME_PASSWORD, user.password)
            put(UserTable.COLUMN_NAME_USERID, user.userId)
        }

        val rowId = db.insert(UserTable.TABLE_NAME, null, contentValues)
        if (rowId >= 0){
            result = rowId
        }
        else{
            result = -1
        }

        return result
    }


    suspend fun loginUser(email: String, password: String): User{
        val db: SQLiteDatabase = usersSqliteDatabaseHelper!!.readableDatabase
        val projection = arrayOf(UserTable.COLUMN_NAME_USERID, UserTable.COLUMN_NAME_FIRST_NAME, UserTable.COLUMN_NAME_LAST_NAME, UserTable.COLUMN_NAME_ID, UserTable.COLUMN_NAME_PHONE, UserTable.COLUMN_NAME_EMAIL, UserTable.COLUMN_NAME_PASSWORD)
        var selection = "${UserTable.COLUMN_NAME_EMAIL} = ? AND ${UserTable.COLUMN_NAME_PASSWORD} = ?"
        var selectionArgs = arrayOf(email, password)

        val cursor = db.query(UserTable.TABLE_NAME,
        projection,
        selection,
        selectionArgs,
        null,
        null,
        null)

        var user: User = User()
        while (cursor.moveToNext()){
            user.userId = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_USERID))
            user.firstName = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_FIRST_NAME))
            user.lastName = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_LAST_NAME))
            user.email = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_EMAIL))
            user.password = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_PASSWORD))
            user.phone = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_PHONE))
        }

        return user
    }

    suspend fun updateUser(userId: String, firstName: String, lastName: String, phone: String, password: String): Int{
        val db = usersSqliteDatabaseHelper!!.writableDatabase
        var contentValues = ContentValues().apply {
            put(UserTable.COLUMN_NAME_FIRST_NAME, firstName)
            put(UserTable.COLUMN_NAME_LAST_NAME, lastName)
            put(UserTable.COLUMN_NAME_PHONE, phone)
            put(UserTable.COLUMN_NAME_PASSWORD, password)
        }
        var selection = "${UserTable.COLUMN_NAME_USERID} LIKE ?"
        var selectionArgs = arrayOf(userId)
        val count = db.update(
            UserTable.TABLE_NAME,
            contentValues,
            selection,
            selectionArgs
        )

        if (count > 0){
            return count
        }
        else{
            return 0
        }
    }

    suspend fun getUsers(): ArrayList<User>{
        val db: SQLiteDatabase = usersSqliteDatabaseHelper!!.readableDatabase
        val projection = arrayOf(UserTable.COLUMN_NAME_USERID, UserTable.COLUMN_NAME_FIRST_NAME, UserTable.COLUMN_NAME_LAST_NAME, UserTable.COLUMN_NAME_ID, UserTable.COLUMN_NAME_PHONE, UserTable.COLUMN_NAME_EMAIL, UserTable.COLUMN_NAME_PASSWORD)

        val cursor = db.query(UserTable.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null)

        val us: ArrayList<User> = ArrayList()

        while (cursor.moveToNext()){
            var user: User = User()
            user.userId = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_USERID))
            user.firstName = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_FIRST_NAME))
            user.lastName = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_LAST_NAME))
            user.email = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_EMAIL))
            user.password = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_PASSWORD))
            user.phone = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.COLUMN_NAME_PHONE))

            us.add(user)
        }

        return us
    }
}