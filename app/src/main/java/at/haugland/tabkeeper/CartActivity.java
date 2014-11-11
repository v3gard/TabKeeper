package at.haugland.tabkeeper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.AbstractMap;


public class CartActivity extends Activity {

    private CBCart _currentCart;
    private Context _c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        _currentCart = MainActivity.cbmanager.getCurrentCart();
        _c = this;

        // Check if cart has been created
        if (_currentCart == null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Du har ikke opprettet en ny barregning. Opprette nå?").setTitle("Ny barregning");
            builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.cbmanager.createCart();
                    updateCart();
                    Toast t = Toast.makeText(getApplicationContext(), "Ny barregning opprettet!", Toast.LENGTH_SHORT);
                    t.show();
                }
            });
            builder.setNegativeButton("Nei", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.create().show();
        }
        else
        {
            updateCart();
        }
        // Enable logic in "payed" button
        final Switch switchBtn = (Switch)findViewById(R.id.cart_btn_toggle_paid);
        final Button paidBtn = (Button)findViewById(R.id.cart_btn_set_paid);
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                paidBtn.setEnabled(switchBtn.isChecked());
            }
        });
        paidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.cbmanager.addCartAsHistoric(_currentCart);
                Toast.makeText(getApplicationContext(), "Barregningen er lagret i historikken.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        // Enable logic when tab item is pressed
        if (_currentCart != null) {
            ListView tabview = (ListView) findViewById(R.id.cart_list);
            CBCartAdapter adapter = new CBCartAdapter(this, R.layout.activity_cart, _currentCart);
            tabview.setAdapter(adapter);

            tabview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int pos, long l) {
                    final CBCart.CBKeyValue cbKeyValue = _currentCart.getItemsAsCBKeyValue().get(pos);
                    final CBItem cbItem = cbKeyValue.getCBItem();

                    View npView = getLayoutInflater().inflate(R.layout.selector_numberpicker, null);

                    final NumberPicker activeItemNumberPicker = (NumberPicker) npView.findViewById(R.id.numberPicker);
                    TextView activeItemDescription = (TextView) npView.findViewById(R.id.cart_item_description);
                    activeItemDescription.setText(cbItem.getDescription());
                    activeItemNumberPicker.setMinValue(1);
                    activeItemNumberPicker.setMaxValue(100);
                    activeItemNumberPicker.setValue(cbKeyValue.value);
                    activeItemNumberPicker.setSelected(false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(_c);
                    builder.setTitle(cbItem.getDisplayName());
                    builder.setView(npView);
                    builder.setPositiveButton("Endre", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            cbKeyValue.value = activeItemNumberPicker.getValue();
                            Toast.makeText(getApplicationContext(), String.format("%s: Endret til %d enheter", cbItem.getDisplayName(), activeItemNumberPicker.getValue()), Toast.LENGTH_SHORT).show();
                            updateCart();
                        }
                    });
                    builder.setNegativeButton("Slett", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            _currentCart.getItemsAsCBKeyValue().remove(pos);
                            Toast.makeText(getApplicationContext(), String.format("Fjernet %s fra barregninga", cbItem.getDisplayName()), Toast.LENGTH_SHORT).show();
                            updateCart();
                        }
                    });
                    builder.create().show();
                }
            });
        }
    }

    public void updateCart()
    {
        _currentCart = MainActivity.cbmanager.getCurrentCart();
        if (_currentCart != null) {
            // update listview with current cart
            ListView listView = (ListView) findViewById(R.id.cart_list);
            CBCartAdapter adapter = new CBCartAdapter(this, R.layout.activity_cart, _currentCart);
            listView.setAdapter(adapter);

            // update listview header with date and total sum
            TextView tabNameDate = (TextView) findViewById(R.id.cart_name_date);
            TextView tabTotalSum = (TextView) findViewById(R.id.cart_total_sum);
            TextView tabTip = (TextView) findViewById(R.id.tv_cart_tip);

            tabNameDate.setText(_currentCart.getDisplayNameWithDate());
            tabTotalSum.setText(String.format("Sum: %.2f kr,-", _currentCart.calculateSum()+_currentCart.getTips()));
            tabTip.setText(String.format("Tips: %.2f kr,-", _currentCart.getTips()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionmenu_cart, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cart_actionitem_tip)
        {
            View tipView = getLayoutInflater().inflate(R.layout.dialog_tip, null);

            final NumberPicker tipMajor = (NumberPicker) tipView.findViewById(R.id.np_tip_major);
            final NumberPicker tipMinor = (NumberPicker) tipView.findViewById(R.id.np_tip_minor);

            tipMajor.setMinValue(0);
            tipMajor.setMaxValue(10000);
            tipMinor.setMinValue(0);
            tipMinor.setMaxValue(99);

            tipMajor.setValue(Math.round((float)Math.floor(_currentCart.getTips())));
            tipMinor.setValue(Math.round((float)(_currentCart.getTips()-Math.floor(_currentCart.getTips()))*100));

            tipMajor.setSelected(false);
            tipMinor.setSelected(false);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Gi tips");
            builder.setView(tipView);
            builder.setPositiveButton("Gi tips", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    float fTipMajor = tipMajor.getValue();
                    float fTipMinor = (float)(tipMinor.getValue())/100;
                    _currentCart.setTips(fTipMajor+fTipMinor);
                    Toast.makeText(getApplicationContext(), String.format("Satte tipsen til %.2f kr,-", _currentCart.getTips()), Toast.LENGTH_SHORT).show();
                    updateCart();
                }
            });
            builder.setNegativeButton("Fjern tips", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    _currentCart.setTips(0);
                    Toast.makeText(getApplicationContext(), String.format("Fjernet tipsen.", _currentCart.getTips()), Toast.LENGTH_SHORT).show();
                    updateCart();
                }
            });
            builder.setNeutralButton("Sett til 10%", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    float fTip = (float)(_currentCart.calculateSum()*0.1);
                    _currentCart.setTips(fTip);
                    Toast.makeText(getApplicationContext(), String.format("Satte tipsen til %.2f kr,-", _currentCart.getTips()), Toast.LENGTH_SHORT).show();
                    updateCart();
                }
            });
            builder.create().show();
        }
        return true;
    }
}
