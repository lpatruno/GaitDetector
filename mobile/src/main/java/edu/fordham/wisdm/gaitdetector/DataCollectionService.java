package edu.fordham.wisdm.gaitdetector;

import android.content.Intent;

import com.google.android.gms.wearable.WearableListenerService;

/**
 * This class is the service responsible for
 *      1. Sampling from the device sensors
 *      2. Connecting with the Wear device
 *      3. Receive data events from the wearable
 *      4. Create and save files containing data
 *      5. Transfer the files
 *
 * @author Luigi Patruno
 */
public class DataCollectionService extends WearableListenerService {

    /**
     * String for tagging purposes
     */
    private static final String TAG = "DataCollectionService";

    /**
     * String to hold the passed username
     */
    private String username;

    /**
     * String to hold the passed task
     */
    private String task;

    /**
     * This function is called when the service is first started from the DataCollectionActivity
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

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }
}
