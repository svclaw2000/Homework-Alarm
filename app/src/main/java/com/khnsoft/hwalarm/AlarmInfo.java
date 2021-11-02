package com.khnsoft.hwalarm;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class AlarmInfo extends LinearLayout {
    public AlarmInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AlarmInfo(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.activity_alarm_info,this,true);
    }
}
