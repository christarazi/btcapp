package app.CT.BTCCalculator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class PriceDataFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private String rate;
    private String time;

    private SwipeRefreshLayout swipeRefreshLayout;
    GraphView graph;
    LineGraphSeries dataPoints;

    // Sets the rate and calls sendRate() to publish to Otto Event Bus.
    public void setRate(String mRate) {
        this.rate = mRate;
        //Log.d("Chris", "setRate = " + rate);
        sendRate();
    }

    // Otto Event Bus method to publish the rate to the Event Bus.
    public void sendRate() {
        BusProvider.getInstance().post(rate);
        //Log.d("CHRIS", "Posting rate");
    }

    // Create the view.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        graph = (GraphView) view.findViewById(R.id.graph);
        graph.getViewport().setScalable(true);

        return view;
    }

    // Creates the activity for the fragment.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Register Bus Provider instance.
        BusProvider.getInstance().register(this);

        //Log.d("CHRIS", "PriceDataFragment onActivityCreated. Register bus");

        // Execute the async tasks.
        new GetCurrentPriceTask().execute();
        new GetGraphDataTask().execute();
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister the bus when pausing the application
        BusProvider.getInstance().unregister(this);

        //Log.d("CHRIS", "PriceDataFragment onPause. Unregister bus");
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register the bus when resuming the application
        BusProvider.getInstance().register(this);

        //Log.d("CHRIS", "PriceDataFragment onResume. register bus");
    }

    @Override
    public void onRefresh() {
        new GetCurrentPriceTask().execute();
    }

    // Class that runs in the background to connect to the Internet and parse the JSON file.
    public class GetCurrentPriceTask extends AsyncTask<String, String, String> {
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
                URL priceURL = new URL("http://api.coindesk.com/v1/bpi/currentprice/USD.json");

                // Establish HTTP connection and get JSON file.
                urlConnection = (HttpURLConnection) priceURL.openConnection();
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

                //Log.d("JSON time", String.valueOf(jObjectTime.getJSONObject("time")));
                //Log.d("JSON BPI", String.valueOf(jObjectTime.getJSONObject("bpi")));

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
                //Log.d("CHRIS", "onPostExecute() returned: " + ignored.getMessage());
            }

        }
    }

    // Get historical price data from CoinDesk and graph it
    public class GetGraphDataTask extends AsyncTask<LineGraphSeries, Void, LineGraphSeries> {

        @Override
        protected LineGraphSeries doInBackground(LineGraphSeries... params) {
            InputStream in = null;
            HttpURLConnection urlConnection = null;
            try {
                URL graphURL = new URL("http://api.coindesk.com/v1/bpi/historical/close.json");

                // Establish HTTP connection and get JSON file.
                urlConnection = (HttpURLConnection) graphURL.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

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
                JSONObject jObjectGraph = new JSONObject(result).getJSONObject("bpi");

                Iterator<String> iter = jObjectGraph.keys();

                LineGraphSeries<DataPoint> dataPointLineSeries = new LineGraphSeries<>();
                TreeMap<Date, String> sortedMap = new TreeMap<>();

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                while (iter.hasNext()) {
                    String key = iter.next();
                    Date date = format.parse(key);
                    String val = String.valueOf(jObjectGraph.get(key));
                    sortedMap.put(date, val);
                }
                for (Map.Entry<Date, String> elem: sortedMap.entrySet()) {
                    dataPointLineSeries.appendData(new DataPoint(elem.getKey(), Double.valueOf(elem.getValue())), true, 31);
                }
                return dataPointLineSeries;
            }
            catch (JSONException | ParseException e) {
                e.printStackTrace();
                Log.d("CHRIS", "error parsing graph data " + e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(final LineGraphSeries data) {
            super.onPostExecute(data);
            dataPoints = data;
            dataPoints.setDrawDataPoints(true);
            dataPoints.setDataPointsRadius(8);
            dataPoints.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
                    Date date = new Date((long) dataPoint.getX());
                    String dateStr = String.valueOf(dateFormat.format(date));
                    Toast.makeText(getActivity(), String.format("%s: $%s", dateStr, dataPoint.getY()), Toast.LENGTH_SHORT).show();
                }
            });
            graph.onDataChanged(true, true);
            graph.addSeries(dataPoints);
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity(), new SimpleDateFormat("MM/dd/yy")));
            graph.getGridLabelRenderer().setLabelVerticalWidth(100);
        }
    }
}