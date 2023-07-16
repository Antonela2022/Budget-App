package eu.ase.ro.planificareabugetuluipersonal

import android.app.ProgressDialog.show
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eu.ase.ro.planificareabugetuluipersonal.util.SingletonList
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AcasaFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    val categoriiExistente = mutableListOf<String>()

    private lateinit var firebaseAuth: FirebaseAuth
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

        firebaseAuth=FirebaseAuth.getInstance()

        var sumaVenituri = 0.0
        var sumaCheltuieli = 0.0
        var sumaBugetRamas = 0.0

        val view= inflater.inflate(R.layout.fragment_acasa, container, false)

        val totalVenituri=view.findViewById<TextView>(R.id.popa_antonela_tv_venit_total)
        val totalCheltuieli=view.findViewById<TextView>(R.id.popa_antonela_tv_cheltuieli_total)
        val bugetRamas =view.findViewById<TextView>(R.id.popa_antonela_tv_buget_ramas)
        val fondDeUrgenta=view.findViewById<TextView>(R.id.popa_antonela_tv_fond_de_urgente)
        val procentObiectiv=view.findViewById<TextView>(R.id.popa_antonela_tv_progress_bar_procentaj)
        val salut=view.findViewById<TextView>(R.id.popa_antonela_tv_title_salut)

        db.collection("Utilizatori")
            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    val userName = document.getString("numeUtilizator")
                    if (userName != null) {
                        salut.text = "Buna, ${userName} !"
                    }
                }


            }
        var areObiective=true
        var formatDate= SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val btnAdaugaVenituri=view.findViewById<Button>(R.id.popa_antonela_btn_adauga_venituri)
        val btnAdaugaBugete=view.findViewById<Button>(R.id.popa_antonela_btn_seteaza_buget)
        val btnAdaugaObiective=view.findViewById<Button>(R.id.popa_antonela_btn_seteaza_obiective)
        val btnAdaugaCheltuieli=view.findViewById<Button>(R.id.popa_antonela_btn_adauga_cheltuieli)

        btnAdaugaVenituri.setOnClickListener{
            val intent = Intent(requireContext(), AdaugaVenituriActivity::class.java)
            startActivity(intent)
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


        val currentDate = Calendar.getInstance()
        val currentDay=currentDate.get(Calendar.DAY_OF_MONTH)
        val currentMonth = currentDate.get(Calendar.MONTH) + 1
        val currentYear = currentDate.get(Calendar.YEAR)

        var maxVenit = 0.0
        var dataVenitMaxim: Date? = null

        //afisare fereastra pop-up la sfarsitul lunii
        db.collection("Venituri")
            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val suma = document.getString("suma")?.toDouble()

                    if (suma != null) {
                        sumaVenituri = sumaVenituri + suma
                        totalVenituri.setText("Total Venituri: ${sumaVenituri}")
                    }

                    val data= formatDate.parse(document.getString("zi").toString())
                    val tipVenit=document.getString("tipVenit").toBoolean()


                    if (tipVenit && suma != null) {
                        if (suma > maxVenit) {
                            maxVenit = suma
                            dataVenitMaxim = data
                        }
                    }
                }
                sumaBugetRamas=sumaBugetRamas+sumaVenituri
                bugetRamas.setText("Buget Ramas: ${sumaBugetRamas}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error getting documents", e)
            }

        db.collection("Cheltuieli")
            .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    val sumaCheltuita = document.getString("suma")?.toDouble()
                    if (sumaCheltuita != null) {
                        sumaCheltuieli = sumaCheltuieli + sumaCheltuita
                        totalCheltuieli.setText("Total Cheltuieli: ${sumaCheltuieli}")

                    }

                }

                sumaBugetRamas=sumaBugetRamas-sumaCheltuieli
                bugetRamas.setText("Buget Ramas: ${sumaBugetRamas}")

                if(dataVenitMaxim!=null){
                    val calendar = Calendar.getInstance()
                    calendar.time = dataVenitMaxim

                    val incomeDay= calendar.get(Calendar.DAY_OF_MONTH)
                    val incomeMonth = calendar.get(Calendar.MONTH) + 1
                    val incomeYear = calendar.get(Calendar.YEAR)


                    Log.d("MyApp", "incomeDay: $incomeDay, currentDay: $currentDay")
                    Log.d("MyApp", "incomeMonth: $incomeMonth, currentMonth: $currentMonth")
                    Log.d("MyApp", "incomeYear: $incomeYear, currentYear: $currentYear")


                    if (incomeDay==currentDay && incomeMonth + 1 == currentMonth && incomeYear == currentYear) {
                        Toast.makeText(requireContext(),"E bine ${ sumaBugetRamas}",Toast.LENGTH_SHORT).show()

                        var valoarePrimObiectiv=0.0
                        if (sumaBugetRamas != null) {
                            if(sumaBugetRamas<=0){
                                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                                alertDialogBuilder.setTitle("Atenție")
                                alertDialogBuilder.setMessage("Nu ai reușit să economisești luna aceasta.")
                                alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                                    dialog.dismiss() // Închide fereastra de dialog
                                }
                                val alertDialog = alertDialogBuilder.create()
                                alertDialog.show()

                            }
                            else{
                                db.collection("Obiective")
                                    .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        if (documents.isEmpty) {
                                            areObiective = false
                                        }else{
                                            for (document in documents) {
                                                val status = document.getString("status")
                                                if (status == "Necompletat") {
                                                    valoarePrimObiectiv = document.getString("valoareObiectiv")?.toDouble()!!

                                                    break
                                                }
                                            }
                                        }

                                        val valoareFondUrgente=0.20*sumaBugetRamas
                                        val valoareObiectiv=0.80*sumaBugetRamas

                                        Toast.makeText(requireContext(),"E bine ${ sumaBugetRamas}",Toast.LENGTH_SHORT).show()

                                        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.succes_dialog_economisire, null)

                                        val builder = AlertDialog.Builder(requireContext())
                                            .setView(dialogView)
                                            .setTitle("Felicitari!")
                                            .setMessage("Ati reusit sa economisiti ${sumaBugetRamas} RON. " +
                                                    "Mai jos trebuie sa alegeti cat doriti sa adaugati in fond de urgenta " +
                                                    "si cat doriti sa adaugati in obiectiv. Va reamintim ca este de preferat" +
                                                    " ca macar 20%  din bugetul ramas sa il adaugati in fond de urgenta ")


                                        val alertDialog = builder.show()

                                        val inputFondUrgenta = dialogView.findViewById<EditText>(R.id.sumaAlocataFondUrgeta)
                                        val inputObiectiv = dialogView.findViewById<EditText>(R.id.sumaAlocatObiectiv)


                                        if(!areObiective){
                                            inputFondUrgenta.setText("${sumaBugetRamas}")
                                            inputObiectiv.setText("0.0")
                                            inputObiectiv.isEnabled = false
                                            inputObiectiv.isClickable = false
                                            inputFondUrgenta.isEnabled = false
                                            inputFondUrgenta.isClickable = false

                                        }else{
                                            inputFondUrgenta.setText("${valoareFondUrgente}")
                                            inputObiectiv.setText("${valoareObiectiv}")
                                        }

                                        alertDialog.setCancelable(false)

                                        val btnOKDialog = dialogView.findViewById<Button>(R.id.btnOKEconomisire)
                                        btnOKDialog.setOnClickListener {
                                            val sumaFondUrgenta=inputFondUrgenta.text.toString()
                                            val sumaObiectiv = inputObiectiv.text.toString()

                                            if (sumaObiectiv.isEmpty()) {
                                                Toast.makeText(requireContext(), "Introduceți o sumă validă pentru obiectiv", Toast.LENGTH_SHORT).show()
                                                return@setOnClickListener
                                            }

                                            if (sumaFondUrgenta.isEmpty()) {
                                                Toast.makeText(requireContext(), "Introduceți o sumă validă pentru fond de urgență", Toast.LENGTH_SHORT).show()
                                                return@setOnClickListener

                                            }
                                            if(sumaObiectiv.toDouble() + sumaFondUrgenta.toDouble()!=sumaBugetRamas){
                                                Toast.makeText(requireContext(), "Adunarea celor doua sume trebuie sa fie egala cu bugetul ramas.Va rugam introduceti sume valide!", Toast.LENGTH_SHORT).show()
                                                return@setOnClickListener
                                            }

                                            if(sumaObiectiv.toDouble()>valoarePrimObiectiv){
                                                Toast.makeText(requireContext(), "Suma introdusa in obiectiv este mai mare decat valoarea obiectivului de ${valoarePrimObiectiv}", Toast.LENGTH_SHORT).show()
                                                return@setOnClickListener
                                            }
                                            bugetRamas.setText("Buget Ramas : 0.0")

                                            fondDeUrgenta.setText("Fond de urgenta :"+ sumaFondUrgenta.toDouble().toString())
                                            alertDialog.dismiss()

                                            if(sumaObiectiv.toDouble()==valoarePrimObiectiv){
                                                db.collection("Obiective")
                                                    .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
                                                    .get()
                                                    .addOnSuccessListener { documents ->
                                                        for (document in documents) {
                                                            val status = document.getString("status")
                                                            if (status.equals("Necompletat")) {
                                                                db.collection("Obiective")
                                                                    .document(document.id)
                                                                    .update("status", "Completat")

                                                                break
                                                            }
                                                        }
                                                    }


                                                val dialogBuilder = AlertDialog.Builder(requireContext())
                                                    .setTitle("Felicitări!")
                                                    .setMessage("Ati reusit sa va atingeti obiectivul! Continuati tot asa!")
                                                    .setPositiveButton("OK") { dialog, _ ->

                                                        dialog.dismiss()
                                                    }
                                                val alertDialog = dialogBuilder.create()
                                                alertDialog.show()

                                            }


                                            if(sumaObiectiv.toDouble()<valoarePrimObiectiv){

                                                val procentajObiectiv=((sumaObiectiv.toDouble() * 100)/valoarePrimObiectiv).toString()

                                                procentObiectiv.setText("$procentajObiectiv" + "%")
                                                val dialogBuilder = AlertDialog.Builder(requireContext())
                                                    .setTitle("Felicitări!")
                                                    .setMessage("Ati reusit sa completati $procentajObiectiv% din obiectiv! Continuati tot asa!")
                                                    .setPositiveButton("OK") { dialog, _ ->

                                                        dialog.dismiss()
                                                    }
                                                val alertDialog = dialogBuilder.create()
                                                alertDialog.show()

                                            }
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                                    }

                            }
                        }
                    }
//                    if(incomeDay==currentDay && incomeMonth == currentMonth && incomeYear == currentYear){
//                        Toast.makeText(requireContext(),"E bine ${ sumaBugetRamas}",Toast.LENGTH_SHORT).show()
//
//                        var valoarePrimObiectiv=0.0
//                        if (sumaBugetRamas != null) {
//                            if(sumaBugetRamas<=0){
//                                val alertDialogBuilder = AlertDialog.Builder(requireContext())
//                                alertDialogBuilder.setTitle("Atenție")
//                                alertDialogBuilder.setMessage("Nu ai reușit să economisești luna aceasta.")
//                                alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
//                                    dialog.dismiss() // Închide fereastra de dialog
//                                }
//                                val alertDialog = alertDialogBuilder.create()
//                                alertDialog.show()
//
//                            }
//                            else{
//                                db.collection("Obiective")
//                                    .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
//                                    .get()
//                                    .addOnSuccessListener { documents ->
//                                        if (documents.isEmpty) {
//                                            areObiective = false
//                                        }else{
//                                            for (document in documents) {
//                                                val status = document.getString("status")
//                                                if (status == "Necompletat") {
//                                                    valoarePrimObiectiv = document.getString("valoareObiectiv")?.toDouble()!!
//
//                                                    break
//                                                }
//                                            }
//                                        }
//
//                                        val valoareFondUrgente=0.20*sumaBugetRamas
//                                        val valoareObiectiv=0.80*sumaBugetRamas
//
//                                        Toast.makeText(requireContext(),"E bine ${ sumaBugetRamas}",Toast.LENGTH_SHORT).show()
//
//                                        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.succes_dialog_economisire, null)
//
//                                        val builder = AlertDialog.Builder(requireContext())
//                                            .setView(dialogView)
//                                            .setTitle("Felicitari!")
//                                            .setMessage("Ati reusit sa economisiti ${sumaBugetRamas} RON. " +
//                                                    "Mai jos trebuie sa alegeti cat doriti sa adaugati in fond de urgenta " +
//                                                    "si cat doriti sa adaugati in obiectiv. Va reamintim ca este de preferat" +
//                                                    " ca macar 20%  din bugetul ramas sa il adaugati in fond de urgenta ")
//
//
//                                        val alertDialog = builder.show()
//
//                                        val inputFondUrgenta = dialogView.findViewById<EditText>(R.id.sumaAlocataFondUrgeta)
//                                        val inputObiectiv = dialogView.findViewById<EditText>(R.id.sumaAlocatObiectiv)
//
//
//                                        if(!areObiective){
//                                            inputFondUrgenta.setText("${sumaBugetRamas}")
//                                            inputObiectiv.setText("0.0")
//                                            inputObiectiv.isEnabled = false
//                                            inputObiectiv.isClickable = false
//                                            inputFondUrgenta.isEnabled = false
//                                            inputFondUrgenta.isClickable = false
//
//                                        }else{
//                                            inputFondUrgenta.setText("${valoareFondUrgente}")
//                                            inputObiectiv.setText("${valoareObiectiv}")
//                                        }
//
//                                        alertDialog.setCancelable(false)
//
//                                        val btnOKDialog = dialogView.findViewById<Button>(R.id.btnOKEconomisire)
//                                        btnOKDialog.setOnClickListener {
//                                            val sumaFondUrgenta=inputFondUrgenta.text.toString()
//                                            val sumaObiectiv = inputObiectiv.text.toString()
//
//                                            if (sumaObiectiv.isEmpty()) {
//                                                Toast.makeText(requireContext(), "Introduceți o sumă validă pentru obiectiv", Toast.LENGTH_SHORT).show()
//                                                return@setOnClickListener
//                                            }
//
//                                            if (sumaFondUrgenta.isEmpty()) {
//                                                Toast.makeText(requireContext(), "Introduceți o sumă validă pentru fond de urgență", Toast.LENGTH_SHORT).show()
//                                                return@setOnClickListener
//
//                                            }
//                                            if(sumaObiectiv.toDouble() + sumaFondUrgenta.toDouble()!=sumaBugetRamas){
//                                                Toast.makeText(requireContext(), "Adunarea celor doua sume trebuie sa fie egala cu bugetul ramas.Va rugam introduceti sume valide!", Toast.LENGTH_SHORT).show()
//                                                return@setOnClickListener
//                                            }
//
//                                            if(sumaObiectiv.toDouble()>valoarePrimObiectiv){
//                                                Toast.makeText(requireContext(), "Suma introdusa in obiectiv este mai mare decat valoarea obiectivului de ${valoarePrimObiectiv}", Toast.LENGTH_SHORT).show()
//                                                return@setOnClickListener
//                                            }
//                                            bugetRamas.setText("Buget Ramas : 0.0")
//
//                                            fondDeUrgenta.setText("Fond de urgenta :"+ sumaFondUrgenta.toDouble().toString())
//                                            alertDialog.dismiss()
//
//                                            if(sumaObiectiv.toDouble()==valoarePrimObiectiv){
//                                                db.collection("Obiective")
//                                                    .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
//                                                    .get()
//                                                    .addOnSuccessListener { documents ->
//                                                        for (document in documents) {
//                                                            val status = document.getString("status")
//                                                            if (status.equals("Necompletat")) {
//                                                                db.collection("Obiective")
//                                                                    .document(document.id)
//                                                                    .update("status", "Completat")
//
//                                                                break
//                                                            }
//                                                        }
//                                                    }
//
//
//                                            val dialogBuilder = AlertDialog.Builder(requireContext())
//                                                    .setTitle("Felicitări!")
//                                                    .setMessage("Ati reusit sa va atingeti obiectivul! Continuati tot asa!")
//                                                    .setPositiveButton("OK") { dialog, _ ->
//
//                                                        dialog.dismiss()
//                                                    }
//                                                val alertDialog = dialogBuilder.create()
//                                                alertDialog.show()
//
//                                            }
//
//
//                                            if(sumaObiectiv.toDouble()<valoarePrimObiectiv){
//
//                                                val procentajObiectiv=((sumaObiectiv.toDouble() * 100)/valoarePrimObiectiv).toString()
//
//                                                procentObiectiv.setText("$procentajObiectiv" + "%")
//                                                val dialogBuilder = AlertDialog.Builder(requireContext())
//                                                    .setTitle("Felicitări!")
//                                                    .setMessage("Ati reusit sa completati $procentajObiectiv% din obiectiv! Continuati tot asa!")
//                                                    .setPositiveButton("OK") { dialog, _ ->
//
//                                                        dialog.dismiss()
//                                                    }
//                                                val alertDialog = dialogBuilder.create()
//                                                alertDialog.show()
//
//                                            }
//                                        }
//                                    }
//                                    .addOnFailureListener { exception ->
//                                        Log.w(ContentValues.TAG, "Error getting documents: ", exception)
//                                    }
//
//                            }
//                        }
//                    }
                }

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }

            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error getting documents", e)
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