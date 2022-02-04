package com.activelook.demo.kotlin

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.activelook.demo.kotlin.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var viewModel : MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manageButtonsListener()
        managePermissions()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        observeSdkLogs()

    }

    private fun observeSdkLogs() {
        viewModel?.scanResult?.observe(this) { glasse ->
            Timber.e("scanResult Glasse : %s", glasse?.name ?: "no name")
            viewModel?.connect()
        }

        viewModel?.errorMessage?.observe(this) {
            Timber.e(it)
            Snackbar.make(binding.fabConnect, "ERROR : $it", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        viewModel?.message?.observe(this) {
            Timber.e(it)
            Snackbar.make(binding.fabConnect, "message : $it" , Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun managePermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_CONNECT
            ), 0
        )
    }

    private fun manageButtonsListener() {
        binding.fabConnect.setOnClickListener { view ->
            Snackbar.make(view, "Connect", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
            viewModel?.scan()
        }

        binding.fabDisconnect.setOnClickListener { view ->
            Snackbar.make(view, "Disconnect", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
            viewModel?.stopScan ()
            viewModel?.disconnect()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}