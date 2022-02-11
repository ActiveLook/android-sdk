package com.activelook.demo.kotlin

import android.app.Application
import android.util.Log
import androidx.core.util.Consumer
import androidx.lifecycle.*
import com.activelook.activelooksdk.DiscoveredGlasses
import com.activelook.activelooksdk.Glasses
import com.activelook.activelooksdk.LogData
import com.activelook.activelooksdk.Sdk
import com.activelook.activelooksdk.types.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var alSdk: Sdk? = null

    val scanResult: MutableLiveData<DiscoveredGlasses?> = MutableLiveData()
    private var discoveredGlasses: DiscoveredGlasses? = null
    private var connectedGlasses: Glasses? = null


    val errorMessage: MutableLiveData<String?> = MutableLiveData()
    //used for snackbar basic info
    val message: MutableLiveData<String?> = MutableLiveData()

    //the logs received on the Glasses callback
    var connectedGlassesLogMessage: MutableLiveData<String> = MutableLiveData("Starting Logs")

    // All logs from BLE Gatt
    var allLogs = mutableListOf<LogData>()
    var logsFlow = MutableLiveData<List<LogData>>()

    init {
        viewModelScope.launch {
            alSdk = Sdk.init(
                getApplication<Application>().applicationContext,
                "unused",
                { gu: GlassesUpdate? ->
                    connectedGlassesLogMessage.postValue(
                        "onUpdateStart " + String.format(
                            " %s",
                            gu
                        )
                    )
                    Timber.e("onUpdateStart ", String.format(" %s", gu))
                },
                { gu: GlassesUpdate? ->
                    connectedGlassesLogMessage.postValue(
                        "onUpdateProgress: %s" + String.format(
                            " %s",
                            gu
                        )
                    )
                    Timber.e("onUpdateProgress: %s", String.format(" %s", gu))
                },
                { gu: GlassesUpdate? ->
                    connectedGlassesLogMessage.postValue(
                        "onUpdateSuccess : %s" + String.format(
                            " %s",
                            gu
                        )
                    )
                    Timber.e("onUpdateSuccess : %s", String.format(" %s", gu))
                }
            ) { gu: GlassesUpdate? ->
                connectedGlassesLogMessage.postValue(
                    "onUpdateError   : %s" + String.format(
                        " %s",
                        gu
                    )
                )
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
        connectedGlasses = null
        GlassesRepository.connectedGlasses = null
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

    fun getAllLogsMessages() : List<LogData> {
        return allLogs
    }

    fun connect() {
        discoveredGlasses?.connect(
            { glasses ->
                //onconnected
                message.postValue("Connected Success")

                //connectedGlassesLogMessage.postValue(glasses.messageLogs.value)

                connectedGlasses = glasses
                GlassesRepository.connectedGlasses = glasses
                connectedGlasses?.setOnDisconnected { glasses ->
                    Timber.e("DISCONNECTEDDDD")
                    connectedGlassesLogMessage.postValue("OnDisconnect")
                    message.postValue("OnDisconnect")
                    GlassesRepository.connectedGlasses = null
                    connectedGlasses = null
                    glasses.disconnect()
                }

                connectedGlasses?.subscribeToFlowControlNotifications { status ->
                    connectedGlassesLogMessage.postValue(
                        String.format(
                            "Flow control: %s",
                            status.name
                        )
                    )
                    message.postValue(String.format("Flow control: %s", status.name))
                }

                connectedGlasses?.subscribeToSensorInterfaceNotifications {
                    connectedGlassesLogMessage.postValue("SensorInterface")
                    message.postValue("SensorInterface")
                }

                connectedGlasses?.subscribeToBatteryLevelNotifications { level ->
                    connectedGlassesLogMessage.postValue(String.format("BatteryLevel: %d", level))
                    message.postValue("SensorInterface")
                }

                viewModelScope.launch(Dispatchers.Main) {
                    connectedGlasses?.messageLogs?.observeForever {
                        allLogs.add(it)
                        logsFlow.postValue(allLogs)
                    }
                }
            },
            { discoveredGlasses ->
                //onconnection Fail
                errorMessage.postValue("Connection Fail")
                this.discoveredGlasses = null
                connectedGlasses = null
                GlassesRepository.connectedGlasses = null
            },
            { glasses ->
                // on disconnect
                Timber.e("DISCONNECTEDDDD 2222222")
                errorMessage.postValue("Disconnected")
                discoveredGlasses = null
                connectedGlasses = null
                GlassesRepository.connectedGlasses = null
                glasses.disconnect()
            }
        ) ?: run {
            errorMessage.postValue("No Glasse Connected")
        }
    }

    fun runTestsConfig() {
        connectedGlasses?.also {
            val g = it
            g.cfgRead("DebugApp") { c: ConfigurationElementsInfo ->
                var message = String.format("%s", c)
                val separator = "\n\r"
                Timber.e(String.format("%s", c))
                Log.i("CONFIG DEBUG", String.format("%s", c))

                message += separator + String.format("getVersion  %d", c.version)
                Timber.e(String.format("getVersion  %d", c.version))
                Log.i("CONFIG DEBUG", String.format("getVersion  %d", c.version))

                message += separator + String.format("getNbImg    %d", c.nbImg)
                Timber.e(String.format("getNbImg    %d", c.nbImg))
                Log.i("CONFIG DEBUG", String.format("getNbImg    %d", c.nbImg))

                message += separator + String.format("getNbLayout %d", c.nbLayout)
                Timber.e(String.format("getNbLayout %d", c.nbLayout))
                Log.i("CONFIG DEBUG", String.format("getNbLayout %d", c.nbLayout))

                message += separator + String.format("getNbFont   %d", c.nbFont)
                Timber.e(String.format("getNbFont   %d", c.nbFont))
                Log.i("CONFIG DEBUG", String.format("getNbFont   %d", c.nbFont))

                message += separator + String.format("getNbPage   %d", c.nbPage)
                Timber.e(String.format("getNbPage   %d", c.nbPage))
                Log.i("CONFIG DEBUG", String.format("getNbPage   %d", c.nbPage))

                message += separator + String.format("getNbGauge  %d", c.nbGauge)
                Timber.e(String.format("getNbGauge  %d", c.nbGauge))
                Log.i("CONFIG DEBUG", String.format("getNbGauge  %d", c.nbGauge))
                connectedGlassesLogMessage.postValue(message)
            }
            g.cfgRename("DebugApp", "DebugConfig", 42)
            g.cfgRename("DebugConfig", "DebugApp", 42)
            g.cfgList { l: List<ConfigurationDescription> ->
                var message = String.format("NB %d", l.size)
                val separator = "\n\r"
                Timber.e(String.format("NB %d", l.size))
                Log.i("CFG LIST", String.format("NB %d", l.size))
                for (cfg in l) {
                    message += separator + String.format("-> %s", cfg)
                    Timber.e(String.format("-> %s", cfg))
                    Log.i("CFG LIST", String.format("-> %s", cfg))
                    g.cfgRead(
                        cfg.name
                    ) { cfgi: ConfigurationElementsInfo? ->
                        String.format("-> %s %s", cfg.name, cfgi)
                        Timber.e(String.format("-> %s %s", cfg.name, cfgi))
                        Log.i(
                            "CFG INFO",
                            String.format("-> %s %s", cfg.name, cfgi)
                        )
                    }
                }
                connectedGlassesLogMessage.postValue(message)
            }
            // g.cfgDelete("DebugApp"); Not working
            // g.cfgDeleteLessUsed(); Not working
            g.cfgGetNb { c: Int? ->
                Timber.e(String.format("Nb %d", c))
                Log.i(
                    "CFG INFO",
                    String.format("Nb %d", c)
                )
                connectedGlassesLogMessage.postValue(String.format("Nb %d", c))
            }
            g.cfgFreeSpace { fs: FreeSpace ->
                var message = String.format("%s", fs)
                val separator = "\n\r"
                Timber.e(String.format("%s", fs))
                Log.i("CFG FREE SPACE", String.format("%s", fs))

                message += separator + String.format("-> TotalSize %d", fs.totalSize)
                Timber.e(String.format("-> TotalSize %d", fs.totalSize))
                Log.i(
                    "CFG FREE SPACE",
                    String.format("-> TotalSize %d", fs.totalSize)
                )

                message += separator + String.format("-> FreeSpace %d", fs.freeSpace)
                Timber.e(String.format("-> FreeSpace %d", fs.freeSpace))
                Log.i(
                    "CFG FREE SPACE",
                    String.format("-> FreeSpace %d", fs.freeSpace)
                )

                connectedGlassesLogMessage.postValue(message)
            }
        }


    }

    fun runTestsStats() {
        connectedGlasses?.also {
            val g = it

            g.pixelCount { c: Long? ->
                Log.i(
                    "STAT",
                    String.format("Pixel count %d", c)
                )
                connectedGlassesLogMessage.postValue(String.format("Pixel count %d", c))
            }
            g.getChargingCounter { c: Long? ->
                Log.i(
                    "STAT",
                    String.format("Charging counter %d", c)
                )
                connectedGlassesLogMessage.postValue(String.format("Charging counter %d", c))
            }
            g.getChargingTime { t: Long? ->
                Log.i(
                    "STAT",
                    String.format("Charging time %d", t)
                )
                connectedGlassesLogMessage.postValue(String.format("Charging time %d", t))
            }
            g.resetChargingParam()
            g.getChargingCounter { c: Long? ->
                Log.i(
                    "STAT",
                    String.format("Charging counter %d", c)
                )
                connectedGlassesLogMessage.postValue(String.format("Charging counter %d", c))
            }
            g.getChargingTime { t: Long? ->
                Log.i(
                    "STAT",
                    String.format("Charging time %d", t)
                )
                connectedGlassesLogMessage.postValue(String.format("Charging time %d", t))
            }
        }
    }

    fun runTestsGauge() {
        connectedGlasses?.also {
            val g = it
            val id: Byte = 0x0B
            val gauge1 = GaugeInfo(
                151.toShort(),
                127.toShort(), 110, 75, 3.toShort(), 14.toShort(), true
            )
            g.gaugeSave(id, gauge1)
            var b: Byte = 0
            while (b <= 100) {
                g.gaugeDisplay(id, b) // VERY LONG TOO PROCESS
                val value = 20
                b = (b + value.toByte()).toByte()
            }
            g.gaugeList { l: List<Int> ->
                var message = String.format("GAUGE LIST NB %d", l.size)
                val separator = "\n\r"

                Log.i("GAUGE LIST", String.format("NB %d", l.size))
                for (ii in l) {
                    message += separator + String.format("-> %d", ii)
                    Log.i("GAUGE LIST", String.format("-> %d", ii))
                    g.gaugeGet(ii.toByte()) { gg: GaugeInfo ->
                        message += separator + String.format("-> get %s", gg)
                        Log.i("GAUGE LIST", String.format("-> get %s", gg))
                        message += separator + String.format("-> get (%d, %d)", gg.x, gg.y)
                        Log.i(
                            "GAUGE LIST",
                            String.format("-> get (%d, %d)", gg.x, gg.y)
                        )
                    }
                }
                connectedGlassesLogMessage.postValue(message)
                g.gaugeDeleteAll()
            }
        }
    }

    fun parseCSV() {

        viewModelScope.launch {

            val fileName = "new-app-log.csv"
            val jsonString = getApplication<Application>().applicationContext.assets.open(fileName)
                .bufferedReader().use {
                it.readText()
            }

            Timber.e("---- SEND COMMAND --- ")
            jsonString.split("\r\n").drop(1).forEach { line ->
                val values = line.split(";")
                Timber.e("command: ${values[1]}")

                connectedGlasses?.sendData(values[1]) {
                    Timber.e(it)
                }
            }

            Timber.e("---- SEND COMMAND ENDED--- ")

        }
    }

}