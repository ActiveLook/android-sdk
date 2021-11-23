package com.activelook.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.activelook.activelooksdk.Glasses;
import com.activelook.activelooksdk.types.DemoPattern;
import com.activelook.activelooksdk.types.LedState;
import com.activelook.activelooksdk.types.Rotation;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private Glasses connectedGlasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && ((DemoApp) this.getApplication()).isConnected()) {
            this.connectedGlasses = savedInstanceState.getParcelable("connectedGlasses");
            this.connectedGlasses.setOnDisconnected(glasses -> MainActivity.this.disconnect());
        }
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle(getTitle());
        this.snack(toolbar, "Welcome");
        this.updateVisibility();
        this.bindActions();
    }

    private void updateVisibility() {
        if (this.connectedGlasses == null) {
            this.findViewById(R.id.connected_content).setVisibility(View.GONE);
            this.findViewById(R.id.disconnected_content).setVisibility(View.VISIBLE);
        } else {
            this.findViewById(R.id.connected_content).setVisibility(View.VISIBLE);
            this.findViewById(R.id.disconnected_content).setVisibility(View.GONE);
        }
    }

    private void bindActions() {
        this.toast("Binding actions");
        this.findViewById(R.id.scan).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ScanningActivity.class);
            MainActivity.this.startActivityForResult(intent, Activity.RESULT_FIRST_USER);
        });
        this.findViewById(R.id.general_commands).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GeneralCommands.class);
            intent.putExtra("connectedGlasses", this.connectedGlasses);
            MainActivity.this.startActivity(intent);
        });
        this.findViewById(R.id.display_luma_commands).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, DisplayLuminanceCommands.class);
            intent.putExtra("connectedGlasses", this.connectedGlasses);
            MainActivity.this.startActivity(intent);
        });
        this.findViewById(R.id.optical_sensor_commands).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, OpticalSensorCommands.class);
            intent.putExtra("connectedGlasses", this.connectedGlasses);
            MainActivity.this.startActivity(intent);
        });
        this.findViewById(R.id.graphics_commands).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GraphicsCommands.class);
            intent.putExtra("connectedGlasses", this.connectedGlasses);
            MainActivity.this.startActivity(intent);
        });
        this.findViewById(R.id.bitmaps_commands).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, BitmapsCommands.class);
            intent.putExtra("connectedGlasses", this.connectedGlasses);
            MainActivity.this.startActivity(intent);
        });
        this.findViewById(R.id.font_commands).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, FontCommands.class);
            intent.putExtra("connectedGlasses", this.connectedGlasses);
            MainActivity.this.startActivity(intent);
        });
        this.findViewById(R.id.layouts_commands).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LayoutsCommands.class);
            intent.putExtra("connectedGlasses", this.connectedGlasses);
            MainActivity.this.startActivity(intent);
        });
        this.findViewById(R.id.gauge_commands).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, GaugeCommands.class);
            intent.putExtra("connectedGlasses", this.connectedGlasses);
            MainActivity.this.startActivity(intent);
        });
        this.findViewById(R.id.page_commands).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PageCommands.class);
            intent.putExtra("connectedGlasses", this.connectedGlasses);
            MainActivity.this.startActivity(intent);
        });
        this.findViewById(R.id.configuration_commands).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ConfigurationCommands.class);
            intent.putExtra("connectedGlasses", this.connectedGlasses);
            MainActivity.this.startActivity(intent);
        });
        this.findViewById(R.id.button_disconnect).setOnClickListener(view -> MainActivity.this.disconnect());
        this.findViewById(R.id.debug).setOnClickListener(view -> {
            MainActivity.this.debugButton();
        });
    }

    private void debugButton() {
        final Handler h = new Handler();
        final Glasses g = this.connectedGlasses;
        final long msStep = 500;
        long ms = 10;
        h.postDelayed(() -> g.power(true), ms+=msStep);
        h.postDelayed(() -> g.led(LedState.BLINK), ms+=msStep);
        h.postDelayed(() -> g.grey((byte) 0x03), ms+=msStep);
        h.postDelayed(() -> g.grey((byte) 0x0F), ms+=msStep);
        h.postDelayed(() -> g.led(LedState.ON), ms+=msStep);
        h.postDelayed(() -> g.power(false), ms+=msStep);
        h.postDelayed(() -> g.grey((byte) 0x0F), ms+=msStep);
        h.postDelayed(() -> g.power(true), ms+=msStep);
        h.postDelayed(() -> g.led(LedState.OFF), ms+=msStep);
        h.postDelayed(() -> g.clear(), ms+=msStep);
        h.postDelayed(() -> g.grey((byte) 0x01), ms+=msStep);
        h.postDelayed(() -> g.clear(), ms+=msStep);
        h.postDelayed(() -> g.led(LedState.TOGGLE), ms+=msStep);
        h.postDelayed(() -> g.demo(DemoPattern.FILL), ms+=msStep);
        h.postDelayed(() -> g.demo(DemoPattern.CROSS), ms+=msStep);
        h.postDelayed(() -> g.demo(DemoPattern.IMAGE), ms+=msStep);
        h.postDelayed(() -> g.demo(DemoPattern.IMAGE), ms+=msStep);
        h.postDelayed(() -> g.demo(DemoPattern.IMAGE), ms+=msStep);
        h.postDelayed(() -> g.led(LedState.TOGGLE), ms+=msStep);
        h.postDelayed(() -> g.battery(r -> snack(String.format("Battery level: %d", r))), ms+=msStep);
        h.postDelayed(() ->
                        g.vers(r -> snack(String.format("Version: %s [serial=%d]", r.getVersion(), r.getSerial()))),
                ms+=msStep);
        h.postDelayed(() -> g.clear(), ms+=msStep);
        h.postDelayed(() -> g.shift((short) 0, (short) 0), ms+=msStep);
        h.postDelayed(() -> g.txt(new Point(10, 100), Rotation.TOP_RL, (byte) 0x00, (byte) 0x0F,
                "1"), ms+=msStep);
        h.postDelayed(() -> g.txt(new Point(10, 100), Rotation.TOP_RL, (byte) 0x00, (byte) 0x0F,
                "22"), ms+=msStep);
        h.postDelayed(() -> g.txt(new Point(10, 100), Rotation.TOP_RL, (byte) 0x00, (byte) 0x0F,
                "333"), ms+=msStep);
        h.postDelayed(() -> g.txt(new Point(10, 100), Rotation.TOP_RL, (byte) 0x00, (byte) 0x0F,
                "4444"), ms+=msStep);
        h.postDelayed(() -> g.txt(new Point(10, 100), Rotation.TOP_RL, (byte) 0x00, (byte) 0x0F,
                "55555"), ms+=msStep);
        h.postDelayed(() -> g.txt(new Point(10, 100), Rotation.TOP_RL, (byte) 0x00, (byte) 0x0F,
                "666666"), ms+=msStep);
        h.postDelayed(() -> g.txt(new Point(10, 100), Rotation.TOP_RL, (byte) 0x00, (byte) 0x0F,
                "7777777"), ms+=msStep);
        h.postDelayed(() -> g.txt(new Point(10, 100), Rotation.TOP_RL, (byte) 0x00, (byte) 0x0F,
                "88888888"), ms+=msStep);
        h.postDelayed(() -> g.txt(new Point(10, 100), Rotation.TOP_RL, (byte) 0x00, (byte) 0x0F,
                "999999999"), ms+=msStep);
        h.postDelayed(() -> g.txt(new Point(10, 100), Rotation.TOP_RL, (byte) 0x00, (byte) 0x0F,
                "0000000000"), ms+=msStep);
        h.postDelayed(() -> g.demo(DemoPattern.CROSS), ms+=msStep);
        h.postDelayed(() ->
                        g.txt(new Point(100, 100), Rotation.TOP_LR, (byte) 0x00, (byte) 0x0F, "TopLR"),
                ms+=msStep);
        h.postDelayed(() ->
                        g.txt(new Point(100, 100), Rotation.TOP_RL, (byte) 0x00, (byte) 0x0F, "TopRL"),
                ms+=msStep);
        h.postDelayed(() ->
                        g.txt(new Point(100, 100), Rotation.BOTTOM_LR, (byte) 0x00, (byte) 0x0F, "BottomLR"),
                ms+=msStep);
        h.postDelayed(() ->
                        g.txt(new Point(100, 100), Rotation.BOTTOM_RL, (byte) 0x00, (byte) 0x0F, "BottomRL"),
                ms+=msStep);
        h.postDelayed(() -> g.shift((short) 100, (short) 100), ms+=msStep);
        h.postDelayed(() -> g.demo(DemoPattern.CROSS), ms+=msStep);
        h.postDelayed(() ->
                        g.txt(new Point(100, 100), Rotation.LEFT_TB, (byte) 0x00, (byte) 0x0F, "LeftTB"),
                ms+=msStep);
        h.postDelayed(() ->
                        g.txt(new Point(100, 100), Rotation.LEFT_BT, (byte) 0x00, (byte) 0x0F, "LeftBT"),
                ms+=msStep);
        h.postDelayed(() ->
                        g.txt(new Point(100, 100), Rotation.RIGHT_TB, (byte) 0x00, (byte) 0x0F, "RightTB"),
                ms+=msStep);
        h.postDelayed(() ->
                        g.txt(new Point(100, 100), Rotation.RIGHT_BT, (byte) 0x00, (byte) 0x0F, "RightBT"),
                ms+=msStep);
        h.postDelayed(() -> g.settings(r -> snack(String.format("Settings: %s", r))), ms+=msStep);
        h.postDelayed(() -> g.shift((short) 1, (short) 1), ms+=msStep);
        h.postDelayed(() -> g.settings(r -> snack(String.format("Settings: %s", r))), ms+=msStep);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == requestCode && requestCode == Activity.RESULT_FIRST_USER) {
            if (data != null && data.hasExtra("connectedGlasses")) {
                this.connectedGlasses = data.getExtras().getParcelable("connectedGlasses");
                this.connectedGlasses.setOnDisconnected(glasses -> MainActivity.this.disconnect());
                this.toast(String.format("Connected to %s", this.connectedGlasses.getName()));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (this.connectedGlasses != null) {
            savedInstanceState.putParcelable("connectedGlasses", this.connectedGlasses);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!((DemoApp) this.getApplication()).isConnected()) {
            this.connectedGlasses = null;
        }
        this.updateVisibility();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void disconnect() {
        runOnUiThread(() -> {
            ((DemoApp) this.getApplication()).onDisconnected();
            MainActivity.this.connectedGlasses.disconnect();
            MainActivity.this.connectedGlasses = null;
            MainActivity.this.updateVisibility();
            MainActivity.this.snack("Disconnected");
        });
    }

    private Toast toast(Object data) {
        Log.d("MainActivity", data.toString());
        Toast toast = Toast.makeText(this, data.toString(), Toast.LENGTH_LONG);
        toast.show();
        return toast;
    }

    private Snackbar snack() {
        return this.snack(null, null);
    }

    private Snackbar snack(Object data) {
        return this.snack(null, data);
    }

    private Snackbar snack(View snackView, Object data) {
        if (snackView == null) {
            snackView = this.findViewById(R.id.toolbar);
        }
        final String msg = data == null ? "" : data.toString();
        Snackbar snack = Snackbar.make(snackView, msg, Snackbar.LENGTH_INDEFINITE);
        snack.show();
        if (data != null) {
            Log.d("MainActivity", data.toString());
        } else {
            snack.dismiss();
        }
        return snack;
    }

}
