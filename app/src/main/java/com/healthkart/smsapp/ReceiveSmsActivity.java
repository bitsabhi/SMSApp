package com.healthkart.smsapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

public class ReceiveSmsActivity extends Activity implements OnItemClickListener {
    private TextView mTextView;
    private static ReceiveSmsActivity inst;
    ArrayList<String> smsMessagesList = new ArrayList<String>();
    ListView smsListView;
    ArrayAdapter arrayAdapter;
    private SwipeDetector mSwipeDetector;

    public static ReceiveSmsActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_sms);
        mTextView = (TextView) findViewById(R.id.textView);
        mSwipeDetector = new SwipeDetector();
        smsListView = (ListView) findViewById(R.id.SMSList);
        arrayAdapter = new SMSAdapter(this, R.layout.list_item, smsMessagesList);
        smsListView.setAdapter(arrayAdapter);
        smsListView.setOnItemClickListener(this);
        smsListView.setOnTouchListener(mSwipeDetector);
        refreshSmsInbox();
    }

    public void refreshSmsInbox() {

        // Get the SMS cursor
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        int indexDate = smsInboxCursor.getColumnIndex("date");



        /*Date date = new Date(timeMillis);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        String dateText = format.format(date);*/

        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();

        do {
            int count = 0;
            // Get time, number and message body from the cursor.

            long timestamp = Long.valueOf(smsInboxCursor.getString(indexDate));
            String address = smsInboxCursor.getString(indexAddress);
            String body = smsInboxCursor.getString(indexBody);

            Timestamp stamp = new Timestamp(timestamp);
            Date date = new Date(stamp.getTime());

            String str = address + "  " +
                    "\n" + body + " at " + date + "\n";
            arrayAdapter.add(str);
            smsMessagesList.add(str);



            count++;

        } while (smsInboxCursor.moveToNext());

        arrayAdapter.notifyDataSetChanged();

    }
    public void updateList(final String smsMessage) {
        arrayAdapter.insert(smsMessage, 0);
        arrayAdapter.notifyDataSetChanged();
    }


    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        if(mSwipeDetector.swipeDetected()) {

            // If the item is right swiped
            if(mSwipeDetector.getAction() == SwipeDetector.Action.LR) {

                TextView tv =  ((TextView) view);

                // Get number from the item
                String phoneNumber = tv.getText().toString().split(" ")[0];

                // if the number is in the contact list
                if (getContactName(ReceiveSmsActivity.this, phoneNumber) != null) {
                    tv.setTypeface(null, Typeface.NORMAL);

                }

                else {

                    arrayAdapter.remove(arrayAdapter.getItem(pos));
                    arrayAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), phoneNumber + " removed" , Toast.LENGTH_LONG).show();
                }





            } else {

            }


        }
    }

    // Get contact name from the number
    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    public void goToCompose(View view) {
        Intent intent = new Intent(ReceiveSmsActivity.this, SendSmsActivity.class);
        startActivity(intent);
    }
}