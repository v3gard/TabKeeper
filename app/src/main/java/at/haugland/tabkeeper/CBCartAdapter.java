package at.haugland.tabkeeper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.AbstractMap;

/**
 * Created by vegard on 11/11/14.
 */
public class CBCartAdapter extends BaseAdapter
{
    LayoutInflater _layoutInflater;
    CBCart _cbCart;

    public CBCartAdapter(Context context, int textViewResourceId, CBCart cbCart)
    {
        _layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
