package eu.ase.ro.planificareabugetuluipersonal.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eu.ase.ro.planificareabugetuluipersonal.R

class ObiectivAdapter( private val obiectiveList: ArrayList<Obiectiv>) :
    RecyclerView.Adapter<ObiectivAdapter.ObiectivViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObiectivViewHolder {
        val itemView=
            LayoutInflater.from(parent.context).inflate(R.layout.lv_row_obiective_item,parent,false)
        return ObiectivViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ObiectivViewHolder, position: Int) {
        val currentItem=obiectiveList[position]
        holder.numeObiectiv.text=currentItem.numeObiectiv
        holder.status.text= currentItem.status
        holder.valoareObiectiv.text=currentItem.valoareObiectiv.toString()

    }

    override fun getItemCount(): Int {
        return obiectiveList.size
    }

    class ObiectivViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val numeObiectiv: TextView = itemView.findViewById(R.id.row_item_tv_obiective_nume)
        val status: TextView = itemView.findViewById(R.id.row_item_tv_obiective_status)
        val valoareObiectiv: TextView = itemView.findViewById(R.id.row_item_obiective_valoare)


    }
}