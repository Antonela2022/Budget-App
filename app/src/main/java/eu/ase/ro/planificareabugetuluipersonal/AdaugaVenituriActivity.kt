package eu.ase.ro.planificareabugetuluipersonal

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AdaugaVenituriActivity : AppCompatActivity() {
    private lateinit var btnInapoiVenituri: Button
    private lateinit var tietNumeVenit:TextInputEditText
    private lateinit var tietSumaVenit: TextInputEditText
    private lateinit var btnAdaugaVenit:Button
    private lateinit var progressBar:ProgressBar
    private lateinit var container:ConstraintLayout
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var btnData:Button

    var formatDate=SimpleDateFormat("dd/MM/yyyy",Locale.US)
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adauga_venituri)

        intitComponents()
        inapoiPagPrincipala()
        seteazaData()
        adaugaVenitBD()
    }

    private fun adaugaVenitBD() {
        btnAdaugaVenit.setOnClickListener{
            progressBar.visibility = View.VISIBLE
            container.visibility = View.GONE

            val data=btnData.text.toString().trim()
            val nume=tietNumeVenit.text.toString().trim()
            val suma=tietSumaVenit.text.toString().trim()


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
                "zi" to "${data}",
                "nume" to "${nume}",
                "suma" to "${suma}",
                "idUser" to "${firebaseAuth.currentUser?.uid.toString()}"
            )

            db.collection("Venituri")
                .add(venit)
                .addOnSuccessListener {
                    progressBar.visibility = View.GONE

                    val Intent= Intent(this,MainActivity::class.java)
                    startActivity(Intent)
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    progressBar.visibility = View.GONE
                    container.visibility = View.VISIBLE
                    Log.w(TAG, "Error writing document", e) }

            println("Application message ${data},${nume},${suma}")
        }
    }



    private fun seteazaData() {
        val currentDate = Calendar.getInstance()
        val currentYear = currentDate.get(Calendar.YEAR)
        val currentMonth = currentDate.get(Calendar.MONTH)

        btnData.text = formatDate.format(currentDate.time)

        btnData.setOnClickListener {
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
                        btnData.text = date
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


    private fun inapoiPagPrincipala() {
        btnInapoiVenituri.setOnClickListener{
            val Intent= Intent(this,MainActivity::class.java)
            startActivity(Intent)
        }
    }

    private fun intitComponents() {
        firebaseAuth = FirebaseAuth.getInstance()
        btnInapoiVenituri = findViewById(R.id.popa_antonela_btn_adauga_venituri_inapoi)
        btnData=findViewById(R.id.popa_antonela_btn_data)
        tietNumeVenit=findViewById(R.id.popa_antonela_tiet_nume_venit)
        tietSumaVenit=findViewById(R.id.popa_antonela_tiet_suma_venit)
        btnAdaugaVenit=findViewById(R.id.popa_antonela_btn_adauga_venitul)
        progressBar=findViewById(R.id.progressBarVenituri)
        container=findViewById(R.id.container_view)
    }
}