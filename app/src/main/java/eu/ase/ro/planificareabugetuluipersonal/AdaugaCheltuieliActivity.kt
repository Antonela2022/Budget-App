package eu.ase.ro.planificareabugetuluipersonal


import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eu.ase.ro.planificareabugetuluipersonal.util.SingletonList
import java.text.SimpleDateFormat
import java.util.*

class AdaugaCheltuieliActivity : AppCompatActivity() {
    private lateinit var btnInapoiCheltuieli: Button
    private lateinit var spnCategorieCheltuieli: Spinner
    private lateinit var tietNumeCheltuiala: TextInputEditText
    private lateinit var tietSumaCheltuiala: TextInputEditText
    private lateinit var btnAdaugaCheltuiala:Button
    private lateinit var progressBar: ProgressBar
    private lateinit var container: ConstraintLayout
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var btnDataCheltuiala:Button

    var formatDate= SimpleDateFormat("dd/MM/yyyy", Locale.US)
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adauga_cheltuieli)

        initComponents()
        seteazaItemSpinner()
        seteazaData()
        adaugaCheltuialaBD()
        inapoiPagPrincipala()
    }

    private fun inapoiPagPrincipala() {
        btnInapoiCheltuieli.setOnClickListener {
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun seteazaItemSpinner() {
        val categorii = SingletonList.getList()
        Log.d(ContentValues.TAG, "Categorii: $categorii")

        if (categorii.isNotEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorii)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnCategorieCheltuieli.adapter = adapter
        } else {
            Log.d(ContentValues.TAG, "Lista categorii este goală")
        }
    }

    private fun initComponents() {
        firebaseAuth= FirebaseAuth.getInstance()
        btnAdaugaCheltuiala=findViewById(R.id.popa_antonela_btn_adauga_cheltuiala)
        progressBar=findViewById(R.id.progressBarCheltuieli)
        container=findViewById(R.id.container_view_cheltuieli)
        btnInapoiCheltuieli = findViewById(R.id.popa_antonela_btn_adauga_cheltuiala_inapoi)
        tietNumeCheltuiala=findViewById(R.id.popa_antonela_tiet_nume_cheltuiala)
        tietSumaCheltuiala=findViewById(R.id.popa_antonela_tiet_suma_cheltuiala)
        btnDataCheltuiala=findViewById(R.id.popa_antonela_btn_dataCheltuieli)

        spnCategorieCheltuieli = findViewById(R.id.popa_antonela_spn_cetegorii_cheltuieli)
    }

    private fun adaugaCheltuialaBD() {
        btnAdaugaCheltuiala.setOnClickListener{
            progressBar.visibility = View.VISIBLE
            container.visibility = View.GONE

            val categorieChetuiala=spnCategorieCheltuieli.selectedItem.toString()
            val data=btnDataCheltuiala.text.toString().trim()
            val nume=tietNumeCheltuiala.text.toString().trim()
            val suma=tietSumaCheltuiala.text.toString().trim()



            //actualizare total cheltuieli-bugete
            db.collection("Bugete")
                .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
                .whereEqualTo("categorie", categorieChetuiala)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val totalCheltuieli = document.getString("totalCheltuieli")?.toDouble()
                        val sumaCheltuiala = suma.toDouble()
                        val bugetAlocat=document.getString("suma")?.toDouble()

                        val totalActualizat = totalCheltuieli?.plus(sumaCheltuiala).toString()



                        // actualizare totalul cheltuielilor în baza de date
                        db.collection("Bugete")
                            .document(document.id)
                            .update("totalCheltuieli", totalActualizat)


                    }
                }


            if (nume.isEmpty() || nume.isBlank() || nume.length < 3) {
                Toast.makeText(this, "Vă rugăm să introduceți o denumire validă (minim 3 caractere).", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                container.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (suma.isEmpty() || suma.isBlank()) {
                Toast.makeText(this, "Vă rugăm să introduceți o sumă validă.", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                container.visibility = View.VISIBLE
                return@setOnClickListener
            }
            val venit = hashMapOf(
                "categorie" to "${categorieChetuiala}",
                "data" to "${data}",
                "nume" to "${nume}",
                "suma" to "${suma}",
                "idUser" to "${firebaseAuth.currentUser?.uid.toString()}"
            )

            db.collection("Cheltuieli")
                .add(venit)
                .addOnSuccessListener {
                    progressBar.visibility = View.GONE

                    val Intent= Intent(this,MainActivity::class.java)
                    startActivity(Intent)
                    Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    progressBar.visibility = View.GONE
                    container.visibility = View.VISIBLE
                    Log.w(ContentValues.TAG, "Error writing document", e) }

            println("Application message ${categorieChetuiala} ,${data},${nume},${suma}")
        }

    }

    private fun seteazaData() {

            val currentDate = Calendar.getInstance()
            val currentYear = currentDate.get(Calendar.YEAR)
            val currentMonth = currentDate.get(Calendar.MONTH)

            btnDataCheltuiala.text = formatDate.format(currentDate.time)

            btnDataCheltuiala.setOnClickListener {
                val getDate = Calendar.getInstance()
                val datePicker = DatePickerDialog(
                    this,
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
                            btnDataCheltuiala.text = date
                            Toast.makeText(this, "Date: $date", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Vă rugăm să selectați luna și anul curent.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    getDate.get(Calendar.YEAR),
                    getDate.get(Calendar.MONTH),
                    getDate.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.show()
            }
        }
    }