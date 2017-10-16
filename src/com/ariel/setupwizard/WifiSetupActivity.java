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

import com.ariel.setupwizard.util.SetupWizardUtils;
import com.android.setupwizardlib.SetupWizardListLayout;

import android.net.wifi.WifiManager;

public class WifiSetupActivity extends BaseSetupWizardActivity {

    private SetupWizardListLayout mListLayout;

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
        mListLayout = (SetupWizardListLayout) findViewById(R.id.setup_wifi);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        this.adapter =  new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arraylist);
        mListLayout.setAdapter(this.adapter);

        scanWifiNetworks();

//        ArrayAdapter<Integer> itemsAdapter =
//                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mWifiList);
//        layout.setAdapter(itemsAdapter);
        layout.setHeaderText("SetupWizardListLayout");
        layout.setIllustration(getResources().getDrawable(R.drawable.bg2));
        layout.setIllustrationAspectRatio(4f);
        layout.getNavigationBar().setNavigationBarListener(new NavigationBar.NavigationBarListener() {
            @Override
            public void onNavigateBack() {
                onBackPressed();
            }

            @Override
            public void onNavigateNext() {
                //startActivity(new Intent(ThirdActivity.this, SecondActivity.class));
                super.onNavigateNext();
            }
        });
    }

    private void scanWifiNetworks(){

        if (mWifiManager.isWifiEnabled() == false) {
            mWifiManager.setWifiEnabled(true);
        }

        arraylist.clear();
        registerReceiver(wifi_receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifi.startScan();

        Log.d("WifScanner", "scanWifiNetworks");

        Toast.makeText(this, "Scanning....", Toast.LENGTH_SHORT).show();

    }

    BroadcastReceiver wifi_receiver= new BroadcastReceiver()
    {

        @Override
        public void onReceive(Context c, Intent intent)
        {
            Log.d("WifScanner", "onReceive");
            results = wifi.getScanResults();
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
