package app.CT.BTCCalculator;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PriceDataFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private String rate;
    private String time;

    private SwipeRefreshLayout swipeRefreshLayout;

    private Bus eventBus = new Bus(ThreadEnforcer.ANY);

    // Sets the rate and calls sendRate() to publish to Otto Event Bus.
    public void setRate(String mRate) {
        this.rate = mRate;
        //Log.d("Chris", "setRate = " + rate);
        sendRate();
    }

    // Otto Event Bus method to publish the rate to the Event Bus.
    public void sendRate() {
        BusProvider.getInstance().post(rate);
    }

    // Create the view.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("CHRIS", "onCreateView() returned: " + container);

        View view = inflater.inflate(R.layout.fragment_price, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    // Creates the activity for the fragment.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("CHRIS", "onActivityCreated() returned: void");

        // Register Bus Provider instance.
        BusProvider.getInstance().register(this);

        // Execute the ConnectInBackground class.
        new ConnectInBackground().execute();
    }

    @Override
    public void onRefresh() {
        new ConnectInBackground().execute();
    }

    // Class that runs in the background to connect to the Internet and parse the JSON file.
    public class ConnectInBackground extends AsyncTask<String, String, String> {
        // Get view and initialize text field.
        View view = getView();
        TextView priceData = (TextView) view.findViewById(R.id.priceData);
        TextView timeData = (TextView) view.findViewById(R.id.timeData);

        // Connects to the Internet and parses the JSON file.
        @Override
        protected String doInBackground(String... params) {
            InputStream in = null;
            HttpURLConnection urlConnection = null;
            try {
                // Connect to this URL.
                URL url = new URL("http://api.coindesk.com/v1/bpi/currentprice/USD.json");

                // Establish HTTP connection and get JSON file.
                urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
            }
            catch (Exception ignored) {
                if (urlConnection == null && in == null)
                    return null;

                if (urlConnection != null && in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    urlConnection.disconnect();

                    return null;
                }
            }

            // Initialize Strings to null to be used later.
            String result = null;
            try {
                assert in != null;
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                result = sb.toString();
                //Log.d("Chris", result);
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            finally {
                try {
                    if (in != null) in.close();
                    if (urlConnection != null) urlConnection.disconnect();
                }
                catch (Exception ignored) {
                }
            }

            // Create JSON Objects and get the strings they hold.
            try {
                JSONObject jObjectTime = new JSONObject(result);

                Log.d("JSON time", String.valueOf(jObjectTime.getJSONObject("time")));
                Log.d("JSON BPI", String.valueOf(jObjectTime.getJSONObject("bpi")));

                time = jObjectTime.getJSONObject("time").getString("updateduk");
                rate = jObjectTime.getJSONObject("bpi").getJSONObject("USD").getString("rate");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            //Log.d("Chris", rate);

            return rate;
        }

        // After parsing has been completed.
        @Override
        protected void onPostExecute(final String mRate) {
            super.onPostExecute(mRate);

            // Tell swipe to refresh animation to stop.
            swipeRefreshLayout.setRefreshing(false);

            if (mRate == null) {
                priceData.setText(R.string.connectionFailed);
                Toast message = Toast.makeText(getActivity(), "Try again...", Toast.LENGTH_SHORT);
                message.show();
                return;
            }

            // Set text field with the rate.
            priceData.setText(String.format("$%s USD/BTC", mRate));
            setRate(mRate);
            timeData.setText(time);

            // Creates a toast that indicates the price has updated.
            try {
                Toast message = Toast.makeText(getActivity(), "Updated.", Toast.LENGTH_SHORT);
                message.show();
            }
            catch (Exception ignored) {
                Log.d("CHRIS", "onPostExecute() returned: " + ignored.getMessage());
            }

        }
    }
}