package com.epsi.myquiz

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.firestore.DocumentSnapshot

class ProfileAdapter(private val context: Context, private val documents : List<DocumentSnapshot>) : BaseAdapter(){

    companion object {
        private var inflater: LayoutInflater? = null
    }

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return documents.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class Holder {
        lateinit var score : TextView
    }

    private fun initHolder(view: View): Holder {
        val holder = Holder()
        holder.score = view.findViewById(R.id.score)
        return holder
    }
    @SuppressLint("SetTextI18n")
    @Suppress("UNCHECKED_CAST")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var cv = convertView
        if (cv == null){
            cv = inflater!!.inflate(R.layout.display_profile_score, parent, false)
        }
        val holder = initHolder(cv!!)
        holder.score.text = documents[position].data?.getValue("date").toString() + " - " + documents[position].data?.getValue("scoreUser").toString() + " pts"
        return cv
    }
}