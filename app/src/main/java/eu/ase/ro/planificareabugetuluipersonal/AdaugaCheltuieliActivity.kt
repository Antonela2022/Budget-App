package eu.ase.ro.planificareabugetuluipersonal

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import eu.ase.ro.planificareabugetuluipersonal.util.SingletonList

class AdaugaCheltuieliActivity : AppCompatActivity() {
    private lateinit var spnCategorieCheltuieli: Spinner
//    val myList = SingletonList.getList()
    private lateinit var btnInapoiCheltuieli: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adauga_cheltuieli)

        spnCategorieCheltuieli = findViewById(R.id.popa_antonela_spn_cetegorii_cheltuieli)
        val categorii = SingletonList.getList()
        Log.d(ContentValues.TAG, "Categorii: $categorii")

        if (categorii.isNotEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorii)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnCategorieCheltuieli.adapter = adapter
        } else {
            Log.d(ContentValues.TAG, "Lista categorii este goală")
        }
        btnInapoiCheltuieli = findViewById(R.id.popa_antonela_btn_adauga_cheltuiala_inapoi)
        btnInapoiCheltuieli.setOnClickListener {
            val Intent= Intent(this,MainActivity::class.java)
            startActivity(Intent)
        }
    }
}