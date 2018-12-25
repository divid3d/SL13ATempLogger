package com.example.divided.sl13atemplogger;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.divided.sl13atemplogger.model.TempMeasurement;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    NfcAdapter mNfcAdapter;
    PendingIntent mPendingIntent;
    Tag tag;

    TextView mUID;
    TextView mDSFID;
    TextView mMemorySize;
    TextView mCurrentTemp;
    TextView mLogForm;
    TextView mStorageRule;
    TextView mCurrentStatus;
    Button mActive;
    Button mPassive;
    Button mLogState;
    Button mReadMemory;
    Button mReadTemperatures;

    boolean mIsTempReadPerforming = false;

    ArrayList<TempMeasurement> buffer = new ArrayList<>();
    private int TIME_PERIOD = 1; // 1 s period for testing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mCurrentTemp = findViewById(R.id.toolbar_current_temperature);
        mUID = findViewById(R.id.text_view_uuid_value);
        mDSFID = findViewById(R.id.text_view_dsfid_value);
        mMemorySize = findViewById(R.id.text_view_memory_size_value);

        mActive = findViewById(R.id.btn_active);
        mPassive = findViewById(R.id.btn_passive);
        mLogState = findViewById(R.id.btn_log_state);
        mReadMemory = findViewById(R.id.btn_read_memory);
        mReadTemperatures = findViewById(R.id.btn_read_temperature);
        mLogForm = findViewById(R.id.text_view_log_form_value);
        mStorageRule = findViewById(R.id.text_view_storage_rule_value);
        mCurrentStatus = findViewById(R.id.text_view_current_status_value);

        mActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tag != null) {
                    SL13A.startLog(tag, new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0});
                }
            }
        });

        mPassive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tag != null) {
                    SL13A.setPassive(tag);
                }
            }
        });

        mLogState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tag != null) {
                    SL13A.getLogState(tag);
                }
            }
        });

        mReadMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tag != null) {
                    SL13A.readBlocks(tag, (byte) 0x00, (byte) 0x18);
                }
            }
        });

        mReadTemperatures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tag != null && !mIsTempReadPerforming) {
                    new Thread(new Runnable() {
                        public void run() {
                            mIsTempReadPerforming = true;
                            buffer.clear();
                            int counter = 0;
                            for (int i = 0x01; i <= 3; i++) {
                                try {
                                    int readingNumber = 0;
                                    double[] tempReadings = SL13A.getTemperatureCodesFromMemoryBlock(tag, (byte) i);
                                    buffer.add(new TempMeasurement((float) tempReadings[0], (counter * TIME_PERIOD), Utils.UNIT_SECOND));
                                    buffer.add(new TempMeasurement((float) tempReadings[1], (counter * TIME_PERIOD) + 1, Utils.UNIT_SECOND));
                                    buffer.add(new TempMeasurement((float) tempReadings[2], (counter * TIME_PERIOD) + 2, Utils.UNIT_SECOND));

                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Log.e("Read temperature code", "Couldn't get temperature data");
                                }
                                counter += 3;
                            }
                            mIsTempReadPerforming = false;

                            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelableArrayList("data", buffer);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }).start();
                }
            }
        });

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "Device don't have NFC adapter", Toast.LENGTH_LONG).show();
            return;
        } else {
            if (!mNfcAdapter.isEnabled()) {
                enableNfcRequest();
            }
        }

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Toast.makeText(this, "Device discovered", Toast.LENGTH_SHORT).show();

            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            try {
                byte[] systemInformation = SL13A.getSystemInformation(tag);
                mCurrentTemp.setText(String.format("Current temp: %.1f °C", SL13A.tempResponseToCelsius(SL13A.getTemperature(tag))));
                mUID.setText(SL13A.getUIDFromSystemInfo(systemInformation));
                mDSFID.setText(SL13A.getDSFIDFromSystemInfo(systemInformation));
                mMemorySize.setText(SL13A.getTagMemorySizeFromSystemInfo(systemInformation) + " byte");
                byte[] measurementSetup = SL13A.getMeasurementSetup(tag);
                mLogForm.setText(SL13A.getLogForm(SL13A.getLogModeFromMeasurementSetup(measurementSetup)));
                mStorageRule.setText(SL13A.getStorageRuleFromMeasurementSetup(measurementSetup));
                byte[] logState = SL13A.getLogState(tag);
                byte[] measurementStatus = SL13A.getMeasurementStatusFromLogState(logState);
                mCurrentStatus.setText(SL13A.getCurrentState(measurementStatus), TextView.BufferType.SPANNABLE);
            } catch (IOException e) {
                e.printStackTrace();
            }


            //SL13A.initialize(tag, new byte[]{(byte) 0, (byte) 0}, new byte[]{(byte) 0, (byte) 1}); // 1 jest LSB przy transmisji?
            //SL13A.setLogMode(tag, SL13A.composeLogModeParameter());

            //byte[] test = SL13A.readSingleBlock(tag,(byte) 0x0C);
            //Log.e("test",Utils.byteArrayToHexString(new byte[]{test[0]}));
            //SL13A.readSingleBlock(tag,(byte)0x00);
            //SL13A.readSingleBlock(tag,(byte)0x01);
            //SL13A.readSingleBlock(tag,(byte)0x0A);
            //SL13A.readBlocks(tag,(byte)0x00,(byte)0x18);
            //SL13A.batteryResponseToVoltage(SL13A.getBatteryLevel(tag), SL13A.Battery.BATTERY_3V);
            //SL13A.setPassive(tag);
            //SL13A.startLog(tag,new byte[]{(byte)0,(byte)0,(byte)0,(byte)0});
            //SL13A.getLogState(tag);
            //SL13A.readBlocks(tag,(byte)0x00,(byte)0x18);
            //SL13A.initialize(tag,new byte[]{(byte)0,(byte)1},new byte[]{(byte)0,(byte)255});
            //SL13A.getMeasurementSetup(tag);
            //SL13A.getCalibrationData(tag);
            //SL13A.initialize(tag, new byte[]{(byte) 0, (byte) 0}, new byte[]{(byte) 0, (byte) 1}); // 1 jest LSB przy transmisji?
            //SL13A.setLogMode(tag, SL13A.composeLogModeParameter());
            //SL13A.startLog(tag, new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0});
        }

    }

    public void enableNfcRequest() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("NFC is currently disabled");
        dialog.setMessage("Please turn on NFC");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }
}
