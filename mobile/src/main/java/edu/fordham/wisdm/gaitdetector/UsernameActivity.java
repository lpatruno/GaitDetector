package edu.fordham.wisdm.gaitdetector;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;


/**
 * Class that prompts user to input name.
 * Name is then converted into all lowercase version with ' ' replaced
 * with '_'.
 *
 */
public class UsernameActivity extends Activity {

    /**
     * Constant string for tagging purposes
     */
    private final String TAG = "UsernameActivity";


    /**
     * Button to begin next activity
     */
    private Button mNextButton;

    /**
     * EditText to hold username
     */
    private EditText mUsernameText;

    /**
     * String to hold all lower case version of username with ' ' replaced with '_'
     */
    private String mUsernameString;



    /**
     * Function to load the UI elements
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        Log.d(TAG, "onCreate method called");

        mNextButton = (Button)findViewById(R.id.username_next_button);
        mUsernameText = (EditText) findViewById(R.id.username_name_field);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            /**
             * function that waits for user to click next button
             * and determines if entered username is valid.
             *
             * @param v
             */
            @Override
            public void onClick(View v) {

                mUsernameString = mUsernameText.getText().toString().toLowerCase().replace(" ", "_");

                if (!mUsernameString.equals("")) {
                    //Toast.makeText(getApplicationContext(), "Name: " + mUsernameString, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(UsernameActivity.this, DemographicActivity.class);
                    i.putExtra("username", mUsernameString);
                    startActivity(i);

                } else {
                    Toast.makeText(getApplicationContext(), "Please enter valid username", Toast.LENGTH_SHORT).show();
                }

            }

        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_username, menu);
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
