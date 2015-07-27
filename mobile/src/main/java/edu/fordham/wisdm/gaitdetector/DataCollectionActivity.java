package edu.fordham.wisdm.gaitdetector;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;


/**
 * Activity to allow user to start/stop sensor data collection.
 *
 * @author Luigi Patruno
 */
public class DataCollectionActivity extends Activity {

    /**
     * String for tagging purposes
     */
    private static final String TAG = "DataCollectionActivity";

    /**
     * TextView to display the username
     */
    private TextView mUsername;

    /**
     * String to hold the passed username (for UI)
     */
    private String username;

    /**
     * username.toLowerCase().replace(" ", "_") for file labeling
     */
    private String usernameFileLabel;

    /**
     * TextView to display the task name
     */
    private TextView mTask;

    /**
     * String to hold the passed task description for UI view
     */
    private String task;

    /**
     * BBS number for file labeling
     */
    private String taskID;

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
     * Enables communication between the watch and the phone
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * String to initiate communication between phone and watch.
     */
    private static final String START_SAMPLING = "/start-sampling";

    /**
     * String to stop sampling on the wearable
     */
    private static final String STOP_SAMPLING = "/stop-sampling";

    /**
     * Path String to pass username and task data to the ListenerService
     */
    private static final String USER_DATA = "/user-data";


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
        initializeGoogleApiClient();

    }

    /**
     * Function to retrieve data passed from previous activity through Intents.
     *
     * In the future I will retrieve the data from the Intents. For now, I just hard code strings.
     */
    private void retrieveAndSetPassedData(){


        Intent intent = getIntent();

        User user = (User)intent.getParcelableExtra("USER");
        username = user.getName();
        usernameFileLabel = username.toLowerCase().replace(" ", "_");

        String[] tempTask = intent.getStringExtra("TASK").split("\\|");

        if (tempTask.length == 2) {
            taskID = tempTask[0];
            task = tempTask[1];
        } else {
            Log.e(TAG, "Data not passed correctly between classes.");
            task = "NULL";
            taskID = "000";
        }

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

                    // Send message to the wearable to start sampling
                    sendMessage(START_SAMPLING);

                    // Create intent to start sampling service
                    phoneSensorServiceIntent =
                            new Intent(getApplicationContext(), PhoneSensorService.class);
                    phoneSensorServiceIntent.putExtra("USERNAME", usernameFileLabel);
                    phoneSensorServiceIntent.putExtra("TASK", task);

                    // Start the service and create a notification
                    startService(phoneSensorServiceIntent);
                    createNotification();

                    // Pass message to the ListenerService
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            NodeApi.GetLocalNodeResult nodes = Wearable.NodeApi.getLocalNode(mGoogleApiClient).await();
                            Node node = nodes.getNode();

                            Log.d(TAG, "Activity Node is : "+node.getId()+ " - " + node.getDisplayName());

                            String message = usernameFileLabel + "&&" + task;

                            MessageApi.SendMessageResult result =
                                    Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), USER_DATA, message.getBytes()).await();

                            if (result.getStatus().isSuccess()) {
                                Log.d(TAG, "Message: {" + message + "} sent to: " + node.getDisplayName());
                            }
                            else {
                                Log.e(TAG, "ERROR: failed to send Activity Message");
                            }
                        }
                    }).start();
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

                    // Send message to the wearable to stop sampling
                    sendMessage(STOP_SAMPLING);

                    // Stop the sampling service and cancel the notification
                    stopService(phoneSensorServiceIntent);
                    notificationManager.cancel(NOTIFICATION_ID);
                }
            }
        });
    }

    /**
     * Initialize the GoogleApiClient in order to connect to the wearable device.
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

    /**
     * Disconnect from the Wearable device in the onDestroy method.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
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

    /**
     * Send message to the wearable
     */
    private void sendMessage(final String message) {

        if (mGoogleApiClient.isConnected()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NodeApi.GetConnectedNodesResult nodes =
                            Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

                    for (Node node : nodes.getNodes()) {

                        MessageApi.SendMessageResult result =
                                Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), message, null).await();

                        Log.d(TAG, "Sent to node: " + node.getId() + " with display name: " + node.getDisplayName());

                        if (!result.getStatus().isSuccess()) {
                            Log.e(TAG, "ERROR: failed to send Message: " + result.getStatus());
                        } else {
                            Log.d(TAG, "Message Successfully sent.");
                        }
                    }
                }
            }).start();
        } else {
            Log.e(TAG, "Wearable not connected");
        }

    }

}
