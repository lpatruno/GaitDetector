package edu.fordham.wisdm.gaitdetector;

import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ListenerService extends WearableListenerService {

    /**
     * String for tagging purposes
     */
    private static final String TAG = "ListenerService";

    /**
     * The list of acceleration records
     */
    private ArrayList<AccelerationRecord> accelerationRecords = new ArrayList<>();

    /**
     * The list of gyroscope records
     */
    private ArrayList<GyroscopeRecord> gyroscopeRecords = new ArrayList<>();

    public ListenerService() {
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

                Log.d(TAG, "Acquiring sensor data from wearable");

                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                Asset accelerometerAsset = dataMapItem.getDataMap().getAsset("accelerometerAsset");
                Asset gyroscopeAsset = dataMapItem.getDataMap().getAsset("gyroscopeAsset");

                loadSensorData(accelerometerAsset, gyroscopeAsset);

            }
        }

        Log.d(TAG, "Total accelerometer records: " + accelerationRecords.size());
        Log.d(TAG, "Total gyroscope records: " + gyroscopeRecords.size());
    }

    private void loadSensorData(Asset accelAsset, Asset gyroAsset) {

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
            ArrayList<AccelerationRecord> accelTemp = (ArrayList<AccelerationRecord>) (
                    new ObjectInputStream(accelBais)).readObject();

            accelerationRecords.addAll(accelTemp);

            ArrayList<GyroscopeRecord> gyroTemp = (ArrayList<GyroscopeRecord>) (
                    new ObjectInputStream(gyroBais)).readObject();

            gyroscopeRecords.addAll(gyroTemp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
