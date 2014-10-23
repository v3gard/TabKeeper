package at.haugland.cbtab;

/**
 * Created by vegard on 10/21/14.
 */

public class CBItem
{
    private int _id;
    private String _name;
    private String _displayName;
    private String _type;
    private Float _price; // TODO! be aware of floating point error propagation with addition!
    private String _description;
    private CBCategory _category;

    public CBItem(String name)
    {
        this._name = name;
        this._id = -1;
        this._category = null;
        this._displayName = null;
        this._description = null;
        this._price = null;
        this._type = null;
    }
    public CBItem(String name, CBCategory category)
    {
        this._name = name;
        this._category = category;
    }
    public CBCategory getCategory()
    {
        return _category;
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
    public void set_type(String type)
    {
        this._type = type;
    }
    public void set_price(float price)
    {
        this._price = price;
    }
    public void set_description(String description)
    {
        this._description = description;
    }
    public void set_category(String category) throws Exception {
        this._category = MainActivity.cbmanager.getCategory(category);
        this._category.addItem(this);
        if (this._category == null)
        {
            throw new Exception("bla");
        }
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
}
