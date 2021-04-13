# ActiveLookSDK

## Requirements

In order to use the ActiveLook SDK for android, you should have Android Studio installed.

## License

```
Copyright 2021 Microoled
Licensed under the Apache License, Version 2.0 (the “License”);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an “AS IS” BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Example

To run the example project, clone the repo, and import the project in Android Studio.

Note: BlueTooth will not work on the android simulator. A physical device should be used instead.

## Initialization

To start using the SDK, first import and initialize the sdk, for example, in a main application class:

```java
package com.activelook.demo;

import android.app.Application;
import com.activelook.activelooksdk.Sdk;

public class DemoApp extends Application {

  private Sdk alsdk;

  @Override
  public void onCreate() {
    super.onCreate();
    this.alsdk = Sdk.init(this.getApplicationContext());
  }

  public Sdk getActiveLookSdk() {
    return this.alsdk;
  }

}
```

Then, use the shared singleton wherever needed. This can be called from anywhere within your application.

```java
import com.activelook.activelooksdk.Sdk;

...

Sdk alsdk = ((DemoApp) this.getApplication()).getActiveLookSdk();
```

## Scanning

To scan for available ActiveLook glasses, simply use the
`startScan(Consumer<DiscoveredGlasses> onDiscoverGlasses)` and
`stopScan()` methods.

When a device is discovered, the `onDiscoverGlasses` callback will be called.

You can handle these cases by providing an instance of `Consumer<DiscoveredGlasses>`.
Since this is a single method interface, you can also create it on the fly with a closure.
For example, you can run some process on the UI thread:

```java
this.alsdk.startScan(discoveredGlasses -> runOnUiThread(() -> {
  Log.d("SDK", discoveredGlasses.toString());
}));
```

## Connect to ActiveLook glasses

To connect to a pair of discovered glasses, use the `connect(Consumer<Glasses> onConnected, Consumer<DiscoveredGlasses> onConnectionFail, Consumer<Glasses> onDisconnected)` method on the `DiscoveredGlasses` object.

If the connection is successful, the `onConnected.accept` method will be called and will return a `Glasses` object,
which can then be used to get information about the connected ActiveLook glasses or send commands.

If the connection fails, the `onConnectionFail.accept` method will be called instead.

Finally, if the connection to the glasses is lost at any point,
later, while the app is running, the `onDisconnected.accept` method will be called.

Since all those handler are single method interfaces, you can create them on the fly with closures:

```java
discoveredGlasses.connect(
  glasses -> { Log.d("SDK", "Connected: " + glasses.toString()); },
  discoveredGlasses -> { Log.d("SDK", "Connection fail: " + discoveredGlasses.toString()); },
  glasses -> { Log.d("SDK", "Disconnected: " + discoveredGlasses.toString()); }
);
```

If you need to share the `Glasses` object between several Activity,
and you find it hard or inconvenient to hold onto the `onDisconnected` callback,
you can reset it or provide a new one by using the
`setOnDisconnected(Consumer<Glasses> onDisconnected)` method on the `Glasses` object:

```java
glasses.setOnDisconnected(
  glasses -> { Log.d("SDK", "New disconnected handler: " + glasses.toString()); }
);
```

## Device information

To get information relative to discovered glasses as published over Bluetooth, you can access the following public properties:

```java
this.alsdk.startScan(discoveredGlasses -> {
  Log.d("discoveredGlasses", "Manufacturer:" + discoveredGlasses.getManufacturer());
  Log.d("discoveredGlasses", "Name:" + discoveredGlasses.getName());
  Log.d("discoveredGlasses", "Address:" + discoveredGlasses.getAddress());
});
```

Once connected, you can access more information about the device such as its firmware version, the model number etc... by using the `DeviceInformation getDeviceInformation()` method:

```java
DeviceInformation di = glasses.getDeviceInformation();
Log.d("Glasses", "ManufacturerName: " + di.getManufacturerName());
Log.d("Glasses", "ModelNumber: " + di.getModelNumber());
Log.d("Glasses", "SerialNumber: " + di.getSerialNumber());
Log.d("Glasses", "HardwareVersion: " + di.getHardwareVersion());
Log.d("Glasses", "FirmwareVersion: " + di.getFirmwareVersion());
Log.d("Glasses", "SoftwareVersion: " + di.getSoftwareVersion());
```

## Commands

All available commands are exposed as methods in the `Glasses` class.
Examples are available in the Example application.
Most commands require parameters to be sent.

```java
// Power on the glasses
glasses.power(true);

// Set the display luminance level
glasses.luma((byte) 15)

// Draw a circle at the center of the screen
glasses.circ((short) 152, (short) 128, (byte) 50)

// Enable gesture detection sensor
glasses.gesture(true)

// Some other example
glasses.power(false);
glasses.clear();
glasses.grey((byte) 0x03);
glasses.grey((byte) 0x07);
glasses.grey((byte) 0x0B);
glasses.grey((byte) 0x0F);
glasses.demo();
glasses.test(DemoPattern.CROSS);
glasses.test(DemoPattern.FILL);
```

When a response is expected from the glasses,
an handler must be provided as a instance or as a closure.
The callback will be called asynchronously.

```java
glasses.battery(r -> { Log.d("Battery", String.format("Battery level: %d", r)); });
```

## Notifications

It is possible to subscribe to three types of notifications that the glasses will send over Bluetooth:

- Battery level updates (periodically, every 30 seconds)
- Gesture detection sensor triggered
- Flow control events (when the state of the flow control changes)

```java
glasses.subscribeToBatteryLevelNotifications( //Consumer<Integer> onEvent
  r -> { Log.d("Notif", "Battery: " + r.toString()); }
);
glasses.subscribeToFlowControlNotifications( //Consumer<FlowControlStatus> onEvent
  r -> { Log.d("Notif", "Flow control: " + r.toString()); }
);
glasses.subscribeToSensorInterfaceNotifications( //Runnable onEvent
  r -> { Log.d("Notif", "Sensor: Gesture!"); }
);
```

## Disconnect

When done interacting with ActiveLook glasses, simply call the `disconnect()` method:

```java
glasses.disconnect()
```

## Sharing glasses across multiple activities

In order to use a `DiscoveredGlasses` or a `Glasses` created in one activty and consumed in another one, `Intent` must be use.
These classes implements the `Parcelable` interface to make it possible.
You can find an example in the sample application where the `Glasses` is shared between the scanning activity and the main activity.

```java
public class MainActivity extends AppCompatActivity {

  private Glasses connectedGlasses;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    ...
    if (savedInstanceState != null) {
      this.connectedGlasses = savedInstanceState.getParcelable("connectedGlasses");
    }
    ...
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    this.connectedGlasses = data.getExtras().getParcelable("connectedGlasses");
  }

  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    if (this.connectedGlasses != null) {
      savedInstanceState.putParcelable("connectedGlasses", this.connectedGlasses);
    }
  }
  ...
}

public class ScanningActivity extends AppCompatActivity {
...
  device.connect(glasses -> {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("connectedGlasses", glasses);
    ScanningActivity.this.setResult(Activity.RESULT_FIRST_USER, returnIntent);
    ScanningActivity.this.finish();
  }, null, null);
...
}
```

## About Android Wear

For now, it is unclear if Android Wear can be supported.
A study on the compatibility and limits needs to be done and
this sample code will be updated accordingly.
