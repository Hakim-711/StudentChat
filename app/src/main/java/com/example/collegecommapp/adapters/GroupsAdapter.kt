package com.example.collegecommapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.collegecommapp.R
import com.example.collegecommapp.interfaces.Generalinterface
import com.example.collegecommapp.models.GroupDisplay


open class GroupsAdapter(var context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var lst: ArrayList<GroupDisplay> = ArrayList()
    private var filteredList: ArrayList<GroupDisplay> = ArrayList()
    private lateinit var generalinterface: Generalinterface

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.group_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var myViewHolder: MyViewHolder = holder as MyViewHolder
        myViewHolder.title.text = lst!![position].group_name
        myViewHolder.message.text = lst[position].message ?: ""
        myViewHolder.time.text = lst[position].time
        myViewHolder.total.text = lst[position].total


        myViewHolder.relCont.setOnClickListener {
            generalinterface.goToChatPage(lst[position].group_id!!)
        }
    }

    override fun getItemCount(): Int {
        return lst!!.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        generalinterface = context as Generalinterface
    }

    protected class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var image: ImageView = view.findViewById(R.id.imgGroupImage)
        var title: TextView = view.findViewById(R.id.groupName)
        var message: TextView = view.findViewById(R.id.groupMessage)
        var time: TextView = view.findViewById(R.id.groupTime)
        var total: TextView = view.findViewById(R.id.groupTotal)
        var relCont: RelativeLayout = view.findViewById(R.id.groupId)

    }

    fun getData(data: ArrayList<GroupDisplay>){
        for (i in data){
            lst!!.add(i)
            notifyDataSetChanged()
        }

        filteredList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                var filList: ArrayList<GroupDisplay> = ArrayList()
                if (p0.isNullOrEmpty()){
                    filList.addAll(filteredList)
                }
                else{
                    var name = p0.toString().lowercase().trim()
                    for (i in filteredList){
                        if (i.group_name.toString().lowercase().contains(name)){
                            filList.add(i)
                        }
                    }
                }
                var result: FilterResults = FilterResults()
                result.values = filList
                return result
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                lst.clear()
                lst.addAll(p1!!.values as ArrayList<GroupDisplay>)
                notifyDataSetChanged()
            }

        }
    }
}