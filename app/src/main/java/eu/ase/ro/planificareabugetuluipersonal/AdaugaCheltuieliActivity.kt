package eu.ase.ro.planificareabugetuluipersonal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AdaugaCheltuieliActivity : AppCompatActivity() {
    private lateinit var btnInapoiCheltuieli: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adauga_cheltuieli)

        btnInapoiCheltuieli = findViewById(R.id.popa_antonela_btn_adauga_cheltuiala_inapoi)
        btnInapoiCheltuieli.setOnClickListener {
            val Intent= Intent(this,MainActivity::class.java)
            startActivity(Intent)
        }
    }
}