package app.CT.BTCCalculator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
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

public class PriceDataFragment extends Fragment
{
    // Interface to allow for the transmission of data through PriceDataFragment to other fragments
    // in order to use the data for the prices upon request of the user.
    public interface DataInterface
    {
        public String getPriceData();
        public String getCostData();
    }

    // Create the view.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_price, container, false);
    }

    // Creates the activity for the fragment.
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Initialize relevant data.
        View v = getView();

        String disclaimer = "Powered by CoinDesk.com. This data was produced from the CoinDesk Bitcoin Price Index (USD). " +
                            "Non-USD currency data converted using hourly conversion rate from openexchangerates.org.";

        TextView disclaimerText = (TextView) v.findViewById(R.id.disclaimer);
        Button refresh = (Button) v.findViewById(R.id.refreshBtn);

        // OnClick listener to refresh the JSON data request.
        refresh.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Execute the ConnectInBackground class.
                new ConnectInBackground().execute();
            }
        });

        // Set the disclaimer text.
        disclaimerText.setText(disclaimer);

        // Execute the ConnectInBackground class.
        new ConnectInBackground().execute();
    }

    // Class that runs in the background to connect to the Internet and parse the JSON file.
    public class ConnectInBackground extends AsyncTask<String, String, String>
    {
        // Get view and initialize text field.
        View v = getView();
        TextView priceData = (TextView) v.findViewById(R.id.priceData);

        // Connects to the Internet and parses the JSON file.
        @Override
        protected String doInBackground(String... params)
        {
            String url = "http://api.coindesk.com/v1/bpi/currentprice/USD.json";

            // Establish HTTP connection and get JSON file.
            DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
            HttpGet httpGet = new HttpGet(url);

            // Set header to JSON.
            httpGet.setHeader("Content-type", "application/json");

            // Initialize Strings to null to be used later.
            InputStream inputStream = null;
            String result = null;
            String rate = null;
            String firstObject = null;
            String secondObject = null;

            try
            {
                HttpResponse response = httpclient.execute(httpGet);
                HttpEntity entity = response.getEntity();

                inputStream = entity.getContent();

                // JSON is UTF-8 by default.
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line).append("\n");
                }
                result = sb.toString();
                Log.d("Chris", result);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                try{if(inputStream != null)inputStream.close();}catch(Exception ignored){}
            }

            // Create JSON Objects and get the strings they hold.
            try
            {
                JSONObject jObjectFirst = new JSONObject(result);
                firstObject = jObjectFirst.getString("bpi");

                JSONObject jObjectSecond = new JSONObject(firstObject);
                secondObject = jObjectSecond.getString("USD");

                JSONObject jObjectThird = new JSONObject(secondObject);
                rate = jObjectThird.getString("rate");
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            //Log.d("Chris", rate);

            return rate;
        }

        // After parsing has been completed.
        @Override
        protected void onPostExecute(String rate)
        {
            super.onPostExecute(rate);

            // Set text field with the rate.
            priceData.setText(rate);

            // Creates a toast that indicates the price has updated.
            Toast message = Toast.makeText(getActivity(), "Updated.", Toast.LENGTH_SHORT);
            message.show();
        }
    }
}