package com.example.collegecommapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collegecommapp.R
import com.example.collegecommapp.interfaces.Generalinterface
import com.example.collegecommapp.models.Group



open class ChatRoomsAdapter(var context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var groupList: ArrayList<Group> = ArrayList()
    private lateinit var generalinterface: Generalinterface
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.new_chat_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var myViewHolder: MyViewHolder = holder as MyViewHolder
        myViewHolder.title.text = groupList[position].group_name
        myViewHolder.message.text = groupList[position].group_description


        myViewHolder.join.setOnClickListener {
            generalinterface.addChatRoom(groupList[position])
        }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        generalinterface = context as Generalinterface
    }

    protected class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        var image: ImageView = view.findViewById(R.id.imgChatImage)
        var title: TextView = view.findViewById(R.id.groupChatName)
        var message: TextView = view.findViewById(R.id.groupChatDescription)
        var join: RelativeLayout = view.findViewById(R.id.relJoin)
    }

    fun getData(lst: ArrayList<Group>){
        for (i in lst){
            groupList.add(i)
            notifyDataSetChanged()
        }
    }
}