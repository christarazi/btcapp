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

public class MainActivity extends SherlockFragmentActivity {
    // Member variables.
    private MyViewPagerAdapter mAdapter = null;
    private ViewPager mViewPager = null;
    private ActionBar mActionBar = null;

    // Class constants.
    private final static String[] mFragmentClasses = {"app.CT.BTCCalculator.BreakevenFragment",
            "app.CT.BTCCalculator.ProfitFragment",
            "app.CT.BTCCalculator.PriceDataFragment"};
    private final static String[] mFragmentTitles = {"Breakeven", "Profit", "Price"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_main);

        // Initialize the ViewPager and Adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new MyViewPagerAdapter(getSupportFragmentManager());

        // Set a Listener and Adapter for the View Pager.
        mViewPager.setOnPageChangeListener(SimplePageListener);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);

        // Setup Tabs for the ActionBar.
        setUpActionBarWithTabs();
    }

    // Generates the Action Bar with NAVIGATION_MODE_TABS
    // and adds the Tabs to the ActionBar.
    private void setUpActionBarWithTabs() {
        // Initialize the ActionBar and set Navigation Mode.
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }

        // Loop through the Classes and add them to the ActionBar.
        for (byte index = 0; index < mFragmentClasses.length; index++) {
            if (mActionBar != null) {
                mActionBar.addTab(mActionBar.newTab().setText(mFragmentTitles[index]).setTabListener(SimpleTabListener));
            }
        }
    }

    // Listeners.
    private ActionBar.TabListener SimpleTabListener = new ActionBar.TabListener() {
        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            // Set the current item using the tab's position.
            mViewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }
    };

    private ViewPager.SimpleOnPageChangeListener SimplePageListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            // Set the current item at the current position.
            mActionBar.setSelectedNavigationItem(position);
        }
    };

    // ViewPagerAdapter class.
    private class MyViewPagerAdapter extends FragmentPagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            // Call to the super class.
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // Fragment currentFragment to return.
            Fragment currentFragment = null;

            // Loop through the Fragment classes and instantiate the fragment at the current Position.
            if ((mFragmentClasses != null) && (mFragmentClasses.length > 0) && (position >= 0) && (position < mFragmentClasses.length)) {
                // Instantiate the current Fragment.
                currentFragment = Fragment.instantiate(getBaseContext(), mFragmentClasses[position]);
                currentFragment.setRetainInstance(true);
            }

            // Return the CurrentFragment.
            return currentFragment;
        }

        @Override
        public int getCount() {
            // Return the length of the Fragment classes array.
            return mFragmentClasses.length;
        }
    }
}
