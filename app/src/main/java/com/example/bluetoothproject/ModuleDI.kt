package com.example.bluetoothproject

import android.bluetooth.BluetoothManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuleDI {
    @Provides
    fun getManager(application: MainApplication): BluetoothManager{
        return application.getSystemService(android.content.Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

}