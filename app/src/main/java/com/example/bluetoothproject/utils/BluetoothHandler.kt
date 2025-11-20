package com.example.bluetoothproject.utils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import java.security.Permissions
import javax.inject.Inject

class BluetoothHandler @Inject constructor(
    private val context: Context
) {

    fun getPaired(){
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