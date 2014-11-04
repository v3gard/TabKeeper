package at.haugland.cbtab;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by vegard on 10/21/14.
 */
public class ShoppingCart
{
    @Expose
    private String _name;
    private ArrayList<String> _items;
    public ShoppingCart(String name)
    {
        this._name = name;
        this._items = new ArrayList<String>();
    }
}
