package com.example.data.bluetooth
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.example.domain.repositories.BluetoothManagerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothManager @Inject constructor(
    private val bluetoothManager: BluetoothManager
): BluetoothManagerRepository{
    override suspend fun checkBluetoothConnection() {
        val adapter: BluetoothAdapter? = bluetoothManager.adapter
        if(adapter == null){
            Log.d("Bluetooth", "Device is not support bluetooth")
        }
    }

    override suspend fun connect() {


    }

    override suspend fun dissconnect() {

    }

    override suspend fun sendImage() {

    }
}