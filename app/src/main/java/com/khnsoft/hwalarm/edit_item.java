package com.khnsoft.hwalarm;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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

public class edit_item extends AppCompatActivity implements View.OnClickListener {
    EditText editTitle;
    EditText editContent;
    TextView Bconfirm;
    TextView Bcancel;
    TextView Bdelete;
    TextView dateEnd;
    int[] enddate;
    TextView timeEnd;
    int[] endtime;
    int state;
    int year;

    TextView hour1;
    TextView hour2;
    TextView hour3;
    TextView hour6;
    TextView hour9;
    TextView hour12;
    TextView hour24;
    TextView hour48;

    SQLiteDatabase db;
    String SQL;
    int number;
    int month;
    int day;
    int hour;
    int minute;
    int enabled;
    int shour1;
    int shour2;
    int shour3;
    int shour6;
    int shour9;
    int shour12;
    int shour24;
    int shour48;
    String title;
    String content;

    AlarmManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        db = openOrCreateDatabase("Alarm", MODE_PRIVATE, null);
        Intent intent = getIntent();
        int line = Integer.parseInt(intent.getStringExtra("line"));

        SQL = "select number, year, month, day, hour, minute, enabled, hour1, hour2, hour3, hour6, " +
                "hour9, hour12, hour24, hour48, title, content " +
                "from Alarm";

        Cursor outCursor = db.rawQuery(SQL, null);
        outCursor.moveToPosition(line);
        number = outCursor.getInt(0);
        year = outCursor.getInt(1);
        month = outCursor.getInt(2);
        day = outCursor.getInt(3);
        hour = outCursor.getInt(4);
        minute = outCursor.getInt(5);
        enabled = outCursor.getInt(6);
        shour1 = outCursor.getInt(7);
        shour2 = outCursor.getInt(8);
        shour3 = outCursor.getInt(9);
        shour6 = outCursor.getInt(10);
        shour9 = outCursor.getInt(11);
        shour12 = outCursor.getInt(12);
        shour24 = outCursor.getInt(13);
        shour48 = outCursor.getInt(14);
        title = outCursor.getString(15);
        content = outCursor.getString(16);

        hour1 = findViewById(R.id.hour1);
        hour2 = findViewById(R.id.hour2);
        hour3 = findViewById(R.id.hour3);
        hour6 = findViewById(R.id.hour6);
        hour9 = findViewById(R.id.hour9);
        hour12 = findViewById(R.id.hour12);
        hour24 = findViewById(R.id.hour24);
        hour48 = findViewById(R.id.hour48);

        if (shour1 == 1) {
            hour1.setBackgroundResource(R.drawable.timechosen);
        } if (shour2 == 1) {
            hour2.setBackgroundResource(R.drawable.timechosen);
        } if (shour3 == 1) {
            hour3.setBackgroundResource(R.drawable.timechosen);
        } if (shour6 == 1) {
            hour6.setBackgroundResource(R.drawable.timechosen);
        } if (shour9 == 1) {
            hour9.setBackgroundResource(R.drawable.timechosen);
        } if (shour12 == 1) {
            hour12.setBackgroundResource(R.drawable.timechosen);
        } if (shour24 == 1) {
            hour24.setBackgroundResource(R.drawable.timechosen);
        } if (shour48 == 1) {
            hour48.setBackgroundResource(R.drawable.timechosen);
        }

        hour1.setOnClickListener(this);
        hour2.setOnClickListener(this);
        hour3.setOnClickListener(this);
        hour6.setOnClickListener(this);
        hour9.setOnClickListener(this);
        hour12.setOnClickListener(this);
        hour24.setOnClickListener(this);
        hour48.setOnClickListener(this);

        state = 0;
        editTitle = findViewById(R.id.editTitle);
        editTitle.setText(title);
        editContent = findViewById(R.id.editContent);
        editContent.setText(content);

        dateEnd = findViewById(R.id.date);
        dateEnd.setText(String.format("%02d / %02d", month + 1, day));
        dateEnd.setOnClickListener(this);

        timeEnd = findViewById(R.id.time);
        timeEnd.setText(String.format("%02d : %02d", hour, minute));
        timeEnd.setOnClickListener(this);

        Bcancel = findViewById(R.id.Bcancel);
        Bcancel.setOnClickListener(this);

        Bconfirm = findViewById(R.id.Bconfirm);
        Bconfirm.setOnClickListener(this);

        Bdelete = findViewById(R.id.Bdelete);
        Bdelete.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        finishAlert();
    }

    void finishAlert(){
        AlertDialog.Builder askfinish = new AlertDialog.Builder(edit_item.this);
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

    void deleteAlert(final int i){
        AlertDialog.Builder askfinish = new AlertDialog.Builder(edit_item.this);
        askfinish.setTitle("삭제");
        askfinish.setMessage("이 알림을 삭제하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.execSQL("DELETE FROM Alarm WHERE number=" + i);
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
        DatePickerDialog dialog = new DatePickerDialog(edit_item.this, new DatePickerDialog.OnDateSetListener() {
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
        TimePickerDialog dialog = new TimePickerDialog(edit_item.this, new TimePickerDialog.OnTimeSetListener() {
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
        Intent cancelIntent = new Intent(edit_item.this, AlarmReceive.class);
        cancelIntent.putExtra("number", number);
        cancelIntent.setAction("AlarmCancel");
        PendingIntent cancelsender = PendingIntent.getBroadcast(edit_item.this, number, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), cancelsender);

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
        String content = String.format(Locale.KOREA, "%02d/%02d %02d:%02d까지 ", month + 1, day, hour, minute);

        Intent startintent = new Intent(edit_item.this, AlarmReceive.class);
        startintent.putExtra("title", title);
        startintent.putExtra("content", content);
        startintent.putExtra("number", number);
        startintent.setAction("AlarmStart");

        PendingIntent startsender = PendingIntent.getBroadcast(edit_item.this, 10000 + number, startintent, PendingIntent.FLAG_UPDATE_CURRENT); //시작 10000

        Intent endintent = new Intent(edit_item.this, AlarmReceive.class);
        endintent.putExtra("title", "마감: " + title);
        endintent.putExtra("content", "과제가 마감되었습니다.");
        endintent.putExtra("number", number);
        endintent.setAction("AlarmEnd");

        PendingIntent endsender = PendingIntent.getBroadcast(edit_item.this, 11000 + number, endintent, PendingIntent.FLAG_UPDATE_CURRENT); //끝 11000

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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
                if (instcal.getTimeInMillis() < System.currentTimeMillis()) {
                    enabled = 0;
                }

                Calendar endcal = Calendar.getInstance();
                endcal.set(year, month, day, hour, minute);
                endcal.set(Calendar.SECOND, 0);
                endcal.set(Calendar.MILLISECOND, 0);

                String title;
                if (editTitle.getText().toString().isEmpty()) {
                    title = "\"No Title\"";
                } else {
                    title = "\"" + editTitle.getText().toString() + "\"";
                }
                String content = "\"" + editContent.getText().toString() + "\"";

                db.execSQL(String.format(Locale.KOREA, "UPDATE Alarm SET year=%d, month=%d, day=%d, hour=%d, minute=%d, enabled=%d, " +
                                "hour1=%d, hour2=%d, hour3=%d, hour6=%d, hour9=%d, hour12=%d, hour24=%d, hour48=%d, title=%s, content=%s, endtime=\"%d\" WHERE number=%d",
                        year, month, day, hour, minute, enabled, shour1, shour2, shour3, shour6, shour9, shour12, shour24, shour48, title, content, endcal.getTimeInMillis(), number));

                if (enabled == 1) {
                    setAlarm();
                }

                finish();
                break;

            case R.id.Bdelete:
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                Intent endintent = new Intent(edit_item.this, AlarmReceive.class);
                endintent.setAction("AlarmEnd");
                PendingIntent endsender = PendingIntent.getBroadcast(edit_item.this, 11000 + number, endintent, PendingIntent.FLAG_UPDATE_CURRENT); //끝 11000

                am.cancel(endsender);
                nm.cancel(number);
                Intent hour1Intent = new Intent(edit_item.this, AlarmReceive.class);
                hour1Intent.setAction("AlarmHour1");
                PendingIntent hour1sender = PendingIntent.getBroadcast(edit_item.this, 13000 + number, hour1Intent, PendingIntent.FLAG_UPDATE_CURRENT); //hour1 13000
                am.cancel(hour1sender);

                Intent hour2Intent = new Intent(edit_item.this, AlarmReceive.class);
                hour2Intent.setAction("AlarmHour2");
                PendingIntent hour2sender = PendingIntent.getBroadcast(edit_item.this, 14000 + number, hour2Intent, PendingIntent.FLAG_UPDATE_CURRENT); //hour2 14000
                am.cancel(hour2sender);

                Intent hour3Intent = new Intent(edit_item.this, AlarmReceive.class);
                hour3Intent.setAction("AlarmHour3");
                PendingIntent hour3sender = PendingIntent.getBroadcast(edit_item.this, 15000 + number, hour3Intent, PendingIntent.FLAG_UPDATE_CURRENT); //hour3 15000
                am.cancel(hour3sender);

                Intent hour6Intent = new Intent(edit_item.this, AlarmReceive.class);
                hour6Intent.setAction("AlarmHour6");
                PendingIntent hour6sender = PendingIntent.getBroadcast(edit_item.this, 16000 + number, hour6Intent, PendingIntent.FLAG_UPDATE_CURRENT); //hour6 16000
                am.cancel(hour6sender);

                Intent hour9Intent = new Intent(edit_item.this, AlarmReceive.class);
                hour9Intent.setAction("AlarmHour9");
                PendingIntent hour9sender = PendingIntent.getBroadcast(edit_item.this, 17000 + number, hour9Intent, PendingIntent.FLAG_UPDATE_CURRENT); //hour9 17000
                am.cancel(hour9sender);

                Intent hour12Intent = new Intent(edit_item.this, AlarmReceive.class);
                hour12Intent.setAction("AlarmHour12");
                PendingIntent hour12sender = PendingIntent.getBroadcast(edit_item.this, 18000 + number, hour12Intent, PendingIntent.FLAG_UPDATE_CURRENT); //hour12 18000
                am.cancel(hour12sender);

                Intent hour24Intent = new Intent(edit_item.this, AlarmReceive.class);
                hour24Intent.setAction("AlarmHour24");
                PendingIntent hour24sender = PendingIntent.getBroadcast(edit_item.this, 19000 + number, hour24Intent, PendingIntent.FLAG_UPDATE_CURRENT); //hour24 19000
                am.cancel(hour24sender);

                Intent hour48Intent = new Intent(edit_item.this, AlarmReceive.class);
                hour48Intent.setAction("AlarmHour48");
                PendingIntent hour48sender = PendingIntent.getBroadcast(edit_item.this, 20000 + number, hour48Intent, PendingIntent.FLAG_UPDATE_CURRENT); //hour48 20000
                am.cancel(hour48sender);
                deleteAlert(number);

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
                Intent hourIntent = new Intent(edit_item.this, AlarmReceive.class);
                hourIntent.putExtra("title", title);
                hourIntent.putExtra("content", "과제 마감까지 " + amount + "시간 남았습니다.");
                hourIntent.putExtra("number", number);
                hourIntent.putExtra("amount", amount);
                hourIntent.putExtra("codeset", codeset);
                hourIntent.setAction("AlarmHour");

                PendingIntent hoursender = PendingIntent.getBroadcast(edit_item.this, codeset + number, hourIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.setExact(AlarmManager.RTC_WAKEUP, calhour.getTimeInMillis(), hoursender);
            }
        }
    }
}
