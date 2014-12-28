package app.CT.BTCCalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.otto.Subscribe;

public class ProfitFragment extends SherlockFragment {
    // Declare variables for this class.
    private EditText editFirstProfit;
    private EditText editSecondProfit;
    private EditText editThirdProfit;
    private EditText editFourthProfit;
    private EditText editPercent;

    private SeekBar seekBar;

    private TextView calculationsText;
    private TextView feeTransResult;
    private TextView subtotalResult;
    private TextView totalProfitResult;

    private Button calcBtn;

    private String rate;

    // Otto function to subscribe to Event Bus changes.
    @Subscribe
    public void onPriceUpdated(String mRate) {
        rate = mRate;
        //Log.d("Chris", "This is coming from the ProfitFragment: " + rate);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set options menu.
        setHasOptionsMenu(true);
    }

    // Called when the activity is attached to this fragment.
    @Override
    public void onAttach(Activity activity) {
        // Call to the super class.
        super.onAttach(activity);
    }

    // Create the view.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout from the XML Resource.
        return inflater.inflate(R.layout.second, container, false);
    }

    // Creates the activity for the fragment.
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Call to the super class.
        super.onActivityCreated(savedInstanceState);

        // Get View.
        View view = getView();

        // Register Bus Provider instance.
        BusProvider.getInstance().register(this);

        // Initialize text fields.
        editFirstProfit = (EditText) view.findViewById(R.id.editFirstProfit);
        editSecondProfit = (EditText) view.findViewById(R.id.editSecondProfit);
        editThirdProfit = (EditText) view.findViewById(R.id.editThirdProfit);
        editFourthProfit = (EditText) view.findViewById(R.id.editFourthProfit);
        editPercent = (EditText) view.findViewById(R.id.editPercent);

        seekBar = (SeekBar) view.findViewById(R.id.percentageBar);

        calculationsText = (TextView) view.findViewById(R.id.calculationsText);
        feeTransResult = (TextView) view.findViewById(R.id.feeTrans);
        subtotalResult = (TextView) view.findViewById(R.id.subtotal);
        totalProfitResult = (TextView) view.findViewById(R.id.totalProfit);

        calcBtn = (Button) view.findViewById(R.id.calcBtn);

        // Initialize percentage variable which is attached to seekbar and editPercent.
        final float[] percentage = new float[1];
        percentage[0] = (float) 0.0;

        // EditText element is clicked in order to enable and show the keyboard to the user.
        // The corresponding XML element has android:imeOptions="actionNext".
        // All EditText elements below are now programmed to show keyboard when pressed.
        editFirstProfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                v = getActivity().getCurrentFocus();
                inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
            }
        });

        editSecondProfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                v = getActivity().getCurrentFocus();
                inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
            }
        });

        editThirdProfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                v = getActivity().getCurrentFocus();
                inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
            }
        });

        editFourthProfit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                v = getActivity().getCurrentFocus();
                inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
            }
        });

        // Listens for the editPercent field to change in order to update the seek bar.
        editPercent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    // Update Seekbar value after entering a number
                    seekBar.setProgress(Integer.parseInt(s.toString()));
                    // Log.d("Chris", "editPercent.addTextChangedListener, percentage = " + percentage[0]);
                } catch (Exception ignored) {
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                percentage[0] = progress;
                // Log.d("Chris", "seekBar.setOnSeekBarChangeListener before, percentage = " + percentage[0]);
                editPercent.setText(String.valueOf(progress));

                // Sets the editPercent selection at the end of the input.
                editPercent.post(new Runnable() {
                    @Override
                    public void run() {
                        editPercent.setSelection(String.valueOf(progress).length());
                    }
                });
                // Log.d("Chris", "seekBar.setOnSeekBarChangeListener after, percentage = " + percentage[0]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        calcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float buyAmount, buyCost, sellAmount, sellPrice, remainder,
                        subtotalCost, subtotalPrice, subtotal, fee;
                float total = 0;

                boolean didItWork = true;
                boolean validTransaction = true;

                /* Dismisses the keyboard.
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                        */


                // Error checking to prevent crashes.
                try {
                    // Gets the input entered from the user.
                    buyAmount = Float.valueOf(editFirstProfit.getText().toString());
                    buyCost = Float.valueOf(editSecondProfit.getText().toString());
                    sellAmount = Float.valueOf(editThirdProfit.getText().toString());
                    sellPrice = Float.valueOf(editFourthProfit.getText().toString());

                    percentage[0] = (float) ((Float.valueOf(editPercent.getText().toString())) / 100.0);
                    percentage[0] = (float) (seekBar.getProgress() / 100.0);
                    // Log.d("Chris", "calcBtn.setOnClickListener, percentage = " + percentage[0]);

                    remainder = Math.abs((buyAmount - sellAmount));

                    if (sellAmount > buyAmount) {
                        // Create new dialog popup.
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                        // Sets title.
                        alertDialog.setTitle("Error");

                        // Sets dialog message.
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
                    subtotalCost = buyAmount * buyCost;
                    subtotalPrice = sellPrice * sellAmount;
                    subtotal = subtotalPrice - subtotalCost;

                    fee = subtotal * percentage[0];
                    total = subtotal - fee;

                    // Checks if the user made a profit.
                    if (total > 0) {
                        feeTransResult.setText("$" + fee);
                        subtotalResult.setText("$" + subtotal);
                        totalProfitResult.setText("$" + total);
                    } else {
                        total = Math.abs(total);
                        calculationsText.setText("You lost: $" + total);
                    }
                } catch (Exception e) {
                    // Sets bool to false in order to execute "finally" block below.
                    didItWork = false;
                } finally {
                    if (!didItWork) {
                        // Creates new dialog popup.
                        final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());

                        // Sets title.
                        alertDialog2.setTitle("Error");

                        // Sets dialog message.
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

    // Create Options Menu.
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    // When Options Menu items are selected.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.AddCurrent:
                AddCurrent();
                break;
            case R.id.RemoveCurrent:
                RemoveCurrent();
                break;
        }
        return false;
    }

    // Adds the current price to the text field.
    public void AddCurrent() {
        EditText editSecondProfit;
        View v = getView();

        editSecondProfit = (EditText) v.findViewById(R.id.editSecondProfit);

        editSecondProfit.setText(rate);

        //Log.d("Chris", "Called in fragment.");
    }

    // Removes the price and sets text field to default.
    public void RemoveCurrent() {
        EditText editSecondProfit;
        View v = getView();

        editSecondProfit = (EditText) v.findViewById(R.id.editSecondProfit);

        editSecondProfit.setText("");

        //Log.d("Chris", "Called in fragment.");
    }
}
