package com.activelook.activelooksdk.core.ble;

import com.activelook.activelooksdk.DiscoveredGlasses;
import com.activelook.activelooksdk.types.GlassesUpdate;

final class UpdateProgress implements GlassesUpdate {

    private final DiscoveredGlasses discoveredGlasses;
    private final State state;
    private final int progress;
    private final String sourceFirmwareVersion;
    private final String targetFirmwareVersion;
    private final String sourceConfigurationVersion;
    private final String targetConfigurationVersion;

    UpdateProgress(final DiscoveredGlasses discoveredGlasses,
                          final State state,
                          final int progress,
                          final String sourceFirmwareVersion,
                          final String targetFirmwareVersion,
                          final String sourceConfigurationVersion,
                          final String targetConfigurationVersion) {
        this.discoveredGlasses = discoveredGlasses;
        this.state = state;
        this.progress = progress;
        this.sourceFirmwareVersion = sourceFirmwareVersion;
        this.targetFirmwareVersion = targetFirmwareVersion;
        this.sourceConfigurationVersion = sourceConfigurationVersion;
        this.targetConfigurationVersion = targetConfigurationVersion;
    }

    UpdateProgress withStatus(final State state) {
        return new UpdateProgress(
                discoveredGlasses, state, progress,
                sourceFirmwareVersion, targetFirmwareVersion,
                sourceConfigurationVersion, targetConfigurationVersion);
    }

    UpdateProgress withProgress(final int progress) {
        return new UpdateProgress(
                discoveredGlasses, state, progress,
                sourceFirmwareVersion, targetFirmwareVersion,
                sourceConfigurationVersion, targetConfigurationVersion);
    }

    UpdateProgress withSourceFirmwareVersion(final String sourceFirmwareVersion) {
        return new UpdateProgress(
                discoveredGlasses, state, progress,
                sourceFirmwareVersion, targetFirmwareVersion,
                sourceConfigurationVersion, targetConfigurationVersion);
    }

    UpdateProgress withTargetFirmwareVersion(final String targetFirmwareVersion) {
        return new UpdateProgress(
                discoveredGlasses, state, progress,
                sourceFirmwareVersion, targetFirmwareVersion,
                sourceConfigurationVersion, targetConfigurationVersion);
    }

    UpdateProgress withSourceConfigurationVersion(final String sourceConfigurationVersion) {
        return new UpdateProgress(
                discoveredGlasses, state, progress,
                sourceFirmwareVersion, targetFirmwareVersion,
                sourceConfigurationVersion, targetConfigurationVersion);
    }

    UpdateProgress withTargetConfigurationVersion(final String targetConfigurationVersion) {
        return new UpdateProgress(
                discoveredGlasses, state, progress,
                sourceFirmwareVersion, targetFirmwareVersion,
                sourceConfigurationVersion, targetConfigurationVersion);
    }

    @Override
    public final DiscoveredGlasses getDiscoveredGlasses() {
        return this.discoveredGlasses;
    }

    @Override
    public final State getState() {
        return this.state;
    }

    @Override
    public final int getProgress() {
        return this.progress;
    }

    @Override
    public final String getSourceFirmwareVersion() {
        return this.sourceFirmwareVersion;
    }

    @Override
    public final String getTargetFirmwareVersion() {
        return this.targetFirmwareVersion;
    }

    @Override
    public final String getSourceConfigurationVersion() {
        return this.sourceConfigurationVersion;
    }

    @Override
    public final String getTargetConfigurationVersion() {
        return this.targetConfigurationVersion;
    }

}
