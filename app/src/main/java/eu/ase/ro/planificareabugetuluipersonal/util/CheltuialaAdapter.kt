package eu.ase.ro.planificareabugetuluipersonal.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eu.ase.ro.planificareabugetuluipersonal.R

class CheltuialaAdapter (private val cheltuieliList:ArrayList<Cheltuiala>):
    RecyclerView.Adapter<CheltuialaAdapter.CheltuialaViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheltuialaViewHolder {
            val itemView= LayoutInflater.from(parent.context).inflate(R.layout.elv_topics_cheltuieli,parent,false)
            return CheltuialaViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: CheltuialaViewHolder, position: Int) {
            val currentItem=cheltuieliList[position]
            holder.data.text=currentItem.data
            holder.denumire.text=currentItem.denumireCheltuiala
            holder.suma.text= currentItem.sumaChetuiala.toString()

        }

        override fun getItemCount(): Int {
            return cheltuieliList.size
        }

        class CheltuialaViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val data : TextView = itemView.findViewById(R.id.topics_tv_cheltuieli_data)
            val denumire : TextView =itemView.findViewById(R.id.topics_tv_cheltuieli_denumire)
            val suma: TextView =itemView.findViewById(R.id.topics_tv_cheltuieli_suma)

        }
}