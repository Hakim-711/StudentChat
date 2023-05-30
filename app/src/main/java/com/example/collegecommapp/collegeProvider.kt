package com.example.collegecommapp

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

class collegeProvider: ContentProvider() {
    companion object{
        val PROVIDER_NAME="com.example.collegecommapp.collegeProvider"
        val URL="content://"+PROVIDER_NAME +"/college"
        val CONTENT_URI=Uri.parse(URL)
        val _ID="_id"
        val NAME="name"
        val EMAIL="email"
        val DATABASE_NAME="College"
        val STUDENT_TABLE_NAME="student"
        val DATABASE_VERSION=1
        val CREATE_DB_TABLE="CREATE TABLE"

    }
    private var db: SQLiteDatabase? = null

    private class DatabaseHelper internal constructor(context: Context?) :
            SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION){
        override fun onCreate(db: SQLiteDatabase?) {
            TODO("Not yet implemented")
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            TODO("Not yet implemented")
        }

    }
    override fun onCreate(): Boolean {
        TODO("Not yet implemented")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? {
        TODO("Not yet implemented")
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int {
        TODO("Not yet implemented")
    }
}