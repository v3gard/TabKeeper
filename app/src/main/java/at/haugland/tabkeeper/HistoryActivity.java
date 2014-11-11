package at.haugland.tabkeeper;

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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class HistoryActivity extends Activity {

    private ArrayList<CBCart> _carts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // get carts
        _carts = MainActivity.cbmanager.getHistoricCarts();

        // populate history list
        ListView lv = (ListView) findViewById(R.id.history_list);
        lv.addHeaderView(getLayoutInflater().inflate(R.layout.activity_historic_list_header, null));
        final CBHistoricCartAdapter adapter = new CBHistoricCartAdapter(this, R.layout.activity_history, _carts);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                View cartView = getLayoutInflater().inflate(R.layout.dialog_historic_cart_show, null);
                ListView lvCart = (ListView) cartView.findViewById(R.id.historic_cart_list);
                TextView tvTip = (TextView) cartView.findViewById(R.id.tv_tip);
                TextView tvSum = (TextView) cartView.findViewById(R.id.tv_sum);
                TextView tvTotal = (TextView) cartView.findViewById(R.id.tv_total);

                final CBCart cbCart = _carts.get(pos-1); // we need to subtract 1 as the previous view also contains a header, thus incrementing the position by one.
                String cartName = cbCart.getDisplayNameWithDate();
                ArrayList<String> items = cbCart.getItemsAsString();
                CBCartAdapter cca = new CBCartAdapter(HistoryActivity.this, R.layout.dialog_historic_cart_show_item, cbCart);
                lvCart.setAdapter(cca);

                tvSum.setText(String.format("Sum: %.2f kr,-",cbCart.calculateSum()));
                tvTip.setText(String.format("Tips: %.2f kr,-",cbCart.getTips()));
                tvTotal.setText(String.format("Total: %.2f kr,-",cbCart.calculateSum()+cbCart.getTips()));

                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                builder.setTitle(cbCart.getDisplayNameWithDate());
                builder.setView(cartView);
                builder.setPositiveButton("Lukk", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Toast.makeText(getApplicationContext(), "Ferdig å se", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Slett", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(HistoryActivity.this);
                        builder2.setMessage("Vil du slette barregninga?").setTitle("Slette barregning");
                        builder2.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.cbmanager.removeCart(cbCart);
                                adapter.notifyDataSetChanged();
                                updateView();
                                Toast.makeText(getApplicationContext(), "Slettet barregninga", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder2.setNegativeButton("Nei", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        builder2.create().show();
                    }
                });
                builder.create().show();
            }
        });
        lv.setAdapter(adapter);

        // update listview header with sum of all sums
        updateView();
    }
    public void updateView()
    {
        // update listview header with sum of all sums
        float fSum_of_all_sums = 0;
        TextView sum_of_all_sums = (TextView) findViewById(R.id.historic_sum_of_all_sums);
        for (CBCart cart : MainActivity.cbmanager.getHistoricCarts())
        {
            fSum_of_all_sums += cart.calculateSum();
            fSum_of_all_sums += cart.getTips();
        }
        sum_of_all_sums.setText(String.format("%.2f kr,-", fSum_of_all_sums));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Pass
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass
        return false;
    }
    private class CBHistoricCartAdapter extends BaseAdapter {
        LayoutInflater _layoutInflater;
        ArrayList<CBCart> _cbCarts;

        public CBHistoricCartAdapter(Context context, int textViewResourceId, ArrayList<CBCart> cbCarts) {
            _layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            _cbCarts = cbCarts;
        }

        @Override
        public int getCount() {
            return _cbCarts.size();
        }

        @Override
        public CBCart getItem(int position) {
            return _cbCarts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            CBHistoricCartViewHolder cbHistoricCartViewHolder;
            if (convertView == null) {
                convertView = _layoutInflater.inflate(R.layout.activity_historic_list_item, null);
                cbHistoricCartViewHolder = new CBHistoricCartViewHolder();
                convertView.setTag(cbHistoricCartViewHolder);
            } else {
                cbHistoricCartViewHolder = (CBHistoricCartViewHolder) convertView.getTag();
            }
            CBCart cbCart = _cbCarts.get(position);
            cbHistoricCartViewHolder.tvDisplayName = detail(convertView, R.id.historic_cart_name, cbCart.getTimeCreated("dd.MM.yy hh:mm"));
            cbHistoricCartViewHolder.tvSum = detail(convertView, R.id.historic_cart_sum, String.format("%.2f", cbCart.calculateSum()+cbCart.getTips()));
            cbHistoricCartViewHolder.tvTip = detail(convertView, R.id.historic_cart_tip, String.format("%.2f", cbCart.getTips()));
            cbHistoricCartViewHolder.tvSize = detail(convertView, R.id.historic_cart_size, Integer.toString(cbCart.getNumberOfUnits()));
            return convertView;
        }

        private class CBHistoricCartViewHolder {
            TextView tvDisplayName, tvDate, tvTip, tvSum, tvSize;
        }

        private TextView detail(View v, Integer resourceId, String text) {
            TextView tv = (TextView) v.findViewById(resourceId);
            tv.setText(text);
            return tv;
        }
    }
}
