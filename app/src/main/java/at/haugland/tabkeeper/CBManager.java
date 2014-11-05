package at.haugland.tabkeeper;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.webkit.ConsoleMessage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by vegard on 10/21/14.
 */
public final class CBManager
{
    public static final String DATA_FILENAME = "data.dat";
    public static final int DATA_SERIALIZE_SUCCESS = 1;
    public static final int DATA_DESERIALIZE_SUCCESS = 2;
    public static final int ITEM_REINITIALIZATION_SUCCESS = 3;
    public static final int ITEM_ADD_SUCCESS = 4;
    public static final int DATA_SERIALIZE_FAIL = -1;
    public static final int DATA_DESERIALIZE_FAIL = -2;
    public static final int ITEM_REINITIALIZATION_FAIL = -3;
    public static final int ITEM_ADD_FAIL = -4;


    private ArrayList<CBItem> _items;
    private ArrayList<String> _errors;
    private ArrayList<CBCategory> _categories;
    private CBCart _cart;
    private ArrayList<CBCart> _historicCarts;

    public CBManager(ArrayList<CBItem> items, ArrayList<CBCategory> categories, CBCart cbCart, ArrayList<CBCart> historicCarts)
    {
        this._items = items;
        this._categories = categories;
        this._cart = cbCart;
        this._historicCarts = historicCarts;
    }
    public CBManager()
    {
        // do nothing. constructor used when deserializing data from DATA_FILENAME
    }
    public void initialize_categories()
    {
        /** TODO Due to my lack of understanding of Android Navigation Drawers, I had to add a "section number" to each category (1,2,3) **/
        CBCategory food = new CBCategory("food", "Mat", "&#xf0f5;", 3);
        CBCategory beer = new CBCategory("beer", "Øl", "&#xf0fc;", 1);
        CBCategory beer_nonalcoholic = new CBCategory("beer_nonalcoholic", "Øl - Alkoholfri", "&#xf0fc;", 2);
        CBCategory wine = new CBCategory("wine", "Vin", "&#xf000;", 4);
        food.setImageIcon(R.drawable.fa_cutlery);
        beer.setImageIcon(R.drawable.fa_beer);
        beer_nonalcoholic.setImageIcon(R.drawable.fa_beer);
        wine.setImageIcon(R.drawable.fa_glass);
        _categories.add(beer);
        _categories.add(beer_nonalcoholic);
        _categories.add(food);
        _categories.add(wine);

    }
    public int initialize_resources(Resources resources) {
        // read categories and items from resource data and populate _items.
        // returns 0 if OK and 1 if errors are detected. Errors are available in the _errors ArrayList.
        this.initialize_categories();
        _errors = new ArrayList<String>();
        String buf = "";
        int counter = 0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(resources.openRawResource(R.raw.items)));
            while ((buf = br.readLine()) != null) {
                String[] data = buf.split(";");
                if (counter == 0 && data.length==6) {
                    // Verify that we are parsing a CSV file containing the data we need
                    if (data[0].replaceAll("^\"|\"$", "").equalsIgnoreCase("name") &&
                            data[1].replaceAll("^\"|\"$", "").equalsIgnoreCase("display_name") &&
                            data[2].replaceAll("^\"|\"$", "").equalsIgnoreCase("category") &&
                            data[3].replaceAll("^\"|\"$", "").equalsIgnoreCase("unit") &&
                            data[4].replaceAll("^\"|\"$", "").equalsIgnoreCase("price") &&
                            data[5].replaceAll("^\"|\"$", "").equalsIgnoreCase("description")) {
                        counter++;
                        continue;
                    } else {
                        _errors.add("CSV format not recognized. Parsing stopped.");
                    }

                } else if (data.length == 0) {
                    _errors.add(String.format("No CSV data to parse on line %d. Parsing stopped.", counter + 1));
                    break;
                }
                try {
                    CBItem item = new CBItem(data[0].replaceAll("^\"|\"$", ""));
                    item.set_id(counter);
                    item.set_displayName(data[1].replaceAll("^\"|\"$", ""));
                    item.set_category(data[2].replaceAll("^\"|\"$", ""));
                    item.set_unit(data[3].replaceAll("^\"|\"$", ""));
                    item.set_price(Float.parseFloat(data[4].replaceAll("^\"|\"$", "")));
                    item.set_description(data[5].replaceAll("^\"|\"$", ""));
                    _items.add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                counter++;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (_errors.size() > 0) {
            return 1;
        }
        return 0;
    }
    public CBCategory getCategory(String category)
    {
        for (CBCategory cbCategory : _categories)
        {
            if (cbCategory.getName().equalsIgnoreCase(category))
            {
                return cbCategory;
            }
        }
        return null;
    }
    public int addCBItem(CBItem cbItem)
    {
        for (CBItem item : this.getItems())
        {
            if (item.getName().equalsIgnoreCase(cbItem.getName()))
            {
                return ITEM_ADD_FAIL;
            }
        }
        this.getCategory(cbItem.getCategoryId()).addItem(cbItem);
        _items.add(cbItem);
        return ITEM_ADD_SUCCESS;
    }
    public ArrayList<String> getTypes()
    {
        //TODO: should change type from string to a custom Type object
        ArrayList<String> types = new ArrayList<String>();
        types.add("N/A");
        types.add("bottle");
        types.add("glass");
        return types;
    }
    public ArrayList<CBCategory> getCategories()
    {
        return _categories;
    }
    public ArrayList<CBCategory> getDistinctCategories()
    {
        ArrayList<String> tmp = new ArrayList<String>();
        ArrayList<CBCategory> categories = new ArrayList<CBCategory>();
        for (CBItem cbItem : _items)
        {
            if (! tmp.contains((cbItem.getCategory().getName())))
            {
                tmp.add(cbItem.getCategory().getName());
                categories.add((cbItem.getCategory()));
            }
        }
        return categories;
    }
    public ArrayList<String> getCategoryDisplayNames()
    {
        ArrayList<String> tmp = new ArrayList<String>();
        for (CBItem cbItem : _items)
        {
            if (! tmp.contains(cbItem.getCategory().getDisplayName()))
            {
                tmp.add(cbItem.getCategory().getDisplayName());
            }
        }
        return tmp;
    }
    public CBCategory getCategoryBySectionNumber(int sectionNumber)
    {
        for (CBCategory cbCategory : _categories)
        {
            if (cbCategory.SectionNumber()==sectionNumber)
                return cbCategory;
        }
        return null;
    }
    public ArrayList<CBItem> getItems()
    {
        return _items;
    }
    public CBItem getItem(String cbItemId)
    {
        for (CBItem cbItem : _items)
        {
            if (cbItem.getName().equalsIgnoreCase(cbItemId))
            {
                return cbItem;
            }
        }
        return null;
    }
    public CBCart getCurrentCart()
    {
        return _cart;
    }
    public void createCart()
    {
        this._cart = new CBCart();
    }
    public void addCartAsHistoric(CBCart cbCart)
    {
        _historicCarts.add(cbCart);
        this._cart = null;
    }
    public ArrayList<CBCart> getHistoricCarts()
    {
        return _historicCarts;
    }
    public int SerializeData(Context context)
    {
        StorageContainer sc = new StorageContainer(this._cart, this._historicCarts, this._items, this._categories);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        try {
            String items = gson.toJson(sc);
            FileOutputStream fos = context.openFileOutput(DATA_FILENAME, Context.MODE_PRIVATE);
            fos.write(items.getBytes());
            fos.close();
            return DATA_SERIALIZE_SUCCESS;
        }
        catch (Exception e)
        {
            return DATA_SERIALIZE_FAIL;
        }
    }
    public int DeserializeData(Context context)
    {
        if (DataFileExists(context))
        {
            try {
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                FileInputStream fis = context.openFileInput(DATA_FILENAME);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null)
                {
                    sb.append(line);
                }
                String jsonRaw = sb.toString();
                StorageContainer sc = gson.fromJson(jsonRaw, StorageContainer.class);
                this._cart = sc._cart;
                this._historicCarts = sc._historicCarts;
                this._items = sc._cbItems;
                this._categories = sc._cbCategories;
                this.ReinitializeItems();
                return DATA_DESERIALIZE_SUCCESS;
            }
            catch (Exception e)
            {
                return DATA_DESERIALIZE_FAIL;
            }
        }
        return DATA_DESERIALIZE_FAIL;
    }
    public boolean IsInitialized()
    {
        boolean tmp =_items != null && _categories != null;
        return tmp;
    }
    public int ReinitializeItems()
    {
        // after loading data from file with gson, the CBCategory and CBItem arrays
        // needs to be reinitialized to ensure that each item is mapped to the correct category
        for (CBCategory cbCategory : _categories)
        {
            cbCategory.Clear();
        }
        // items
        try
        {
            for (CBItem cbItem : _items) {
                cbItem.set_category(cbItem.getCategoryId());
            }
        }
        catch (Exception e) {
            return ITEM_REINITIALIZATION_FAIL;
        }
        return ITEM_REINITIALIZATION_SUCCESS;
    }
    public static boolean DataFileExists(Context context)
    {
        File f = new File(context.getFilesDir(), DATA_FILENAME);
        return f.exists();
    }
    /** TODO: Future SQL implementation **/
    private class MSQLiteOpenHelper extends SQLiteOpenHelper
    {
        private static final String DATABASE_NAME = "data.db";
        private static final int DATABASE_VERSION = 1;
        public static final String TABLE_CBITEM = "cbitem";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DISPLAYNAME = "displayname";

        // db creation sql statement
        public static final String DATABASE_CREATE = "CREATE TABLE"
                + TABLE_CBITEM + "(" + COLUMN_ID
                + "INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME
                + "TEXT NOT NULL);";

        public MSQLiteOpenHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.w(MSQLiteOpenHelper.class.getName(),
                    "Upgrading db from version " + oldVersion + " to "
                            + newVersion + ", which will destroy ALL old data.");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CBITEM);
        }
    }
    private class StorageContainer
    {
        @Expose public CBCart _cart;
        @Expose public ArrayList<CBCart> _historicCarts;
        @Expose public ArrayList<CBItem> _cbItems;
        @Expose public ArrayList<CBCategory> _cbCategories;

        public StorageContainer(CBCart cart, ArrayList<CBCart> historicCarts, ArrayList<CBItem> cbItems, ArrayList<CBCategory> cbCategories)
        {
            _cart = cart;
            _historicCarts = historicCarts;
            _cbItems = cbItems;
            _cbCategories = cbCategories;
        }
    }
}
