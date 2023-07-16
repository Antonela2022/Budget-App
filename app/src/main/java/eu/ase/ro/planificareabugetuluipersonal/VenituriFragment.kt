package eu.ase.ro.planificareabugetuluipersonal

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eu.ase.ro.planificareabugetuluipersonal.util.OnVenitDeleteListener
import eu.ase.ro.planificareabugetuluipersonal.util.OnVenitUpdateListener
import eu.ase.ro.planificareabugetuluipersonal.util.Venit
import eu.ase.ro.planificareabugetuluipersonal.util.VenitAdapter


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VenituriFragment : Fragment(), OnVenitUpdateListener , OnVenitDeleteListener {

    private lateinit var adapter: VenitAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var venituriList:ArrayList<Venit>


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
        val view= inflater.inflate(R.layout.fragment_venituri, container, false)


        venituriList = ArrayList()
        recyclerView = view.findViewById(R.id.popa_antonela_lv_venituri)
        firebaseAuth = FirebaseAuth.getInstance()

        db.collection("Venituri")
            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    val denumire=document.getString("nume")
                    val suma=document.getString("suma")?.toDouble()
                    val zi = document.getString("zi")
                    val tipVenit=document.getString("tipVenit")?.toBoolean()

                    if (denumire != null && suma != null && zi != null && tipVenit != null) {
                        val venit = Venit(zi, denumire, suma,tipVenit)
                        venituriList.add(venit)
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

        adapter = VenitAdapter(venituriList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setOnVenitUpdateListener(this)
        adapter.setOnVenitDeleteListener(this)
    }

    override fun onVenitUpdated() {
        refreshRecyclerView()
    }

    override fun onVenitDeleted() {
        refreshRecyclerView()
    }



    private fun refreshRecyclerView() {

        val updatedVenituriList = ArrayList<Venit>()

        db.collection("Venituri")
            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    val denumire = document.getString("nume")
                    val suma = document.getString("suma")?.toDouble()
                    val zi = document.getString("zi")
                    val tipVenit = document.getString("tipVenit")?.toBoolean()

                    if (denumire != null && suma != null && zi != null && tipVenit != null) {
                        val venit = Venit(zi, denumire, suma, tipVenit)
                        updatedVenituriList.add(venit)
                    }
                }

                venituriList.clear()
                venituriList.addAll(updatedVenituriList)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
       }
    }