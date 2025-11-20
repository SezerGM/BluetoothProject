package com.example.bluetoothproject.utils

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
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import java.security.Permissions
import javax.inject.Inject

class BluetoothHandler (
    private val context: Context
) {
    private var discoveryReceiver: BroadcastReceiver? = null
    val handler = Handler(Looper.getMainLooper())

    private val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            when(action){
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    Log.d("Bluetooth", "Device found asdasd: $device")
                }
            }
        }
    }

    fun getPaired(){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
            val manager = ContextCompat.getSystemService(context, BluetoothManager::class.java) as BluetoothManager
            val adapter: BluetoothAdapter? = manager.adapter
            val paired = adapter?.bondedDevices
            val pairedDevices = paired?.map {
                    paired ->
                Pair(paired.name, paired.address)}
            Log.d("Bluetooth", "Paired devices: $pairedDevices")
            println("Paired devices: $pairedDevices")
            println("fasdf")
        }

    }

    fun searchDevices() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Bluetooth", "No required permissions")
            return
        }

        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = manager.adapter

        if (adapter == null) {
            Log.e("Bluetooth", "Bluetooth is not supported")
            return
        }

        if (!adapter.isEnabled) {
            Log.e("Bluetooth", "Bluetooth is not enabled")
            return
        }

        stopDiscovery()

        discoveryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
                    val action = intent?.action
                    when (action) {
                        BluetoothDevice.ACTION_FOUND -> {
                            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                            if (device != null) {
                                val name = device.name ?: "Unknown"
                                val address = device.address
                                Log.d("Bluetooth", "Device found: $name ($address)")
                            }
                        }
                        BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                            Log.d("Bluetooth", "Discovery finished")
                        }
                    }
                }

            }
        }

        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }

        ContextCompat.registerReceiver(context, discoveryReceiver!!, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        Log.d("Bluetooth", "Starting discovery...")
        adapter.startDiscovery()
        handler.postDelayed({
            stopDiscovery()
            Log.d("Bluetooth", "Discovery stopped by timer")
        }, 15_000)
    }

    fun stopDiscovery() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){
            discoveryReceiver?.let { receiver ->
                try {
                    context.unregisterReceiver(receiver)
                } catch (e: IllegalArgumentException) {
                }
                discoveryReceiver = null
            }

            val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val adapter = manager.adapter
            adapter?.cancelDiscovery()
        }
        }




    }


