package edu.fordham.wisdm.gaitdetector;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.content.res.Resources;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Class to select specific activity to start. Gives task title, completion status through an icon,
 * and task completion date if applicable.
 */
public class TaskSelectionActivity extends Activity {

    /**
     * String for tagging purposes
     */
    private static final String TAG = "TaskSelectionActivity";

    /**
     * Button to start selected task.
     */
    private Button mStartButton;

    /**
     * ListView that holds tasks.
     */
    private ListView mTaskListView;

    /**
     * String to hold the task chosen by the user
     */
    private String taskChosen;

    /**
     * Hashmap containing the BBS task numbers and labels
     */
    private HashMap<String, String> tasksMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_selection);

        Resources res = getResources();
        String[] taskStringArray = res.getStringArray(R.array.task_array);
        String na = res.getString(R.string.not_applicable);

        Task taskData[] = new Task[taskStringArray.length];

        tasksMap = new HashMap<String, String>();

        for (String task_entry: taskStringArray){
            // Split the string resources into key value pairs
            String[] keyVal = task_entry.split("\\|");
            tasksMap.put(keyVal[0], keyVal[1]);
        }

        for (int i = 0; i < taskStringArray.length; i++) {
            Task t = new Task(taskStringArray[i], na, R.drawable.powered_by_google_light);
            taskData[i] = t;
        }


        /**
         * Create instance of custom adapter and attach to ListView.
         */
        TaskAdapter adapter = new TaskAdapter(this, R.layout.activity_list_layout, taskData);
        mTaskListView = (ListView)findViewById(R.id.taskselection_tasklist);
        mTaskListView.setAdapter(adapter);

        mStartButton = (Button)findViewById(R.id.taskselection_startbutton);

        /**
         * method that waits for user to chose a task.
         * Upon task selection, taskChosen is updated.
         */
        mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //THIS LINE DOES NOT WORK AND CASUES IT TO CRASH
                taskChosen = ((TextView)view).getText().toString();
            }
        });


        /**
         * method to wait for user to click next button.
         */
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), taskChosen, Toast.LENGTH_SHORT).show();
            }
        });



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
