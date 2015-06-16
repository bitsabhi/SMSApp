package com.healthkart.smsapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by admin on 6/16/2015.
 */
public class SMSAdapter extends ArrayAdapter {
    private ArrayList<String> item;
    private Context context;
    public SMSAdapter(Context context, int resource, ArrayList<String> item) {
        super(context, resource);
        this.item = item;
        this.context= context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        String phone = textView.getText().toString().split(" ")[0];

        // If number is not in contact list
        if (ReceiveSmsActivity.getContactName(context, phone)== null) {
            textView.setTextColor(Color.parseColor("#000FFF"));
        }

        else {
            textView.setTextColor(Color.parseColor("#000000"));
        }



        return textView;
    }
}
