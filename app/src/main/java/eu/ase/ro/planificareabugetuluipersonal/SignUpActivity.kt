package eu.ase.ro.planificareabugetuluipersonal

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eu.ase.ro.planificareabugetuluipersonal.databinding.ActivitySignUpBinding
import eu.ase.ro.planificareabugetuluipersonal.util.Obiectiv

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        val db = Firebase.firestore
        val emailPattern="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

//        binding.textView.setOnClickListener {
//            val intent = Intent(this, SignInActivity::class.java)
//            startActivity(intent)
//        }
        binding.signUpBtnInregistrare.setOnClickListener {
            val email = binding.signUpTietEmail.text.toString().trim()
            val pass = binding.signUpTietParola.text.toString().trim()
            val username=binding.signUpTietUsername.text.toString().trim()




            if(isValidEmailId(email)){
                if (email.isNotEmpty() && pass.isNotEmpty() && username.isNotEmpty()) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val utilizator = hashMapOf(
                                "numeUtilizator" to "${username}",
                                "email" to "${email}",
                                "parola" to "${pass}",
                                "idUser" to "${firebaseAuth.currentUser?.uid.toString()}"
                            )
                            db.collection("Utilizatori")

                                .add(utilizator)
                                .addOnSuccessListener {
                                    val intent= Intent(this,LogInActivity::class.java)
                                    startActivity(intent)
                                    Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(ContentValues.TAG, "Error writing document", e) }



                        } else {
                            Toast.makeText(this, "Parola invalida!Introduceti minim 6 cararactere", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Va rugam completati toate campurile!", Toast.LENGTH_SHORT).show()
                }
            }else {
                Toast.makeText(this, "Email-ul introdus nu este valid! Va rugam introduceti un emial valid!", Toast.LENGTH_SHORT).show()
            }


        }


    }

    private fun isValidEmailId(email: String): Boolean {
        val emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

        return email.matches(emailPattern.toRegex())
    }
}