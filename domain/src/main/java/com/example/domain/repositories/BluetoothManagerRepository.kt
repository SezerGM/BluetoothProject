package com.example.domain.repositories

interface BluetoothManagerRepository{
    suspend fun checkBluetoothConnection()
    suspend fun connect()
    suspend fun sendImage()
    suspend fun dissconnect()
}
