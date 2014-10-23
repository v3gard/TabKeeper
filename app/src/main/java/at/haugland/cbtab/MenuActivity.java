package at.haugland.cbtab;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
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
    private CBCategory mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

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
        mCategory = MainActivity.cbmanager.getCategoryBySectionNumber(number);
        mTitle = mCategory.getDisplayName();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * skjønner ikke dette...
     */
    public class ButtonAdapter extends BaseAdapter
    {
        private Context _context;
        private CBCategory _category;

        public ButtonAdapter(Context context, CBCategory category)
        {
            _context = context;
            _category = category;
        }
        public int getCount()
        {
            return _category.getItems().size();
        }
        public Object getItem(int position)
        {
            return position;
        }
        public long getItemId(int position)
        {
            return position;
        }
        @Override
        public View getView(int position, final View convertView, ViewGroup parent)
        {
            Button btn;
            if (convertView == null) {
                btn = new Button(_context);
                btn.setLayoutParams(new GridView.LayoutParams(190, 190));
                btn.setPadding(1, 1, 1, 1);
                btn.setHighlightColor(Color.GREEN);
                //btn.setId(cbItem.getId());
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(view.getContext(), "blah", Toast.LENGTH_SHORT);
                    }
                });
            }
            else
            {
                btn = (Button)convertView;
            }
            btn.setText(position);
            btn.setTextColor(Color.WHITE);
            btn.setId(position);
            return btn;
        }
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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
            GridView gridView =(GridView) rootView.findViewById(R.id.gridView);
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            CBCategory cbCategory = MainActivity.cbmanager.getCategoryBySectionNumber(sectionNumber);

            //ButtonAdapter adapter = new ButtonAdapter(rootView.getContext(), cbCategory);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(), cbCategory.getLayout(), cbCategory.getItemsAsArrayList());
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    {}
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
