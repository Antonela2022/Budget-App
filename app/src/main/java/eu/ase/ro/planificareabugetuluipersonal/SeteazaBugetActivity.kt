package eu.ase.ro.planificareabugetuluipersonal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SeteazaBugetActivity : AppCompatActivity() {
    private lateinit var btnInapoiObiective:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seteaza_buget)

        btnInapoiObiective=findViewById(R.id.popa_antonela_btn_adauga_bugete_inapoi)
        btnInapoiObiective.setOnClickListener{
            val Intent= Intent(this,MainActivity::class.java)
            startActivity(Intent)
        }
    }
}