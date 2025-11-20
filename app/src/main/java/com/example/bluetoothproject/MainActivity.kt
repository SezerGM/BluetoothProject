package com.example.bluetoothproject

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bluetoothproject.utils.BluetoothHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity(
): AppCompatActivity() {


    val permissionLaucnher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        {

        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()
        val blHandler by lazy { BluetoothHandler(this) }

        if(bluetoothAdapter == null){
            Log.d("Bluetooth","Device not supported bluetooth")
            Toast.makeText(this, "Device not supported bluetooth", Toast.LENGTH_SHORT).show()
        }
        enableBluetoothPermission()
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED)
            {
                blHandler.getPaired()

            }
    }


    fun enableBluetoothPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            permissionLaucnher.launch(arrayOf(android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_CONNECT))
        }
    }


}