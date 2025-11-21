package com.example.bluetoothproject.recycler

data class DeviceModel(
    val name: String = "",
    val mac: String = "",
    val lastSeen: Long = System.currentTimeMillis(),
    val updateId: Long = 0
)