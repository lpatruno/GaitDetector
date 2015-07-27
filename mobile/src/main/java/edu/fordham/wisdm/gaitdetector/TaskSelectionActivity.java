package edu.fordham.wisdm.gaitdetector;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.content.res.Resources;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Calendar;

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
     * Object that holds user information.
     */
    private User user;

    /**
     * String to hold the task chosen by the user.
     */
    private String taskChosen = "";

    /**
     * Hashmap containing the BBS task numbers and labels.
     */
    private HashMap<String, String> tasksMap;

    /**
     * View for purpose of changing icons.
     */
    private View positionOfItemSelected;

    /**
     * Array holding unformatted task Strings
     */
    String[] taskStringArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_selection);

        mStartButton = (Button)findViewById(R.id.taskselection_startbutton);

        /**
         * Get user info from previous activity.
         */
        Intent intent = getIntent();
        final User user = (User)intent.getParcelableExtra("USER");

        /**
         * Get string resources.
         */
        Resources res = getResources();
        //String[] taskStringArray = res.getStringArray(R.array.task_array);
        taskStringArray = res.getStringArray(R.array.task_array);
        String notApplicable = res.getString(R.string.not_applicable);

        /**
         * Initialize and fill hashmap (task id & title) and array with tasks.
         */
        tasksMap = new HashMap<String, String>();
        Task taskData[] = new Task[taskStringArray.length];

        for (int i = 0; i < taskStringArray.length; i++) {
            // Split the string resources into key value pairs
            String task = taskStringArray[i];
            String[] keyVal = task.split("\\|");

            // Fill task hashmap
            tasksMap.put(keyVal[0], keyVal[1]);

            // Fill task array
            Task t = new Task(tasksMap.get(keyVal[0]), notApplicable,
                    R.drawable.ic_incomplete);
            taskData[i] = t;
        }

        /**
         * Create custom adapter and attach array of Task items to ListView.
         */
        TaskAdapter adapter = new TaskAdapter(this, R.layout.activity_list_layout, taskData);
        mTaskListView = (ListView)findViewById(R.id.taskselection_tasklist);
        mTaskListView.setAdapter(adapter);



        /**
         * Method that waits for user to chose a task.
         * Upon task selection, taskChosen is updated.
         */
        mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Cast RelativeLayout to TextView
                TextView tv = ((TextView) view.findViewById(R.id.layout_task_id));
                positionOfItemSelected = view;

                // Get task title from Task object
                /*
                TODO
                Grab the correct String from the HashMap by comparing the text value of
                    taskChosen = tv.getText().toString()
                to the values in the hashmap and then passing the new String
                    key|value
                 */
                taskChosen = taskStringArray[position];
            }
        });

        /**
         * Method to wait for user to click next button.
         */
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!taskChosen.equals("")) {
                    // Testing purposes - comment out when using intent.
                    Toast.makeText(getApplicationContext(), taskChosen, Toast.LENGTH_SHORT).show();

                    ImageView iv = ((ImageView) positionOfItemSelected.findViewById(R.id.layout_completion_icon));
                    iv.setImageResource(R.drawable.ic_complete);

                    // Format and set completion date
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDate = sdFormat.format(c.getTime());
                    TextView tv = ((TextView) positionOfItemSelected.findViewById(R.id.layout_completion_date));
                    tv.setText(getString(R.string.date_completed) + currentDate);

                    Intent i = new Intent(TaskSelectionActivity.this, DataCollectionActivity.class);
                    i.putExtra("USER", user);
                    i.putExtra("TASK", taskChosen);

                    startActivity(i);
                }
                else {
                    Toast.makeText(getApplicationContext(), R.string.no_task_chosen, Toast.LENGTH_SHORT).show();
                }
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
