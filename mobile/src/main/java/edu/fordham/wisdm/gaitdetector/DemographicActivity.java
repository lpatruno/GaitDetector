package edu.fordham.wisdm.gaitdetector;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;


/**
 * Class that prompts user for demographic information
 * including gender, age, height, special conditions, handedness
 *
 */
public class DemographicActivity extends ActionBarActivity {

    /**
     * Constant string for tagging purposes
     */
    private final String TAG = "DemographicActivity";


    /**
     * Button to begin next activity
     */
    private Button mNextButton;

    /**
     * String to hold username
     */
    private String mUsernameString;

    /**
     * TextView to display username
     */
    private TextView mUsernameTextView;

    /**
     * String to hold gender chosen by user
     */
    private String gender = "";

    /**
     * String to hold age entered by user
     */
    private String age;

    /**
     * String to hold height entered by user
     */
    private String height;

    /**
     * String to save special conditions entered by user
     */
    private String specialConditions;

    /**
     * Spinner that defines the handedness of the user
     */
    private Spinner handednessSpinner;

    /**
     * String to hold the handedness selected by user
     */
    private String handedness;


    /**
     * method to load the UI elements
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographic);

        Log.d(TAG, "onCreate method called");


        /**
         * Extract username string from intent and display on screen
         */
        Intent intent = getIntent();
        mUsernameString = intent.getExtras().getString("username").toUpperCase();
        mUsernameTextView = (TextView)findViewById(R.id.username_textview);
        mUsernameTextView.setText(mUsernameString);

        mNextButton= (Button)findViewById(R.id.demographic_next_button);
        handednessSpinner= (Spinner)findViewById(R.id.handedness_spinner);


        /**
         * method that waits for the user to click next button
         */
        mNextButton.setOnClickListener(new View.OnClickListener() {
            /**
             * method called when user clicks next button
             * @param v
             */
            @Override
            public void onClick(View v) {

                //get information entered by user and save in member variables
                age = ((EditText)findViewById(R.id.demographic_age)).getText().toString();
                height = ((EditText)findViewById(R.id.demographic_height)).getText().toString();
                specialConditions = ((EditText)findViewById(R.id.demographic_special_conditions)).getText().toString();
                handedness= handednessSpinner.getSelectedItem().toString();

                if(!gender.equals("") && !age.equals("") && !height.equals("") ) {

                    //Toast.makeText(getApplicationContext(), "Gender: " + gender + " Age: " + age + " Height: " + height +" Special Conditions: " + specialConditions + " Handedness: " + handedness, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(DemographicActivity.this, TaskSelectionActivity.class);
                    i.putExtra("username", mUsernameString);
                    startActivity(i);

                }
                else{
                    Toast.makeText(getApplicationContext(), "Please answer all fields", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    /**
     * Wait for user to chose radio button option
     * on click, set the gender variable accordingly.
     *
     * @param view
     */
    public void onRadioGenderClicked(View view){
        //is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        //check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_female:
                gender= "Female";
                break;

            case R.id.radio_male:
                gender= "Male";
                break;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_demographic, menu);
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
