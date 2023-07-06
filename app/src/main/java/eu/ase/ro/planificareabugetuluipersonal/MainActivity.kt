package eu.ase.ro.planificareabugetuluipersonal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout=findViewById<DrawerLayout>(R.id.drawer_layout)

        val toolbar=findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        val navigationView=findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

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
           R.id.popa_antonela_nav_buget_alocat->supportFragmentManager.beginTransaction()
               .replace(R.id.main_frame_container,FragmentBugete()).commit()
           R.id.popa_antonela_nav_cheltuieli->supportFragmentManager.beginTransaction()
               .replace(R.id.main_frame_container,CheltuieliFragment()).commit()
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



}