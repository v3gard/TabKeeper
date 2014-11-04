package at.haugland.cbtab;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by vegard on 10/21/14.
 */
public class CBCategory
{
    @Expose private String _name;
    @Expose private String _displayName;
    @Expose private String _textIcon;
    @Expose private int _sectionNumber;
    @Expose private int _imageIcon;
    private ArrayList<CBItem> _items;

    public CBCategory(String name, String displayName, String textIcon, int sectionNumber)
    {
        this._name = name;
        this._displayName = displayName;
        this._textIcon = textIcon;
        this._sectionNumber = sectionNumber;
        _items = new ArrayList<CBItem>();
    }
    public void setImageIcon(int imageIconReference)
    {
        _imageIcon = imageIconReference;
    }
    public String getName()
    {
        return _name;
    }
    public String getDisplayName()
    {
        return _displayName;
    }
    public int getImageIcon()
    {
        return _imageIcon;
    }
    public int SectionNumber()
    {
        return _sectionNumber;
    }
    public void addItem(CBItem item)
    {
        _items.add(item);
    }
    public void Clear()
    {
        _items = new ArrayList<CBItem>();
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