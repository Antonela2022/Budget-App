package eu.ase.ro.planificareabugetuluipersonal.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import eu.ase.ro.planificareabugetuluipersonal.R

class ExpandableListViewAdapter internal constructor(private val context:Context, private val listaBugete: List<Buget>, private val listaCheltuieli: List<Cheltuiala>):
    BaseExpandableListAdapter() {
    override fun getGroupCount(): Int {
        return listaBugete.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val categoria = listaBugete[groupPosition].categorie
        return listaCheltuieli.count { it.categorie == categoria }
    }

    override fun getGroup(groupPosition: Int): Any {
        return listaBugete[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        val categoria = listaBugete[groupPosition].categorie
        return listaCheltuieli.filter { it.categorie == categoria }[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
      return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return  childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        val bugeteInfo= getGroup(groupPosition) as Buget // Presupunând că obiectul grupului este de tip Buget


        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.elv_group_bugete, null)
        }

        val categoriaTextView = convertView!!.findViewById<TextView>(R.id.row_item_group_tv_bugete_categorie)
        val totalCheltuieliTextView = convertView!!.findViewById<TextView>(R.id.row_item_group_tv_bugete_total_cheltuieli)
        val valoareBugetTextView = convertView!!.findViewById<TextView>(R.id.row_item_group_tv_bugete_buget_setat)

        if (categoriaTextView != null) {
            categoriaTextView.text = bugeteInfo.categorie
        }
        if (totalCheltuieliTextView != null) {
            totalCheltuieliTextView.text = getTotalCheltuieli(bugeteInfo.categorie).toString() + "/"
        }
        if (valoareBugetTextView != null) {
            valoareBugetTextView.text = bugeteInfo.suma.toString()
        }

        return convertView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        val cheltuieliInfo = getChild(groupPosition,childPosition) as Cheltuiala // Presupunând că obiectul grupului este de tip Buget


        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.elv_topics_cheltuieli, null)
        }


        val dataCheltuieliTextView = convertView!!.findViewById<TextView>(R.id.topics_tv_cheltuieli_data)
        val denumireTextView = convertView!!.findViewById<TextView>(R.id.topics_tv_cheltuieli_denumire)
        val sumaCheltuitaTextView=convertView!!.findViewById<TextView>(R.id.topics_tv_cheltuieli_suma)

        if (denumireTextView != null) {
            denumireTextView.text = cheltuieliInfo.denumireCheltuiala
        }
        if (dataCheltuieliTextView != null) {
            dataCheltuieliTextView.text = cheltuieliInfo.data
        }
        if (sumaCheltuitaTextView != null) {
            sumaCheltuitaTextView.text = cheltuieliInfo.sumaChetuiala.toString()
        }

        return convertView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
       return true
    }

    private fun getTotalCheltuieli(categoria: String): Double {
        return listaCheltuieli.filter { it.categorie == categoria }.sumByDouble { it.sumaChetuiala }
    }
}