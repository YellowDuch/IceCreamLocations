package com.example.findmygolda

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.findmygolda.alerts.AlertManager
import com.example.findmygolda.databinding.ActivityMainBinding
import com.example.findmygolda.location.LocationAdapter
import com.example.findmygolda.network.BranchManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

class MainActivity : AppCompatActivity(), PermissionsListener {
    lateinit var permissionManager: PermissionsManager
    lateinit var binding: ActivityMainBinding
    lateinit var branchManager :BranchManager
    lateinit var alerManager : AlertManager
    lateinit var locationAdapter: LocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askForPermissions()
        setupOptionMenu()
        createBottomNavigation()
    }

    private fun createBottomNavigation() {
        val bottomNavigation = binding.bottomNavigation
        val onBottomNavigationSelect =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.settingsFragment -> {
                        onNavDestinationSelected(
                            item,
                            Navigation.findNavController(this, R.id.myNavHostFragment)
                        )
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.alertsFragment -> {
                        onNavDestinationSelected(
                            item,
                            Navigation.findNavController(this, R.id.myNavHostFragment)
                        )
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.branchesFragment -> {
                        onNavDestinationSelected(
                            item,
                            Navigation.findNavController(this, R.id.myNavHostFragment)
                        )
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.mapFragment -> {
                        onNavDestinationSelected(
                            item,
                            Navigation.findNavController(this, R.id.myNavHostFragment)
                        )
                        return@OnNavigationItemSelectedListener true
                    }
                    else -> {
                        return@OnNavigationItemSelectedListener false
                    }
                }
            }
        bottomNavigation.setupWithNavController(
            Navigation.findNavController(
                this,
                R.id.myNavHostFragment
            )
        )
        bottomNavigation.setOnNavigationItemSelectedListener(onBottomNavigationSelect)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(applicationContext,
            getString(R.string.appNeedLocationPermission),
            Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            askForPermissions()
        } else {
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.golda_check -> {
                Toast.makeText(getApplicationContext(),"golda layer",Toast.LENGTH_SHORT).show()
                item.isChecked = !item.isChecked
                return true
            }
            R.id.anita_check -> {
                Toast.makeText(getApplicationContext(),"anita layer",Toast.LENGTH_SHORT).show()
                item.isChecked = !item.isChecked
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.top_map_layer_settings,menu)

        return true
    }

    private fun setupOptionMenu() {
        val toolbar = binding.toolbar
        toolbar.title = ""
        setSupportActionBar(toolbar)
    }

     fun isLocationEnabled(mContext: Context): Boolean {
        val lm = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    private fun showLocationIsDisabledAlert() {
        alert(getString(R.string.cannotShowYourPositionMessage)) {
            yesButton {
                finish()
                moveTaskToBack(true)
                android.os.Process.killProcess(android.os.Process.myPid())
                System.exit(1)
            }
            neutralPressed(getString(R.string.settings)) {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }.show()
    }

    private fun initGlobalVariables(){
        branchManager = BranchManager(application)
        alerManager = AlertManager(application, branchManager)
        locationAdapter = LocationAdapter(application)
        locationAdapter.subscribeToLocationChangeEvent(alerManager)
    }

    private fun askForPermissions(){
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            if (!isLocationEnabled(applicationContext)) {
                showLocationIsDisabledAlert()
            } else {
                initGlobalVariables()
                binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
            }
        } else {
            permissionManager = PermissionsManager(this)
            permissionManager.requestLocationPermissions(this)
        }
    }

}


