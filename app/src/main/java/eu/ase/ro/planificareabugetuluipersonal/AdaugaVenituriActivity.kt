package eu.ase.ro.planificareabugetuluipersonal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment

class AdaugaVenituriActivity : AppCompatActivity() {
    private lateinit var btnInapoiVenituri: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adauga_venituri)

        btnInapoiVenituri = findViewById(R.id.popa_antonela_btn_adauga_venituri_inapoi)


        btnInapoiVenituri.setOnClickListener{
            val Intent= Intent(this,MainActivity::class.java)
            startActivity(Intent)

        }
    }


}