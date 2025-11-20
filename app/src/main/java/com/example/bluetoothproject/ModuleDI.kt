package com.example.bluetoothproject

import android.app.Application
import android.bluetooth.BluetoothManager
import android.content.Context
import com.example.domain.repositories.ContextProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


class AndroidContextProvider(private val context: Context): ContextProvider{
    override fun getContext(): Context = context
}
@Module
@InstallIn(SingletonComponent::class)
object ModuleDI {
    @Provides
    fun getManager(application: MainApplication): BluetoothManager{
        return application.getSystemService(android.content.Context.BLUETOOTH_SERVICE) as BluetoothManager
    }
    @Provides
    fun getContext(@ApplicationContext application: Application): Context{
        return application
    }


}