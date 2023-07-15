package eu.ase.ro.planificareabugetuluipersonal

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

class AdaugaObiectiveActivity : AppCompatActivity() {
    private lateinit var btnInapoiObiective:Button
    private lateinit var tietNumeObiectiv: TextInputEditText
    private lateinit var tietValoareobiectiv: TextInputEditText
    private lateinit var btnAdaugaObiectiv:Button
    private lateinit var progressBar: ProgressBar
    private lateinit var container: ConstraintLayout
    private lateinit var firebaseAuth: FirebaseAuth

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adauga_obiective)

        intitComponents()
        inapoiPagPrincipala()
        adaugaObiectiveBD()

    }

    private fun adaugaObiectiveBD() {
        btnAdaugaObiectiv.setOnClickListener{
            progressBar.visibility = View.VISIBLE
            container.visibility = View.GONE


            val nume=tietNumeObiectiv.text.toString().trim()
            val valoare=tietValoareobiectiv.text.toString().trim()
            val status="Necompletat"

            if (nume.isEmpty() || nume.isBlank() || nume.length < 3) {
                Toast.makeText(this, "Vă rugăm să introduceți o denumire validă (minim 3 caractere).", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                container.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (valoare.isEmpty() || valoare.isBlank()) {
                Toast.makeText(this, "Vă rugăm să introduceți o valoare validă.", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                container.visibility = View.VISIBLE
                return@setOnClickListener
            }
            val obiectiv = hashMapOf(
                "numeObiectiv" to "${nume}",
                "valoareObiectiv" to "${valoare}",
                "status" to "${status}",
                "idUser" to "${firebaseAuth.currentUser?.uid.toString()}"
            )

            db.collection("Obiective")
                .add(obiectiv)
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

            println("Application message ${nume},${valoare}")
        }
    }


    private fun intitComponents() {
        firebaseAuth=FirebaseAuth.getInstance()
        btnInapoiObiective=findViewById(R.id.popa_antonela_btn_adauga_obiective_inapoi)
        tietNumeObiectiv=findViewById(R.id.popa_antonela_tiet_seteaza_obiectiv)
        tietValoareobiectiv=findViewById(R.id.popa_antonela_tiet_valoare_obiectiv)
        btnAdaugaObiectiv=findViewById(R.id.popa_antonela_marina_narcisa_btn_adauga_obiectiv)
        progressBar=findViewById(R.id.progressBarObiective)
        container=findViewById(R.id.container_view_obiective)

    }

    private fun inapoiPagPrincipala() {
        btnInapoiObiective.setOnClickListener{
            val Intent= Intent(this,MainActivity::class.java)
            startActivity(Intent)
        }
    }
}