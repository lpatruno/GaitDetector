package edu.fordham.wisdm.gaitdetector;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.TextView;


public class DemographicActivity extends ActionBarActivity {

    /**
     * String to hold all lower case version of username with ' ' replaced with '_'
     */
    private String mUsernameString;

    /**
     * TextView to display username
     */
    private TextView mUsernameTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographic);

        /**
         * Extract username string from intent and display on screen
         */
        Intent intent = getIntent();
        mUsernameString = intent.getExtras().getString("username").toUpperCase().replace("_", " ");
        mUsernameTextView = (TextView)findViewById(R.id.username_textview);
        mUsernameTextView.setText(mUsernameString);
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
