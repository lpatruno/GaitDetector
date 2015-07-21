package edu.fordham.wisdm.gaitdetector;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


/**
 * Activity to allow user to start/stop sensor data collection.
 *
 * @author Luigi Patruno
 */
public class DataCollectionActivity extends Activity {

    /**
     * String for tagging purposes
     */
    private final String TAG = "DataCollectionActivity";

    /**
     * TextView to display the username
     */
    private TextView mUsername;

    /**
     * String to hold the passed username
     */
    private String username;

    /**
     * TextView to display the task name
     */
    private TextView mTask;

    /**
     * String to hold the passed task
     */
    private String task;

    /**
     * Button to start the data sampling service
     */
    private Button mStartButton;

    /**
     * Button to stop the data sampling service
     */
    private Button mStopButton;

    /**
     * Intent to trigger the PhoneSensorService
     */
    private Intent phoneSensorServiceIntent;

    /**
     * Builder for Android Notification
     */
    private NotificationCompat.Builder notificationBuilder;

    /**
     * Manager used to issue the Notification when the PhoneSensorService is started
     */
    private NotificationManager notificationManager;

    /**
     * Id number passed to the NotificationManager to issue and cancel Notification
     */
    private final int NOTIFICATION_ID = 0;

    /**
     * Initialize the UI for the DataCollectionActivity.
     *
     * This includes TextViews for the username and task name and Buttons
     * to start and stop sensor data collection.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection);

        mUsername = (TextView) findViewById(R.id.dca_username);
        mTask = (TextView) findViewById(R.id.dca_task);
        mStartButton = (Button) findViewById(R.id.dca_start_button);
        mStopButton = (Button) findViewById(R.id.dca_stop_button);

        retrieveAndSetPassedData();
        setButtonClickListeners();
    }

    /**
     * Function to retrieve data passed from previous activity through Intents.
     *
     * In the future I will retrieve the data from the Intents. For now, I just hard code strings.
     */
    private void retrieveAndSetPassedData(){

        /*
        Intent intent = getIntent();

        username = intent.getStringExtra("USERNAME");
        task = intent.getStringExtra("TASK");
        */

        username = "Luigi Patruno";
        task = "Turn 360 degrees";

        mUsername.setText(username);
        mTask.setText(task);
    }

    /**
     * Function to set the start/stop button click listeners for the DataCollectionActivity
     */
    private void setButtonClickListeners(){

        /**
         * If the service is not running, start the service and issue a notification.
         */
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isMyServiceRunning(PhoneSensorService.class)) {
                    phoneSensorServiceIntent =
                            new Intent(getApplicationContext(), PhoneSensorService.class);

                    // Pass this data for file labeling purposes
                    phoneSensorServiceIntent.putExtra("USERNAME", username);
                    phoneSensorServiceIntent.putExtra("TASK", task);

                    startService(phoneSensorServiceIntent);

                    createNotification();
                }
            }
        });

        /**
         * If the service is running, stop the service and cancel the notification.
         */
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isMyServiceRunning(PhoneSensorService.class)) {
                    stopService(phoneSensorServiceIntent);
                    notificationManager.cancel(NOTIFICATION_ID);
                }
            }
        });
    }

    /**
     * Create notification that appears if the service is started successfully.
     */
    private void createNotification(){

        notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.cast_ic_notification_0)
                        .setTicker("GaitDetector service started")
                        .setContentTitle("GaitDetector")
                        .setContentText("Sampling device sensors")
                        .setOngoing(true);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }


    /**
     * Function to check if a service is running.
     *
     * See the accepted answer to this StackOverflow post
     * http://stackoverflow.com/questions/600207/how-to-check-if-a-service-is-running-on-android
     * @param serviceClass
     * @return
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_collection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
