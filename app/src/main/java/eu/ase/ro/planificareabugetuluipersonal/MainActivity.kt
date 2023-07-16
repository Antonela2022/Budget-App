package eu.ase.ro.planificareabugetuluipersonal


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView

import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var headerUserNameTextView: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout=findViewById<DrawerLayout>(R.id.drawer_layout)


        val toolbar=findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        val navigationView=findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val headerView = navigationView.getHeaderView(0)
        headerUserNameTextView = headerView.findViewById(R.id.popa_antonela_tv_header_user_name)


        firebaseAuth= FirebaseAuth.getInstance()

            db.collection("Utilizatori")
                .whereEqualTo("idUser", firebaseAuth.currentUser?.uid.toString())
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents){
                        val userName = document.getString("numeUtilizator")
                        if (userName != null) {
                            headerUserNameTextView.text = userName
                        }
                    }


                }



        val toggle=ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if(savedInstanceState==null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame_container,AcasaFragment()).commit()
            navigationView.setCheckedItem(R.id.popa_antonela_nav_acasa)
        }


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
       when(item.itemId){
           R.id.popa_antonela_nav_acasa->supportFragmentManager.beginTransaction()
               .replace(R.id.main_frame_container,AcasaFragment()).commit()
           R.id.popa_antonela_nav_venit->supportFragmentManager.beginTransaction()
               .replace(R.id.main_frame_container,VenituriFragment()).commit()
           R.id.popa_antonela_nav_bugete_cheltuieli->supportFragmentManager.beginTransaction()
               .replace(R.id.main_frame_container,BugeteCheltuieliFragment()).commit()
           R.id.popa_antonela_nav_obiective->supportFragmentManager.beginTransaction()
               .replace(R.id.main_frame_container,ObiectiveFragment()).commit()
//           R.id.popa_antonela_nav_grafic->supportFragmentManager.beginTransaction()
//               .replace(R.id.main_frame_container,AcasaFragment()).commit()
           R.id.popa_antonela_nav_logout->{
               Firebase.auth.signOut()
               val intent = Intent(this, LogInActivity::class.java)
               startActivity(intent)
           }
       }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            onBackPressedDispatcher.onBackPressed()
        }
    }
    override fun onResume() {
        super.onResume()

        // Resetați valoarea depășirii bugetului în SharedPreferences
        val sharedPreferences = getSharedPreferences("BudgetExceeded", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isBudgetExceeded", false)
        editor.apply()
    }

}