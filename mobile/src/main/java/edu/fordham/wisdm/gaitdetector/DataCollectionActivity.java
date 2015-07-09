package edu.fordham.wisdm.gaitdetector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
