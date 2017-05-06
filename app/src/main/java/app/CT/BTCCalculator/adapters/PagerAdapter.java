package app.CT.BTCCalculator.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import app.CT.BTCCalculator.fragments.BreakevenFragment;
import app.CT.BTCCalculator.fragments.PriceDataFragment;
import app.CT.BTCCalculator.fragments.ProfitFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int numOfTabs;

    public PagerAdapter(android.support.v4.app.FragmentManager fm, int tabs) {
        super(fm);
        this.numOfTabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new BreakevenFragment();
            case 1:
                return new ProfitFragment();
            case 2:
                return new PriceDataFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
