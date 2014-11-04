package at.haugland.cbtab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
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
        ListView tabview = (ListView)findViewById(R.id.cart_list);
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
            tabNameDate.setText(_currentCart.getDisplayNameWithDate());
            tabTotalSum.setText(String.format("Sum: %.2f kr,-", _currentCart.calculateSum()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cart, menu);
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
    private class CBCartAdapter extends BaseAdapter
    {
        LayoutInflater _layoutInflater;
        CBCart _cbCart;

        public CBCartAdapter(Context context, int textViewResourceId, CBCart cbCart)
        {
            _layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            _cbCart = cbCart;
        }
        @Override
        public int getCount()
        {
            return _cbCart.size();
        }
        @Override
        public AbstractMap.SimpleEntry<CBItem, Integer> getItem(int position)
        {
            CBItem item = _cbCart.getItemsAsArrayList().get(position);
            Integer value = _cbCart.getItems().get(item);
            return new AbstractMap.SimpleEntry<CBItem, Integer>(item, value);
        }
        @Override
        public long getItemId(int position)
        {
            return 0;
        }
        public View getView(int position, View convertView, ViewGroup parent)
        {
            CBCartViewHolder cbCartViewHolder;
            if (convertView==null)
            {
                convertView = _layoutInflater.inflate(R.layout.activity_cart_list_item, null);
                cbCartViewHolder = new CBCartViewHolder();
                convertView.setTag(cbCartViewHolder);
            }
            else
            {
                cbCartViewHolder = (CBCartViewHolder)convertView.getTag();
            }
            CBItem cbItem = _cbCart.getItemsAsArrayList().get(position);
            int cbValue = _cbCart.getItems().get(cbItem);
            cbCartViewHolder.tvDisplayName = detail(convertView, R.id.cart_item_name, cbItem.getDisplayName());
            cbCartViewHolder.tvAmount = detail(convertView, R.id.cart_item_amount, String.format("%d",cbValue));
            cbCartViewHolder.tvTotal = detail(convertView, R.id.cart_item_price_sum, String.format("%.2f", cbValue*cbItem.getPrice()));
            //cbCategoryViewHolder.tvDisplayName = detail(convertView, R.id.item_displayName, _cbCategory.getItems().get(position).getDisplayName());
            //cbCategoryViewHolder.tvUnit = detail(convertView, R.id.item_unit, _cbCategory.getItems().get(position).getDisplayUnit());
            //cbCategoryViewHolder.tvPrice = detail(convertView, R.id.item_price, String.format("%s kr", _cbCategory.getItems().get(position).getPrice().toString()));
            //cbCategoryViewHolder.ivIcon = detail(convertView, R.id.item_icon, _cbCategory.getImageIcon());
//
            return convertView;
        }
        private class CBCartViewHolder
        {
            TextView tvDisplayName, tvUnit, tvPrice, tvTotal, tvAmount;
        }
        private TextView detail(View v, Integer resourceId, String text)
        {
            TextView tv = (TextView) v.findViewById(resourceId);
            tv.setText(text);
            return tv;
        }

    }
}
