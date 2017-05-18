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

public class ProfitFragment extends Fragment {
    private EditText btcBought;
    private EditText btcBoughtPrice;
    private EditText btcSell;
    private EditText btcSellPrice;
    private EditText transPercent;

    private TextView feeTransResult;
    private TextView subtotalResult;
    private TextView totalProfitResult;

    private String rate;
    private float feePercent;
    private boolean[] containsCurrentRate = {false, false};

    private Menu menu;

    // Otto function to subscribe to Event Bus changes.
    @Subscribe
    public void onPriceUpdated(String mRate) {
        rate = mRate.replace(",", "");

        // If btcBoughtPrice field has the current price, update it.
        if (containsCurrentRate[0]) btcBoughtPrice.setText(rate);
        // If btcSellPrice has the current price, update it as well.
        if (containsCurrentRate[1]) btcSellPrice.setText(rate);
    }

    // Function round to two decimals.
    public double roundTwoDecimals(double d) {
        DecimalFormat df = new DecimalFormat("###.##");
        return Double.parseDouble(df.format(d));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout from the XML Resource.
        return inflater.inflate(R.layout.fragment_profit, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        BusProvider.getInstance().register(this);

        // Initialize text fields.
        assert view != null;
        btcBought = (EditText) view.findViewById(R.id.btcBought);
        btcBoughtPrice = (EditText) view.findViewById(R.id.btcBoughtPrice);
        btcSell = (EditText) view.findViewById(R.id.btcSell);
        btcSellPrice = (EditText) view.findViewById(R.id.btcSellPrice);
        transPercent = (EditText) view.findViewById(R.id.transPercent);

        feeTransResult = (TextView) view.findViewById(R.id.transFeeCost);
        subtotalResult = (TextView) view.findViewById(R.id.subtotal);
        totalProfitResult = (TextView) view.findViewById(R.id.totalProfit);

        Button calculate = (Button) view.findViewById(R.id.calculate);

        // EditText element is clicked in order to enable and show the keyboard to the user.
        // The corresponding XML element has android:imeOptions="actionNext".
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

        btcSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSoftKeyboard(v);
            }
        });

        btcSellPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSoftKeyboard(v);
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

        btcSellPrice.addTextChangedListener(new TextWatcher() {
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

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float buyAmount, buyCost, sellAmount, sellPrice, subtotalCost, subtotalPrice,
                        subtotal, fee, total;

                boolean validTrans = true;
                boolean didItWork = true;

                // Error checking to prevent crashes.
                try {
                    // Gets the input entered from the user.
                    buyAmount = Float.valueOf(btcBought.getText().toString());
                    buyCost = Float.valueOf(btcBoughtPrice.getText().toString());
                    sellAmount = Float.valueOf(btcSell.getText().toString());
                    sellPrice = Float.valueOf(btcSellPrice.getText().toString());

                    feePercent = (Float.valueOf(transPercent.getText().toString())) / 100.0f;

                    if (sellAmount > buyAmount) {
                        // Create new dialog popup.
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

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

                        validTrans = false;
                    }

                    // Calculations to output.
                    subtotalCost = buyAmount * buyCost;
                    subtotalPrice = sellPrice * sellAmount;
                    subtotal = subtotalPrice - subtotalCost;

                    fee = subtotalPrice * feePercent;
                    total = subtotal - fee;

                    if (validTrans) {
                        feeTransResult.setText(String.format("$%s", String.valueOf(roundTwoDecimals(fee))));
                        subtotalResult.setText(String.format("$%s", String.valueOf(roundTwoDecimals(subtotal))));
                        totalProfitResult.setText(String.format("$%s", String.valueOf(roundTwoDecimals(total))));
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

    // Create Options Menu.
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
        this.menu = menu;
        updateMenuTitles();

    }

    // When Options Menu items are selected; adds or removes the current price from their corresponding fields.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
            0 represents the buy field
            1 represents the sell field
        */

        View view = getView();
        if (view == null) {
            return false;
        }

        switch (item.getItemId()) {
            case R.id.editCurrentBuyField: {
                try {
                    EditText btcBoughtPrice = (EditText) view.findViewById(R.id.btcBoughtPrice);

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
                break;
            }
            case R.id.editCurrentSellField: {
                try {
                    EditText btcSellPrice = (EditText) view.findViewById(R.id.btcSellPrice);

                    // If field contains the current price, remove it; else, add the current price.
                    if (containsCurrentRate[1]) {
                        btcSellPrice.setText("");
                        containsCurrentRate[1] = false;
                    } else {
                        btcSellPrice.setText(rate);
                        containsCurrentRate[1] = true;
                    }
                }
                catch (Exception ignored) {
                }
                break;
            }
            case R.id.resetAll: {
                btcBought.setText("");
                btcBoughtPrice.setText("");
                btcSell.setText("");
                btcSellPrice.setText("");
                transPercent.setText("");
                feeTransResult.setText("$");
                subtotalResult.setText("$");
                totalProfitResult.setText("$");
                containsCurrentRate[0] = containsCurrentRate[1] = false;
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

        updateMenuTitles();
        return true;
    }

    private void updateMenuTitles() {
        if (containsCurrentRate[0])
            menu.findItem(R.id.editCurrentBuyField).setTitle(R.string.removeBuyField);
        else
            menu.findItem(R.id.editCurrentBuyField).setTitle(R.string.addBuyField);

        if (containsCurrentRate[1])
            menu.findItem(R.id.editCurrentSellField).setTitle(R.string.removeSellField);
        else
            menu.findItem(R.id.editCurrentSellField).setTitle(R.string.addSellField);
    }
}
