package com.example.bluetoothproject

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
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
import com.example.bluetoothproject.utils.BluetoothHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var devices: String

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    private var isReceiverRegistered = false


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("Bluetooth", "onReceive called with action: ${intent?.action}")
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
                val action = intent?.action
                when (action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? = intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        if (device != null) {
                            val name = device.name ?: "Unknown"
                            Log.d("Bluetooth", "Bluetooth found device: $name (${device.address})")
                        }
                        devices = device!!.name
                    }
                }
            }
        }
    }
    val scanCalBack = object: ScanCallback(){

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
                val device = result?.device?.name
                if(device != null){
                    Log.d("Bluetooth", "Bluetooth found device: $device")
                }
            }

        }

        override fun onScanFailed(errorCode: Int) {
            Log.d("Bluetooth", "Ble scanner failture")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val button: Button = findViewById<Button>(R.id.ButtonBl)
        val buttonStop: Button = findViewById<Button>(R.id.ButtonBlStop)

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
        button.setOnClickListener {
            searchDiveceBle()
        }
        buttonStop.setOnClickListener {
            stopBle()
        }

        enableBluetoothPermission()

    }

    private fun enableBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT))
        }

    }

    private fun searchDevice() {
        if (isReceiverRegistered) {
            Log.w("Bluetooth", "Receiver already registered")
            return
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Bluetooth", "Permissions not granted")
            return
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        isReceiverRegistered = true

        Log.d("Bluetooth", "Starting discovery...")
        bluetoothAdapter.startDiscovery()
    }

    private fun searchDiveceBle(){
        val bleScanner = bluetoothAdapter.bluetoothLeScanner

        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
            Log.d("Bluetooth", "Ble scan started")
            bleScanner.startScan(scanCalBack)
        }

    }

    fun stopBle(){
        val bleScanner = bluetoothAdapter.bluetoothLeScanner
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
            Log.d("Bluetooth", "Ble scan stoped")
            bleScanner.stopScan(scanCalBack)
        }
    }



    override fun onDestroy() {
        if (isReceiverRegistered) {
            try {
                unregisterReceiver(receiver)
                isReceiverRegistered = false
            } catch (e: IllegalArgumentException) {
                Log.e("Bluetooth", "Receiver was not registered", e)
            }
        }
        super.onDestroy()
    }


}