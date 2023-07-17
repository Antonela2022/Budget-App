package eu.ase.ro.planificareabugetuluipersonal

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eu.ase.ro.planificareabugetuluipersonal.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BugeteCheltuieliFragment : Fragment() , OnUpdateListener , OnDeleteListener {

    private lateinit var expandableListView: ExpandableListView
    private lateinit var adapter: ExpandableListViewAdapter
    private val listaBugete = mutableListOf<Buget>()
    private val listaCheltuieli = mutableListOf<Cheltuiala>()


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
        savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_bugete_cheltuieli, container, false)

        expandableListView = view.findViewById(R.id.popa_antonela_elv_bugete_cheltuieli)

        firebaseAuth = FirebaseAuth.getInstance()


        db.collection("Bugete")
            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    val categorie=document.getString("categorie")
                    val totalCheltuieli = document.getString("totalCheltuieli")?.toDouble()
                    val suma=document.getString("suma")?.toDouble()

                    if(categorie!=null && totalCheltuieli!=null && suma!=null){
                        val buget = Buget(categorie, totalCheltuieli, suma)
                        listaBugete.add(buget)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }

        db.collection("Cheltuieli")
            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")

                    val categorie=document.getString("categorie")
                    val data=document.getString("data")
                    val denumireCheltuiala=document.getString("nume")
                    val sumaCheltuita = document.getString("suma")?.toDouble()


                    if(categorie!=null && data!=null && denumireCheltuiala!=null  && sumaCheltuita!=null){
                        val cheltuiala = Cheltuiala(categorie,data, denumireCheltuiala, sumaCheltuita)
                        listaCheltuieli.add(cheltuiala)

                    }

                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }


        adapter = ExpandableListViewAdapter(requireContext(), listaBugete, listaCheltuieli)
        expandableListView.setAdapter(adapter)
        adapter.setOnCheltuialaUpdateListener(this)
        adapter.setOnCheltuialaDeleteListener(this)
        adapter.setOnCategorieDeleteListener(this)
        return view
    }

    override fun updated() {
        refreshRecyclerView()
    }

    override fun deleted() {
        refreshRecyclerView()
        refreshRecyclerViewCategory()

    }

    private fun refreshRecyclerViewCategory() {
        val updateBugeteList = ArrayList<Buget>()

        db.collection("Bugete")
            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    val suma = document.getString("suma")?.toDouble()
                    val totalCheltuieli = document.getString("totalCheltuieli")?.toDouble()
                    val categorie = document.getString("categorie")

                    if ( suma != null && totalCheltuieli != null && categorie != null) {
                        val buget = Buget(categorie, totalCheltuieli , suma)
                        updateBugeteList.add(buget)
                    }
                }

                listaBugete.clear()
                listaBugete.addAll(updateBugeteList)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }

    }


    private fun refreshRecyclerView() {

        val updatedCheltuieliList = ArrayList<Cheltuiala>()

        db.collection("Cheltuieli")
            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    val denumire = document.getString("nume")
                    val suma = document.getString("suma")?.toDouble()
                    val data = document.getString("data")
                    val categorie = document.getString("categorie")

                    if (denumire != null && suma != null && data != null && categorie != null) {
                        val cheltuiala = Cheltuiala(categorie, data, denumire, suma)
                        updatedCheltuieliList.add(cheltuiala)
                    }
                }

                listaCheltuieli.clear()
                listaCheltuieli.addAll(updatedCheltuieliList)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }
}


