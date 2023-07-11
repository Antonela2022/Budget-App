package eu.ase.ro.planificareabugetuluipersonal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import eu.ase.ro.planificareabugetuluipersonal.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

//        binding.textView.setOnClickListener {
//            val intent = Intent(this, SignInActivity::class.java)
//            startActivity(intent)
//        }
        binding.signUpBtnInregistrare.setOnClickListener {
            val email = binding.signUpTietEmail.text.toString().trim()
            val pass = binding.signUpTietParola.text.toString().trim()
            val username=binding.signUpTietUsername.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty() && username.isNotEmpty()) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, LogInActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Va rugam completati toate campurile!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}