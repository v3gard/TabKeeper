package at.haugland.tabkeeper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CBCategoryAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private ArrayList<CBCategory> mCategory;

    public CBCategoryAdapter(Context context, ArrayList<CBCategory> categories) {
        super();
        mInflater = LayoutInflater.from(context);
        mCategory = categories;
    }
    @Override
    public int getCount()
    {
        return mCategory.size();
    }
    @Override
    public Object getItem(int pos) {
        return mCategory.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        View view;
        CBCategoryHolder holder;
        if (convertView == null)
        {
            view = (TextView) mInflater.inflate(R.layout.dialog_cbitem_spinner_item, viewGroup, false);
            holder = new CBCategoryHolder();
            holder.displayName = (TextView) view.findViewById(R.id.cbItem_spinner_item);
            view.setTag(holder);
        }
        else
        {
            view = convertView;
            holder = (CBCategoryHolder)view.getTag();
        }
        CBCategory category = mCategory.get(pos);
        holder.displayName.setText(category.getDisplayName());
        return view;
    }
    private class CBCategoryHolder
    {
        public TextView displayName;
    }
}