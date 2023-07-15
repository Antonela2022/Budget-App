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
import eu.ase.ro.planificareabugetuluipersonal.util.SingletonList

class SeteazaBugetActivity : AppCompatActivity() {
    private lateinit var btnInapoiObiective:Button
    private lateinit var spnCategorie: Spinner
    private lateinit var tietSumaBuget: TextInputEditText
    private lateinit var btnAdaugaBuget:Button
    private lateinit var progressBar: ProgressBar
    private lateinit var container: ConstraintLayout
    private lateinit var firebaseAuth: FirebaseAuth

    val categoriiTotale= mutableListOf<String>()
    val categoriiExistente = mutableListOf<String>()
    val db = Firebase.firestore
    var totalBugete=0.0
    var totalVenituri=0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seteaza_buget)

        intitComponents()
        inapoiPagPrincipala()

        getCategoriiExistente()

    }

    private fun adaugaBugetBD(categoriiExistente: List<String>,totalBugete:Double,totalVenituri:Double) {
        btnAdaugaBuget.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            container.visibility = View.GONE


            val selectedValue: String = spnCategorie.selectedItem.toString()
            val suma=tietSumaBuget.text.toString().trim()
            val totalCheltuieli=0.0

            if (suma.isNotEmpty()) {
                val sumaDouble = suma.toDouble()
                if(totalBugete+sumaDouble>totalVenituri){
                    Toast.makeText(
                        this,
                        "Total Bugete ${totalBugete} si ${totalVenituri}",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar.visibility = View.GONE
                    container.visibility = View.VISIBLE
                    return@setOnClickListener
                }

            } else {
                Toast.makeText(this, "Vă rugăm să introduceți o sumă validă.", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                container.visibility = View.VISIBLE
            }





            // Verificăm dacă categoria selectată există deja în lista de categorii
            if (categoriiExistente.contains(selectedValue)) {
                Toast.makeText(
                    this,
                    "Pentru categoria selectata s-a introdus deja bugetul! Va rugam alocati buget pentru alta categorie!",
                    Toast.LENGTH_SHORT
                ).show()
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

                    categoriiTotale.addAll(categoriiExistente)
                    categoriiTotale.add(selectedValue)
                    SingletonList.setList(categoriiTotale)
                    Log.d(TAG,"Categorii totale: $categoriiTotale")

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
            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    val categorie = document.getString("categorie")
                    if (categorie != null) {
                        categoriiExistente.add(categorie)
                    }

                }
                db.collection("Bugete")
                    .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val sumaBuget = document.getString("suma")?.toDouble()

                            if (sumaBuget != null) {
                                totalBugete = totalBugete + sumaBuget
                            }


                        }
                        Log.d(ContentValues.TAG,"TotalBugete:${totalBugete}")
                        // Actualizați totalul cheltuielilor în baza de date
                        db.collection("Venituri")
                            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    val sumaVenit = document.getString("suma")?.toDouble()

                                    if (sumaVenit != null) {
                                        totalVenituri = totalVenituri + sumaVenit
                                    }

                                }
                                Log.d(ContentValues.TAG,"TotalBugete:${totalBugete}")
                                adaugaBugetBD(categoriiExistente,totalBugete,totalVenituri)
                            }
                    }



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

    private fun getTotalVeniturisiBugete(){


    }

}