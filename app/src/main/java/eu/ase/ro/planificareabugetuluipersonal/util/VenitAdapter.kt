package eu.ase.ro.planificareabugetuluipersonal.util


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
        holder.suma.text=currentItem.suma

    }

    override fun getItemCount(): Int {
        return venituriList.size
    }

     class VenitViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
         val zi: TextView = itemView.findViewById(R.id.row_item_tv_venituri_data)
         val denumire: TextView = itemView.findViewById(R.id.row_item_tv_venituri_provenienta)
         val suma: TextView = itemView.findViewById(R.id.row_item_tv_venituri_suma)


    }
}

//
//class VenitAdapter(private val venituriList: List<Venit>) : BaseAdapter(){
//
//    override fun getCount(): Int {
//        return venituriList.size
//    }
//
//    override fun getItem(position: Int): Any {
//        return venituriList[position]
//    }
//
//    override fun getItemId(position: Int): Long {
//        // Puteți returna un ID unic pentru fiecare element din listă, sau poziția în listă
//        return position.toLong()
//    }
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val view: View
//        val viewHolder: ViewHolder
//
//        if (convertView == null) {
//            view = LayoutInflater.from(parent.context).inflate(R.layout.lv_row_venituri_item, parent, false)
//            viewHolder = ViewHolder(view)
//            view.tag = viewHolder
//        } else {
//            view = convertView
//            viewHolder = view.tag as ViewHolder
//        }
//
//        val venit = venituriList[position]
//        viewHolder.bind(venit)
//
//        return view
//    }
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        fun bind(venit: Venit) {
//            val zi: TextView = itemView.findViewById(R.id.row_item_tv_venituri_data)
//            val denumire: TextView = itemView.findViewById(R.id.row_item_tv_venituri_provenienta)
//            val suma: TextView = itemView.findViewById(R.id.row_item_tv_venituri_suma)
//
//            zi.text = venit.zi.toString()
//            denumire.text = venit.denumire
//            suma.text = venit.suma.toString()
//        }
//    }
//
//
//


//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.lv_row_venituri_item, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val venit = venituriList[position]
//        holder.bind(venit)
//    }
//
//    override fun getItemCount(): Int {
//        return venituriList.size
//    }
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        fun bind(venit: Venit) {
//            val zi: TextView = itemView.findViewById(R.id.row_item_tv_venituri_data)
//            val denumire: TextView = itemView.findViewById(R.id.row_item_tv_venituri_provenienta)
//            val suma: TextView = itemView.findViewById(R.id.row_item_tv_venituri_suma)
//
//            zi.text = venit.zi.toString()
//            denumire.text = venit.denumire
//            suma.text = venit.suma.toString()
//        }
//    }





