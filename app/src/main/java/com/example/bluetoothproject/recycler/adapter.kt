package com.example.bluetoothproject.recycler

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bluetoothproject.databinding.BlDeviceBinding
import com.example.bluetoothproject.utils.BluetoothHandler
import kotlinx.coroutines.flow.Flow

class AdapterMain(
    private var devices: List<BluetoothDevice> = emptyList(),
): RecyclerView.Adapter<AdapterMain.ViewHolderMain>() {

    inner class ViewHolderMain(
        private val binding: BlDeviceBinding
    ) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("MissingPermission")
        fun bind(device: BluetoothDevice) {

            binding.deviceName.text = device.name
            binding.deviceMac.text = device.address

            binding.root.setOnClickListener {
                Log.d("Recycler", "User tap on ${device.name}")
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolderMain, position: Int) {
        holder.bind(devices[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMain {
        val binding = BlDeviceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolderMain(binding)
    }

    override fun getItemCount(): Int = devices.size

    fun updateList(newList: List<BluetoothDevice>) {
        devices = newList
        notifyDataSetChanged()
        Log.d("Bluetooth", "Device adapter updated")
    }
}