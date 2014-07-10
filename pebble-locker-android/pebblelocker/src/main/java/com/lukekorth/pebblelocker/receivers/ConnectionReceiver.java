package com.lukekorth.pebblelocker.receivers;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import com.lukekorth.pebblelocker.LockState;
import com.lukekorth.pebblelocker.Locker;
import com.lukekorth.pebblelocker.helpers.BaseBroadcastReceiver;
import com.lukekorth.pebblelocker.helpers.BluetoothHelper;
import com.lukekorth.pebblelocker.helpers.DeviceHelper;
import com.lukekorth.pebblelocker.helpers.WifiHelper;

public class ConnectionReceiver extends BaseBroadcastReceiver {

	private static final String PEBBLE_CONNECTED       = "com.getpebble.action.pebble_connected";
	private static final String PEBBLE_DISCONNECTED    = "com.getpebble.action.pebble_disconnected";
	private static final String BLUETOOTH_CONNECTED    = "android.bluetooth.device.action.acl_connected";
	private static final String BLUETOOTH_DISCONNECTED = "android.bluetooth.device.action.acl_disconnected";
	private static final String CONNECTIVITY_CHANGE    = "android.net.conn.connectivity_change";
	private static final String USER_PRESENT           = "android.intent.action.user_present";
	public  static final String STATUS_CHANGED_INTENT  = "com.lukekorth.pebblelocker.STATUS_CHANGED";
	public  static final String LOCKED                 = "locked";

	private String mAction;

	@SuppressLint("DefaultLocale")
	@Override
	public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        acquireWakeLock();

	    mAction = intent.getAction().toLowerCase();
		mLogger.log("ConnectionReceiver: " + mAction);

        DeviceHelper deviceHelper = new DeviceHelper(context, mLogger);
        deviceHelper.sendLockStatusChangedBroadcast();

		LockState lockState = LockState.getCurrentState(context);
		if (lockState == LockState.AUTO) {
			checkForBluetoothDevice(((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)));
			boolean isWifiConnected = new WifiHelper(context, mLogger).isTrustedWifiConnected();

			if (mAction.equals(USER_PRESENT) && deviceHelper.isUnlockNeeded()) {
				mLogger.log("User present and need to unlock...attempting to unlock");
				new Locker(context, mTag).handleLocking();
			} else if ((mAction.equals(PEBBLE_CONNECTED) || mAction.equals(BLUETOOTH_CONNECTED) ||
                    (mAction.equals(CONNECTIVITY_CHANGE) && isWifiConnected)) && deviceHelper.isLocked(true)) {
				mLogger.log("Attempting unlock");
				new Locker(context, mTag).handleLocking();
			} else if ((mAction.equals(PEBBLE_DISCONNECTED) || mAction.equals(BLUETOOTH_DISCONNECTED) ||
                    (mAction.equals(CONNECTIVITY_CHANGE) && !isWifiConnected)) && !deviceHelper.isLocked(false)) {
				mLogger.log("Attempting lock");
                new Locker(context, mTag).lockWithDelay();
			}
		} else {
			mLogger.log("Lock state was manually set to " + lockState.getDisplayName());
		}

        releaseWakeLock();
	}

	private void checkForBluetoothDevice(BluetoothDevice device) {
		if (mAction.equals(BLUETOOTH_CONNECTED)) {
			new BluetoothHelper(mContext, mLogger).setDeviceStatus(device.getName(), device.getAddress(), true);
		} else if (mAction.equals(BLUETOOTH_DISCONNECTED)) {
			new BluetoothHelper(mContext, mLogger).setDeviceStatus(device.getName(), device.getAddress(), false);
		}
	}
}