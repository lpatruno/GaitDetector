package edu.fordham.wisdm.gaitdetector;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.view.ViewGroup;

import java.util.List;

/**
 * Class to select specific activity to start. Gives activity completion status through background
 * color of individual items. Green indicates completed task, red indicates incomplete task.
 */
public class TaskSelectionActivity extends Activity {

    /**
     * Button to start selected activity.
     */
    private Button mStartButton;

    /**
     * Listview that holds tasks.
     */
    private ListView mActivityListView;

    private int mSelectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_selection);

        Task task_data[] = new Task[] {
                new Task(R.drawable.powered_by_google_light, "Activity 1", "Date Completed: N/A"),
                new Task(R.drawable.powered_by_google_light, "Activity 1", "Date Completed: N/A"),
                new Task(R.drawable.powered_by_google_light, "Activity 1", "Date Completed: N/A"),
        };

        TaskAdapter adapter = new TaskAdapter(this, R.layout.activity_list_layout, task_data);
        mActivityListView = (ListView)findViewById(R.id.taskselection_activitylist);

        mActivityListView.setAdapter(adapter);

       /* *//**
         * Array of tasks.
         *//*
        String[] activityList = new String[] {
                "Activity 1", "Activity 2", "Activity 3",
                "Activity 4", "Activity 5", "Activity 6",
                "Activity 7", "Activity 8", "Activity 9",
        };

        *//**
         * Create ArrayAdapter and bind to ListView.
         *//*
        final ArrayAdapter<String> activityListArrayAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, activityList);
        mActivityListView = (ListView)findViewById(R.id.taskselection_activitylist);
        mActivityListView.setAdapter(activityListArrayAdapter);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_selection, menu);
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
