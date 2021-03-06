/*
 * Copyright (C) 2017 Chris Tarazi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package app.CT.BTCCalculator.fragments;

import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import app.CT.BTCCalculator.activities.AboutActivity;
import app.CT.BTCCalculator.events.BusProvider;
import app.CT.BTCCalculator.R;

public class PriceDataFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnChartValueSelectedListener {
    private Context context;
    private String rate;
    private String time;

    private SwipeRefreshLayout swipeRefreshLayout;
    private LineChart graph;

    // Sets the rate and calls sendRate() to publish to Otto Event Bus.
    public void setRate(String mRate) {
        this.rate = mRate;
        sendRate();
    }

    // Otto Event Bus method to publish the rate to the Event Bus.
    public void sendRate() {
        BusProvider.getInstance().post(rate);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    // Create the view.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price, container, false);

        context = getContext();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
        graph = (LineChart) view.findViewById(R.id.graph);

        return view;
    }

    // Creates the activity for the fragment.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Register Bus Provider instance.
        BusProvider.getInstance().register(this);

        // Initialize graph settings
        graph.setOnChartValueSelectedListener(this);
        graph.getAxisLeft().setStartAtZero(false);
        graph.getAxisRight().setEnabled(false);
        graph.setDoubleTapToZoomEnabled(false);
        graph.setHighlightPerDragEnabled(true);
        graph.setAutoScaleMinMaxEnabled(true);
        graph.setDescription("BTC price for past 30 days");

        // Format Y axis labels to have dollar sign in front of price
        graph.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return String.format("$%.1f", value);
            }
        });
        graph.getAxisRight().setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return String.format("$%.1f", value);
            }
        });

        // Execute the async tasks.
        new GetCurrentPriceTask().execute();
        new GetGraphDataTask().execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutPage: {
                startActivity(new Intent(getContext(), AboutActivity.class));
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Unregister the bus when pausing the application
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register the bus when resuming the application
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onRefresh() {
        new GetCurrentPriceTask().execute();
        new GetGraphDataTask().execute();
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
                if (urlConnection == null)
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
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            finally {
                try {
                    if (in != null) in.close();
                    urlConnection.disconnect();
                }
                catch (Exception ignored) {}
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
                Toast.makeText(context, R.string.api_failed, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            return rate;
        }

        // After parsing has been completed.
        @Override
        protected void onPostExecute(final String mRate) {
            super.onPostExecute(mRate);

            // Tell swipe-to-refresh animation to stop.
            swipeRefreshLayout.setRefreshing(false);

            if (mRate == null) {
                priceData.setText(R.string.connectionFailed);
                Toast.makeText(context, R.string.connectionFailed, Toast.LENGTH_SHORT).show();
                return;
            }

            // Set text field with the rate.
            priceData.setText(String.format("$%s", mRate));
            setRate(mRate);
            timeData.setText(time);

            // Creates a toast that indicates the price has updated.
            Toast.makeText(context, "Updated.", Toast.LENGTH_SHORT).show();
        }
    }

    // Get historical price data from CoinDesk and graph it
    public class GetGraphDataTask extends AsyncTask<LineData, Void, LineData> {
        @Override
        protected LineData doInBackground(LineData... params) {
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
                TreeMap<String, Entry> stringEntryTreeMap = new TreeMap<>();

                // Insert json data into sorted tree map because json ordering is not guaranteed
                String key;
                while (iter.hasNext()) {
                    key = iter.next();
                    stringEntryTreeMap.put(key, new Entry((float) jObjectGraph.getDouble(key), 0));
                }

                // Convert sorted data from json to array lists so we can pass it to the graphing library
                ArrayList<String> xVals = new ArrayList<>(stringEntryTreeMap.keySet());
                ArrayList<Entry> yVals = new ArrayList<>(stringEntryTreeMap.values());

                // Modify the index of each entry so that it is ordered
                int index = 0;
                for (Entry entry : yVals) {
                    entry.setXIndex(index);
                    index++;
                }

                LineDataSet lineDataSet = new LineDataSet(yVals, "$ / BTC");

                int color = ContextCompat.getColor(context, R.color.accent);
                lineDataSet.setColor(color);
                lineDataSet.setCircleColor(color);
                lineDataSet.setCircleColorHole(color);
                lineDataSet.setDrawValues(false);
                lineDataSet.setLineWidth(Utils.convertPixelsToDp(6));
                lineDataSet.setHighlightLineWidth(Utils.convertPixelsToDp(4));

                return new LineData(xVals, lineDataSet);
            }
            catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(LineData data) {
            super.onPostExecute(data);

            View view = getView();
            if (view == null) {
                return;
            }

            TextView timeData = (TextView) view.findViewById(R.id.timeData);
            int graphLabelAxisColor = timeData.getCurrentTextColor();

            graph.getAxisLeft().setTextColor(graphLabelAxisColor);
            graph.getXAxis().setTextColor(graphLabelAxisColor);
            graph.setData(data);
            graph.invalidate();
            graph.animateXY(1000, 1000);
        }
    }

    // When value on graph is selected, set toast to value
    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Toast.makeText(context,
                String.format("%s: $%s", graph.getXValue(e.getXIndex()), e.getVal()),
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onNothingSelected() {

    }
}