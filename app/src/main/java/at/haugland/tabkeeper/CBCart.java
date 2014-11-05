package at.haugland.tabkeeper;

import android.widget.Filterable;

import com.google.gson.annotations.Expose;

import java.security.Key;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by vegard on 10/24/14.
 */
public class CBCart
{
    @Expose private CBKVStore _items;
    @Expose private Calendar _calendar;
    @Expose private Date _created;

    public CBCart()
    {
        _items = new CBKVStore();
        _calendar = Calendar.getInstance();
        _created = _calendar.getTime();
    }
    private class CBKVStore implements Iterable<CBKeyValue>
    {
        @Expose ArrayList<CBKeyValue> _items;
        public CBKVStore()
        {
            _items = new ArrayList<CBKeyValue>();
        }
        public CBKVStore(ArrayList<CBKeyValue> items)
        {
            _items = items;
        }
        public Iterator<CBKeyValue> iterator()
        {
            return _items.iterator();
        }
        private CBKeyValue _getCBTuple(String cbItemId)
        {
            for (CBKeyValue kv : _items)
            {
                if (kv.cbItemId.equalsIgnoreCase(cbItemId))
                {
                    return kv;
                }
            }
            return null;
        }
        public void put(CBItem cbItem, int amount)
        {
            CBKeyValue cbKeyValue = _getCBTuple(cbItem.getName());
            if (cbKeyValue == null) {
                cbKeyValue = new CBKeyValue(cbItem.getName(), amount);
                _items.add(cbKeyValue);
            } else {
                cbKeyValue.value += amount;
            }
        }
        public int size()
        {
            return _items.size();
        }
        public ArrayList<CBItem> keySet()
        {
            ArrayList<CBItem> items = new ArrayList<CBItem>();
            for (CBKeyValue cbKeyValue : _items)
            {
                items.add(MainActivity.cbmanager.getItem(cbKeyValue.cbItemId));
            }
            return items;
        }
    }
    public class CBKeyValue
    {
        @Expose public String cbItemId;
        @Expose public int value;
        public CBKeyValue(String cbItemId, int value)
        {
            this.cbItemId = cbItemId;
            this.value = value;
        }
        public CBItem getCBItem()
        {
            return MainActivity.cbmanager.getItem(this.cbItemId);
        }
    }
    public void add(CBItem cbItem, int amount)
    {
        _items.put(cbItem, amount);
    }
    public int size()
    {
        return _items.size();
    }
    public ArrayList<CBKeyValue> getItemsAsCBKeyValue()
    {
        return _items._items;
    }
    @Deprecated //TODO old crap. to be rewritten.
    public HashMap<CBItem, Integer> getItems()
    {
       HashMap<CBItem, Integer> tmp = new HashMap<CBItem, Integer>();
       for (CBKeyValue cbKeyValue : _items)
       {
           tmp.put(MainActivity.cbmanager.getItem(cbKeyValue.cbItemId), cbKeyValue.value);
       }
       return tmp;
    }
    public ArrayList<CBItem> getItemsAsArrayList()
    {
        return _items.keySet();
    }
    public ArrayList<String> getItemsAsString()
    {
        ArrayList<String> tmp = new ArrayList<String>();
        for (CBItem cbItem : _items.keySet())
        {
            tmp.add(cbItem.getDisplayName());
        }
        return tmp;
    }
    public String getDisplayNameWithDate()
    {
        return String.format("Barregning %s", this.getTimeCreated("dd.MM.yy"));
    }
    public String getDisplayNameWithDayAndTime()
    {
        return String.format("Barregning %s", this.getTimeCreated("dd.MM.yy hh:mm"));
    }
    public String getTimeCreated(String dateFormat)
    {
        DateFormat df = new SimpleDateFormat(dateFormat);
        return df.format(_created);
    }
    public float calculateSum()
    {
        // TODO: this can get messy with several items! (floating point rounding error)
        float sum = 0;
        for (CBKeyValue cbKeyValue : _items)
        {
            sum += cbKeyValue.getCBItem().getPrice()*cbKeyValue.value;
        }
        return sum;
    }
    public int getSize()
    {
        return _items.size();
    }
    public int getNumberOfUnits()
    {
        int units = 0;
        for (CBKeyValue cbKeyValue : _items)
        {
            units += cbKeyValue.value;
        }
        return units;
    }
}
