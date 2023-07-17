package eu.ase.ro.planificareabugetuluipersonal.util

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import eu.ase.ro.planificareabugetuluipersonal.R
import java.text.SimpleDateFormat
import java.util.*

class ExpandableListViewAdapter internal constructor(private val context:Context, private val listaBugete: List<Buget>, private val listaCheltuieli: List<Cheltuiala>):
    BaseExpandableListAdapter() {
    private var cheltuialaUpdateListener: OnUpdateListener? = null
    private var cheltuialaDeleteListener: OnDeleteListener? = null
    private var categorieDeleteListener: OnDeleteListener? = null
    private lateinit var firebaseAuth: FirebaseAuth
    override fun getGroupCount(): Int {
        return listaBugete.size
    }

    fun setOnCheltuialaUpdateListener(listener: OnUpdateListener) {
        cheltuialaUpdateListener = listener
    }

    fun setOnCheltuialaDeleteListener(listener: OnDeleteListener) {
        cheltuialaDeleteListener = listener
    }
    fun setOnCategorieDeleteListener(listener: OnDeleteListener) {
        categorieDeleteListener = listener
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
        val bugeteInfo= getGroup(groupPosition) as Buget


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

        val image = convertView!!.findViewById<ImageView>(R.id.row_item_group_iv_bugete)
        image.setOnClickListener { popupMenusBugete(it, bugeteInfo) }
        return convertView
        
    }

    private fun popupMenusBugete(v: View, bugeteInfo: Buget) {

        val popupMenus= PopupMenu(v.context,v)
        popupMenus.inflate(R.menu.show_menu)
        popupMenus.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.editText->{
                    val dialog=LayoutInflater.from(v.context).inflate(R.layout.modifica_bugete_dialog,null)
                    val inputSumaBugetSeteaza=dialog.findViewById<EditText>(R.id.modifica_buget_suma)
                    val btnSalveaza=dialog.findViewById<Button>(R.id.btn_salveaza_modifica_buget)
                    val btnInapoi=dialog.findViewById<Button>(R.id.btn_inapoi_modifica_buget)


                    val builder = AlertDialog.Builder(v.context)
                        .setView(dialog)
                        .setTitle("Modifica")


                    val alertDialog = builder.show()

                    alertDialog.setCancelable(false)



                    inputSumaBugetSeteaza.setText(bugeteInfo.suma.toString())



                    btnSalveaza.setOnClickListener {

                        val suma = inputSumaBugetSeteaza.text.toString().toDoubleOrNull()

                        if (suma == null) {
                            Toast.makeText(v.context, "Vă rugăm să introduceți o sumă validă.", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }



                        val db = FirebaseFirestore.getInstance()
                        var firebaseAuth = FirebaseAuth.getInstance()
                        db.collection("Bugete")
                            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {

                                    Log.d(ContentValues.TAG,"Bagaamias")
                                    val categorie=document.getString("categorie")
                                    val sumaBd=document.getString("suma")?.toDouble()
                                    val totalCheltuieliBd=document.getString("totalCheltuieli")?.toDouble()


//                                    Log.d(ContentValues.TAG,"${numeBd}, ${sumaBd}, ${ziBd} , ${tipBd}")


                                    if ( sumaBd==bugeteInfo.suma
                                        && totalCheltuieliBd== bugeteInfo.totalCheltuieli
                                        && categorie.equals(bugeteInfo.categorie))
                                    {
//                                        Log.d(ContentValues.TAG,"${currentItem.denumire}, ${currentItem.suma.toString()}, ${currentItem.zi} , ${currentItem.tipVenit.toString()}")
                                        val documentId = document.id
                                        val dataset = hashMapOf(
                                            "idUser" to firebaseAuth.currentUser?.uid.toString(),
                                            "totalCheltuieli" to bugeteInfo.totalCheltuieli.toString(),
                                            "suma" to inputSumaBugetSeteaza.text.toString(),
                                            "categorie" to bugeteInfo.categorie.toString())
                                        db.collection("Bugete")
                                            .document(documentId)
                                            .set(dataset)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    v.context,
                                                    "Datele au fost actualizate cu succes!",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                cheltuialaUpdateListener?.updated()
                                                notifyDataSetChanged()
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(
                                                    v.context,
                                                    "Eroare la actualizarea datelor: ${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                            }

                                    }

                                }
                            }


                        alertDialog.dismiss()

                    }

                    btnInapoi.setOnClickListener {
                        alertDialog.dismiss()
                    }
                    true
                }

                R.id.delete->{
                    val dialogBuilder = AlertDialog.Builder(v.context)
                    dialogBuilder.setTitle("Ștergere categorie")
                    dialogBuilder.setMessage("Sigur doriți să ștergeți aceasta categorie?")
                    dialogBuilder.setPositiveButton("Șterge") { _, _ ->
                        val dialog=LayoutInflater.from(v.context).inflate(R.layout.modifica_cheltuieli_dialog,null)
                        val inputdataCheltuiala=dialog.findViewById<Button>(R.id.modifica_cheltuiala_data)


                        dialogBuilder.setCancelable(false)


                        val db = FirebaseFirestore.getInstance()
                        var firebaseAuth = FirebaseAuth.getInstance()
                        db.collection("Cheltuieli")
                            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {

                                    val numeBd=document.getString("nume")
                                    val sumaBd=document.getString("suma")?.toDouble()
                                    val dataBd=document.getString("data")
                                    val categorie=document.getString("categorie")

//                                    Log.d(ContentValues.TAG,"${numeBd}, ${sumaBd}, ${ziBd} , ${tipBd}")


                                    if (categorie.equals(bugeteInfo.categorie))

                                    {

//                                        Log.d(ContentValues.TAG,"${currentItem.denumire}, ${currentItem.suma.toString()}, ${currentItem.zi} , ${currentItem.tipVenit.toString()}")
                                        val documentId = document.id

                                        db.collection("Cheltuieli")
                                            .document(documentId)
                                            .delete()
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    v.context,
                                                    "Datele au fost actualizate cu succes!",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                cheltuialaDeleteListener?.deleted()
                                                notifyDataSetChanged()

                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(
                                                    v.context,
                                                    "Eroare la actualizarea datelor: ${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                            }

                                    }

                                }

                                val db = FirebaseFirestore.getInstance()
                                var firebaseAuth = FirebaseAuth.getInstance()
                                db.collection("Bugete")
                                    .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        for (document in documents) {

                                            val categorie=document.getString("categorie")
                                            val sumaBd=document.getString("suma")?.toDouble()
                                            val totalCheltuieliBd=document.getString("totalCheltuieli")?.toDouble()

                                            if (
                                                sumaBd==bugeteInfo.suma
                                                && totalCheltuieliBd== bugeteInfo.totalCheltuieli
                                                && categorie.equals(bugeteInfo.categorie))
                                            {
                                                val documentId = document.id
                                                db.collection("Bugete")
                                                    .document(documentId)
                                                    .delete()
                                                    .addOnSuccessListener {
                                                        Toast.makeText(
                                                            v.context,
                                                            "Datele au fost actualizate cu succes!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                        categorieDeleteListener?.deleted()
                                                        notifyDataSetChanged()
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Toast.makeText(
                                                            v.context,
                                                            "Eroare la actualizarea datelor: ${e.message}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                    }

                                            }

                                        }
                                    }
                            }




                    }
                    dialogBuilder.setNegativeButton("Anulare", null)
                    dialogBuilder.create().show()
                    true
                }
                else->true
            }

        }
        popupMenus.show()
        val popup= PopupMenu::class.java.getDeclaredField("mPopup")
        popup.isAccessible=true
        val menu=popup.get(popupMenus)
        menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
            .invoke(menu,true)

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
        val image = convertView!!.findViewById<ImageView>(R.id.row_item_group_iv_cheltuieli)
        image.setOnClickListener { popupMenus(it, cheltuieliInfo) }
        return convertView
    }

    private fun popupMenus(v: View, cheltuieliInfo: Cheltuiala) {
        var formatDate= SimpleDateFormat("dd/MM/yyyy", Locale.US)

        val popupMenus= PopupMenu(v.context,v)
        popupMenus.inflate(R.menu.show_menu)
        popupMenus.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.editText->{
                    val dialog=LayoutInflater.from(v.context).inflate(R.layout.modifica_cheltuieli_dialog,null)
                    val inputdataCheltuiala=dialog.findViewById<Button>(R.id.modifica_cheltuiala_data)
                    val inputNumeCheltuiala = dialog.findViewById<EditText>(R.id.modifica_cheltuiala_nume)
                    val inputSumaCheltuiala=dialog.findViewById<EditText>(R.id.modifica_cheltuiala_suma)
                    val inputCategorieCheltuiala=dialog.findViewById<EditText>(R.id.modifica_cheltuiala_categorie)
                    val btnSalveaza=dialog.findViewById<Button>(R.id.btn_salveaza_modificari_cheltuiala)
                    val btnInapoi=dialog.findViewById<Button>(R.id.btn_inapoi_modifificari_cheltuieli)


                    val builder = AlertDialog.Builder(v.context)
                        .setView(dialog)
                        .setTitle("Modifica")


                    val alertDialog = builder.show()

                    alertDialog.setCancelable(false)

                    val currentDate = Calendar.getInstance()
                    val currentYear = currentDate.get(Calendar.YEAR)
                    val currentMonth = currentDate.get(Calendar.MONTH)

                    inputdataCheltuiala.text = formatDate.format(currentDate.time)


                    inputdataCheltuiala.setOnClickListener {
                        val getDate = Calendar.getInstance()
                        val datePicker = DatePickerDialog(
                            v.context,
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
                                val selectedDate = Calendar.getInstance()
                                selectedDate.set(Calendar.YEAR, year)
                                selectedDate.set(Calendar.MONTH, month)
                                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                                val selectedYear = selectedDate.get(Calendar.YEAR)
                                val selectedMonth = selectedDate.get(Calendar.MONTH)

                                if (selectedYear == currentYear && selectedMonth == currentMonth) {
                                    val date = formatDate.format(selectedDate.time)
                                    inputdataCheltuiala.text = date
                                    Toast.makeText(v.context, "Date: $date", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(v.context, "Vă rugăm să selectați luna și anul curent.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            getDate.get(Calendar.YEAR),
                            getDate.get(Calendar.MONTH),
                            getDate.get(Calendar.DAY_OF_MONTH)
                        )
                        datePicker.show()
                    }

                    inputNumeCheltuiala.setText(cheltuieliInfo.denumireCheltuiala)
                    inputSumaCheltuiala.setText(cheltuieliInfo.sumaChetuiala.toString())
                    inputdataCheltuiala.text = cheltuieliInfo.data
                    inputCategorieCheltuiala.setText(cheltuieliInfo.categorie)

                    btnSalveaza.setOnClickListener {
                        val nume=inputNumeCheltuiala.text.toString()
                        val suma=inputSumaCheltuiala.text.toString()
                        val categorie=inputCategorieCheltuiala.text.toString()


                        if (nume.isEmpty() || nume.isBlank() || nume.length < 3) {
                            Toast.makeText(v.context, "Vă rugăm să introduceți o denumire validă (minim 3 caractere).", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        if (suma.isEmpty() || suma.isBlank()) {
                            Toast.makeText(v.context, "Vă rugăm să introduceți o sumă validă.", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        firebaseAuth = FirebaseAuth.getInstance()

                        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
                        val categorii = CategoryManager.getCategories(userId, v.context)

                        if(!categorii.contains(categorie)){
                            Toast.makeText(v.context, "Vă rugăm să introduceți o categorie validă.", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        val db = FirebaseFirestore.getInstance()
                        var firebaseAuth = FirebaseAuth.getInstance()
                        db.collection("Cheltuieli")
                            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {

                                    val numeBd=document.getString("nume")
                                    val sumaBd=document.getString("suma")?.toDouble()
                                    val dataBd=document.getString("data")
                                    val categorie=document.getString("categorie")

//                                    Log.d(ContentValues.TAG,"${numeBd}, ${sumaBd}, ${ziBd} , ${tipBd}")


                                    if (numeBd.equals(cheltuieliInfo.denumireCheltuiala)
                                        && sumaBd==cheltuieliInfo.sumaChetuiala
                                        && dataBd.equals(cheltuieliInfo.data)
                                        && categorie.equals(cheltuieliInfo.categorie))

                                    {

//                                        Log.d(ContentValues.TAG,"${currentItem.denumire}, ${currentItem.suma.toString()}, ${currentItem.zi} , ${currentItem.tipVenit.toString()}")
                                        val documentId = document.id
                                        val dataset = hashMapOf(
                                            "idUser" to firebaseAuth.currentUser?.uid.toString(),
                                            "nume" to inputNumeCheltuiala.text.toString(),
                                            "suma" to inputSumaCheltuiala.text.toString(),
                                            "data" to inputdataCheltuiala.text.toString().trim(),
                                            "categorie" to inputCategorieCheltuiala.text.toString())
                                        db.collection("Cheltuieli")
                                            .document(documentId)
                                            .set(dataset)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    v.context,
                                                    "Datele au fost actualizate cu succes!",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                cheltuialaUpdateListener?.updated()
                                                notifyDataSetChanged()
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(
                                                    v.context,
                                                    "Eroare la actualizarea datelor: ${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                            }

                                    }

                                }
                            }


                        alertDialog.dismiss()

                    }

                    btnInapoi.setOnClickListener {
                        alertDialog.dismiss()
                    }
                    true
                }
                R.id.delete->{
                    val dialogBuilder = AlertDialog.Builder(v.context)
                    dialogBuilder.setTitle("Ștergere venit")
                    dialogBuilder.setMessage("Sigur doriți să ștergeți acest venit?")
                    dialogBuilder.setPositiveButton("Șterge") { _, _ ->
                        val dialog=LayoutInflater.from(v.context).inflate(R.layout.modifica_cheltuieli_dialog,null)
                        val inputdataCheltuiala=dialog.findViewById<Button>(R.id.modifica_cheltuiala_data)


                        dialogBuilder.setCancelable(false)


                        val db = FirebaseFirestore.getInstance()
                        var firebaseAuth = FirebaseAuth.getInstance()
                        db.collection("Cheltuieli")
                            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {

                                    val numeBd=document.getString("nume")
                                    val sumaBd=document.getString("suma")?.toDouble()
                                    val dataBd=document.getString("data")
                                    val categorie=document.getString("categorie")

//                                    Log.d(ContentValues.TAG,"${numeBd}, ${sumaBd}, ${ziBd} , ${tipBd}")


                                    if (numeBd.equals(cheltuieliInfo.denumireCheltuiala)
                                        && sumaBd==cheltuieliInfo.sumaChetuiala
                                        && dataBd.equals(cheltuieliInfo.data)
                                        && categorie.equals(cheltuieliInfo.categorie))

                                    {

//                                        Log.d(ContentValues.TAG,"${currentItem.denumire}, ${currentItem.suma.toString()}, ${currentItem.zi} , ${currentItem.tipVenit.toString()}")
                                        val documentId = document.id

                                        db.collection("Cheltuieli")
                                            .document(documentId)
                                            .delete()
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    v.context,
                                                    "Datele au fost actualizate cu succes!",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                cheltuialaDeleteListener?.deleted()
                                                notifyDataSetChanged()
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(
                                                    v.context,
                                                    "Eroare la actualizarea datelor: ${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                            }

                                    }

                                }
                            }




                    }
                    dialogBuilder.setNegativeButton("Anulare", null)
                    dialogBuilder.create().show()
                     true
                }
                else->true
            }

        }
        popupMenus.show()
        val popup= PopupMenu::class.java.getDeclaredField("mPopup")
        popup.isAccessible=true
        val menu=popup.get(popupMenus)
        menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
            .invoke(menu,true)

    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
       return true
    }

    private fun getTotalCheltuieli(categoria: String): Double {
        return listaCheltuieli.filter { it.categorie == categoria }.sumByDouble { it.sumaChetuiala }
    }
}