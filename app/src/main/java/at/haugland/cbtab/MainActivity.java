package at.haugland.cbtab;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class MainActivity extends Activity {

    public static CBManager cbmanager;
    public ArrayList<CBItem> cbItems;
    public ArrayList<CBCategory> cbCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set font on buttons in main activity
        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");
        Button btn_menu = (Button)findViewById(R.id.menu);
        Button btn_history = (Button)findViewById(R.id.history);
        Button btn_cart = (Button)findViewById(R.id.cart);
        btn_menu.setTypeface(font);
        btn_history.setTypeface(font);
        btn_cart.setTypeface(font);

        // Initialize lists
        cbItems = new ArrayList<CBItem>();
        cbCategories = new ArrayList<CBCategory>();
        cbmanager = new CBManager(cbItems, cbCategories);
        cbmanager.initialize_resources(this.getResources());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void startMenu(View view)
    {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
    public void startCart(View view)
    {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }
    public void startHistory(View view)
    {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }
}
