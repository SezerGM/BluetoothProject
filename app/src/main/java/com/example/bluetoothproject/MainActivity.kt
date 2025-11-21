package com.example.bluetoothproject

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetoothproject.databinding.ActivityMainBinding
import com.example.bluetoothproject.recycler.AdapterMain
import com.example.bluetoothproject.recycler.DeviceModel
import com.example.bluetoothproject.ui.viewmodels.MainScreenViewModel
import com.example.bluetoothproject.utils.BluetoothHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainScreenViewModel
    private lateinit var adapter: AdapterMain
    private val bondReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED){
                val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val bondState = intent?.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)
                when(bondState){
                    BluetoothDevice.BOND_BONDED ->{

                    }
                }
            }

        }
    }







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainScreenViewModel::class.java]

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            Log.d("Bluetooth", "Device not supported bluetooth")
            Toast.makeText(this, "Device not supported bluetooth", Toast.LENGTH_SHORT).show()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "Please enable Bluetooth", Toast.LENGTH_SHORT).show()
            return
        }
        enableBluetoothPermission()
        registerReceiver(bondReceiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
        val blManager = BluetoothHandler(this, viewModel)
        binding.ButtonBl.setOnClickListener {
            blManager.searchBleDevices()

        }

        binding.ButtonBlStop.setOnClickListener {
            blManager.bleStopSearching()
        }
        adapter = AdapterMain()
        binding.blRecycler.adapter = adapter
        binding.blRecycler.layoutManager = LinearLayoutManager(this)
        viewModel.devices.observe(this) { devices ->
            Log.d("OBSERVER", "LiveData observer triggered! List size: ${devices.size}")
            adapter.updateList(devices)
        }
    }

    private fun enableBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT))
        }

    }



}