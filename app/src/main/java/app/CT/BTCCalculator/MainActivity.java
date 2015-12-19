package app.CT.BTCCalculator;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Break-even"));
        tabLayout.addTab(tabLayout.newTab().setText("Profit"));
        tabLayout.addTab(tabLayout.newTab().setText("Price"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                // Hide keyboard on tab switch.
                hideKeyboard();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override protected void onPause() {
        super.onPause();

        // Always unregister when an object no longer should be on the bus.
        BusProvider.getInstance().unregister(this);

        //Log.d("CHRIS", "MainActivity onPause. Unregister bus");
    }

    @Override protected void onResume() {
        super.onResume();

        // Register ourselves so that we can provide the initial value.
        BusProvider.getInstance().register(this);

        //Log.d("CHRIS", "MainActivity onResume. register bus");
    }

    private void hideKeyboard() {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
