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
import eu.ase.ro.planificareabugetuluipersonal.util.Obiectiv
import eu.ase.ro.planificareabugetuluipersonal.util.ObiectivAdapter


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ObiectiveFragment : Fragment() {
    private lateinit var adapter: ObiectivAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var obiectiveList:ArrayList<Obiectiv>

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
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_obiective, container, false)
        obiectiveList = ArrayList()
        recyclerView = view.findViewById(R.id.popa_antonela_lv_obiective)
        firebaseAuth = FirebaseAuth.getInstance()

        db.collection("Obiective")
            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    val denumire=document.getString("numeObiectiv")
                    val valoare=document.getString("valoareObiectiv")?.toDouble()
                    val status = document.getString("status")

                    if (denumire != null && valoare != null && status != null) {
                        val obiectiv = Obiectiv(denumire, status, valoare)
                        obiectiveList.add(obiectiv)
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

        adapter = ObiectivAdapter(obiectiveList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }


}