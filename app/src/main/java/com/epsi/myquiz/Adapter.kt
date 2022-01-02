package com.epsi.myquiz

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Integer.parseInt

class Adapter(private val context: Context, private val documents : List<DocumentSnapshot>) : BaseAdapter(){
    private val db = Firebase.firestore
    private lateinit var editIntent : Intent
    private val activity: AdminActivity = context as AdminActivity

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
        lateinit var lblQuestion : TextView
        lateinit var questions: TextView
        lateinit var lblResponses : TextView
        lateinit var response1: TextView
        lateinit var response2: TextView
        lateinit var response3: TextView
        lateinit var response4: TextView
        lateinit var lblgoodResponse : TextView
        lateinit var goodResponses: TextView
        lateinit var edit : ImageView
        lateinit var delete : ImageView
    }

    private fun initHolder(view: View): Holder {
        val holder = Holder()
        holder.lblQuestion = view.findViewById(R.id.lblQuestion)
        holder.questions = view.findViewById(R.id.Question)
        holder.lblResponses = view.findViewById(R.id.lblResponses)
        holder.response1 = view.findViewById(R.id.response1)
        holder.response2 = view.findViewById(R.id.response2)
        holder.response3 = view.findViewById(R.id.response3)
        holder.response4 = view.findViewById(R.id.response4)
        holder.lblgoodResponse = view.findViewById(R.id.lblgoodResponse)
        holder.goodResponses = view.findViewById(R.id.goodResponse)
        holder.edit = view.findViewById(R.id.edit)
        holder.delete = view.findViewById(R.id.delete)
        return holder
    }
    @SuppressLint("SetTextI18n")
    @Suppress("UNCHECKED_CAST")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var cv = convertView
        if (cv == null){
            cv = inflater!!.inflate(R.layout.display_admin_questions, parent, false)
        }
        val holder = initHolder(cv!!)
        holder.lblQuestion.text = "Question n°" + (position+1)
        holder.questions.text = documents[position].data?.getValue("question").toString()
        holder.lblResponses.text = "Réponses Disponibles :"
        val list : List<String> = documents[position].data?.getValue("responses") as List<String>
        holder.response1.text = list[0]
        holder.response2.text = list[1]
        holder.response3.text = list[2]
        holder.response4.text = list[3]
        holder.lblgoodResponse.text = "Numéro de la bonne réponse :"
        holder.goodResponses.text = documents[position].data?.getValue("goodResponse").toString() +
                                " (" +
                                list[(parseInt(documents[position].data?.getValue("goodResponse").toString())-1)] +
                                ")"
        holder.edit.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_baseline_edit_24))
        holder.delete.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_baseline_delete_24))
        holder.edit.setOnClickListener{
                            editIntent = Intent(context, EditQuestionActivity::class.java)
                            editIntent.putExtra("idQuestion", documents[position].id)
                            context.startActivity(editIntent)
                        }
        holder.delete.setOnClickListener {
                            val builder = AlertDialog.Builder(context)
                            builder.setTitle(R.string.alert_dialog_title)
                                .setMessage(R.string.alert_dialog_msg)
                                .setPositiveButton(R.string.yes
                                ) { _, _ ->
                                    db.collection("quiz").document(documents[position].id)
                                        .delete()
                                    activity.reload()
                                }
                                .setNegativeButton(R.string.no
                                ) { _, _ -> }
            // Create the AlertDialog object and return it
                        builder.create().show()
                        }
        return cv
    }
}