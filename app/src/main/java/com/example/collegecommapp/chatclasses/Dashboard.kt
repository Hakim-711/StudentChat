package com.example.collegecommapp.chatclasses

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.MediaCodec.MetricsConstants.MODE
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collegecommapp.R
import com.example.collegecommapp.interfaces.Generalinterface
import com.example.collegecommapp.models.Group
import com.example.collegecommapp.models.GroupDisplay
import com.example.collegecommapp.viewmodels.ChattingViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class Dashboard : Fragment(), View.OnClickListener {
    private val TAG = "Dashboard"
    private lateinit var more: RelativeLayout
    private lateinit var floatingActionButton: ImageButton
    private lateinit var homeTxt: TextView
    private lateinit var profile: RelativeLayout
    private lateinit var generalinterface: Generalinterface
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var chattingViewModel: ChattingViewModel
     private lateinit var btnChart: Button
    private lateinit var progress: ProgressBar
    private var groups: ArrayList<GroupDisplay> = ArrayList()
    private lateinit var sharedPreferences: SharedPreferences
    private var userId: String? = null
    private var filePath: Uri? = null
    private var imgUrl: String = ""
    private lateinit var pic: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chattingViewModel = ViewModelProvider(requireActivity()).get(ChattingViewModel::class.java)


        var context = activity as Context

        //views
        more = view.findViewById(R.id.relMore)
        floatingActionButton = view.findViewById(R.id.floatDashboard)
        profile = view.findViewById(R.id.relProfile)
        recyclerView = view.findViewById(R.id.recyclerGroups)
        homeTxt = view.findViewById(R.id.txtHome)
        homeTxt.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        //clicks
        profile.setOnClickListener(this)
        floatingActionButton.setOnClickListener(this)
        more.setOnClickListener(this)

        //Sharedpreferences
        sharedPreferences = activity?.getSharedPreferences(getString(R.string.User), Context.MODE_PRIVATE)!!
        userId = sharedPreferences.getString(getString(R.string.id), "")

        //layout managers
        linearLayoutManager = LinearLayoutManager(activity)

        //list data
        getListData()
    }

    private fun getListData() {
        chattingViewModel.getMemberGroups(userId!!).observe(viewLifecycleOwner, Observer {
            if (it!!.isNotEmpty()){
                groups.clear()
                showRecycler(it)
            }
            else{
                Toast.makeText(activity, "Not Found", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showRecycler(it: List<GroupDisplay>?) {
        for (i in it!!){
            groups.add(i)
        }

        if (groups.size > 0){
            homeTxt.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            recyclerView.layoutManager = linearLayoutManager
        }
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.relMore -> {
                showBottomSheet()
            }
            R.id.floatDashboard -> {
                showChatRoomAdditionSheet()
            }
            R.id.relProfile -> {
                generalinterface.goToProfile()
            }
        }

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
                pic.setImageBitmap(bitmap)
                if (bitmap != null){
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

    @SuppressLint("SimpleDateFormat")
    private fun showChatRoomAdditionSheet() {
        var context = activity as Context
        var bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(context, R.style.SheetDialog)
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        bottomSheetDialog.setContentView(R.layout.newchatroom_bottom_sheet)

        var name: TextView = bottomSheetDialog.findViewById<TextView>(R.id.chatName)!!
        var desc: TextView = bottomSheetDialog.findViewById<TextView>(R.id.chatDescription)!!
        var cap: EditText = bottomSheetDialog.findViewById<EditText>(R.id.chatCapacity)!!
        btnChart = bottomSheetDialog.findViewById<Button>(R.id.btnChat)!!
        progress = bottomSheetDialog.findViewById<ProgressBar>(R.id.progressNew)!!
        var picClick: TextView = bottomSheetDialog.findViewById<TextView>(R.id.chatPic)!!
        pic = bottomSheetDialog.findViewById<ImageView>(R.id.imgChatRoomPic)!!

        progress.visibility = View.GONE
        bottomSheetDialog.show()

        picClick.setOnClickListener {
            btnChart.isEnabled = false
            progress.visibility = View.VISIBLE
            val intent: Intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                chatPic.launch(intent)
            }
        }

        btnChart.setOnClickListener {

                var time = SimpleDateFormat("yyyy-MM-dd").format(Date())

                var group: Group = Group()
                group.group_name = name.text.toString().trim()
                group.group_description = desc.text.toString().trim()
                group.group_capacity = cap.text.toString().toInt()
                group.group_created_by = userId
                group.group_image = imgUrl
                group.group_date_created = time
                group.group_id = "${userId.toString()}${Random.nextInt(100, 10000).toString()}"

                if (group != null){
                    var resp = chattingViewModel.createGroup(group)

                    Log.i(TAG, "showChatRoomAdditionSheet: ${resp}")

                    if (resp >= 0){
                        Toast.makeText(activity, "Group Created Successfully, wait for joining code", Toast.LENGTH_LONG).show()
                        generalinterface.addChatRoom(group)
                        bottomSheetDialog.hide()
                    }
                    else{
                        Toast.makeText(activity, "Group Not Created, try again", Toast.LENGTH_LONG).show()
                    }
                }
            }

                Toast.makeText(activity, "Connect to the internet for code generation", Toast.LENGTH_LONG).show()


    }

    private fun showBottomSheet() {
        var context = activity as Context
        var bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(context, R.style.SheetDialog)
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        bottomSheetDialog.setContentView(R.layout.options_bottom_sheet)

        var join = bottomSheetDialog.findViewById<LinearLayout>(R.id.linJoin)
        var search = bottomSheetDialog.findViewById<LinearLayout>(R.id.linSearch)
        bottomSheetDialog.show()

        join!!.setOnClickListener {
            generalinterface.goToNewChatRooms()
        }

        search!!.setOnClickListener {
            generalinterface.goToSearch()
            bottomSheetDialog.hide()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        generalinterface = context as Generalinterface
    }
}