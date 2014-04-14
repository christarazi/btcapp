package app.CT.BTCCalculator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PriceDataFragment extends Fragment
{
    // Interface to allow for the transmission of data through PriceDataFragment to other fragments
    // in order to use the data for the prices upon request of the user.
    public interface DataInterface
    {
        public String getPriceData();
        public String getCostData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_price, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        View v = getView();

        TextView priceData = (TextView) v.findViewById(R.id.priceData);

        priceData.setText("This is created from the PriceFragment class.");
    }
}