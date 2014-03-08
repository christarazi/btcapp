package com.example.BTC_Break_Even_Calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.Math;
import java.text.DecimalFormat;
import java.lang.String;

public class MyActivity extends Activity
{
    // rounds numbers to two decimal places
    public double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

     // called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // create text fields and buttons
        final EditText editFirst = (EditText) findViewById(R.id.editFirst);
        final EditText editSecond = (EditText) findViewById(R.id.editSecond);
        final EditText editThird = (EditText) findViewById(R.id.editThird);
        final EditText editFourth = (EditText) findViewById(R.id.editFourth);
        final EditText editFifth = (EditText) findViewById(R.id.editFifth);
        final EditText editSixth = (EditText) findViewById(R.id.editSixth);
        final TextView editResultText = (TextView) findViewById(R.id.resultText);
        final TextView editOptimizeText = (TextView) findViewById(R.id.optimizeMessage);

        Button buttonCalculate = (Button) findViewById(R.id.calculate);
        Button buttonOptimize = (Button) findViewById(R.id.optimize);

        // when "Calculate" button is pressed
        buttonCalculate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                double buyAmount, buyCost, sellAmount, sellPrice, buyAmount2, buyCost2, remainder,
                        totalCost, totalAmount, finalPrice;
                boolean didItWork = true; boolean validTransaction = true;

                // dismisses the keyboard
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                // error checking to prevent crashes
                try
                {
                    // get the input entered from the user
                    buyAmount = Double.valueOf(editFirst.getText().toString());
                    buyCost = Double.valueOf(editSecond.getText().toString());
                    sellAmount = Double.valueOf(editThird.getText().toString());
                    sellPrice = Double.valueOf(editFourth.getText().toString());
                    buyAmount2 = Double.valueOf(editFifth.getText().toString());
                    buyCost2 = Double.valueOf(editSixth.getText().toString());

                    // calculate remainder from the buying and selling
                    remainder = Math.abs((buyAmount - sellAmount));

                    //checks if the user typed in a greater selling amount than buying
                    if(sellAmount > buyAmount)
                    {
                        // create new dialog popup
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyActivity.this);

                        // set title

                        alertDialog.setTitle("Error");

                        // set dialog message
                        alertDialog.setMessage("You cannot sell more than you own.");
                        alertDialog.setCancelable(false);
                        alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                // if this button is clicked, close current activity
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();

                        // invalid trans. since selling amount > buying amount
                        validTransaction = false;
                    }

                    // calculations to output
                    totalCost = buyAmount * buyCost;
                    totalAmount = buyAmount2 + remainder;
                    finalPrice = totalCost / totalAmount;

                    // checks if valid
                    if(validTransaction)
                    {
                        editResultText.setText("You need to sell " + String.valueOf(totalAmount) + " BTC at " + String.valueOf(roundTwoDecimals(finalPrice)));
                    }
                }
                catch(Exception e)
                {
                    // set bool to false to execute finally block below
                    didItWork = false;
                }
                finally
                {
                    if(!didItWork)
                    {
                        // create new dialog popup
                        final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(MyActivity.this);

                        // set title
                        alertDialog2.setTitle("Error");

                        // set dialog message
                        alertDialog2.setMessage("Please fill in all fields.");
                        alertDialog2.setCancelable(false);
                        alertDialog2.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                // if this button is clicked, close current activity
                                dialog.cancel();
                            }
                        });

                        // show the dialog
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
                double sellAmount, sellPrice, buyCost2, newBalance, optimalBTC;
                boolean didItWork = true;

                // dismisses the keyboard
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                // error checking to prevent crashes
                try
                {
                    // get the input entered from the user
                    sellAmount = Double.valueOf(editThird.getText().toString());
                    sellPrice = Double.valueOf(editFourth.getText().toString());
                    buyCost2 = Double.valueOf(editSixth.getText().toString());

                    String amount = Double.toString(sellAmount);
                    String price = Double.toString(sellPrice);
                    String cost = Double.toString(buyCost2);

                    newBalance = sellAmount * sellPrice;
                    optimalBTC = newBalance / buyCost2;

                    editOptimizeText.setText("The optimal BTC you should buy is " + String.valueOf(roundTwoDecimals(optimalBTC)) + ".");
                }
                catch (Exception e)
                {
                    // set bool to false to execute finally block below
                    didItWork = false;
                }
                finally
                {
                    if(!didItWork)
                    {
                        // create new dialog popup
                        final AlertDialog.Builder alertDialog3 = new AlertDialog.Builder(MyActivity.this);

                        // set title
                        alertDialog3.setTitle("Error");

                        // set dialog message
                        alertDialog3.setMessage("Please look at the note.");
                        alertDialog3.setCancelable(false);
                        alertDialog3.setNeutralButton("Ok", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                // if this button is clicked, close current activity
                                dialog.cancel();
                            }
                        });

                        // show dialog
                        alertDialog3.show();
                    }
                }
            }
        });

        /*  DISCLAIMER:
                This section of the app is a prank I've implemented for my friends to have fun with.
                It does not actually have any access to the user's data.
                It requires no permissions.
        */
        buttonCalculate.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                // string array with messages to print, note: app crashes if the blank string isn't included at the end
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

                // calling the class and passing the string array through to ASyncTask
                new ProgressUpdater().execute(jokesToPrint);
                return true;
            }
        });
    }

    // class that changes the dialog (string array) in the background
    public class ProgressUpdater extends AsyncTask<String, String, Void>
    {
        // create index for string array
        int n = 0;
        ProgressDialog jokeDialog;

        // create progress dialog object
        protected void onPreExecute()
        {
            jokeDialog = new ProgressDialog(MyActivity.this);
            jokeDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            jokeDialog.setCancelable(false);
            jokeDialog.show();
        }

        // runs in the background, returns void (null), takes in string array (jokesDialog)
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

                // publishes the change (calls onProgressUpdate to main thread on UI)
                // with a string passed through, increments index by 1
                publishProgress(params); n++;
            }
            return null;
        }

        // takes in string and changes the dialog message according to the index
        protected void onProgressUpdate(String... progress)
        {
            // having the index at "n" crashes the app, having "n-1" solves the issue
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
}