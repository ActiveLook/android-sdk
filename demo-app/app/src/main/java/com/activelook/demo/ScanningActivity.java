package com.activelook.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.activelook.activelooksdk.DiscoveredGlasses;
import com.activelook.activelooksdk.Sdk;

import java.util.ArrayList;

public class ScanningActivity extends AppCompatActivity {

    private Sdk alsdk;
    private ArrayAdapter<DiscoveredGlasses> scanResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_activity);

        /*
         * Check location permission (needed for BLE scan)
         */
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                0);

        /*
         * Initialize scan result list
         */
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.scanResults = new ArrayAdapter<DiscoveredGlasses>(this, R.layout.listitem_device) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View glassesView = convertView;
                if (convertView == null) {
                    final DiscoveredGlasses glasses = this.getItem(position);
                    glassesView = inflater.inflate(R.layout.listitem_device, null);
                    TextView name = glassesView.findViewById(R.id.device_name);
                    TextView address = glassesView.findViewById(R.id.device_address);
                    name.setText(glasses.getName());
                    String addressText = String.format(parent.getResources().getString(R.string.listitem_address),
                            glasses.getManufacturer(),
                            glasses.getAddress());
                    address.setText(addressText);
                }
                return glassesView;
            }
        };
        if (savedInstanceState != null) {
            savedInstanceState.setClassLoader(getClass().getClassLoader());
            ArrayList<DiscoveredGlasses> dGlasses = savedInstanceState.getParcelableArrayList("discoveredGlasses");
            if (dGlasses != null) {
                this.scanResults.addAll(dGlasses);
            }
        }
        final ListView list = this.findViewById(R.id.scan_results);
        list.setAdapter(this.scanResults);
        list.setOnItemClickListener((adapterView, view, i, l) -> {
            DiscoveredGlasses device = ScanningActivity.this.scanResults.getItem(i);
            ScanningActivity.this.scanResults.clear();
            ScanningActivity.this.alsdk.stopScan();
            Toast.makeText(this, "Connecting to...", Toast.LENGTH_SHORT).show();
            device.connect(glasses -> {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("connectedGlasses", glasses);
                        ScanningActivity.this.setResult(Activity.RESULT_FIRST_USER, returnIntent);
                        ScanningActivity.this.finish();
                    },
                    null, null);
        });
        /*
         * Scan / Stop scan button
         */
        Button fab = this.findViewById(R.id.button_stop_scan);
        fab.setOnClickListener(view -> {
            ScanningActivity.this.scanResults.clear();
            ScanningActivity.this.alsdk.stopScan();
            ScanningActivity.this.setResult(Activity.RESULT_CANCELED);
            ScanningActivity.this.finish();
        });
        this.alsdk = ((DemoApp) this.getApplication()).getActiveLookSdk();
        this.alsdk.startScan(activeLookDiscoveredGlassesI -> runOnUiThread(() -> {
            ScanningActivity.this.scanResults.add(activeLookDiscoveredGlassesI);
        }));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        ArrayList<DiscoveredGlasses> underlying = new ArrayList<>();
        for (int i = 0; i < this.scanResults.getCount(); i++) {
            underlying.add(this.scanResults.getItem(i));
        }
        savedInstanceState.putParcelableArrayList("discoveredGlasses", underlying);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
