package at.haugland.cbtab;

import java.util.ArrayList;

/**
 * Created by vegard on 10/21/14.
 */
public class CBCategory
{
    private String _name;
    private String _displayName;
    private String _icon;
    private int _sectionNumber;
    private int _layout;
    private ArrayList<CBItem> _items;

    public CBCategory(String name, String displayName, String icon, int sectionNumber)
    {
        this._name = name;
        this._displayName = displayName;
        this._icon = icon;
        this._sectionNumber = sectionNumber;
        _items = new ArrayList<CBItem>();
    }
    public void setLayout(int layout)
    {
        _layout = layout;
    }
    public String getName()
    {
        return _name;
    }
    public String getDisplayName()
    {
        return _displayName;
    }
    public int getLayout()
    {
        return _layout;
    }
    public int SectionNumber()
    {
        return _sectionNumber;
    }
    public void addItem(CBItem item)
    {
        _items.add(item);
    }
    public ArrayList<String> getItemsAsArrayList()
    {
        ArrayList<String> tmp = new ArrayList<String>();
        for (CBItem cbItem : _items) {
            tmp.add(cbItem.getDisplayName());
        }
        return tmp;
    }
    public ArrayList<CBItem> getItems()
    {
        return _items;
    }
}