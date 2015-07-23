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
import android.widget.TextView;

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
     * Broadcast receiver to update the UI when the ListenerService receives messages from the phone
     */
    private BroadcastReceiver mMessageReceiver;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.message);
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

                    // Stop sampling sensors
                    unregisterSensors();
                }
            }
        };

        // Register a LocalBroadcastManager to get messages from the ListenerService
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(mMessageReceiver, new IntentFilter(LISTENER_INTENT));
    }


    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        unregisterSensors();
        super.onDestroy();
    }

    /**
     * This method acquires and registers the sensors and the wake lock for sensor sampling
     *
     */
    private void registerSensorListeners(){

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

    }

    /**
     * Unregister the sensors and stop sampling.
     */
    private void unregisterSensors() {

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
                //accelerationRecords.add(new AccelerationRecord(ts, x, y, z));
                Log.d(TAG, "Accel:\tts:" + ts + "x: " + x);
                break;
            case Sensor.TYPE_GYROSCOPE:
                //gyroscopeRecords.add(new GyroscopeRecord(ts, x, y, z));
                Log.d(TAG, "Gyro:\tts:" + ts + "x: " + x);
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
}
