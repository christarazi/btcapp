package app.CT.BTCCalculator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;
import com.squareup.otto.Subscribe;

import java.text.DecimalFormat;

public class FragmentFirst extends SherlockFragment
{
    EditText editFirst;      EditText editSecond;       EditText editThird;
    EditText editFourth;     EditText editFifth;        EditText editSixth;
    TextView editResultText; TextView editOptimizeText; Button buttonCalculate;
    Button buttonOptimize;   String rate;

    // Function to take the input and round to two decimals.
    double roundTwoDecimals(double d)
    {
        DecimalFormat df = new DecimalFormat("###.##");
        return Double.parseDouble(df.format(d));
    }

    // Otto function to subscribe to Event Bus changes.
    @Subscribe
    public void onPriceUpdated(String mRate)
    {
        rate = mRate;
        Log.d("Chris", "This is coming from the Fragment: " + rate);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Set options menu.
        setHasOptionsMenu(true);
    }

    // Create the view.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Return the view.
        // Log.d("Chris", "onCreateView from FragmentFirst was called");
        return inflater.inflate(R.layout.first, container, false);
    }

    // Creates the activity for the fragment.
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Register Bus Provider instance.
        BusProvider.getInstance().register(this);

        View v = getView();

        // Initialize text fields.
        editFirst        = (EditText) v.findViewById(R.id.editFirst);
        editSecond       = (EditText) v.findViewById(R.id.editSecond);
        editThird        = (EditText) v.findViewById(R.id.editThird);
        editFourth       = (EditText) v.findViewById(R.id.editFourth);
        editFifth        = (EditText) v.findViewById(R.id.editFifth);
        editSixth        = (EditText) v.findViewById(R.id.editSixth);
        editResultText   = (TextView) v.findViewById(R.id.resultText);
        editOptimizeText = (TextView) v.findViewById(R.id.optimizeMessage);

        // Initialize buttons.
        buttonCalculate  = (Button) v.findViewById(R.id.calculate);
        buttonOptimize   = (Button) v.findViewById(R.id.optimize);
        Button buttonTest = (Button) v.findViewById(R.id.test);

        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d("Chris", "This is coming from the Fragment: " + rate);
            }
        });

        // Checks whether the first visible EditText element is focused in order to enable
        // and show the keyboard to the user. The corresponding XML element has android:imeOptions="actionNext".
        // All EditText elements below are now programmed to show keyboard when pressed.
        editFirst.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editFirst.clearFocus();
                editFirst.requestFocus();
                if(editFirst.isFocused())
                {
                    // Log.d("Chris", "editFirst onClick, is focused, should have cleared focus, keyboard should show.");
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        editSecond.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editSecond.clearFocus();
                editSecond.requestFocus();
                if (editSecond.isFocused())
                {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        editThird.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editThird.clearFocus();
                editThird.requestFocus();
                if(editThird.isFocused())
                {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        editFourth.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editFourth.clearFocus();
                editFourth.requestFocus();
                if(editFourth.isFocused())
                {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        editFifth.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editFifth.clearFocus();
                editFifth.requestFocus();
                if(editFifth.isFocused())
                {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        editSixth.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editSixth.clearFocus();
                editSixth.requestFocus();
                if(editSixth.isFocused())
                {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        // Listens to when "Calculate" button is pressed.
        buttonCalculate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                float buyAmount, buyCost, sellAmount, sellPrice, buyAmount2,
                      buyCost2, remainder, totalCost, totalAmount, finalPrice;
                boolean didItWork = true; boolean validTransaction = true;

                /* Dismisses the keyboard.
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                        */

                // Error checking to prevent crashes.
                try
                {
                    // Gets the input entered from the user.
                    buyAmount = Float.valueOf(editFirst.getText().toString());
                    buyCost = Float.valueOf(editSecond.getText().toString());
                    sellAmount = Float.valueOf(editThird.getText().toString());
                    sellPrice = Float.valueOf(editFourth.getText().toString());
                    buyAmount2 = Float.valueOf(editFifth.getText().toString());
                    buyCost2 = Float.valueOf(editSixth.getText().toString());

                    // Calculates remainder from the buying and selling.
                    remainder = Math.abs((buyAmount - sellAmount));

                    // Checks if the user typed in a greater selling amount than buying.
                    if(sellAmount > buyAmount)
                    {
                        // Create new dialog popup.
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                        // Sets title.
                        alertDialog.setTitle("Error");

                        // Sets dialog message.
                        alertDialog.setMessage("You cannot sell more than you own.");
                        alertDialog.setCancelable(false);
                        alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                // If this button is clicked, close current dialog.
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();

                        // Invalidates transaction since selling amount > buying amount.
                        validTransaction = false;
                    }

                    // Calculations to output.
                    totalCost = buyAmount * buyCost;
                    totalAmount = buyAmount2 + remainder;
                    finalPrice = totalCost / totalAmount;

                    // Checks if valid.
                    if(validTransaction)
                    {
                        editResultText.setText("You need to sell " + String.valueOf(totalAmount) + " BTC at " + String.valueOf(roundTwoDecimals(finalPrice)));
                    }
                }
                catch(Exception e)
                {
                    // Sets bool to false in order to execute "finally" block below.
                    didItWork = false;
                }
                finally
                {
                    if(!didItWork)
                    {
                        // Creates new dialog popup.
                        final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());

                        // Sets title.
                        alertDialog2.setTitle("Error");

                        // Sets dialog message.
                        alertDialog2.setMessage("Please fill in all fields.");
                        alertDialog2.setCancelable(false);
                        alertDialog2.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                // If this button is clicked, close current dialog.
                                dialog.cancel();
                            }
                        });

                        // Show the dialog.
                        alertDialog2.show();
                    }
                }

            }
        });

        buttonOptimize.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                float sellAmount, sellPrice, buyCost2, newBalance, optimalBTC;
                boolean didItWork = true;

                // Dismisses the keyboard.
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                // Error checking to prevent crashes.
                try
                {
                    // Gets the input entered from the user.
                    sellAmount = Float.valueOf(editThird.getText().toString());
                    sellPrice = Float.valueOf(editFourth.getText().toString());
                    buyCost2 = Float.valueOf(editSixth.getText().toString());

                    newBalance = sellAmount * sellPrice;
                    optimalBTC = newBalance / buyCost2;

                    editOptimizeText.setText("The optimal BTC you should buy is " + String.valueOf(roundTwoDecimals(optimalBTC)) + ".");
                }
                catch (Exception e)
                {
                    // Sets bool to false in order to execute "finally" block below.
                    didItWork = false;
                }
                finally
                {
                    if(!didItWork)
                    {
                        // Create new dialog popup.
                        final AlertDialog.Builder alertDialog3 = new AlertDialog.Builder(getActivity());

                        // Sets title.
                        alertDialog3.setTitle("Error");

                        // Sets dialog message.
                        alertDialog3.setMessage("Please look at the note.");
                        alertDialog3.setCancelable(false);
                        alertDialog3.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                // If this button is clicked, close current dialog.
                                dialog.cancel();
                            }
                        });

                        // Show dialog
                        alertDialog3.show();
                    }
                }
            }
        });

        /*  DISCLAIMER:
                    This section of the app is a prank I've implemented for my friends to have fun with.
                    It does not actually have any access to the user's data.
                    The app has no permissions.
        */
        // Listens for a long press of the "Calculate" button.
        buttonCalculate.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                // String array with messages to print.
                // Note: app crashes if the last blank string isn't included at the end.
                String[] jokesToPrint =
                        {
                                "Searching for personal information...please wait.",
                                "Initializing data transfer.",
                                "Uploading bank records...",
                                "Uploading credit card information...",
                                "Uploading Social Security Number...",
                                "Finalizing identity profile...",
                                "Running heuristics...",
                                "Data transfer complete. Thanks for your patience.",
                                ""
                        };

                // Calling the class and passing the string array through to ASyncTask.
                new ProgressUpdater().execute(jokesToPrint);
                return true;
            }
        });
    }

    // Class that changes the dialog (string array) in the background.
    public class ProgressUpdater extends AsyncTask<String, String, Void>
    {
        // Initialize index for string array to zero.
        int n = 0;
        ProgressDialog jokeDialog;

        // Create progress dialog object.
        protected void onPreExecute()
        {
            jokeDialog = new ProgressDialog(getActivity());
            jokeDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            jokeDialog.setCancelable(false);
            jokeDialog.show();
        }

        // Runs in the background; returns void (null); takes in string array (jokesDialog).
        @Override
        protected Void doInBackground(String... params)
        {
            for (String param : params)
            {
                try
                {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                // Publishes the change (calls onProgressUpdate to main thread on UI),
                // with a string passed through; increments index by 1.
                publishProgress(params); n++;
            }
            return null;
        }

        // Takes in string and changes the dialog message according to the index.
        protected void onProgressUpdate(String... progress)
        {
            // Having the index at "n" crashes the app, having "n-1" prevents it.
            jokeDialog.setMessage(progress[n-1]);
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            jokeDialog.dismiss();
            n = 0;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.AddCurrent: AddCurrent(); break;
            case R.id.RemoveCurrent: RemoveCurrent(); break;
        }
        return false;
    }

    public void AddCurrent()
    {
        EditText editSecond; View v = getView();
        editSecond = (EditText) v.findViewById(R.id.editSecond);

        editSecond.setText(rate);

        //Log.d("Chris", "Called in fragment.");
    }

    public void RemoveCurrent()
    {
        EditText editSecond; View v = getView();
        editSecond = (EditText) v.findViewById(R.id.editSecond);

        editSecond.setText("");

        //Log.d("Chris", "Called in fragment.");
    }
}

