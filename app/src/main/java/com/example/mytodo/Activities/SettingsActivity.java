package com.example.mytodo.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.mytodo.R;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    Context context;

    // For Brightness
    private SeekBar sbBrightness;
    private Switch switchFlashlight;
    private int brightness;

    // For FlashLight
    private CameraManager mCameraManager;
    private String mCameraID;

    // For WiFi
    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;
    private ListView lvWifi;
    private String[] wifis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.action_settings);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        // Code for adjusting brightness

        sbBrightness = (SeekBar) findViewById(R.id.sbBrightness);

        context = getApplicationContext();
        brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0);

        sbBrightness.setMax(255);
        sbBrightness.setKeyProgressIncrement(1);
        sbBrightness.setProgress(brightness);

        sbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    if (Settings.System.canWrite(context)) {
                        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
                    } else {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        // Code for turning on/off flashlight

        switchFlashlight = (Switch) findViewById(R.id.switchFlashlight);
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            mCameraID = mCameraManager.getCameraIdList()[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

        switchFlashlight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        boolean systemHasFlash = getApplicationContext()
                                .getPackageManager()
                                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

                        if (!systemHasFlash) {
                            showSystemHasNoFlash();
                        }
                    }
                    mCameraManager.setTorchMode(mCameraID, isChecked);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        // Code for connecting to a wifi

        lvWifi = findViewById(R.id.lvWifi);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        wifiReceiver = new WifiReceiver();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager.startScan();

        lvWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ssid= ((TextView) view).getText().toString();
                connectToWifi(ssid);
            }
        });

    }

    private void showSystemHasNoFlash() {
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Flash Not Available")
            .setMessage("Flash is not available on this device")
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switchFlashlight.setChecked(false);
                }
            })
            .create();
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onPause() {
        unregisterReceiver(wifiReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 87);
            }
            if(checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.CHANGE_WIFI_STATE}, 87);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 87);
            }
        }

        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    class WifiReceiver extends BroadcastReceiver {
        @SuppressLint("UseValueOf")
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> wifiScanList = wifiManager.getScanResults();
            wifis = new String[wifiScanList.size()];
            for (int i = 0; i < wifiScanList.size(); i++) {
                wifis[i] = wifiScanList.get(i).toString();
            }

            String[] filtered = new String[wifiScanList.size()];

            int index = 0;
            for (int i = 0; i < wifiScanList.size(); i++) {
                String[] wifiInfo = wifiScanList.get(i).toString().split(",");
                filtered[index++] = wifiInfo[0].substring(5).trim();
            }

            lvWifi.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.wifi_list_item, R.id.tvWifiItem, filtered));
        }
    }

    private void connectToWifi(final String ssid) {
        final EditText wifiPassword = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Connect to " + ssid)
                .setMessage("Enter password")
                .setView(wifiPassword)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = wifiPassword.getText().toString();
                        startConnection(ssid, password);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void startConnection(String ssid, String password) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = String.format("\"%s\"", ssid);
        wifiConfiguration.preSharedKey = String.format("\"%s\"", password);

        int networkID = wifiManager.addNetwork(wifiConfiguration);
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkID, true);
        wifiManager.reconnect();

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"\"" + ssid + "\"\"";
        conf.preSharedKey = "\"" + password + "\"";
        wifiManager.addNetwork(conf);
    }

}
