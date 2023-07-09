package eu.ase.ro.planificareabugetuluipersonal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AdaugaObiectiveActivity : AppCompatActivity() {
    private lateinit var btnInapoiObiective:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adauga_obiective)

        btnInapoiObiective=findViewById(R.id.popa_antonela_btn_adauga_obiective_inapoi)
        btnInapoiObiective.setOnClickListener{
            val Intent= Intent(this,MainActivity::class.java)
            startActivity(Intent)
        }
    }
}