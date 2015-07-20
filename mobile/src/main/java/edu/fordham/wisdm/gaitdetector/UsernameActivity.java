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

/**
 * To be filled in by Julia Getsos
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
     * Function to load the UI elements
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        Log.d(TAG, "onCreate method called");

        // mNextButton = (Button)findViewById(R.id.next);
        Button next= (Button)findViewById(R.id.next);


        next.setOnClickListener(new View.OnClickListener() {
            /**
             * function that waits for user to click next button
             * and determines if entered username is valid.
             *
             * @param v
             */
            @Override
            public void onClick(View v) {

                // Declare the EditText variable outside of the onCreate method.
                // Initialize the EditText variable outside of the click handler
                EditText username = (EditText) findViewById(R.id.username);
                final String name = username.getText().toString().toLowerCase().replace(" ", "_");

                if (!name.equals("")) {
                    Toast.makeText(getApplicationContext(), "Name: " + name, Toast.LENGTH_SHORT).show();
                }
                else {
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
