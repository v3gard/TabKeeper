package at.haugland.cbtab;

import android.app.Activity;
import android.content.Context;
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
        CBHistoricCartAdapter adapter = new CBHistoricCartAdapter(this, R.layout.activity_history, _carts);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "Her ville du fått opp en liste over hva barregninga inneholdt, men det er ikke implementert enda :p", Toast.LENGTH_SHORT).show();
            }
        });
        lv.setAdapter(adapter);

        // update listview header with sum of all sums
        float fSum_of_all_sums = 0;
        TextView sum_of_all_sums = (TextView) findViewById(R.id.historic_sum_of_all_sums);
        for (CBCart cart : MainActivity.cbmanager.getHistoricCarts())
        {
            fSum_of_all_sums += cart.calculateSum();
        }
        sum_of_all_sums.setText(String.format("%.2f kr,-", fSum_of_all_sums));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
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
            cbHistoricCartViewHolder.tvSum = detail(convertView, R.id.historic_cart_sum, String.format("%.2f", cbCart.calculateSum()));
            cbHistoricCartViewHolder.tvSize = detail(convertView, R.id.historic_cart_size, Integer.toString(cbCart.getNumberOfUnits()));
            return convertView;
        }

        private class CBHistoricCartViewHolder {
            TextView tvDisplayName, tvDate, tvSum, tvSize;
        }

        private TextView detail(View v, Integer resourceId, String text) {
            TextView tv = (TextView) v.findViewById(resourceId);
            tv.setText(text);
            return tv;
        }
    }
}
