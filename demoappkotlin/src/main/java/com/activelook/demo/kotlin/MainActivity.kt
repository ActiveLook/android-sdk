package com.activelook.demo.kotlin

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.activelook.demo.kotlin.databinding.ActivityDebugAdapterItemBinding
import com.activelook.demo.kotlin.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var viewModel: MainViewModel? = null

    private var adapter: DebugAdapter? = null
    private var logMessages = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manageButtonsListener()
        managePermissions()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        observeSdkLogs()

        adapter = DebugAdapter()
        binding.rvCommand.layoutManager = LinearLayoutManager(this)
        binding.rvCommand.adapter = adapter
//        adapter.submitList(viewModel.getCommandList())

    }

    private fun observeSdkLogs() {
        viewModel?.scanResult?.observe(this) { glasse ->
            Timber.e("scanResult Glasse : %s", glasse?.name ?: "no name")
            viewModel?.connect()
        }

        viewModel?.errorMessage?.observe(this) {
            Timber.e(it)
            Snackbar.make(binding.btnConnect, "ERROR : $it", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        viewModel?.message?.observe(this) {
            Timber.e(it)
            Snackbar.make(binding.btnDisconnect, "message : $it", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        viewModel?.connected?.observe(this) {
            if (it) {
                viewModel?.observeLogs()?.observe(this) { message ->
                    logMessages.add(message)
                    adapter?.addItems(logMessages)
                }
            }
        }

        viewModel?.connectedGlassesLogMessage?.observe(this) { message ->
            logMessages.add(message)
            adapter?.addItems(logMessages)
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
        binding.btnConnect.setOnClickListener { view ->
            Snackbar.make(view, "Connecting", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
            viewModel?.scan()
        }

        binding.btnDisconnect.setOnClickListener { view ->
            Snackbar.make(view, "Disconnecting", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
            viewModel?.stopScan()
            viewModel?.disconnect()
        }

        binding.btnClear.setOnClickListener {
            logMessages.clear()
            adapter?.submitList(emptyList())
            adapter?.notifyDataSetChanged()
        }

        binding.btnShare.setOnClickListener {
            val joined: String = TextUtils.join("\r\n",  logMessages)

            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.putExtra(Intent.EXTRA_TEXT, joined)
            sendIntent.type = "text/html"
            startActivity(Intent.createChooser(sendIntent, "Logs"))
        }

        binding.btnSendData.setOnClickListener {
            // send unique command
            // marke it random ?
        }

        binding.btnSendAutoData.setOnClickListener {
            // send automatique script
            // stop if started
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
}

class DebugAdapter : ListAdapter<String, DebugAdapter.DebugViewHolder>(DIFF_CALLBACK) {
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DebugViewHolder(
        ActivityDebugAdapterItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: DebugViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }

    fun addItems(lines: List<String>) {
        this.submitList(lines)
        this.notifyDataSetChanged()
    }

    inner class DebugViewHolder(val binding: ActivityDebugAdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) = with(itemView) {
            binding.tvCommand.text = item
        }
    }
}