package app.CT.BTCCalculator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PriceDataFragment extends SherlockFragment {
    private String rate;
    private String time;

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
        return inflater.inflate(R.layout.fragment_price, container, false);
    }

    // Creates the activity for the fragment.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Register Bus Provider instance.
        BusProvider.getInstance().register(this);

        // Initialize relevant data.
        View view = getView();

        Button refresh = (Button) view.findViewById(R.id.refreshBtn);

        // OnClick listener to refresh the JSON data request.
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Execute the ConnectInBackground class.
                new ConnectInBackground().execute();
            }
        });

        // Execute the ConnectInBackground class.
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
            // Connect to this URL.
            String url = "http://api.coindesk.com/v1/bpi/currentprice/USD.json";

            // Establish HTTP connection and get JSON file.
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            HttpGet httpGet = new HttpGet(url);

            // Set header to JSON.
            httpGet.setHeader("Content-type", "application/json");

            // Initialize Strings to null to be used later.
            InputStream inputStream = null;
            String result = null;
            String firstString;
            String secondString;

            try {
                HttpResponse response = httpclient.execute(httpGet);
                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();

                // JSON is UTF-8 by default.
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                result = sb.toString();
                //Log.d("Chris", result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) inputStream.close();
                } catch (Exception ignored) {
                }
            }

            // Create JSON Objects and get the strings they hold.
            try {
                JSONObject jObjectTime = new JSONObject(result);
                time = jObjectTime.getString("time");

                JSONObject jObjectTimeSecond = new JSONObject(time);
                time = jObjectTimeSecond.getString("updateduk");

                JSONObject jObjectFirst = new JSONObject(result);
                firstString = jObjectFirst.getString("bpi");

                JSONObject jObjectSecond = new JSONObject(firstString);
                secondString = jObjectSecond.getString("USD");

                JSONObject jObjectThird = new JSONObject(secondString);
                rate = jObjectThird.getString("rate");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Log.d("Chris", rate);

            return rate;
        }

        // After parsing has been completed.
        @Override
        protected void onPostExecute(final String mRate) {
            super.onPostExecute(mRate);

            // Set text field with the rate.
            priceData.setText("$" + mRate + " USD/BTC");
            setRate(mRate);
            timeData.setText(time);

            // Creates a toast that indicates the price has updated.
            Toast message = Toast.makeText(getActivity(), "Updated.", Toast.LENGTH_SHORT);
            message.show();
        }
    }
}