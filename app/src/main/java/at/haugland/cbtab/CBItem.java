package at.haugland.cbtab;

import com.google.gson.annotations.Expose;

/**
 * Created by vegard on 10/21/14.
 */

public class CBItem
{
    @Expose private int _id;
    @Expose private String _name;
    @Expose private String _displayName;
    @Expose private String _unit;
    @Expose private Float _price; // TODO! be aware of floating point error propagation with addition!
    @Expose private String _description;
    @Expose private CBCategory _category;
    @Expose private String _categoryId;

    public CBItem(String name)
    {
        this._name = name;
        this._id = -1;
        this._category = null;
        this._categoryId = null;
        this._displayName = null;
        this._description = null;
        this._price = null;
        this._unit = null;
    }
    @Deprecated //Use CBItem(String name, String categoryId) instead
    public CBItem(String name, CBCategory category)
    {
        this._name = name;
        this._categoryId = category.getName();
    }
    public CBItem(String name, String categoryId)
    {
        this._name = name;
        this._categoryId = categoryId;
    }
    public CBCategory getCategory()
    {
        return MainActivity.cbmanager.getCategory(_categoryId);
    }
    /*** SETTERS ***/
    public void set_name(String name)
    {
        this._name = name;
    }
    public void set_id(int id)
    {
        _id = id;
    }
    public void set_displayName(String displayName)
    {
        this._displayName = displayName;
    }
    public void set_unit(String unit)
    {
        this._unit = unit;
    }
    public void set_price(float price)
    {
        this._price = price;
    }
    public void set_description(String description)
    {
        this._description = description;
    }
    public void set_category(String categoryId) throws Exception {
        this._categoryId = categoryId;
        CBCategory category = MainActivity.cbmanager.getCategory(this._categoryId);
        if (category == null)
        {
            throw new Exception("Unable to find category. Most likely the objects haven't been initialized properly. This might happen if a new category has been added to the CSV file, or the CSV file is unparsable.");
        }
        else category.addItem(this);
    }
    public String getCategoryId()
    {
        return _categoryId;
    }
    public String getDisplayName()
    {
        return _displayName;
    }
    public String getName()
    {
        return _name;
    }
    public int getId()
    {
        return _id;
    }
    public String getUnit()
    {
        return _unit;
    }
    public String getDisplayUnit()
    {
        if (_unit.equalsIgnoreCase("glass"))
        {
            return "Glass";
        }
        else if (_unit.equalsIgnoreCase(("bottle")))
        {
            return "Flaske";
        }
        else
        {
            return "Enhet";
        }
    }
    public String getDescription()
    {
        return _description;
    }
    public Float getPrice()
    {
        return _price;
    }
}
