package eu.ase.ro.planificareabugetuluipersonal

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import eu.ase.ro.planificareabugetuluipersonal.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BugeteCheltuieliFragment : Fragment() {

    private lateinit var expandableListView: ExpandableListView
    private lateinit var adapter: ExpandableListViewAdapter
    private val listaBugete = mutableListOf<Buget>()
    private val listaCheltuieli = mutableListOf<Cheltuiala>()

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

        // Adăugați bugete în listaBugete
         adaugaBuget("Categoria 1", 1000.0,67.2)
         adaugaBuget("Categoria 2", 2000.0,67.3)
         adaugaBuget("Categoria 3", 1500.0,67.2)

        // Adăugați cheltuieli în listaCheltuieli
        adaugaCheltuiala("Categoria 1", "01/07/2023", "Cheltuiala 1", 500.0)
        adaugaCheltuiala("Categoria 1", "02/07/2023", "Cheltuiala 2", 300.0)
        adaugaCheltuiala("Categoria 2", "03/07/2023", "Cheltuiala 3", 800.0)
        adaugaCheltuiala("Categoria 3", "04/07/2023", "Cheltuiala 4", 200.0)
        adaugaCheltuiala("Categoria 3", "05/07/2023", "Cheltuiala 5", 400.0)

        adapter = ExpandableListViewAdapter(requireContext(), listaBugete, listaCheltuieli)
        expandableListView.setAdapter(adapter)
        return view
    }

    private fun adaugaBuget(categoria: String, totalCheltuieli :Double,valoareBuget: Double) {
        val buget = Buget(categoria, totalCheltuieli , valoareBuget)
        listaBugete.add(buget)
    }

    private fun adaugaCheltuiala(categoria: String, data: String, denumire: String, sumaCheltuita: Double) {
        val cheltuiala = Cheltuiala(categoria, data, denumire, sumaCheltuita)
        listaCheltuieli.add(cheltuiala)
    }
}


