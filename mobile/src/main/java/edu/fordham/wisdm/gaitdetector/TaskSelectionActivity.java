package edu.fordham.wisdm.gaitdetector;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

/**
 * Class to select specific activity to start. Gives task title, completion status through an icon,
 * and task completion date if applicable.
 */
public class TaskSelectionActivity extends Activity {

    /**
     * Button to start selected task.
     */
    private Button mStartButton;

    /**
     * ListView that holds tasks.
     */
    private ListView mTaskListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_selection);

        /**
         * TEST DATA ARRAY: MUST BE MOVED TO STRINGS.XML W/ APPROPRIATE ID.
         */
        Task task_data[] = new Task[] {
                new Task("Task 1", "Date Completed: N/A", R.drawable.powered_by_google_light),
                new Task("Task 2", "Date Completed: 07/22/2015 10:59AM", R.drawable.powered_by_google_light),
                new Task("Task 3", "Date Completed: 66/66/6666 66:66PM", R.drawable.powered_by_google_light),
        };

        /**
         * Create instance of custom adapter and attach to ListView.
         */
        TaskAdapter adapter = new TaskAdapter(this, R.layout.activity_list_layout, task_data);
        mTaskListView = (ListView)findViewById(R.id.taskselection_tasklist);
        mTaskListView.setAdapter(adapter);
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
