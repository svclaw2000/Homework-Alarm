package com.schlife.project02;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class add_item extends AppCompatActivity {
    EditText editTitle;
    EditText editContent;
    TextView Bconfirm;
    TextView Bcancel;
    TextView timeStart;
    int[] starttime;
    TextView timeEnd;
    int[] endtime;
    int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_item);

        state = 0;

        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);

        final Calendar cal = Calendar.getInstance();
        starttime = new int[2];
        starttime[0] = cal.get(Calendar.HOUR_OF_DAY);
        starttime[1] = cal.get(Calendar.MINUTE);

        timeStart = findViewById(R.id.timeStart);
        timeStart.setText(String.format("%02d : %02d", starttime[0], starttime[1]));
        timeStart.setFocusableInTouchMode(true);
        timeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 0) {
                    state = 1;
                    TimePickerDialog dialog = new TimePickerDialog(add_item.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            timeStart.requestFocus();
                            starttime[0] = hourOfDay;
                            starttime[1] = minute;
                            timeStart.setText(String.format("%02d : %02d", starttime[0], starttime[1]));
                        }
                    }, starttime[0], starttime[1], false);
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            state = 0;
                        }
                    });
                    dialog.show();
                }
            }
        });

        endtime = new int[2];
        endtime[0] = cal.get(Calendar.HOUR_OF_DAY) + 1;
        endtime[1] = cal.get(Calendar.MINUTE);

        timeEnd = findViewById(R.id.timeEnd);
        timeEnd.setText(String.format("%02d : %02d", endtime[0], endtime[1]));
        timeEnd.setFocusableInTouchMode(true);
        timeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 0) {
                    state = 1;
                    TimePickerDialog dialog = new TimePickerDialog(add_item.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            timeEnd.requestFocus();
                            endtime[0] = hourOfDay;
                            endtime[1] = minute;
                            timeEnd.setText(String.format("%02d : %02d", endtime[0], endtime[1]));
                        }
                    }, endtime[0], endtime[1], false);
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            state = 0;
                        }
                    });
                    dialog.show();
                }
            }
        });

        Bcancel = findViewById(R.id.Bcancel);
        Bcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAlert();
            }
        });

        Bconfirm = findViewById(R.id.Bconfirm);
        Bconfirm.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Notification notify = new Notification.Builder(add_item.this)
                        .setTicker("Alarm")
                        .setSmallIcon(R.drawable.alarm)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle(editTitle.getText().toString())
                        .setContentText(editContent.getText().toString())
                        .build();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAlert();
    }

    void finishAlert(){
        AlertDialog.Builder askfinish = new AlertDialog.Builder(add_item.this);
        askfinish.setTitle("종료");
        askfinish.setMessage("변경 사항이 저장되지 않습니다. 그래도 종료하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    public class Alarm {
        private Context context;

        public Alarm(Context context){
            this.context = context;
        }

        public void Alarm() throws ParseException {
            String[] data = new String[2];
            data[0] = editTitle.getText().toString();
            data[1] = editContent.getText().toString();

            AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(add_item.this, BroadcastD.class);
            intent.putExtra("data", data);

            PendingIntent sender = PendingIntent.getBroadcast(add_item.this, 0, intent, 0);
            Calendar calendar = Calendar.getInstance();

            calendar.setTime(new Date());
            calendar.set(Calendar.HOUR_OF_DAY, endtime[0]);
            calendar.set(Calendar.MINUTE, endtime[1]);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            String lasttime = timeEnd.getText().toString();
            String nowtime = String.format("%02d : %02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH : mm", Locale.getDefault());
            Date datelast = dateFormat.parse(lasttime);
            Date datenow = dateFormat.parse(nowtime);
            if (datenow.after(datelast)) {
                calendar.add(Calendar.DATE, 1);
            }

            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }
}
