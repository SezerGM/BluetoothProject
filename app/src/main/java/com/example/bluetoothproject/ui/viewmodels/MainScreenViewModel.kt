package com.example.bluetoothproject.ui.viewmodels

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bluetoothproject.recycler.DeviceModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
class MainScreenViewModel: ViewModel() {
    private val _devices = MutableLiveData<List<BluetoothDevice>>()
    val devices: LiveData<List<BluetoothDevice>> = _devices

    @SuppressLint("MissingPermission")
    fun addDevice(device: BluetoothDevice?) {
        val newDevice = device

        val currentList = _devices.value?.toMutableList() ?: mutableListOf()
        val currentIndex = currentList.indexOfFirst { it.address == newDevice?.address }

        if (currentIndex != -1) {
            currentList[currentIndex] = newDevice!!
        } else {
            currentList.add(0, newDevice!!)
        }

        _devices.value = currentList
        Log.d("VM", "LiveData updated with ${currentList.size} devices")
    }
}
