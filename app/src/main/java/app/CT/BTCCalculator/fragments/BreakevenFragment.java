package app.CT.BTCCalculator.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.text.DecimalFormat;

import app.CT.BTCCalculator.events.BusProvider;
import app.CT.BTCCalculator.R;

public class BreakevenFragment extends Fragment {
    private EditText btcBought;
    private EditText btcBoughtPrice;
    private EditText btcSold;
    private EditText btcSoldPrice;
    private EditText optimalBTCPrice;
    private EditText optimalBTC;

    private TextView resultText;

    private String rate;

    private boolean[] containsCurrentRate = {false, false};

    // Function round to two decimals.
    public double roundTwoDecimals(double d) {
        DecimalFormat df = new DecimalFormat("###.##");
        return Double.parseDouble(df.format(d));
    }

    // Otto function to subscribe to Event Bus changes.
    @Subscribe
    public void onPriceUpdated(String mRate) {
        rate = mRate.replace(",", "");

        // If btcBoughtPrice field has the current price, update it.
        if (containsCurrentRate[0]) btcBoughtPrice.setText(rate);
        // If btcSoldPrice has the current price, update it as well.
        if (containsCurrentRate[1]) btcSoldPrice.setText(rate);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_breakeven, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Register Bus Provider instance.
        BusProvider.getInstance().register(this);

        View view = getView();

        // Initialize text fields.
        assert view != null;
        btcBought = (EditText) view.findViewById(R.id.btcBought);
        btcBoughtPrice = (EditText) view.findViewById(R.id.btcBoughtPrice);
        btcSold = (EditText) view.findViewById(R.id.btcSold);
        btcSoldPrice = (EditText) view.findViewById(R.id.btcSoldPrice);
        optimalBTCPrice = (EditText) view.findViewById(R.id.optimalBTCPrice);
        optimalBTC = (EditText) view.findViewById(R.id.optimalBTC);
        resultText = (TextView) view.findViewById(R.id.resultText);

        Button buttonCalculate = (Button) view.findViewById(R.id.buttonCalculate);

        // Checks whether the first visible EditText element is focused in order to enable
        // and show the keyboard to the user. The corresponding XML element has android:imeOptions="actionNext".
        // All EditText elements below are now programmed to show keyboard when pressed.
        btcBought.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSoftKeyboard(v);
            }
        });

        btcBoughtPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSoftKeyboard(v);
            }
        });

        btcSold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSoftKeyboard(v);
            }
        });

        btcSoldPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSoftKeyboard(v);
            }
        });

        optimalBTCPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSoftKeyboard(v);
            }
        });

        optimalBTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSoftKeyboard(v);
            }
        });

        optimalBTCPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float result = ((Float.valueOf(btcSold.getText().toString())) * Float.valueOf(btcSoldPrice.getText().toString())) / Float.valueOf(optimalBTCPrice.getText().toString());
                    optimalBTC.setText(String.valueOf(result));
                } catch (Exception ignored) {
                }

            }
        });

        btcBoughtPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                containsCurrentRate[0] = false;
            }
        });

        btcSoldPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                containsCurrentRate[1] = false;
            }
        });

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float buyAmount, buyCost, sellAmount, sellPrice, optimalBTCCost,
                        optimalBTCAmount, remainder, totalCost, totalAmount, finalPrice;
                boolean didItWork = true;
                boolean validTransaction = true;

                // Error checking to prevent crashes.
                try {
                    // Gets the input entered from the user.
                    buyAmount = Float.valueOf(btcBought.getText().toString());
                    buyCost = Float.valueOf(btcBoughtPrice.getText().toString());
                    sellAmount = Float.valueOf(btcSold.getText().toString());
                    sellPrice = Float.valueOf(btcSoldPrice.getText().toString());
                    optimalBTCCost = Float.valueOf(optimalBTCPrice.getText().toString());
                    optimalBTCAmount = Float.valueOf(optimalBTC.getText().toString());

                    // Calculates remainder from the buying and selling.
                    remainder = Math.abs((buyAmount - sellAmount));

                    // User cannot sell more than they own.
                    if (roundTwoDecimals(optimalBTCAmount * optimalBTCCost) > roundTwoDecimals(sellAmount*sellPrice)) {
                        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage(getString(R.string.optimalBTCErrorMsg));
                        alertDialog.setCancelable(false);
                        alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();
                        validTransaction = false;
                    }

                    // Checks if the user typed in a greater selling amount than buying.
                    if (sellAmount > buyAmount) {
                        // Create new dialog popup.
                        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity());

                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("You cannot sell more than you own.");
                        alertDialog.setCancelable(false);
                        alertDialog.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
                    totalAmount = optimalBTCAmount + remainder;
                    finalPrice = totalCost / totalAmount;

                    if (validTransaction) {
                        resultText.setText(String.format(getString(R.string.resultText), String.valueOf(totalAmount), String.valueOf(roundTwoDecimals(finalPrice))));
                    }
                } catch (Exception e) {
                    // Sets bool to false in order to execute "finally" block below.
                    didItWork = false;
                } finally {
                    if (!didItWork) {
                        // Creates new dialog popup.
                        final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());

                        alertDialog2.setTitle("Error");
                        alertDialog2.setMessage("Please fill in all fields.");
                        alertDialog2.setCancelable(false);
                        alertDialog2.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    // When Options Menu items are selected; adds or removes the current price from their corresponding fields.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
            0 represents the buy field
            1 represents the sell field
        */
        switch (item.getItemId()) {
            case R.id.editCurrentBuyField: {

                try {
                    EditText btcBoughtPrice = (EditText) getView().findViewById(R.id.btcBoughtPrice);

                    // If field contains the current price, remove it; else, add the current price.
                    if (containsCurrentRate[0]) {
                        btcBoughtPrice.setText("");
                        containsCurrentRate[0] = false;
                    } else {
                        btcBoughtPrice.setText(rate);
                        containsCurrentRate[0] = true;
                    }
                } catch (Exception ignored) {
                }

                return true;
            }
            case R.id.editCurrentSellField: {

                try {
                    EditText btcSoldPrice = (EditText) getView().findViewById(R.id.btcSoldPrice);

                    // If field contains the current price, remove it; else, add the current price.
                    if (containsCurrentRate[1]) {
                        btcSoldPrice.setText("");
                        containsCurrentRate[1] = false;
                    } else {
                        btcSoldPrice.setText(rate);
                        containsCurrentRate[1] = true;
                    }
                }
                catch (Exception ignored) {
                }

                return true;
            }
            case R.id.resetAll: {
                btcBought.setText("");
                btcBoughtPrice.setText("");
                btcSold.setText("");
                btcSoldPrice.setText("");
                optimalBTC.setText("");
                optimalBTCPrice.setText("");
                resultText.setText("");
                containsCurrentRate[0] = containsCurrentRate[1] = false;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

