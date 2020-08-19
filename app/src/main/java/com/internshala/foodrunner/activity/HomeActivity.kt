package com.internshala.foodrunner.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.inflate
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.internshala.foodrunner.R
import com.internshala.foodrunner.fragment.*
import kotlinx.android.synthetic.main.drawer_header.view.*
import org.w3c.dom.Text

class HomeActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView

    var previousMenuItem: MenuItem? = null

    companion object{
        lateinit var sharedPreferences: SharedPreferences
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)
        setUpToolbar()
        openHome()

        sharedPreferences = getSharedPreferences(getString(R.string.preference_login), Context.MODE_PRIVATE)
        val user_id = sharedPreferences.getString("user_id","0").toString()
        val user_name = sharedPreferences.getString("user_name","Sudhir Daga").toString()
        val user_email = sharedPreferences.getString("user_email","johndoe@emaple.com").toString()
        val user_mobile = sharedPreferences.getString("user_mobile","+91-1115555555").toString()
        val user_address = sharedPreferences.getString("user_address","Gurugram").toString()


        val actionBarDrawerToggle = ActionBarDrawerToggle(this@HomeActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        val convertView = LayoutInflater.from(this@HomeActivity).inflate(R.layout.drawer_header,null)
        val userName: TextView = convertView.findViewById(R.id.txtName)
        val userPhone: TextView = convertView.findViewById(R.id.txtPhoneNumber)

        userName.text = user_name
        userPhone.text = "+91-${user_mobile}"

        navigationView.addHeaderView(convertView)

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId)
            {
                R.id.homeApp -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }

                R.id.myProfile -> {

                    val bundle = Bundle()
                    bundle.putString("user_name",user_id)
                    bundle.putString("user_mobile",user_mobile)
                    bundle.putString("user_email",user_email)
                    bundle.putString("user_address",user_address)
                    val fragment = MyProfileFragment()
                    fragment.arguments=bundle
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            MyProfileFragment()
                        )
                        .commit()
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                }

                R.id.favouriteRestraunts -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FavouriteRestaurantsFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }

                R.id.orderHistory -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            OrderHistoryFragment()
                        )
                        .commit()
                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }

                R.id.faq -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FaqFragment()
                        )
                        .commit()
                    supportActionBar?.title = "FAQs"
                    drawerLayout.closeDrawers()
                }

                R.id.logOut -> {
                    val dialog = AlertDialog.Builder(this@HomeActivity as Context)
                    dialog.setTitle("Conformation")
                    dialog.setMessage("Are you sure you want to Logout?")
                    dialog.setPositiveButton("Yes"){text, listener ->
                        val intent = Intent(this@HomeActivity,LoginActivity::class.java)
                        startActivity(intent)
                        sharedPreferences.edit().putBoolean("isLoggedIn",false).apply()
                        finish()
                    }

                    dialog.setNegativeButton("No") {text, listener ->

                    }
                    dialog.create()
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun setUpToolbar()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if(id==android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    fun openHome()
    {
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame,fragment)
        transaction.commit()
        navigationView.setCheckedItem(R.id.homeApp)
        supportActionBar?.title = "All Restaurants"
    }

    fun openMyProfileFragment(){

        val bundle = Bundle()

    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)

        when(frag)
        {
            !is HomeFragment -> openHome()

            else->super.onBackPressed()
        }
    }

}