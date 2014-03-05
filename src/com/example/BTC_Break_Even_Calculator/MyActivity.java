package com.example.BTC_Break_Even_Calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.Math;
import java.text.DecimalFormat;

public class MyActivity extends Activity
{
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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

        buttonCalculate.setOnClickListener(new View.OnClickListener()
        {
            public double roundTwoDecimals(double d)
            {
                DecimalFormat twoDForm = new DecimalFormat("#.##");
                return Double.valueOf(twoDForm.format(d));
            }

            @Override
            public void onClick(View v)
            {
                double buyAmount = Double.valueOf(editFirst.getText().toString());
                double buyCost = Double.valueOf(editSecond.getText().toString());
                double sellAmount = Double.valueOf(editThird.getText().toString());
                double sellPrice = Double.valueOf(editFourth.getText().toString());
                double buyAmount2 = Double.valueOf(editFifth.getText().toString());
                double buyCost2 = Double.valueOf(editSixth.getText().toString());

                double remainder = Math.abs((buyAmount - sellAmount));

                double totalCost = buyAmount * buyCost;
                double totalAmount = buyAmount2 + remainder;
                double finalPrice = totalCost / totalAmount;

                editResultText.setText("You need to sell " + String.valueOf(totalAmount) + " BTC at " + String.valueOf(roundTwoDecimals(finalPrice)));
            }
        });

        buttonOptimize.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                double sellAmount = Double.valueOf(editThird.getText().toString());
                double sellPrice = Double.valueOf(editFourth.getText().toString());
                double buyCost2 = Double.valueOf(editSixth.getText().toString());

                double newBalance = sellAmount * sellPrice;
                double optimalBTC = newBalance / buyCost2;

                editOptimizeText.setText("The optimal BTC you should buy is " + String.valueOf(optimalBTC) + ".");
            }
        });
    }
}