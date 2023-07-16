package eu.ase.ro.planificareabugetuluipersonal.util


import android.app.DatePickerDialog
import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import eu.ase.ro.planificareabugetuluipersonal.R
import java.text.SimpleDateFormat
import java.util.*

class VenitAdapter( private val venituriList: ArrayList<Venit>) :
    RecyclerView.Adapter<VenitAdapter.VenitViewHolder>() {
    private lateinit var firebaseAuth: FirebaseAuth
    private var venitUpdateListener: OnUpdateListener? = null
    private var venitDeleteListener: OnDeleteListener? = null

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


        holder.image.setOnClickListener{popupMenus(it,currentItem)}


    }

    fun setOnVenitUpdateListener(listener: OnUpdateListener) {
        venitUpdateListener = listener
    }

    fun setOnVenitDeleteListener(listener: OnDeleteListener) {
        venitDeleteListener = listener
    }

    private fun popupMenus(v:View,currentItem:Venit) {
        var formatDate= SimpleDateFormat("dd/MM/yyyy",Locale.US)

        val popupMenus=PopupMenu(v.context,v)
        popupMenus.inflate(R.menu.show_menu)
        popupMenus.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.editText->{
                    val dialog=LayoutInflater.from(v.context).inflate(R.layout.modifica_venit_dialog,null)
                    val inputnumeVenit = dialog.findViewById<EditText>(R.id.modifica_venit_proveninenta)
                    val inputsumaVenit=dialog.findViewById<EditText>(R.id.modifica_venit_suma)
                    val inputdata=dialog.findViewById<Button>(R.id.modifica_venit_data)
                    val inputtipVenit=dialog.findViewById<SwitchCompat>(R.id.tip_venit_swich)
                    val btnInapoi=dialog.findViewById<Button>(R.id.btn_inapoi_modifificari_venituri)
                    val btnSalveaza=dialog.findViewById<Button>(R.id.btn_salveaza_modificari_venituri)


                    val builder = AlertDialog.Builder(v.context)
                        .setView(dialog)
                        .setTitle("Modifica")


                    val alertDialog = builder.show()

                    alertDialog.setCancelable(false)

                    val currentDate = Calendar.getInstance()
                    val currentYear = currentDate.get(Calendar.YEAR)
                    val currentMonth = currentDate.get(Calendar.MONTH)

                    inputdata.text = formatDate.format(currentDate.time)


                    inputdata.setOnClickListener {
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
                                    inputdata.text = date
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

                    inputnumeVenit.setText(currentItem.denumire)
                    inputsumaVenit.setText(currentItem.suma.toString())
                    inputdata.text = currentItem.zi

                    btnSalveaza.setOnClickListener {
                        val numeVenit=inputnumeVenit.text.toString()
                        val sumaVenit=inputsumaVenit.text.toString()


                        if (numeVenit.isEmpty() || numeVenit.isBlank() || numeVenit.length < 3) {
                            Toast.makeText(v.context, "Vă rugăm să introduceți o denumire validă (minim 3 caractere).", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        if (sumaVenit.isEmpty() || sumaVenit.isBlank()) {
                            Toast.makeText(v.context, "Vă rugăm să introduceți o sumă validă.", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }


                        val db = FirebaseFirestore.getInstance()
                        firebaseAuth = FirebaseAuth.getInstance()
                        db.collection("Venituri")
                            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {

                                    val numeBd=document.getString("nume")
                                    val sumaBd=document.getString("suma")?.toDouble()
                                    val ziBd=document.getString("zi")
                                    val tipBd=document.getString("tipVenit").toBoolean()

                                    Log.d(ContentValues.TAG,"${numeBd}, ${sumaBd}, ${ziBd} , ${tipBd}")


                                    if (numeBd.equals(currentItem.denumire)
                                        && sumaBd==currentItem.suma
                                        && ziBd.equals(currentItem.zi)
                                        && tipBd==currentItem.tipVenit)

                                    {

                                        Log.d(ContentValues.TAG,"${currentItem.denumire}, ${currentItem.suma.toString()}, ${currentItem.zi} , ${currentItem.tipVenit.toString()}")
                                        val documentId = document.id
                                        val dataset = hashMapOf(
                                            "idUser" to firebaseAuth.currentUser?.uid.toString(),
                                            "nume" to inputnumeVenit.text.toString(),
                                            "suma" to inputsumaVenit.text.toString(),
                                            "zi" to inputdata.text.toString().trim(),
                                            "tipVenit" to inputtipVenit.isChecked.toString())
                                        db.collection("Venituri")
                                            .document(documentId)
                                            .set(dataset)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    v.context,
                                                    "Datele au fost actualizate cu succes!",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                venitUpdateListener?.updated()
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


                        val dialog=LayoutInflater.from(v.context).inflate(R.layout.modifica_venit_dialog,null)
                        val inputnumeVenit = dialog.findViewById<EditText>(R.id.modifica_venit_proveninenta)
                        val inputsumaVenit=dialog.findViewById<EditText>(R.id.modifica_venit_suma)
                        val inputdata=dialog.findViewById<Button>(R.id.modifica_venit_data)


                        dialogBuilder.setCancelable(false)






                        inputnumeVenit.setText(currentItem.denumire)
                        inputsumaVenit.setText(currentItem.suma.toString())
                        inputdata.text = currentItem.zi

                            val db = FirebaseFirestore.getInstance()
                            firebaseAuth = FirebaseAuth.getInstance()
                            db.collection("Venituri")
                                .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {

                                        val numeBd=document.getString("nume")
                                        val sumaBd=document.getString("suma")?.toDouble()
                                        val ziBd=document.getString("zi")
                                        val tipBd=document.getString("tipVenit").toBoolean()

                                        Log.d(ContentValues.TAG,"${numeBd}, ${sumaBd}, ${ziBd} , ${tipBd}")


                                        if (numeBd.equals(currentItem.denumire)
                                            && sumaBd==currentItem.suma
                                            && ziBd.equals(currentItem.zi)
                                            && tipBd==currentItem.tipVenit)

                                        {

                                            Log.d(ContentValues.TAG,"${currentItem.denumire}, ${currentItem.suma.toString()}, ${currentItem.zi} , ${currentItem.tipVenit.toString()}")
                                            val documentId = document.id

                                            db.collection("Venituri")
                                                .document(documentId)
                                                .delete()
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        v.context,
                                                        "Data venitului a fost actualizată cu succes.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()

                                                    venitDeleteListener?.deleted()
                                                    notifyDataSetChanged()
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(
                                                        v.context,
                                                        "Eroare la actualizarea datei venitului: ${e.message}",
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
        val popup=PopupMenu::class.java.getDeclaredField("mPopup")
        popup.isAccessible=true
        val menu=popup.get(popupMenus)
        menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
            .invoke(menu,true)
    }



    override fun getItemCount(): Int {
        return venituriList.size
    }

     class VenitViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
         val zi: TextView = itemView.findViewById(R.id.row_item_tv_venituri_data)
         val denumire: TextView = itemView.findViewById(R.id.row_item_tv_venituri_provenienta)
         val suma: TextView = itemView.findViewById(R.id.row_item_tv_venituri_suma)
         val tipVenit:TextView=itemView.findViewById(R.id.row_item_tv_venituri_tip)
         val image:ImageView=itemView.findViewById(R.id.row_item_iv_venituri)

    }
}





