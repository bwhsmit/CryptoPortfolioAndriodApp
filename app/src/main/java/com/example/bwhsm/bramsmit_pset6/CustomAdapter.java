package com.example.bwhsm.bramsmit_pset6;


import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by bwhsm on 18-10-2017.
 */

public class CustomAdapter extends ArrayAdapter<Coin> {

    public CustomAdapter(@NonNull Context context, ArrayList<Coin> coinList) {
        super(context, R.layout.custom_row,coinList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_row, parent, false);

        final Coin coin = getItem(position);

        ImageView coinLogo = (ImageView) customView.findViewById(R.id.coinLogo);
        TextView tvSymbol = (TextView) customView.findViewById(R.id.tvSymbol);
        TextView tvHoldingValue = (TextView) customView.findViewById(R.id.tvHoldingValue);
        TextView tvHoldingAmount = (TextView) customView.findViewById(R.id.tvHoldingAmount);
        TextView tvPrice = (TextView) customView.findViewById(R.id.tvPrice);

        // TODO initialize row elements

        // TODO add coinLogo
        double amount = coin.getAmount();
        double price = coin.getPriceUSD();
        double holdingValue = amount * price;

        tvSymbol.setText(coin.getSymbol());
        tvHoldingValue.setText("$" + new DecimalFormat("##.##").format(holdingValue));
        tvHoldingAmount.setText(Double.toString(amount));
        tvPrice.setText("$" + Double.toString(price));

        return customView;
    }
}