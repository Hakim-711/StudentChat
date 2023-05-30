package com.example.collegecommapp.chatclasses

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collegecommapp.R
import com.example.collegecommapp.adapters.ChatAdapter
import com.example.collegecommapp.interfaces.Generalinterface
import com.example.collegecommapp.models.Chat
import com.example.collegecommapp.sqlitedatabase.helpers.UsersSqliteDatabaseHelper
import com.example.collegecommapp.sqlitedatabase.queries.ChatEntries
import com.example.collegecommapp.sqlitedatabase.queries.ChatEntries.Companion.CHAT_SQL_ENTRIES
import com.example.collegecommapp.sqlitedatabase.tables.ChatsTable
import com.example.collegecommapp.viewmodels.ChattingViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class ChatRoom : Fragment(), View.OnClickListener {
    private val TAG = "ChatRoom"
    private lateinit var chat: EditText
    private lateinit var send: ImageView
    private lateinit var attach: ImageView
    private lateinit var bot: RelativeLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var desc: TextView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var back: RelativeLayout
    private lateinit var title: TextView
    private lateinit var imgChat: ImageView
    private lateinit var chattingViewModel: ChattingViewModel
    private var groupId: String? = null
    private var userId: String? = null
    private var chatList: ArrayList<Chat> = ArrayList()
    private lateinit var generalinterface: Generalinterface
    private var filePath: Uri? = null
    private var imgUrl: String? = null
    var chatAdapter = ChatAdapter(activity as Context)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chattingViewModel = ViewModelProvider(requireActivity()).get(ChattingViewModel::class.java)


        recyclerView = view.findViewById(R.id.recyclerChats)
        chat = view.findViewById(R.id.editChat)
        attach = view.findViewById(R.id.attach)
        send = view.findViewById(R.id.send)
        back = view.findViewById(R.id.relBackChats)
        title = view.findViewById(R.id.txtTitleChat)
        desc = view.findViewById(R.id.txtDesc)
        bot = view.findViewById(R.id.relBot)
        imgChat = view.findViewById(R.id.imgChat)
        imgChat.visibility = View.GONE

        linearLayoutManager = LinearLayoutManager(activity)
        var chatAdapter = ChatAdapter(activity as Context)

        attach.setOnClickListener(this)
        send.setOnClickListener(this)
        back.setOnClickListener(this)
        bot.setOnClickListener(this)

        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = linearLayoutManager

        getGroupDetails()

        getChats()

        getUserId()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    var chatPic = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
        if (it.resultCode == AppCompatActivity.RESULT_OK && it.data != null){
            filePath = it.data!!.data!!
            try {
                val source = ImageDecoder.createSource(requireActivity().contentResolver,
                    filePath!!
                )
                val bitmap: Bitmap = ImageDecoder.decodeBitmap(source)
                if (bitmap != null){
                    imgChat.visibility= View.VISIBLE
                    imgChat.setImageBitmap(bitmap)

                    sendToDb()
                }

            }
            catch (e: IOException){
                e.printStackTrace()
            }
        }
    })

    private fun sendToDb() {
        if (!filePath!!.equals(null)){
            Log.i(TAG, "sendToDb: " + "Sent" + filePath)


        }
        else{
            Toast.makeText(activity, "Not sent", Toast.LENGTH_LONG).show()
        }
    }

    private fun getUserId() {
        var sharedPrefs: SharedPreferences = activity?.getSharedPreferences(getString(R.string.User),
            Context.MODE_PRIVATE
        )!!

        userId = sharedPrefs.getString(getString(R.string.id), "")
    }

    private fun getGroupDetails() {
        var sharedPrefs: SharedPreferences = activity?.getSharedPreferences("GROUPID",
            Context.MODE_PRIVATE
        )!!

        groupId = sharedPrefs.getString("groupId", "")

        chattingViewModel.getGroupDetails(groupId!!).observe(viewLifecycleOwner, Observer {
            if (it != null){
                title.text = it.group_name
                desc.text = it.group_description
            }
        })
    }

    private fun getChats() {
        chattingViewModel.getGroupChats(groupId!!).observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()){
                setChats(it)
            }
        })
    }

    private fun setChats(it: List<Chat>?) {
        for (i in it!!){
            chatList.add(i)
        }

        chatAdapter.getData(chatList)
        recyclerView.scrollToPosition(chatList.size - 1)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){

            R.id.send -> {
                sendChat()
            }
            R.id.relBackChats -> {

            }
            R.id.relBot -> {
                showBottomSheet()
            }
        }

    }

    private fun sendChat() {
        Log.i(TAG, "sendChat: Sending")
        var date = SimpleDateFormat("yyyy-MM-dd").format(Date())
        var chatId: String = Random.nextInt(10, 1000).toString()
        var time = SimpleDateFormat("hh:mm").format(Date())
        var message = chat.text.toString().trim()

        if (!TextUtils.isEmpty(message)){
            var nChat: Chat = Chat()
            nChat.userId = userId
            nChat.groupId = groupId
            nChat.time = time
            nChat.date = date
            nChat.message = message
            nChat.chatId = chatId
            nChat.img = imgUrl ?: ""

            val db = UsersSqliteDatabaseHelper(requireContext()).writableDatabase
            val values = ContentValues()
            values.put(ChatsTable.COLUMN_NAME_USERID, chatId)
            values.put(ChatsTable.COLUMN_NAME_USERID, userId)
            values.put(ChatsTable.COLUMN_NAME_GROUPID, groupId)
            values.put(ChatsTable.COLUMN_NAME_TIME, time)
            values.put(ChatsTable.COLUMN_NAME_DATE, date)
            values.put(ChatsTable.COLUMN_NAME_MESSAGE, message)
            values.put(ChatsTable.COLUMN_NAME_IMAGE, imgUrl)

            val success = db.insert(ChatEntries.CHAT_SQL_ENTRIES, null, values)
            db.close()

            if (success >= 0){
                Log.i(TAG, "sendChat: Added")
                chatList.add(nChat)
                if (chatList.size > 0){
                    chatAdapter.addNewData(nChat, chatList.size - 1)
                    recyclerView.scrollToPosition(chatList.size - 1)
                }
            }
            else{
                Log.i(TAG, "sendChat: Not Added")
            }
        }
    }

    private fun showBottomSheet() {
        var context = activity as Context
        var bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(context, R.style.SheetDialog)
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        bottomSheetDialog.setContentView(R.layout.leave_sheet)

        var join = bottomSheetDialog.findViewById<LinearLayout>(R.id.linLeave)
        bottomSheetDialog.show()

        join!!.setOnClickListener {
            leaveGroup()
            bottomSheetDialog.hide()
        }
    }

    private fun leaveGroup() {
        if (userId != null){
            var res = chattingViewModel.leaveGroup(userId!!, groupId!!)

            if (res > 0){

            }
            else{
                Log.i(TAG, "leaveGroup: Left")
            }
        }
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        generalinterface = context as Generalinterface
    }


}