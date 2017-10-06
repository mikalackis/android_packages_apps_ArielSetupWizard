/*
 * Copyright (C) 2016 The ariel Project
 * Copyright (C) 2017 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ariel.setupwizard;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import android.provider.Settings;
import android.app.admin.DevicePolicyManager;
import android.util.Slog;

import com.ariel.setupwizard.util.EnableAccessibilityController;

public class WelcomeActivity extends BaseSetupWizardActivity {

    public static final String TAG = WelcomeActivity.class.getSimpleName();

    private View mRootView;
    private EnableAccessibilityController mEnableAccessibilityController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initiArielOS();

        mRootView = findViewById(R.id.root);
        setNextText(R.string.next);
        setBackText(R.string.emergency_call);
        setBackDrawable(null);
        mEnableAccessibilityController =
                EnableAccessibilityController.getInstance(getApplicationContext());
        mRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mEnableAccessibilityController.onTouchEvent(event);
            }
        });
    }

    private void initiArielOS(){
        PackageManager pm = getPackageManager();

        int isDeviceProvisioned = Settings.Global.getInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 0);

        if (isDeviceProvisioned == 0) {
            setDeviceOwner();
            try {
                IBackupManager ibm = IBackupManager.Stub.asInterface(
                        ServiceManager.getService(Context.BACKUP_SERVICE));
                ibm.setBackupServiceActive(UserHandle.USER_OWNER, true);

                // try to find google backup transport
                // and set it, only if google apps are installed
                if (isGoogleAppsPresent) {
                    String[] availableTransports = ibm.listAllTransports();

                    boolean found = false;

                    for (int i = 0; i < availableTransports.length; i++) {
                        String tmpTransport = availableTransports[i];
                        Slog.i(TAG, "Checking transport: " + tmpTransport);
                        if (tmpTransport.equals(GOOGLE_BACKUP_TRANSPORT1) ||
                                tmpTransport.equals(GOOGLE_BACKUP_TRANSPORT2)) {
                            Slog.i(TAG, "Bingo! Google backup transport found");
                            // this is the one we need, set it
                            ibm.selectBackupTransport(tmpTransport);
                            found = true;
                            break;
                        } else {
                            // this is weird, it has google but not the one we know about
                            Slog.i(TAG, "Weird! Backup transport " +
                                    "found but not the one we need: " + tmpTransport);
                        }
                    }

                    if(!found){
                        Slog.i(TAG, "We didnt find google backup while gapps are present. Force.");
                        ibm.selectBackupTransport(GOOGLE_BACKUP_TRANSPORT1);
                    }
                    else{
                        Slog.i(TAG, "All cool, google backup transport set");
                    }
                }
            } catch (RemoteException e) {
                throw new IllegalStateException("Failed activating backup service.", e);
            }

//            if (!isPackageInstalled("com.google.android.setupwizard", pm)) {
//                // provisioning complete!
//                Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
//                Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);
//            }
        }

        // Add a persistent setting to allow other apps to know the device has been provisioned.
        //Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);
        //Settings.Secure.putInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 1);

        // remove this activity from the package manager.
//        ComponentName name = new ComponentName(this, DefaultActivity.class);
//        pm.setComponentEnabledSetting(name, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                PackageManager.DONT_KILL_APP);

    }

    private void setDeviceOwner() {
        DevicePolicyManager mDPM =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        Slog.i(TAG, "Setting device owner info...");
        ComponentName cn = new ComponentName("com.ariel.guardian",
                "com.ariel.guardian.receivers.ArielDeviceAdminReceiver");

        try {
            // first, we need to set ourselves as active admin
            mDPM.setActiveAdmin(cn, true);

            // second, set ourselves as device owner
            // btw at this point bellow code wont work
            // because the upper statement will cause an exception :)
            boolean result = mDPM.setDeviceOwner(cn, "ArielGuardian");
            if (result) {
                Slog.i(TAG, "Setting device owner success!");
            } else {
                Slog.i(TAG, "Setting device owner failed...");
            }

            Slog.i(TAG, "New device owner: " + mDPM.getDeviceOwner());
        } catch (IllegalStateException e) {
            Slog.e("ArielSystemServer", "Set active admin failed!!");
            e.printStackTrace();
        } catch (Exception e) {
            Slog.e("ArielSystemServer", "Set active admin failed!!");
            e.printStackTrace();
        }

        Slog.i(TAG, "New device owner: " + mDPM.getDeviceOwner());
    }

    @Override
    public void onBackPressed() {}

    @Override
    public void onNavigateBack() {
        startEmergencyDialer();
    }

    @Override
    protected int getTransition() {
        return TRANSITION_ID_SLIDE;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.welcome_activity;
    }
}
