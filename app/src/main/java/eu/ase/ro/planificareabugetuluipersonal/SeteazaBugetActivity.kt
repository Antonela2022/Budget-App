package eu.ase.ro.planificareabugetuluipersonal

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SeteazaBugetActivity : AppCompatActivity() {
    private lateinit var btnInapoiObiective:Button
    private lateinit var spnCategorie: Spinner
    private lateinit var tietSumaBuget: TextInputEditText
    private lateinit var btnAdaugaBuget:Button
    private lateinit var progressBar: ProgressBar
    private lateinit var container: ConstraintLayout
    private lateinit var firebaseAuth: FirebaseAuth


    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seteaza_buget)

        intitComponents()
        inapoiPagPrincipala()
        getCategoriiExistente()
    }

    private fun adaugaBugetBD(categoriiExistente: List<String>) {
        btnAdaugaBuget.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            container.visibility = View.GONE

            val selectedValue: String = spnCategorie.selectedItem.toString()
            val suma=tietSumaBuget.text.toString().trim()
            val totalCheltuieli=0

            // Verificăm dacă categoria selectată există deja în lista de categorii
            if (categoriiExistente.contains(selectedValue)) {
                Toast.makeText(this, "Pentru categoria selectata s-a introdus deja bugetul! Va rugam alocati buget pentru alta categorie!", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                container.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (suma.isEmpty()) {
                Toast.makeText(this, "Vă rugăm introduceți o sumă validă!", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                container.visibility = View.VISIBLE
                return@setOnClickListener
            }


            val bugete = hashMapOf(
                "categorie" to "${selectedValue}",
                "totalCheltuieli" to "${totalCheltuieli}",
                "suma" to "${suma}",
                "idUser" to "${firebaseAuth.currentUser?.uid.toString()}"
            )


            db.collection("Bugete")
                .add(bugete)
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

            println("Application message ${selectedValue} , ${totalCheltuieli} ,${suma}")
        }
    }

    private fun getCategoriiExistente() {
        db.collection("Bugete")
            .get()
            .addOnSuccessListener { documents ->
                val categoriiExistente = mutableListOf<String>()
                for (document in documents) {
                    val categorie = document.getString("categorie")
                    if (categorie != null) {
                        categoriiExistente.add(categorie)
                    }
                }
                adaugaBugetBD(categoriiExistente)

            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting documents", e)
            }
    }
    private fun inapoiPagPrincipala() {
        btnInapoiObiective.setOnClickListener{
            val Intent= Intent(this,MainActivity::class.java)
            startActivity(Intent)
        }
    }

    private fun intitComponents() {
        firebaseAuth = FirebaseAuth.getInstance()
        btnInapoiObiective=findViewById(R.id.popa_antonela_btn_adauga_bugete_inapoi)
        spnCategorie=findViewById(R.id.popa_antonela_spn_cetegorii_buget)
        tietSumaBuget=findViewById(R.id.popa_antonela_tiet_seteaza_suma_buget)
        btnAdaugaBuget=findViewById(R.id.popa_antonela_marina_narcisa_btn_adauga_buget)
        progressBar=findViewById(R.id.progressBarBugete)
        container=findViewById(R.id.container_view_bugete)
    }
}