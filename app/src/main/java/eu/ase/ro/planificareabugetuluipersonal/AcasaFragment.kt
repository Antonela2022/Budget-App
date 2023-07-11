package eu.ase.ro.planificareabugetuluipersonal

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [AcasaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AcasaFragment : Fragment() {
    // TODO: Rename and change types of parameters
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
            val Intent= Intent(requireContext(),AdaugaCheltuieliActivity::class.java)
            startActivity(Intent)
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AcasaFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AcasaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}