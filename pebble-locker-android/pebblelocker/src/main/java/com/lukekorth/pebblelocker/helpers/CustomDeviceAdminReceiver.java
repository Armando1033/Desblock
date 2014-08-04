package com.lukekorth.pebblelocker.helpers;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.lukekorth.pebblelocker.logging.Logger;

/**
 * All callbacks are on the UI thread and your implementations should not engage in any
 * blocking operations, including disk I/O.
 */
public class CustomDeviceAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        new Logger(context).log("[DEVICE_ADMIN_RECEIVER]", "Device admin enabled");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        new Logger(context).log("[DEVICE_ADMIN_RECEIVER]", "Device admin disable requested, disabling");

        ComponentName deviceAdmin = new ComponentName(context, CustomDeviceAdminReceiver.class);
        ((DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE)).removeActiveAdmin(deviceAdmin);

        return null;
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        new Logger(context).log("[DEVICE_ADMIN_RECEIVER]", "Device admin disabled");
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        new Logger(context).log("[DEVICE_ADMIN_RECEIVER]", "Password changed");
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        new Logger(context).log("[DEVICE_ADMIN_RECEIVER]", "Password failed");
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        new Logger(context).log("[DEVICE_ADMIN_RECEIVER]", "Password succeeded");
    }

    @Override
    public void onPasswordExpiring(Context context, Intent intent) {
        new Logger(context).log("[DEVICE_ADMIN_RECEIVER]", "Password expiring");
    }

}
