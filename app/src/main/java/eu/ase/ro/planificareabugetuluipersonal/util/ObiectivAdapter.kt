package eu.ase.ro.planificareabugetuluipersonal.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eu.ase.ro.planificareabugetuluipersonal.R
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ObiectivAdapter( private val obiectiveList: ArrayList<Obiectiv>) :
    RecyclerView.Adapter<ObiectivAdapter.ObiectivViewHolder>() {
    private var obiectivUpdateListener: OnUpdateListener? = null
    private var obiectivDeleteListener: OnDeleteListener? = null
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

        holder.image.setOnClickListener{popupMenus(it,currentItem)}
    }

    fun setOnObiectivUpdateListener(listener: OnUpdateListener) {
        obiectivUpdateListener = listener
    }

    fun setOnObiectivDeleteListener(listener: OnDeleteListener) {
        obiectivDeleteListener = listener
    }


    private fun popupMenus(v: View, currentItem: Obiectiv) {
        val popupMenus=PopupMenu(v.context ,v)
        popupMenus.inflate(R.menu.show_menu)
        popupMenus.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.editText->{
                    val dialog=LayoutInflater.from(v.context).inflate(R.layout.modifica_obiective_dialog,null)
                    val inputnumeObiectiv = dialog.findViewById<EditText>(R.id.modifica_obiectiv_nume)
                    val inputSumaObiectiv=dialog.findViewById<EditText>(R.id.modifica_obiectiv_suma)
                    val btnSalveaza=dialog.findViewById<Button>(R.id.btn_salveaza_modifica_obiectiv)
                    val btnInapoi=dialog.findViewById<Button>(R.id.btn_inapoi_modifica_obiectiv)



                    val builder = AlertDialog.Builder(v.context)
                        .setView(dialog)
                        .setTitle("Modifica")


                    val alertDialog = builder.show()

                    alertDialog.setCancelable(false)



                    inputnumeObiectiv.setText(currentItem.numeObiectiv)
                    inputSumaObiectiv.setText(currentItem.valoareObiectiv.toString())


                    btnSalveaza.setOnClickListener {
                        val numeObiectiv=inputnumeObiectiv.text.toString()
                        val sumaObiectiv=inputSumaObiectiv.text.toString()


                        if (numeObiectiv.isEmpty() || numeObiectiv.isBlank() || numeObiectiv.length < 3) {
                            Toast.makeText(v.context, "Vă rugăm să introduceți o denumire validă (minim 3 caractere).", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        if (sumaObiectiv.isEmpty() || sumaObiectiv.isBlank()) {
                            Toast.makeText(v.context, "Vă rugăm să introduceți o sumă validă.", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }


                        val db = FirebaseFirestore.getInstance()
                        var firebaseAuth = FirebaseAuth.getInstance()
                        db.collection("Obiective")
                            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {

                                    val numeObiectivBd=document.getString("numeObiectiv")
                                    val sumaObiectivBd=document.getString("valoareObiectiv")?.toDouble()
                                    val statusObiectivBd=document.getString("status")

//                                    Log.d(ContentValues.TAG,"${numeBd}, ${sumaBd}, ${ziBd} , ${tipBd}")


                                    if (numeObiectivBd.equals(currentItem.numeObiectiv)
                                        && sumaObiectivBd==currentItem.valoareObiectiv
                                        && statusObiectivBd.equals(currentItem.status))

                                    {

//                                        Log.d(ContentValues.TAG,"${currentItem.denumire}, ${currentItem.suma.toString()}, ${currentItem.zi} , ${currentItem.tipVenit.toString()}")
                                        val documentId = document.id
                                        val dataset = hashMapOf(
                                            "idUser" to firebaseAuth.currentUser?.uid.toString(),
                                            "numeObiectiv" to inputnumeObiectiv.text.toString(),
                                            "valoareObiectiv" to inputSumaObiectiv.text.toString(),
                                            "status" to statusObiectivBd)

                                        db.collection("Obiective")
                                            .document(documentId)
                                            .set(dataset)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    v.context,
                                                    "Data venitului a fost actualizată cu succes.",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                obiectivUpdateListener?.updated()
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


                        alertDialog.dismiss()

                    }

                    btnInapoi.setOnClickListener {
                        alertDialog.dismiss()
                    }
                    true
                }
                R.id.delete->{
                    val dialogBuilder = AlertDialog.Builder(v.context)
                    dialogBuilder.setTitle("Ștergere obiectiv")
                    dialogBuilder.setMessage("Sigur doriți să ștergeți acest obiectiv?")
                    dialogBuilder.setPositiveButton("Șterge") { _, _ ->
                        val dialog=LayoutInflater.from(v.context).inflate(R.layout.modifica_obiective_dialog,null)
                        val inputnumeObiectiv = dialog.findViewById<EditText>(R.id.modifica_obiectiv_nume)
                        val inputSumaObiectiv=dialog.findViewById<EditText>(R.id.modifica_obiectiv_suma)


                        dialogBuilder.setCancelable(false)

                        inputnumeObiectiv.setText(currentItem.numeObiectiv)
                        inputSumaObiectiv.setText(currentItem.valoareObiectiv.toString())


                        val db = FirebaseFirestore.getInstance()
                        var firebaseAuth = FirebaseAuth.getInstance()
                        db.collection("Obiective")
                            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    val numeObiectivBd=document.getString("numeObiectiv")
                                    val sumaObiectivBd=document.getString("valoareObiectiv")?.toDouble()
                                    val statusObiectivBd=document.getString("status")

                                    if (numeObiectivBd.equals(currentItem.numeObiectiv)
                                            && sumaObiectivBd==currentItem.valoareObiectiv
                                            && statusObiectivBd.equals(currentItem.status))

                                        {
                                            val documentId = document.id
                                            db.collection("Obiective")
                                                .document(documentId)
                                                .delete()
                                                .addOnSuccessListener {
                                                    Toast.makeText(v.context, "Datele au fost actualizate cu succes cu succes.", Toast.LENGTH_SHORT).show()
                                                    obiectivDeleteListener?.deleted()
                                                    notifyDataSetChanged()
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(v.context, "Eroare la actualizarea datelor: ${e.message}", Toast.LENGTH_SHORT).show()
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
        return obiectiveList.size
    }

    class ObiectivViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val numeObiectiv: TextView = itemView.findViewById(R.id.row_item_tv_obiective_nume)
        val status: TextView = itemView.findViewById(R.id.row_item_tv_obiective_status)
        val valoareObiectiv: TextView = itemView.findViewById(R.id.row_item_obiective_valoare)
        val image: ImageView =itemView.findViewById(R.id.row_item_iv_obiective)

    }
}