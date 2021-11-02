package com.khnsoft.hwalarm;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class add_item extends AppCompatActivity implements View.OnClickListener {
    EditText editTitle;
    EditText editContent;
    TextView Bconfirm;
    TextView Bcancel;
    TextView dateEnd;
    TextView timeEnd;
    int state;
    int year;
    int month;
    int day;
    int hour;
    int minute;
    int enabled;

    TextView hour1;
    TextView hour2;
    TextView hour3;
    TextView hour6;
    TextView hour9;
    TextView hour12;
    TextView hour24;
    TextView hour48;

    int shour1;
    int shour2;
    int shour3;
    int shour6;
    int shour9;
    int shour12;
    int shour24;
    int shour48;

    SQLiteDatabase db;
    String fore;
    int number;

    AlarmManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = getIntent();
        number = intent.getIntExtra("number", 1);
        db = openOrCreateDatabase("Alarm", MODE_PRIVATE, null);

        fore = "INSERT INTO Alarm " +
                "(number, year, month, day, hour, minute, enabled, hour1, hour2, hour3, hour6, " +
                "hour9, hour12, hour24, hour48, title, content, endtime) " +
                "VALUES ";

        state = 0;
        enabled = 1;
        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);

        hour1 = findViewById(R.id.hour1);
        hour2 = findViewById(R.id.hour2);
        hour3 = findViewById(R.id.hour3);
        hour6 = findViewById(R.id.hour6);
        hour9 = findViewById(R.id.hour9);
        hour12 = findViewById(R.id.hour12);
        hour24 = findViewById(R.id.hour24);
        hour48 = findViewById(R.id.hour48);

        hour1.setOnClickListener(this);
        hour2.setOnClickListener(this);
        hour3.setOnClickListener(this);
        hour6.setOnClickListener(this);
        hour9.setOnClickListener(this);
        hour12.setOnClickListener(this);
        hour24.setOnClickListener(this);
        hour48.setOnClickListener(this);

        shour1 = 0;
        shour2 = 0;
        shour3 = 0;
        shour6 = 0;
        shour9 = 0;
        shour12 = 0;
        shour24 = 0;
        shour48 = 0;

        Calendar cal = Calendar.getInstance();
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DATE);
        year = cal.get(Calendar.YEAR);

        dateEnd = findViewById(R.id.date);
        dateEnd.setText(String.format("%02d / %02d", month + 1, day));
        dateEnd.setOnClickListener(this);

        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);

        timeEnd = findViewById(R.id.time);
        timeEnd.setText(String.format("%02d : %02d", hour, minute));
        timeEnd.setOnClickListener(this);

        Bcancel = findViewById(R.id.Bcancel);
        Bcancel.setOnClickListener(this);

        Bconfirm = findViewById(R.id.Bconfirm);
        Bconfirm.setOnClickListener(this);
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

    void datedialog(){
        state = 1;
        DatePickerDialog dialog = new DatePickerDialog(add_item.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year2, int month2, int dayOfMonth) {
                year = year2;
                month = month2;
                day = dayOfMonth;
            }
        }, year, month, day);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                state = 0;
                Calendar instcal = Calendar.getInstance();
                instcal.set(year, month, day, hour, minute, 0);
                dateEnd.setText(String.format("%02d / %02d", month + 1, day));
                timeEnd.setText(String.format("%02d : %02d", hour, minute));
            }
        });
        dialog.show();
    }

    void timedialog(){
        state = 1;
        TimePickerDialog dialog = new TimePickerDialog(add_item.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute2) {
                hour = hourOfDay;
                minute = minute2;
            }
        }, hour, minute, false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                state = 0;
                Calendar instcal = Calendar.getInstance();
                instcal.set(instcal.get(Calendar.YEAR), month, day, hour, minute, 0);
                dateEnd.setText(String.format("%02d / %02d", month + 1, day));
                timeEnd.setText(String.format("%02d : %02d", hour, minute));
            }
        });
        dialog.show();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void setAlarm(){
        Calendar endcal = Calendar.getInstance();
        endcal.set(year, month, day, hour, minute);
        endcal.set(Calendar.SECOND, 0);
        endcal.set(Calendar.MILLISECOND, 0);

        long diff = endcal.getTimeInMillis() - System.currentTimeMillis();
        long days = diff / 86400000;
        long hours = diff % 86400000 / 3600000;
        long minutes = diff % 3600000 / 60000;

        String title;
        if (editTitle.getText().toString().isEmpty()) {
            title = "No Title";
        } else {
            title = editTitle.getText().toString();
        }
        String content = String.format(Locale.KOREA, "%02d/%02d %02d:%02d까지", month + 1, day, hour, minute);

        Intent startintent = new Intent(add_item.this, AlarmReceive.class);
        startintent.putExtra("title", title);
        startintent.putExtra("content", content);
        startintent.putExtra("number", number);
        startintent.setAction("AlarmStart");
        PendingIntent startsender = PendingIntent.getBroadcast(add_item.this, 10000 + number, startintent, PendingIntent.FLAG_UPDATE_CURRENT); //시작 10000
        am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), startsender);

        Intent endintent = new Intent(add_item.this, AlarmReceive.class);
        endintent.putExtra("title", "마감: " + title);
        endintent.putExtra("content", "과제가 마감되었습니다.");
        endintent.putExtra("number", number);
        endintent.setAction("AlarmEnd");
        PendingIntent endsender = PendingIntent.getBroadcast(add_item.this, 11000 + number, endintent, PendingIntent.FLAG_UPDATE_CURRENT); //끝 11000
        am.setExact(AlarmManager.RTC_WAKEUP, endcal.getTimeInMillis(), endsender);

        if (days == 0) {
            if (hours == 0) {
                if(minutes == 0) {
                    Toast.makeText(this, "과제 마감까지 1분 미만 남았습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "과제 마감까지 " + minutes + "분 " + " 남았습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "과제 마감까지 " + hours + "시간 " + minutes + "분 " + " 남았습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "과제 마감까지 " + days + "일 " + hours + "시간 " + minutes + "분 남았습니다.", Toast.LENGTH_SHORT).show();
        }

        am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), startsender);
        am.setExact(AlarmManager.RTC_WAKEUP, endcal.getTimeInMillis(), endsender);

        sethourtimer(year, month, day, hour, minute, 1, shour1, 13000, title, number);
        sethourtimer(year, month, day, hour, minute, 2, shour2, 14000, title, number);
        sethourtimer(year, month, day, hour, minute, 3, shour3, 15000, title, number);
        sethourtimer(year, month, day, hour, minute, 6, shour6, 16000, title, number);
        sethourtimer(year, month, day, hour, minute, 9, shour9, 17000, title, number);
        sethourtimer(year, month, day, hour, minute, 12, shour12, 18000, title, number);
        sethourtimer(year, month, day, hour, minute, 24, shour24, 19000, title, number);
        sethourtimer(year, month, day, hour, minute, 48, shour48, 20000, title, number);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date:
                if (state == 0) {
                    datedialog();
                }
                break;

            case R.id.time:
                if (state == 0) {
                    timedialog();
                }
                break;

            case R.id.Bcancel:
                finishAlert();
                break;

            case R.id.Bconfirm:
                Calendar instcal = Calendar.getInstance();
                instcal.set(year, month, day, hour, minute, 0);
                instcal.set(Calendar.MILLISECOND, 0);
                if (instcal.getTimeInMillis() < System.currentTimeMillis()) {
                    enabled = 0;
                }

                String title;
                if (editTitle.getText().toString().isEmpty()) {
                    title = "\"No Title\"";
                } else {
                    title = "\"" + editTitle.getText().toString() + "\"";
                }
                String content = "\"" + editContent.getText().toString() + "\"";

                db.execSQL(fore + String.format(Locale.KOREA, "(%d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %d, %s, %s, \"%d\")",
                        number, year, month, day, hour, minute, enabled, shour1, shour2, shour3, shour6, shour9, shour12, shour24, shour48, title, content, instcal.getTimeInMillis()));

                if (enabled == 1) {
                    setAlarm();
                }
                finish();
                break;

            case R.id.hour1:
                if (shour1 == 0) {
                    v.setBackgroundResource(R.drawable.timechosen);
                    shour1 = 1;
                } else {
                    v.setBackgroundResource(R.drawable.timenotchosen);
                    shour1 = 0;
                }
                break;

            case R.id.hour2:
                if (shour2 == 0) {
                    v.setBackgroundResource(R.drawable.timechosen);
                    shour2 = 1;
                } else {
                    v.setBackgroundResource(R.drawable.timenotchosen);
                    shour2 = 0;
                }
                break;

            case R.id.hour3:
                if (shour3 == 0) {
                    v.setBackgroundResource(R.drawable.timechosen);
                    shour3 = 1;
                } else {
                    v.setBackgroundResource(R.drawable.timenotchosen);
                    shour3 = 0;
                }
                break;

            case R.id.hour6:
                if (shour6 == 0) {
                    v.setBackgroundResource(R.drawable.timechosen);
                    shour6 = 1;
                } else {
                    v.setBackgroundResource(R.drawable.timenotchosen);
                    shour6 = 0;
                }
                break;

            case R.id.hour9:
                if (shour9 == 0) {
                    v.setBackgroundResource(R.drawable.timechosen);
                    shour9 = 1;
                } else {
                    v.setBackgroundResource(R.drawable.timenotchosen);
                    shour9 = 0;
                }
                break;

            case R.id.hour12:
                if (shour12 == 0) {
                    v.setBackgroundResource(R.drawable.timechosen);
                    shour12 = 1;
                } else {
                    v.setBackgroundResource(R.drawable.timenotchosen);
                    shour12 = 0;
                }
                break;

            case R.id.hour24:
                if (shour24 == 0) {
                    v.setBackgroundResource(R.drawable.timechosen);
                    shour24 = 1;
                } else {
                    v.setBackgroundResource(R.drawable.timenotchosen);
                    shour24 = 0;
                }
                break;

            case R.id.hour48:
                if (shour48 == 0) {
                    v.setBackgroundResource(R.drawable.timechosen);
                    shour48 = 1;
                } else {
                    v.setBackgroundResource(R.drawable.timenotchosen);
                    shour48 = 0;
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void sethourtimer(int year, int month, int day, int hour, int minute, int amount, int tf, int codeset, String title, int number) {
        if (tf == 1) {
            Calendar calhour = Calendar.getInstance();
            calhour.set(year, month, day, hour, minute);
            calhour.set(Calendar.SECOND, 0);
            calhour.set(Calendar.MILLISECOND, 0);
            calhour.add(Calendar.HOUR_OF_DAY, -amount);

            if (calhour.getTimeInMillis() > System.currentTimeMillis()) {
                Intent hourIntent = new Intent(add_item.this, AlarmReceive.class);
                hourIntent.putExtra("title", title);
                hourIntent.putExtra("content", "과제 마감까지 " + amount + "시간 남았습니다.");
                hourIntent.putExtra("number", number);
                hourIntent.putExtra("amount", amount);
                hourIntent.putExtra("codeset", codeset);
                hourIntent.setAction("AlarmHour");

                PendingIntent hoursender = PendingIntent.getBroadcast(add_item.this, codeset + number, hourIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.setExact(AlarmManager.RTC_WAKEUP, calhour.getTimeInMillis(), hoursender);
            }
        }
    }
}