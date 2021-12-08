package com.activelook.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.util.Consumer;

import com.activelook.activelooksdk.Glasses;
import com.google.android.material.snackbar.Snackbar;

import java.util.AbstractMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {

    private ArrayAdapter<Map.Entry<String, Consumer<Glasses>>> items;
    private Glasses connectedGlasses;

    protected static final Map.Entry<String, Consumer<Glasses>> item(String name, Consumer<Glasses> callback) {
        return new AbstractMap.SimpleImmutableEntry<>(name, callback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setTitle(String.format("Commands: %s", this.getCommandGroup()));
        Intent intent = this.getIntent();
        if (intent != null && ((DemoApp) this.getApplication()).isConnected() && intent.hasExtra("connectedGlasses")) {
            this.connectedGlasses = intent.getExtras().getParcelable("connectedGlasses");
            this.connectedGlasses.setOnDisconnected(glasses -> {
                glasses.disconnect();
                MainActivity2.this.disconnect();
            });
            this.connectedGlasses.subscribeToFlowControlNotifications(status ->
                    MainActivity2.this.snack(String.format("Flow control: %s", status.name()))
            );
            this.connectedGlasses.subscribeToSensorInterfaceNotifications(() ->
                    MainActivity2.this.snack("SensorInterface")
            );
            this.toast(String.format("Connected to %s", this.connectedGlasses.getName()));
        }
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = new ArrayAdapter<Map.Entry<String, Consumer<Glasses>>>
                (this, R.layout.command_button) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                final Map.Entry<String, Consumer<Glasses>> cmd = this.getItem(position);
                convertView = inflater.inflate(R.layout.command_button, null);
                final TextView result = convertView.findViewById(R.id.command_returned);
                Button button = convertView.findViewById(R.id.command_button);
                button.setText(cmd.getKey());
                button.setOnClickListener(view -> {
                    cmd.getValue().accept(MainActivity2.this.connectedGlasses);
                    result.setText("Sended");
                });
                return convertView;
            }
        };
        final ListView list = this.findViewById(R.id.list_command);
        list.setAdapter(this.items);
        this.bindActions();
    }

    protected String getCommandGroup() {
        return "Commands";
    }

    protected Map.Entry<String, Consumer<Glasses>>[] getCommands() {
        return new Map.Entry[]{};
    }

    private void bindActions() {
        this.findViewById(R.id.button_back).setOnClickListener(view -> {
            MainActivity2.this.finish();
        });
        this.items.addAll(this.getCommands());
    }

    private void disconnect() {
        runOnUiThread(() -> {
            ((DemoApp) this.getApplication()).onDisconnected();
            MainActivity2.this.connectedGlasses = null;
            MainActivity2.this.finish();
        });
    }

    private Toast toast(Object data) {
        Log.d("ActivityCommand", data.toString());
        Toast toast = Toast.makeText(this, data.toString(), Toast.LENGTH_LONG);
        toast.show();
        return toast;
    }

    protected Snackbar snack() {
        return this.snack(null, null);
    }

    protected Snackbar snack(Object data) {
        return this.snack(null, data);
    }

    protected Snackbar snack(View snackView, Object data) {
        if (snackView == null) {
            snackView = this.findViewById(R.id.button_back);
        }
        final String msg = data == null ? "" : data.toString();
        Snackbar snack = Snackbar.make(snackView, msg, Snackbar.LENGTH_LONG);
        snack.show();
        if (data != null) {
            Log.d("ActivityCommand", data.toString());
        } else {
            snack.dismiss();
        }
        return snack;
    }

}
