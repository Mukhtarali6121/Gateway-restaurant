package com.example.gatewayrestaurant.Activity

import com.example.gatewayrestaurant.Class.BaseActivity
import com.google.android.material.navigation.NavigationView
import com.example.gatewayrestaurant.Adapter.myadapter
import com.example.gatewayrestaurant.Adapter.offerAdapter
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import androidx.annotation.RequiresApi
import android.os.Build
import android.os.Bundle
import com.example.gatewayrestaurant.R
import android.app.ProgressDialog
import android.text.Html
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.example.gatewayrestaurant.model.model
import com.google.firebase.database.FirebaseDatabase
import com.example.gatewayrestaurant.model.model2
import androidx.core.view.GravityCompat
import android.net.Uri
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.gatewayrestaurant.databinding.ActivityMainBinding

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mBinding: ActivityMainBinding
    //recycler  view with database
    private var adapter: myadapter? = null
    private var offerAdapter: offerAdapter? = null
    private var user: FirebaseUser? = null

    //Drawer Menu
    private var mToggle: ActionBarDrawerToggle? = null
    private var fAuth: FirebaseAuth? = null
    private var menu: Menu? = null
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        user = FirebaseAuth.getInstance().currentUser
        val dialog = ProgressDialog(this, R.style.AppCompatAlertDialogStyle)
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        //        dialog.setTitle("Loading");
        dialog.setMessage("Saving. Please wait...!")
        dialog.isIndeterminate = true
        dialog.setCanceledOnTouchOutside(false)
        init()
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun init() {

        //menu hooks
        mToggle = ActionBarDrawerToggle(this, mBinding.drawerLayout, R.string.Open, R.string.Close)
        mBinding.drawerLayout.addDrawerListener(mToggle!!)
        mToggle!!.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        fAuth = FirebaseAuth.getInstance()
        menu = mBinding.navigationMenu.getMenu()
        if (fAuth!!.currentUser != null) {
            menu!!.findItem(R.id.nav_login).isVisible = false
            menu!!.findItem(R.id.nav_logout).isVisible = true
            menu!!.findItem(R.id.nav_profile).isVisible = true
        } else {
            menu!!.findItem(R.id.nav_login).isVisible = true
            menu!!.findItem(R.id.nav_logout).isVisible = false
            menu!!.findItem(R.id.nav_profile).isVisible = false
        }


        //text color change in action bar
        supportActionBar!!.setTitle(Html.fromHtml("<font color=\"black\">" + getString(R.string.app_name) + "</font>"))
        //toggle color change in action bar
        mToggle!!.drawerArrowDrawable.color = getColor(R.color.colorAccent)

        //recycler
        mBinding.recview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvOffer.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        firebasedata()
        firebaseOfferData()
        navigationDrawer()
        //        nointernet();
        mBinding.BtnMenu.setOnClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, TabLayoutActivity::class.java)
            startActivity(intent)
        })
        mBinding.btnGoogleMap.setOnClickListener(View.OnClickListener {
            val uri = "http://maps.google.com/maps?q=loc:" + 19.146316 + "," + 72.834948
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            intent.setPackage("com.google.android.apps.maps")
            startActivity(intent)
        })
    }

    //calling firebase data
    fun firebasedata() {
        val options = FirebaseRecyclerOptions.Builder<model>()
            .setQuery(FirebaseDatabase.getInstance().reference.child("menu"), model::class.java)
            .build()
        adapter = myadapter(options)
        mBinding.recview.adapter = adapter
    }

    fun firebaseOfferData() {
        val options1 = FirebaseRecyclerOptions.Builder<model2>()
            .setQuery(
                FirebaseDatabase.getInstance().reference.child("offerItem"),
                model2::class.java
            )
            .build()
        offerAdapter = offerAdapter(options1, this)
        mBinding.rvOffer!!.adapter = offerAdapter
    }

    //Navigation drawer
    private fun navigationDrawer() {
        //to interact with selected navigation drawer
        mBinding.navigationMenu!!.bringToFront()
        //this is to select a navigation drawer
        mBinding.navigationMenu!!.setNavigationItemSelectedListener(this)
        mBinding.navigationMenu!!.setCheckedItem(R.id.nav_home)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    //By pressing back button navigation drawer will be closed
    override fun onBackPressed() {
        if (mBinding.drawerLayout.isDrawerVisible(GravityCompat.START)) mBinding.drawerLayout.closeDrawer(
            GravityCompat.START
        ) else {
            super.onBackPressed()
        }
    }

    // Navigation Drawer onclick
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_cart -> if (user != null) {
//                val intent5 = Intent(this, CartActivity::class.java)
//                startActivity(intent5)
            } else {
                // if user is not logged in
                val warning = AlertDialog.Builder(this)
                    .setTitle("Login!")
                    .setMessage("Please login to add item in cart.")
                    .setPositiveButton("Login") { dialogInterface, i ->
                        startActivity(
                            Intent(
                                this@MainActivity,
                                LoginActivity::class.java
                            )
                        )
                    }
                    .setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
                warning.show()
            }
            R.id.nav_setting -> {
                val intent1 = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent1)
                /*if (user != null) {
                    startActivity(Intent(applicationContext, Profile::class.java))
                    finish()
                } else {
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                    finish()
                }*/
            }
            R.id.nav_login -> {
                val intent2 = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent2)
            }
            R.id.about_us -> startActivity(Intent(applicationContext, AboutUsActivity::class.java))
            R.id.nav_logout -> {
                val alertDialog = AlertDialog.Builder(this@MainActivity).create()
                alertDialog.setTitle(getString(R.string.app_name))
                alertDialog.setMessage(getString(R.string.warningLogout))
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { dialog, which ->
                    logOutUser()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    showShortToast("You Have Successfully Logged Out!")
                }
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEGATIVE,
                    "Cancel"
                ) { dialog, which -> dialog.cancel() }
                alertDialog.show()
            }
            R.id.nav_profile -> {
                val intent3 = Intent(this@MainActivity, ContactsContract.Profile::class.java)
                startActivity(intent3)
            }
        }
        return true
    }

    private fun logOutUser() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(applicationContext, MainActivity::class.java))
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
    }


    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
        offerAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
        offerAdapter!!.stopListening()
    }
}