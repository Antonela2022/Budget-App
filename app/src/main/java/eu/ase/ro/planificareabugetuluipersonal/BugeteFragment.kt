package eu.ase.ro.planificareabugetuluipersonal

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eu.ase.ro.planificareabugetuluipersonal.util.Buget
import eu.ase.ro.planificareabugetuluipersonal.util.BugetAdapter


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FragmentBugete : Fragment() {
    private lateinit var adapter: BugetAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var bugeteList:ArrayList<Buget>

    private lateinit var firebaseAuth: FirebaseAuth
    val db = Firebase.firestore

    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_bugete, container, false)
        bugeteList = ArrayList()
        recyclerView = view.findViewById(R.id.popa_antonela_lv_bugete)
        firebaseAuth = FirebaseAuth.getInstance()


        db.collection("Bugete")
            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    val categorie=document.getString("categorie")
                    val totalCheltuieli = document.getString("totalCheltuieli")?.toInt()
                    val suma=document.getString("suma")

                    if(categorie!=null && totalCheltuieli!=null && suma!=null){
                        val buget = Buget(categorie, totalCheltuieli, suma)
                        bugeteList.add(buget)

                    }

                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = BugetAdapter(bugeteList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

}