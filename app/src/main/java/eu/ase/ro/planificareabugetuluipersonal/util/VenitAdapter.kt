package eu.ase.ro.planificareabugetuluipersonal.util


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import eu.ase.ro.planificareabugetuluipersonal.R

class VenitAdapter( private val venituriList: ArrayList<Venit>) :
    RecyclerView.Adapter<VenitAdapter.VenitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenitViewHolder {
             val itemView=LayoutInflater.from(parent.context).inflate(R.layout.lv_row_venituri_item,parent,false)
        return VenitViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VenitViewHolder, position: Int) {
        val currentItem=venituriList[position]
        holder.zi.text=currentItem.zi
        holder.denumire.text=currentItem.denumire
        holder.suma.text=currentItem.suma.toString()
        val tipVenitText = if (currentItem.tipVenit) "Venit Fix" else "Venit Variabil"
        holder.tipVenit.text = tipVenitText


    }

    override fun getItemCount(): Int {
        return venituriList.size
    }

     class VenitViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
         val zi: TextView = itemView.findViewById(R.id.row_item_tv_venituri_data)
         val denumire: TextView = itemView.findViewById(R.id.row_item_tv_venituri_provenienta)
         val suma: TextView = itemView.findViewById(R.id.row_item_tv_venituri_suma)
         val tipVenit:TextView=itemView.findViewById(R.id.row_item_tv_venituri_tip)


    }
}





