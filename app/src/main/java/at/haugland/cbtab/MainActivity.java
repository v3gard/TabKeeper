package at.haugland.cbtab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class MainActivity extends Activity {

    public static CBManager cbmanager;
    public ArrayList<CBItem> cbItems;
    public ArrayList<CBCategory> cbCategories;
    public CBCart cbCart; // current cart
    public ArrayList<CBCart> cbHistoricCarts;

    @Override
    protected void onPause() {
        super.onPause();
        // user leaves activity. store content.
        int result = cbmanager.SerializeData(this);
        if (result!=CBManager.DATA_SERIALIZE_SUCCESS)
        {
            Toast.makeText(this, String.format("An error occurred while writing data to the disk drive. Error code %d", result), Toast.LENGTH_SHORT).show();
        }
    }
    private void ResetCBManagerToDefault()
    {
        cbItems = new ArrayList<CBItem>();
        cbCategories = new ArrayList<CBCategory>();
        cbHistoricCarts = new ArrayList<CBCart>();
        cbmanager = new CBManager(cbItems, cbCategories, cbCart, cbHistoricCarts);
        cbmanager.initialize_resources(this.getResources());
    }
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
        // check if datafile exists. if yes, deserialize and restore content. if not, initialize new objects.
        if (CBManager.DataFileExists(this))
        {
            cbmanager = new CBManager();
            int result = cbmanager.DeserializeData(this);
            if (result!=CBManager.DATA_DESERIALIZE_SUCCESS)
            {
                Toast.makeText(this, String.format("An error occurred while reading data from the disk drive. Error code %d", result), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            if (cbmanager == null) // create new cbmanager
            {
                ResetCBManagerToDefault();
            }
            else
            {
                // check if cbmanager data objects are initialized. if not, initialize.
                if (cbmanager.IsInitialized())
                {
                    // do nothing
                }
                else
                { // data objects are not initialized. initialize.
                    if (cbItems == null)
                        cbItems = new ArrayList<CBItem>();
                    if (cbCategories == null)
                        cbCategories = new ArrayList<CBCategory>();
                    if (cbHistoricCarts == null)
                        cbHistoricCarts = new ArrayList<CBCart>();
                    if (cbmanager== null) {
                        cbmanager = new CBManager(cbItems, cbCategories, cbCart, cbHistoricCarts);
                        cbmanager.initialize_resources(this.getResources());
                    }
                }
            }
        }
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

        if (id == R.id.about) {

            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.reset_user_content)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Nullstill alt brukerinnhold? Dette fjerner alle barregninger og varer!").setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ResetCBManagerToDefault();
                    Toast.makeText(getApplicationContext(), "Alt brukerinnhold er nullstilt!", Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getApplicationContext(), "Nullstilling avbrutt av bruker", Toast.LENGTH_SHORT).show();
                }
            });
            builder.create().show();
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
