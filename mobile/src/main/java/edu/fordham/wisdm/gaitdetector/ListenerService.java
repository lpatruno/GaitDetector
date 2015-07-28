package edu.fordham.wisdm.gaitdetector;


import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ListenerService extends WearableListenerService {

    /**
     * String for tagging purposes
     */
    private static final String TAG = "ListenerService";

    /**
     * The list of acceleration records
     */
    private ArrayList<ThreeTupleRecord> accelerationRecords = new ArrayList<>();

    /**
     * The list of gyroscope records
     */
    private ArrayList<ThreeTupleRecord> gyroscopeRecords = new ArrayList<>();

    /**
     * String to pass user and activity data to the ListenerService
     */
    private static final String USER_DATA = "/user-data";

    /**
     * String to hold the passed username for file labeling
     */
    private String username;

    /**
     * String to hold the passed task for file labeling
     */
    private String task;


    public ListenerService() {
    }

    /**
     * Callback method to receive the username and task data from the DataCollectionActivity for
     * file labeling purposes.
     *
     * @param messageEvent
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        if (messageEvent.getPath().equals(USER_DATA)){

            String messageLiteral = new String(messageEvent.getData());
            String[] message = messageLiteral.split("\\&\\&");

            if (message.length == 2) {
                username = message[0];
                task = message[1];
                Log.d(TAG, "User: " + username + "\tTask: " + task);
            }
        }
    }


    /**
     * Method to acquire data from the wearable device.
     *
     * @param dataEvents
     */
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        for (DataEvent event : dataEvents) {

            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals("/sensor-data")) {

                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                Asset accelerometerAsset = dataMapItem.getDataMap().getAsset("accelerometerAsset");
                Asset gyroscopeAsset = dataMapItem.getDataMap().getAsset("gyroscopeAsset");

                unpackWatchData(accelerometerAsset, gyroscopeAsset);

            }
        }

        writeFiles();
    }

    /**
     * Method to unpack the streamed data from the Wearable device and add the
     * tuple records to the appropriate data structures
     *
     * @param accelAsset
     * @param gyroAsset
     */
    private void unpackWatchData(Asset accelAsset, Asset gyroAsset) {

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult =
                googleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return;
        }

        InputStream accelBais = Wearable.DataApi.getFdForAsset(
                googleApiClient, accelAsset).await().getInputStream();

        InputStream gyroBais = Wearable.DataApi.getFdForAsset(
                googleApiClient, gyroAsset).await().getInputStream();


        googleApiClient.disconnect();

        try {
            ArrayList<ThreeTupleRecord> accelTemp = (ArrayList<ThreeTupleRecord>) (
                    new ObjectInputStream(accelBais)).readObject();

            accelerationRecords.addAll(accelTemp);

            ArrayList<ThreeTupleRecord> gyroTemp = (ArrayList<ThreeTupleRecord>) (
                    new ObjectInputStream(gyroBais)).readObject();

            gyroscopeRecords.addAll(gyroTemp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the watch sensor data to files
     */
    private void writeFiles() {

        FileSaver fileSaver = new FileSaver(username);

        fileSaver.writeFile(task, FileSaver.WATCH_ACCEL, accelerationRecords);
        fileSaver.writeFile(task, FileSaver.WATCH_GYRO, gyroscopeRecords);

    }
}
