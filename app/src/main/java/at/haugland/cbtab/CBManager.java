package at.haugland.cbtab;

import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by vegard on 10/21/14.
 */
public final class CBManager
{
    private ArrayList<CBItem> _items;
    private ArrayList<String> _errors;
    private ArrayList<CBCategory> _categories;

    public CBManager(ArrayList<CBItem> items, ArrayList<CBCategory> categories)
    {
        this._items = items;
        this._categories = categories;
    }
    public void initialize_categories()
    {
        /** TODO Due to my lack of understanding of Android Navigation Drawers, I had to add a "section number" to each category (1,2,3) **/
        CBCategory food = new CBCategory("food", "Mat", "&#xf0f5;", 2);
        CBCategory beer = new CBCategory("beer", "Øl", "&#xf0fc;", 1);
        CBCategory wine = new CBCategory("wine", "Vin", "&#xf000;", 3);
        beer.setLayout(R.layout.beermenu_button);
        food.setLayout(R.layout.foodmenu_button);
        wine.setLayout(R.layout.winemenu_button);

        _categories.add(beer);
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
                String[] data = buf.split(",");
                if (counter == 0 && data.length==6) {
                    // Verify that we are parsing a CSV file containing the data we need
                    if (data[0].replaceAll("^\"|\"$", "").equalsIgnoreCase("name") &&
                            data[1].replaceAll("^\"|\"$", "").equalsIgnoreCase("display_name") &&
                            data[2].replaceAll("^\"|\"$", "").equalsIgnoreCase("category") &&
                            data[3].replaceAll("^\"|\"$", "").equalsIgnoreCase("type") &&
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
                    item.set_type(data[3].replaceAll("^\"|\"$", ""));
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
}
