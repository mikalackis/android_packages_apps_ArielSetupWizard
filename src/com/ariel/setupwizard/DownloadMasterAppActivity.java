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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.MccTable;
import com.android.internal.telephony.TelephonyIntents;
import com.android.setupwizardlib.util.WizardManagerHelper;

import com.ariel.setupwizard.R;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import android.graphics.Bitmap;

public class DownloadMasterAppActivity extends BaseSetupWizardActivity {

    public static final String TAG = DownloadMasterAppActivity.class.getSimpleName();

    private static final String PARENTAL_APP_URL = "http://play.google.com/store/apps/details?id=com.ariel.guardian.parentalcontrol";

    private ImageView mQrImageView;

    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 700;
    public final static int HEIGHT = 700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNextAllowed(true);

        mQrImageView = (ImageView) findViewById(R.id.qr_code);

        generateAndDisplayQRCode();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected int getTransition() {
        return TRANSITION_ID_SLIDE;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.setup_download_master_app;
    }

    @Override
    protected int getTitleResId() {
        return R.string.setup_download_master_app;
    }

    @Override
    protected int getIconResId() {
        return R.drawable.ic_locale;
    }

    private void generateAndDisplayQRCode() {
        new QRCodeGenerator().execute(PARENTAL_APP_URL);
    }

    private class QRCodeGenerator extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... param) {
            try {
                return generateDeviceQRCode(param[0]);
            } catch (WriterException we) {
                return null;
            }
        }

        private Bitmap generateDeviceQRCode(String str) throws WriterException {
            BitMatrix result;
            try {
                result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
            } catch (IllegalArgumentException iae) {
                // Unsupported format
                return null;
            }

            int width = result.getWidth();
            int height = result.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                mQrImageView.setImageBitmap(result);
            }
        }

    }

}
