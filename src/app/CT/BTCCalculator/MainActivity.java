package app.CT.BTCCalculator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockFragmentActivity
{
    // Member variables.
    private MyViewPagerAdapter m_adapter 	= null;
    private ViewPager          m_viewPager 	= null;
    private ActionBar          m_actionBar	= null;

    // Class constants.
    private final static String [] m_fragmentClasses = {"app.CT.BTCCalculator.FragmentFirst",
                                                        "app.CT.BTCCalculator.FragmentSecond",
                                                        "app.CT.BTCCalculator.PriceDataFragment"};
    private final static String [] m_fragmentTitles = {"Breakeven", "Profit", "Price"};
    private final static String ACTIVITY_TAG = "BTC CALC";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_main);

        // Initialize the ViewPager and Adapter.
        m_viewPager = (ViewPager) findViewById(R.id.pager);
        m_adapter = new MyViewPagerAdapter(getSupportFragmentManager());

        // Set a Listener and Adapter for the View Pager.
        m_viewPager.setOnPageChangeListener(SimplePageListener);
        m_viewPager.setAdapter(m_adapter);
        m_viewPager.setOffscreenPageLimit(3);

        // Setup Tabs for the ActionBar.
        setUpActionBarWithTabs();
    }

    // Generates the Action Bar with NAVIGATION_MODE_TABS
    // and adds the Tabs to the ActionBar.
    private void setUpActionBarWithTabs()
    {
        // Initialize the ActionBar and set Navigation Mode.
        m_actionBar = getSupportActionBar();
        if (m_actionBar != null)
        {
            m_actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }

        // Loop through the Class References and Add them to the ActionBar.
        for(int index = 0; index < m_fragmentClasses.length; index++)
        {
            if (m_actionBar != null)
            {
                m_actionBar.addTab(m_actionBar.newTab().setText(m_fragmentTitles[index]).setTabListener(SimpleTabListener));
            }
        }
    }

    // Listeners.
    private ActionBar.TabListener SimpleTabListener = new ActionBar.TabListener()
    {

        @Override public void onTabReselected(Tab tab, FragmentTransaction ft) {}

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft)
        {
            // Set the current item using the tab's position.
            m_viewPager.setCurrentItem(tab.getPosition());
        }

        @Override public void onTabUnselected(Tab tab, FragmentTransaction ft) {}

    };

    private ViewPager.SimpleOnPageChangeListener SimplePageListener = new ViewPager.SimpleOnPageChangeListener()
    {
        @Override
        public void onPageScrollStateChanged(int state) {}

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position)
        {
            // Set the current item at the current position.
            m_actionBar.setSelectedNavigationItem(position);
        }
    };

    // ViewPagerAdapter class.
    private class MyViewPagerAdapter extends FragmentPagerAdapter
    {

        public MyViewPagerAdapter(FragmentManager fm)
        {
            // Call to the super class.
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            // Fragment currentFragment to return.
            Fragment currentFragment = null;

            // Loop through the Fragment classes and instantiate the fragment at the current Position.
            if((m_fragmentClasses != null) && (m_fragmentClasses.length > 0) && (position >= 0) && (position < m_fragmentClasses.length))
            {
                // Instantiate the current Fragment.
                currentFragment = Fragment.instantiate(getBaseContext(), m_fragmentClasses[position]);
                currentFragment.setRetainInstance(true);
            }

            // Return the CurrentFragment.
            return currentFragment;
        }

        @Override
        public int getCount()
        {
            // Return the length of the Fragment classes array.
            return m_fragmentClasses.length;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.AddCurrent:
                // Not implemented here.
                return true;
            case R.id.RemoveCurrent:
                // Not implemented here.
                return false;
            default:
                break;
        }

        return false;
    }
}
