package com.activelook.activelooksdk.core.ble;

import android.content.Context;

import androidx.core.util.Consumer;

import com.activelook.activelooksdk.types.GlassesUpdate;

class GlassesUpdater {

    private final Context context;
    final String token;
    final Consumer<GlassesUpdate> onUpdateStart;
    final Consumer<GlassesUpdate> onUpdateProgress;
    final Consumer<GlassesUpdate> onUpdateSuccess;
    final Consumer<GlassesUpdate> onUpdateError;

    GlassesUpdater(final Context context,
                   final String token,
                   final Consumer<GlassesUpdate> onUpdateStart,
                   final Consumer<GlassesUpdate> onUpdateProgress,
                   final Consumer<GlassesUpdate> onUpdateSuccess,
                   final Consumer<GlassesUpdate> onUpdateError) {
        this.context = context;
        this.token = token;
        this.onUpdateStart = onUpdateStart;
        this.onUpdateProgress = onUpdateProgress;
        this.onUpdateSuccess = onUpdateSuccess;
        this.onUpdateError = onUpdateError;
    }

    public void update(final Glasses glasses, final Consumer<Glasses> onConnected) {
    }
}
