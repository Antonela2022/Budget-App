package eu.ase.ro.planificareabugetuluipersonal

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eu.ase.ro.planificareabugetuluipersonal.util.SingletonList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AcasaFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val categoriiExistente = mutableListOf<String>()
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_acasa, container, false)
        val btnAdaugaVenituri=view.findViewById<Button>(R.id.popa_antonela_btn_adauga_venituri)
        val btnAdaugaBugete=view.findViewById<Button>(R.id.popa_antonela_btn_seteaza_buget)
        val btnAdaugaObiective=view.findViewById<Button>(R.id.popa_antonela_btn_seteaza_obiective)
        val btnAdaugaCheltuieli=view.findViewById<Button>(R.id.popa_antonela_btn_adauga_cheltuieli)

        btnAdaugaVenituri.setOnClickListener{
            val Intent= Intent(requireContext(),AdaugaVenituriActivity::class.java)
            startActivity(Intent)
        }

        btnAdaugaBugete.setOnClickListener{
            val Intent= Intent(requireContext(),SeteazaBugetActivity::class.java)
            startActivity(Intent)
        }

        btnAdaugaObiective.setOnClickListener{
            val Intent= Intent(requireContext(),AdaugaObiectiveActivity::class.java)
            startActivity(Intent)
        }

        btnAdaugaCheltuieli.setOnClickListener{
            getCategoriiExistente()

            val intent= Intent(requireContext(),AdaugaCheltuieliActivity::class.java)
            intent.putStringArrayListExtra("categoriiCheltuieli", ArrayList(categoriiExistente))
            startActivity(intent)
        }
        return view
    }

    private fun getCategoriiExistente() {
        db.collection("Bugete")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val categorie = document.getString("categorie")
                    if (categorie != null) {
                        categoriiExistente.add(categorie)
                    }
                }
                SingletonList.setList(categoriiExistente)
                Log.d(ContentValues.TAG, "Categorii existente:${SingletonList.getList()}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error getting documents", e)
            }
    }
}