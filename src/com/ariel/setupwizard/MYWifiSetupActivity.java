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

import static com.ariel.setupwizard.SetupWizardApp.ACTION_SETUP_WIFI;
import static com.ariel.setupwizard.SetupWizardApp.EXTRA_MATERIAL_LIGHT;
import static com.ariel.setupwizard.SetupWizardApp.REQUEST_CODE_SETUP_WIFI;

import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.widget.Toast;
import com.ariel.setupwizard.util.SetupWizardUtils;
import com.android.setupwizardlib.SetupWizardListLayout;
import com.android.setupwizardlib.view.NavigationBar;

import android.widget.ArrayAdapter;
import android.content.Context;
import android.os.Bundle;

import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;
import android.widget.ListView;

public class MYWifiSetupActivity extends BaseSetupWizardActivity {

    ListView lv;
    private WifiManager mWifiManager;

    String ITEM_KEY = "key";
    ArrayList<String> arraylist = new ArrayList<>();
    ArrayAdapter adapter;
    List<ScanResult> results;
    int size = 0;

    public static final String TAG = WifiSetupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lv = (ListView)findViewById(R.id.wifilist);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        this.adapter =  new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arraylist);
        lv.setAdapter(this.adapter);

        scanWifiNetworks();
    }

    private void scanWifiNetworks(){

        if (mWifiManager.isWifiEnabled() == false) {
            mWifiManager.setWifiEnabled(true);
        }

        arraylist.clear();
        registerReceiver(wifi_receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        mWifiManager.startScan();

        Log.d("WifScanner", "scanWifiNetworks");

        Toast.makeText(this, "Scanning....", Toast.LENGTH_SHORT).show();

    }

    BroadcastReceiver wifi_receiver= new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context c, Intent intent)
        {
            Log.d("WifScanner", "onReceive");
            results = mWifiManager.getScanResults();
            size = results.size();
            unregisterReceiver(this);

            try
            {
                while (size >= 0)
                {
                    size--;
                    arraylist.add(results.get(size).SSID);
                    adapter.notifyDataSetChanged();
                }
            }
            catch (Exception e)
            {
                Log.w("WifScanner", "Exception: "+e);

            }


        }
    };

    @Override
    public void onNavigateNext() {
        super.onNavigateNext();
    }

    @Override
    protected int getTransition() {
        return TRANSITION_ID_SLIDE;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.setup_wifi_page;
    }

    @Override
    protected int getTitleResId() {
        return R.string.setup_wifi;
    }

    @Override
    protected int getIconResId() {
        return R.drawable.ic_sim;
    }

}
