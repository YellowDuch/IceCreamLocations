package com.example.findmygolda

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.findmygolda.Constants.Companion.LOCATION_SERVICE_NOT_ENABLE
import com.example.findmygolda.Constants.Companion.PERMISSIONS_GRANTED_AND_LOCATION_SERVICE_ENABLE
import com.example.findmygolda.Constants.Companion.PERMISSIONS_NOT_GRANTED
import com.example.findmygolda.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

class MainActivity : AppCompatActivity(), PermissionsListener {
    private lateinit var permissionManager: PermissionsManager
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when(isLocationServicesAndPermissionsGranted()){
            LOCATION_SERVICE_NOT_ENABLE -> popupLocationServicesDisabledAlert()
            PERMISSIONS_NOT_GRANTED -> askForPermissions()
            else -> loadHomePage()
        }
    }

    private fun createBottomNavigation() {
        val bottomNavigation = binding.bottomNavigation
        val onBottomNavigationSelect =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                onNavDestinationSelected(
                    item,
                    Navigation.findNavController(this, R.id.myNavHostFragment)
                )
                return@OnNavigationItemSelectedListener true
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
            loadHomePage()
            if (!isLocationServiceEnabled(applicationContext)){
                popupLocationServicesDisabledAlert()
            }
        } else {
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setupActionToolbar() {
        val toolbar = binding.toolbar
        toolbar.title = "" // Use for remove the default value - the name of the fragment
        setSupportActionBar(toolbar)
    }

     private fun isLocationServiceEnabled(mContext: Context): Boolean {
        val locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    private fun popupLocationServicesDisabledAlert() {
        alert(getString(R.string.cannotShowYourPositionMessage)) {
            yesButton {
                finish()
                moveTaskToBack(true)
                android.os.Process.killProcess(android.os.Process.myPid())
                System.exit(1)
            }
            neutralPressed(getString(R.string.settings)) {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                loadHomePage()
            }
        }.show()
    }

    private fun askForPermissions() {
        permissionManager = PermissionsManager(this)
        permissionManager.requestLocationPermissions(this)
    }

    private fun isLocationServicesAndPermissionsGranted(): Int{
        if (areLocationPermissionsGranted()) {
            if (!isLocationServiceEnabled(applicationContext)) {
                return LOCATION_SERVICE_NOT_ENABLE
            } else {
                return PERMISSIONS_GRANTED_AND_LOCATION_SERVICE_ENABLE
            }
        } else {
            return PERMISSIONS_NOT_GRANTED
        }
    }

    private fun areLocationPermissionsGranted():Boolean{
        return PermissionsManager.areLocationPermissionsGranted(this)
    }

    private fun loadHomePage() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupActionToolbar()
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)
        createBottomNavigation()
    }

}


