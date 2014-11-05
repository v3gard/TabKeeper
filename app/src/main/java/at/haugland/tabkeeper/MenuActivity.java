package at.haugland.tabkeeper;

import android.app.Activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;


public class MenuActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * Used to store the last selected category.
     */
    private CBCategory _category;
    private static CBCart _cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        _cart = MainActivity.cbmanager.getCurrentCart();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        PlaceholderFragment pf = PlaceholderFragment.newInstance(position + 1);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, pf);
        ft.commit();
    }

    public void onCategoryAttached(int number) {
        // when a category is selected, do the following
        // update the title
        _category = MainActivity.cbmanager.getCategoryBySectionNumber(number);
        mTitle = _category.getDisplayName();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen())
        {
            getMenuInflater().inflate(R.menu.menu, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass
        return false;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        private class CBCategoryAdapter extends BaseAdapter
        {
            LayoutInflater _layoutInflater;
            CBCategory _cbCategory;

            public CBCategoryAdapter(Context context, int textViewResourceId, CBCategory cbCategory)
            {
                _layoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                _cbCategory = cbCategory;
            }
            @Override
            public int getCount()
            {
                return _cbCategory.getItems().size();
            }
            @Override
            public CBItem getItem(int position)
            {
                return _cbCategory.getItems().get(position);
            }
            @Override
            public long getItemId(int position)
            {
                return 0;
            }
            public View getView(int position, View convertView, ViewGroup parent)
            {
                CBCategoryViewHolder cbCategoryViewHolder;
                if (convertView==null)
                {
                    convertView = _layoutInflater.inflate(R.layout.activity_menu_list_item, null);
                    cbCategoryViewHolder = new CBCategoryViewHolder();
                    convertView.setTag(cbCategoryViewHolder);
                }
                else
                {
                    cbCategoryViewHolder = (CBCategoryViewHolder) convertView.getTag();
                }
                cbCategoryViewHolder.tvDisplayName = detail(convertView, R.id.item_displayName, _cbCategory.getItems().get(position).getDisplayName());
                cbCategoryViewHolder.tvUnit = detail(convertView, R.id.item_unit, _cbCategory.getItems().get(position).getDisplayUnit());
                cbCategoryViewHolder.tvPrice = detail(convertView, R.id.item_price, String.format("%s kr", _cbCategory.getItems().get(position).getPrice().toString()));
                cbCategoryViewHolder.ivIcon = detail(convertView, R.id.item_icon, _cbCategory.getImageIcon());

                return convertView;
            }
            private class CBCategoryViewHolder
            {
                ImageView ivIcon;
                TextView tvDisplayName, tvUnit, tvPrice;
            }
            private TextView detail(View v, int resourceId, String text)
            {
                TextView tv = (TextView) v.findViewById(resourceId);
                tv.setText(text);
                return tv;
            }
            private ImageView detail(View v, int resourceId, int icon)
            {
                ImageView iv = (ImageView) v.findViewById(resourceId);
                iv.setImageResource(icon);
                return iv;
            }

        }

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            CBCategory cbCategory = MainActivity.cbmanager.getCategoryBySectionNumber(sectionNumber);
            ListView listView = (ListView) rootView.findViewById(R.id.list);
            TextView textView = (TextView) rootView.findViewById(R.id.empty);
            textView.setVisibility(View.INVISIBLE);
            final CBCategoryAdapter adapter = new CBCategoryAdapter(rootView.getContext(), R.layout.activity_menu_list_item, cbCategory);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                    final CBItem item = (CBItem) adapter.getItem(i);
                    View npView = inflater.inflate(R.layout.selector_numberpicker, null);
                    final NumberPicker activeItemNumberPicker = (NumberPicker) npView.findViewById(R.id.numberPicker);
                    TextView activeItemDescription = (TextView) npView.findViewById(R.id.cart_item_description);
                    activeItemDescription.setText(item.getDescription());
                    activeItemNumberPicker.setMinValue(1);
                    activeItemNumberPicker.setMaxValue(100);
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle(item.getDisplayName());
                    builder.setView(npView);
                    builder.setPositiveButton("Legg til", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            _cart = MainActivity.cbmanager.getCurrentCart();
                            if (_cart==null)
                            {
                                MainActivity.cbmanager.createCart();
                                _cart = MainActivity.cbmanager.getCurrentCart();
                                Toast.makeText(view.getContext(), String.format("Opprettet ny barregning"), Toast.LENGTH_SHORT).show();
                            }
                            _cart.add(item, activeItemNumberPicker.getValue());
                            Toast.makeText(view.getContext(), String.format("La til %d %s", activeItemNumberPicker.getValue(), item.getDisplayName()), Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast t = Toast.makeText(view.getContext(), "avbryt", Toast.LENGTH_SHORT);
                            t.show();
                        }
                    });
                    builder.create().show();
                }
            });
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MenuActivity) activity).onCategoryAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
