package com.activelook.demo.kotlin

import android.app.Application
import androidx.core.util.Consumer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.activelook.activelooksdk.DiscoveredGlasses
import com.activelook.activelooksdk.Glasses
import com.activelook.activelooksdk.Sdk
import com.activelook.activelooksdk.types.GlassesUpdate
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var alSdk: Sdk? = null

    val scanResult : MutableLiveData<DiscoveredGlasses?> = MutableLiveData()
    private var discoveredGlasses : DiscoveredGlasses? = null
    private var connectedGlasses : Glasses? = null

    // voir pour mettre un state de connection
    val errorMessage : MutableLiveData<String?> = MutableLiveData()

    val message : MutableLiveData<String?> = MutableLiveData()

    var logMessage : LiveData<String> = MutableLiveData()

    var connected: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        viewModelScope.launch {
            alSdk = Sdk.init(
                getApplication<Application>().applicationContext,
                "unused",
                { gu: GlassesUpdate? ->
                    Timber.e("onUpdateStart ", String.format(" %s", gu))
                },
                { gu: GlassesUpdate? ->
                    Timber.e("onUpdateProgress: %s", String.format(" %s", gu))
                },
                { gu: GlassesUpdate? ->
                    Timber.e("onUpdateSuccess : %s", String.format(" %s", gu))
                }
            ) { gu: GlassesUpdate? ->
                Timber.e("onUpdateError   : %s", String.format(" %s", gu))
            }
        }
    }

    fun stopScan() {
        viewModelScope.launch {
            alSdk?.stopScan()
            scanResult.postValue(null)
            discoveredGlasses = null
        }
    }

    fun disconnect() {
        connectedGlasses?.disconnect()
    }

    fun scan() {
        viewModelScope.launch {
            alSdk?.startScan(Consumer { glasses ->
                alSdk?.stopScan()
                scanResult.postValue(glasses)
                discoveredGlasses = glasses
            })
        }
    }

    fun connect() {
        discoveredGlasses?.connect(
            { glasses ->
                //onconnected
                message.postValue("Connected Success")

                logMessage = glasses.messageLogs

                connectedGlasses = glasses
                 connectedGlasses?.setOnDisconnected { glasses ->
                     message.postValue("OnDisconnect")
                     glasses.disconnect()
                 }

                connectedGlasses?.subscribeToFlowControlNotifications { status ->
                    message.postValue(String.format("Flow control: %s", status.name))
                }

                connectedGlasses?.subscribeToSensorInterfaceNotifications {
                    message.postValue("SensorInterface")
                }

                connected.postValue(true)

            },
            { discoveredGlasses ->
                //onconnection Fail
                errorMessage.postValue("Connection Fail")
                this.discoveredGlasses = null
                connectedGlasses = null
                connected.postValue(false)
            },
            { glasses ->
                // on disconnect
                errorMessage.postValue("Disconnected")
                discoveredGlasses = null
                connectedGlasses = null
                glasses.disconnect()
                connected.postValue(false)
            }
        ) ?:run {
            errorMessage.postValue("No Glasse Connected")
        }
    }

    fun observeLogs() :LiveData<String>? {
        return connectedGlasses?.messageLogs
    }
}