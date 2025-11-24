package com.example.bluetoothproject

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
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.bluetoothproject.databinding.ActivityMainBinding
import com.example.bluetoothproject.recycler.AdapterMain
import com.example.bluetoothproject.ui.viewmodels.MainScreenViewModel
import com.example.bluetoothproject.utils.BluetoothHandler
import com.mht.print.sdk.PrinterConstants
import com.mht.print.sdk.PrinterInstance
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.URI


class MainActivity : AppCompatActivity() {

    private var mPrinter: PrinterInstance? = null
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainScreenViewModel
    private lateinit var adapter: AdapterMain
    private var printerInst: BluetoothDevice? = null
    private lateinit var bondedDevices: Set<BluetoothDevice>
    private var galeryBitmap: Bitmap? = null

    private val bondReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED){
                val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val bondState = intent?.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)
                when(bondState){
                    BluetoothDevice.BOND_BONDED ->{
                        Log.d("Bond", "Device bonded $device")
                        connect(device)
                    }
                }
            }

        }
    }
    val scanCalBack = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            if(ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this@MainActivity,
                    Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
            ){
                val devices = result?.device
                if(devices!!.name != null){
                    viewModel.addDevice(device = devices)
                }
                Log.d("Bluetooth", "BluetoothBle found device: ${devices?.address}")
            }
        }
    }

    private val getImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){
        uri : Uri? ->
        try {
            uri?.let {
                galeryBitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
            }
        } catch (e: Exception) {
            Log.d("Select", "Select image failed by $e")
            galeryBitmap = null
        }

    }

    private val mHandler = object : android.os.Handler(Looper.myLooper()!!){
        override fun dispatchMessage(msg: Message) {
            super.dispatchMessage(msg)
            Log.d("Printer", "$msg")
            when(msg.what){
                PrinterConstants.Connect.SUCCESS -> {
                    Log.d("Printer", "Connect SUCCESS")
                }
                PrinterConstants.Connect.FAILED -> {
                    Log.d("Printer", "Connect FAILED")
                }
                PrinterConstants.Connect.CLOSED -> {
                    Log.d("Printer", "Connect CLOSED")
                }

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.setPadding(24, 80 ,24 ,24)
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

        binding.printImage.setOnClickListener {
            printTest()
        }
        binding.testPrint.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                    Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
            ){
                bondedDevices = bluetoothAdapter.bondedDevices
            }
            if(bondedDevices.isNotEmpty()){
                connect(printerInst)
            }else{
                searchBleDevices()
            }
        }
        binding.pickImage.setOnClickListener {
            getImage.launch("image/*")
        }
        val blHander = BluetoothHandler(this)
        adapter = AdapterMain(){
            device ->
            val blDevice = bluetoothAdapter.getRemoteDevice(device.address)
            blHander.bondDevice(blDevice)
        }
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
    fun searchBleDevices(){
        val manager = ContextCompat.getSystemService(this, BluetoothManager::class.java) as BluetoothManager
        val adapter: BluetoothAdapter? = manager.adapter
        val bleScanner = adapter?.bluetoothLeScanner
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){

            Log.d("Bluetooth", "Ble start searching")
            bleScanner?.startScan(scanCalBack)
        }else{
            Log.d("Bluetooth", "Permissions not granted")
        }
    }

    fun bleStopSearching(){
        val manager = ContextCompat.getSystemService(this, BluetoothManager::class.java) as BluetoothManager
        val adapter: BluetoothAdapter? = manager.adapter
        val bleScanner = adapter?.bluetoothLeScanner
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED){

            Log.d("Bluetooth", "Ble start searching")
            bleScanner?.stopScan(scanCalBack)
        }
    }
    @SuppressLint("MissingPermission")
    fun connect(printer: BluetoothDevice?){
        val devices = bondedDevices.filter { it.name == "XP-365B" }.toMutableList()
        Log.d("Printer", "Has a bonded devices $devices")
        if(devices.isNotEmpty()){
            mPrinter = PrinterInstance(this, devices[0], mHandler)
            mPrinter!!.openConnection()
        }else{
            Toast.makeText(this, "No paired devices!", Toast.LENGTH_SHORT).show()
        }


    }
    fun printTest(){
        lifecycleScope.launch {
            try {
                var nbit = galeryBitmap?.scale(460, 325, false)
//            mPrinter!!.printText("Самогон Стешковский")
                mPrinter!!.printLabelImage(58, 40, nbit)
                mPrinter!!.cutPaper()
            }catch (e: Exception){
                Log.d("Printer", "Printing failed by $e")
            }
        }

    }
}