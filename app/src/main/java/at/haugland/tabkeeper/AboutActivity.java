package at.haugland.tabkeeper;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        try {
            String versionString = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView tv = (TextView) findViewById(R.id.about_version);
            tv.setText(versionString);
        }
        catch (Exception e)
        {
            // do nothing
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Pass
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass
        return true;
    }
}
