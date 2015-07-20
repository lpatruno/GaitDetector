package edu.fordham.wisdm.gaitdetector;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Class to select specific activity to start. Gives activity completion status through background
 * color of individual dropdown spinner item. Green indicates completed task, red indicates
 * incomplete task.
 */
public class TaskSelectionActivity extends Activity {

    /**
     * Dropdown spinner to select activity.
     */
    private Spinner mActivitySpinner;

    /**
     * Button to start selected activity.
     */
    private Button mStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_selection);

        addListenerOnButton();
    }

    /**
     * Function that waits for user to select activity from spinner and displays the choice.
     */
    public void addListenerOnButton() {

        mActivitySpinner = (Spinner) findViewById(R.id.activity_spinner);
        mStartButton = (Button) findViewById(R.id.taskselection_start_button);

        mStartButton.setOnClickListener(new OnClickListener() {

            /**
             * Function that makes a toast indicating which activity was selected.
             * @param v
             */
            @Override
            public void onClick(View v) {
                Toast.makeText(TaskSelectionActivity.this,
                        "Spinner Selection: " + String.valueOf(mActivitySpinner.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();
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
