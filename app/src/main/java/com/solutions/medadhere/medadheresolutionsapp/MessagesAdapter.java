package com.solutions.medadhere.medadheresolutionsapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Yeshy on 3/6/2016.
 */
class MessagesAdapter extends BaseAdapter {

    Context context;
    String[] data;
    String[][] dataFull;
    int[] count;
    private static LayoutInflater inflater = null;

    public MessagesAdapter(Context context, String[] data, int[] count) {

        this.context = context;
        this.data = data;
        this.count = count;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    public MessagesAdapter(Context context, String[] data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.medication_list, null);
        }
        TextView text = (TextView) vi.findViewById(R.id.text);
        LinearLayout layout = (LinearLayout) vi.findViewById(R.id.linearLayout111);
        String textmodified = "  " + data[position];
        text.setText(textmodified);
        if(count != null) {
            if(count.length == data.length) {
                if(count[position] == 1) {
                    layout.setBackgroundColor(Color.parseColor("#a6d785"));
                }
            }

        }
        return vi;
    }
}
