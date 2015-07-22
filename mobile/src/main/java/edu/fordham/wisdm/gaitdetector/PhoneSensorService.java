package edu.fordham.wisdm.gaitdetector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class PhoneSensorService extends Service implements SensorEventListener {

    /**
     * String for tagging
     */
    private final String TAG = "PhoneSensorService";

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
     * String to hold the passed username
     */
    private String username;

    /**
     * String to hold the passed task
     */
    private String task;

    /**
     * The list of acceleration records
     */
    private ArrayList<AccelerationRecord> accelerationRecords = new ArrayList<AccelerationRecord>();

    /**
     * The list of gyroscope records
     */
    private ArrayList<GyroscopeRecord> gyroscopeRecords = new ArrayList<GyroscopeRecord>();

    /**
     * Start command for the phone sensor service.
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // retrieve passed data
        username = intent.getStringExtra("USERNAME");
        task = intent.getStringExtra("TASK");

        // Start sensors
        registerSensorListeners();

        //Don't restart when app is shut down and reopened
        return START_NOT_STICKY;
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
     * Unregister the sensors and release the wake lock.
     *
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wakeLock != null) {
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        }
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }

        //writeAndSendFiles();
    }

    /**
     * Method to write records to files and email these files as attachments.
     *
     */
    private void writeAndSendFiles(){

        final String usernameLower = username.toLowerCase().replace(" ", "_");

        File rootDirectory =
                new File(Environment.getExternalStorageDirectory() + File.separator
                        + "GaitDetector" + File.separator + usernameLower);

        // Create parent directory if does not exist
        if(!rootDirectory.isDirectory()) {
            rootDirectory.mkdirs();
        }

        final String accelerometerFileHandle =
                rootDirectory.getPath() + File.separator + usernameLower + "_" + "taskHere" + "_phone_accel.txt";

        final String gyroscopeFileHandle =
                rootDirectory.getPath() + File.separator + usernameLower + "_" + "taskHere" + "_phone_gyro.txt";

        File accelerometerFile = new File(accelerometerFileHandle);
        File gyroscopeFile = new File(gyroscopeFileHandle);

        // Write the accel records to a file
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(accelerometerFile);
            for (AccelerationRecord record : accelerationRecords) {
                writer.println(record.toString());
            }
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Write the gyro records to a file
        try {
            writer = new PrintWriter(gyroscopeFile);
            for (GyroscopeRecord record : gyroscopeRecords) {
                writer.println(record.toString());
            }
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Email files
        new Thread(
                new EmailData("wisdm.gaitlab@gmail.com", "WiSdM403!", username,
                        accelerometerFile.getAbsolutePath(), gyroscopeFile.getAbsolutePath())).start();
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
     * This method must be overridden according to Android.
     * If the Service is started with startService() rather than onBind(), have this method
     * return null.
     *
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * Class to email the sensor data files as attachments.
     * This class implements runnable in order to be run in a separate thread.
     *
     */
    class EmailData implements Runnable {

        private final String TAG = "EmailData";

        // Gmail account data
        private final String email;
        private final String password;
        private final String recipient = "wisdm.gait@gmail.com";

        // Username
        private final String username;

        // Sensor data files
        private File phoneAccelerometerFile;
        private File phoneGyroscopeFile;

        /**
         * Constructor for the email task.
         *
         * @param email
         * @param password
         * @param phoneAccel
         * @param phoneGyro
         */
        public EmailData(String email, String password, String username, String phoneAccel, String phoneGyro) {
            this.email = email;
            this.password = password;
            this.username = username;
            this.phoneAccelerometerFile = new File(phoneAccel);
            this.phoneGyroscopeFile = new File(phoneGyro);
        }

        /**
         * Method called when the runnable is initiated in the thread. This sends an email
         * containing the sensor data in another Thread.
         *
         */
        @Override
        public void run() {
            GMailSender sender = new GMailSender(email, password);
            try {
                File[] attach = {phoneAccelerometerFile, phoneGyroscopeFile};
                sender.sendMail("Data for " + username, "This is the data", email, recipient, attach);
            } catch (Exception e) {
                Log.e(TAG, "error");
            }
        }

    }
}
