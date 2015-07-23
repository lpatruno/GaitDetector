package edu.fordham.wisdm.gaitdetector;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends Activity implements SensorEventListener {

    /**
     * String for tagging purposes
     */
    private final String TAG = "MainActivity";

    /**
     * String to identify the custom intent from the ListenerService
     */
    private static final String LISTENER_INTENT = "listener_intent";

    /**
     * String to initiate communication between phone and watch.
     */
    private static final String START_SAMPLING = "/start-sampling";

    /**
     * String to stop sampling on the wearable
     */
    private static final String STOP_SAMPLING = "/stop-sampling";

    /**
     * TextView to display messages indicating status
     */
    private TextView mTextView;

    /**
     * Button to stop sampling the device sensors
     */
    private Button mStopBtn;

    /**
     * Broadcast receiver to update the UI when the ListenerService receives messages from the phone
     */
    private BroadcastReceiver mMessageReceiver;

    /**
     * Enables communication between the watch and the phone
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * The sampling rate in microseconds to collect acceleration records at (this is 20Hz)
     */
    private int SAMPLE_RATE = 50000;

    /**
     * Sensor manager to acquire the device sensors.
     *
     */
    private SensorManager mSensorManager;

    /**
     * Handle for the accelerometer sensor
     */
    private Sensor mAccelerometer;

    /**
     * Handle for the gyroscope sensor
     */
    private Sensor mGyroscope;

    /**
     * I am used in determining and manipulating the state of the screen.
     */
    private PowerManager powerManager = null;

    /**
     * I am responsible for keeping the device partly awake while collecting data.
     */
    private PowerManager.WakeLock wakeLock = null;

    /**
     * The list of acceleration records
     */
    private ArrayList<AccelerationRecord> accelerationRecords;

    /**
     * The list of gyroscope records
     */
    private ArrayList<GyroscopeRecord> gyroscopeRecords;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.message);
                mStopBtn = (Button) stub.findViewById(R.id.stop_btn);

                mStopBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        unregisterSensors();
                    }
                });
            }
        });

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message");

                if (message.equals(START_SAMPLING)){
                    mTextView.setText("Sampling sensors...");

                    // Start sampling sensors
                    registerSensorListeners();

                } else if (message.equals(STOP_SAMPLING)){
                    mTextView.setText("Sampling completed");

                    finalizeDataCollection();
                }
            }
        };

        // Register a LocalBroadcastManager to get messages from the ListenerService
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(mMessageReceiver, new IntentFilter(LISTENER_INTENT));

        initializeGoogleApiClient();
    }

    /**
     * Initialize the GoogleApiClient in order to connect to the phone.
     *
     */
    private void initializeGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d(TAG, "onConnected method called");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(TAG, "Connection to wearable suspended. Code: " + i);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnection failed: " + connectionResult);
                    }
                })
                .addApi(Wearable.API)
                .build();
    }

    /**
     * Connect to the Wearable device in the onResume method.
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        unregisterSensors();
        mGoogleApiClient.disconnect();
    }

    /**
     * This method acquires and registers the sensors and the wake lock for sensor sampling
     *
     */
    private void registerSensorListeners(){

        accelerationRecords = new ArrayList<AccelerationRecord>();
        gyroscopeRecords = new ArrayList<GyroscopeRecord>();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Get the accelerometer and gyroscope sensors if they exist
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
            mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }

        // Acquire the wake lock to sample with the screen off
        powerManager = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);

        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wakeLock.acquire();

        mSensorManager.registerListener(this, mAccelerometer, SAMPLE_RATE);
        mSensorManager.registerListener(this, mGyroscope, SAMPLE_RATE);

        mStopBtn.setVisibility(View.VISIBLE);

    }

    /**
     * This method is called when the stop message is received either from the
     * button on the wearable UI or from the phone.
     * We unregister the sensors and then send the sensor data to the phone
     */
    private void finalizeDataCollection() {

        // Stop sampling sensors
        unregisterSensors();

        // Send data to the phone
        sendData();
    }

    /**
     * Unregister the sensors and stop sampling.
     */
    private void unregisterSensors() {

        mStopBtn.setVisibility(View.GONE);

        if (wakeLock != null) {
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        }
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }


    /**
     * Callback method for the sensors.
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        long ts = System.currentTimeMillis();
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        switch(event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerationRecords.add(new AccelerationRecord(ts, x, y, z));
                break;
            case Sensor.TYPE_GYROSCOPE:
                gyroscopeRecords.add(new GyroscopeRecord(ts, x, y, z));
                break;
        }

    }

    /**
     * Method needs to be overridden according to Android
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    /**
     * Send sensor data to the phone.
     */
    private void sendData() {

        if (mGoogleApiClient.isConnected()){

            new Thread(new Runnable() {

                @Override
                public void run() {

                    try{
                        // Initialize sensor dataMap for transfer
                        PutDataMapRequest dataMap = PutDataMapRequest.create("/sensor-data");

                        // Convert accel records to byte array
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ObjectOutputStream oos = new ObjectOutputStream(baos);

                        oos.writeObject(accelerationRecords);
                        oos.flush();
                        oos.close();

                        byte[] accelByte = baos.toByteArray();


                        // Create Asset from accel records
                        Asset accelerometerData = Asset.createFromBytes(accelByte);

                        // Append the accel records Asset
                        dataMap.getDataMap().putAsset("accelerometerData", accelerometerData);

                        PutDataRequest request = dataMap.asPutDataRequest();
                        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                                .putDataItem(mGoogleApiClient, request);


                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            Log.e(TAG, "Wearable not connected");
        }

    }

}
