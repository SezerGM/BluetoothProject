package com.example.bluetoothproject.utils

import android.Manifest
import android.annotation.SuppressLint
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
import com.example.bluetoothproject.ui.viewmodels.MainScreenViewModel
import java.security.Permissions
import javax.inject.Inject

class BluetoothHandler (
    private val context: Context,
    private val viewmodel: MainScreenViewModel
) {


    val scanCalBack = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context,
                    Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
            ){
                val devices = result?.device
                viewmodel.addDevice(device = devices)
                Log.d("Bluetooth", "BluetoothBle found device: ${devices?.address}")
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

    fun searchBleDevices(){
        val manager = ContextCompat.getSystemService(context, BluetoothManager::class.java) as BluetoothManager
        val adapter: BluetoothAdapter? = manager.adapter
        val bleScanner = adapter?.bluetoothLeScanner
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){

                Log.d("Bluetooth", "Ble start searching")
                bleScanner?.startScan(scanCalBack)
            }else{
                Log.d("Bluetooth", "Permissions not granted")
            }


    }

    fun bleStopSearching(){
        val manager = ContextCompat.getSystemService(context, BluetoothManager::class.java) as BluetoothManager
        val adapter: BluetoothAdapter? = manager.adapter
        val bleScanner = adapter?.bluetoothLeScanner
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){

            Log.d("Bluetooth", "Ble start searching")
            bleScanner?.stopScan(scanCalBack)
        }

    }
    @SuppressLint("MissingPermission")
    fun bondDevice(device: BluetoothDevice){
        when(device.bondState){
            BluetoothDevice.BOND_NONE -> {
                device.createBond()
            }
            BluetoothDevice.BOND_BONDED -> {
                Log.d("Bluetooth", "Device already connected")

            }
            BluetoothDevice.BOND_BONDING -> {

                Log.d("Bluetooth", "Connecting to device")

            }
        }
    }

    fun connectDevice(device: BluetoothDevice){

    }
}


