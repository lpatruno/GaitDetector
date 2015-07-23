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
                Asset accelerometerAsset = dataMapItem.getDataMap().getAsset("accelerometerData");

                loadAccelData(accelerometerAsset);

            }
        }

        Log.d(TAG, "Total records: " + accelerationRecords.size());
    }

    private void loadAccelData(Asset asset) {

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult =
                googleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return;
        }

        InputStream bais = Wearable.DataApi.getFdForAsset(
                googleApiClient, asset).await().getInputStream();

        googleApiClient.disconnect();

        try {
            ArrayList<AccelerationRecord> records = (ArrayList<AccelerationRecord>) (
                    new ObjectInputStream(bais)).readObject();

            accelerationRecords.addAll(records);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
