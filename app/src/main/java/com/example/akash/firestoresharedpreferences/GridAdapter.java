package com.example.akash.firestoresharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;
import static android.util.TypedValue.COMPLEX_UNIT_DIP;

public class GridAdapter extends BaseAdapter {

    private Context mcontext;
    private int[] indexes;


    public GridAdapter(Context context, int[] indexes){
        this.mcontext = context;
        this.indexes = indexes;
    }

    @Override
    public int getCount() {

        return indexes.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        TextView numberview;

        if (view == null){
            numberview = new TextView(mcontext);
        }
        else {
            numberview = (TextView) view;
        }

        numberview.setText(String.valueOf(i+1));
        numberview.setTextSize(COMPLEX_UNIT_DIP,20);
        numberview.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        numberview.setHeight((int) TypedValue.applyDimension(COMPLEX_UNIT_DIP,50, mcontext.getResources().getDisplayMetrics()));
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.RECTANGLE);



        switch (indexes[i]){


            case 1://Attempted and no need of review
                gd.setColor(mcontext.getResources().getColor(R.color.colorGreen));
                gd.setStroke(5, mcontext.getResources().getColor(R.color.colorGreen));
                break;
            case 2://Attempted and need to be reviewed
                gd.setColor(mcontext.getResources().getColor(R.color.colorPurple));
                gd.setStroke(5, mcontext.getResources().getColor(R.color.colorGreen));
                break;
            case 3://Skipped and no need to be reviewed
                gd.setColor(mcontext.getResources().getColor(R.color.colorRed));
                gd.setStroke(5, mcontext.getResources().getColor(R.color.colorRed));
                break;
            case 4://Skipped and need to be reviewed
                gd.setColor(mcontext.getResources().getColor(R.color.colorPurple));
                gd.setStroke(5, mcontext.getResources().getColor(R.color.colorRed));
                break;
            case 0://Not seen yet
                gd.setColor(mcontext.getResources().getColor(R.color.colorWhite));
                gd.setStroke(5, mcontext.getResources().getColor(R.color.colorWhite));
                break;

            default:
                break;
        }


        numberview.setBackground(gd);
        return numberview;
    }
}