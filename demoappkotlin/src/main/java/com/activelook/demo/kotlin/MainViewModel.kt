package com.activelook.demo.kotlin

import android.app.Application
import androidx.core.util.Consumer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.activelook.activelooksdk.DiscoveredGlasses
import com.activelook.activelooksdk.Glasses
import com.activelook.activelooksdk.Sdk
import com.activelook.activelooksdk.types.FlowControlStatus
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var alSdk: Sdk? = null
    //private val context = getApplication<Application>().applicationContext

    val scanResult : MutableLiveData<DiscoveredGlasses?> = MutableLiveData()
    private var discoveredGlasses : DiscoveredGlasses? = null
    private var connectedGlasses : Glasses? = null

    val errorMessage : MutableLiveData<String?> = MutableLiveData() // voir pour mettre un state de connection

    val message : MutableLiveData<String?> = MutableLiveData()

    init {
        alSdk = Sdk.init(getApplication<Application>().applicationContext)
    }

    fun scan() {
        viewModelScope.launch {
            alSdk?.startScan(Consumer { glasses ->
                scanResult.postValue(glasses)
                discoveredGlasses = glasses
                alSdk?.stopScan()
            })
        }
    }

    fun connect() {
        discoveredGlasses?.connect(
            { glasses ->
                //onconnected
                message.postValue("Success")

                 connectedGlasses.setOnDisconnected { glasses ->
                     glasses.disconnect()
                 }
                connectedGlasses?.subscribeToFlowControlNotifications { status ->
                    message.postValue(String.format("Flow control: %s", status.name))
                }

                connectedGlasses?.subscribeToSensorInterfaceNotifications {
                    message.postValue("SensorInterface")
                }
            },
            { discoveredGlasses ->
                //onconnection Fail
                errorMessage.postValue("Connection Fail")
                this.discoveredGlasses = null
                connectedGlasses = null
            },
            { glasses ->
                // on disconnect
                errorMessage.postValue("Disconnected")
                discoveredGlasses = null
                connectedGlasses = null
                glasses.disconnect()
            }
        ) ?:run {
            errorMessage.postValue("No Glasse Connected")
        }
    }
}