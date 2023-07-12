package eu.ase.ro.planificareabugetuluipersonal.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eu.ase.ro.planificareabugetuluipersonal.R

class BugetAdapter(private val bugeteList:ArrayList<Buget>):
    RecyclerView.Adapter<BugetAdapter.BugetViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BugetViewHolder {
        val itemView= LayoutInflater.from(parent.context).inflate(R.layout.lv_row_bugete_item,parent,false)
        return BugetViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: BugetViewHolder, position: Int) {
        val currentItem=bugeteList[position]
        holder.categorie.text=currentItem.categorie
        holder.totalCheltuieli.text=currentItem.totalCheltuieli + "/"
        holder.suma.text=currentItem.suma

    }

    override fun getItemCount(): Int {
        return bugeteList.size
    }

    class BugetViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val categorie : TextView = itemView.findViewById(R.id.row_item_tv_bugete_categorie)
        val totalCheltuieli : TextView =itemView.findViewById(R.id.row_item_tv_bugete_total_cheltuieli)
        val suma: TextView =itemView.findViewById(R.id.row_item_tv_bugete_buget_setat)

    }
}