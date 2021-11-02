package com.khnsoft.hwalarm;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton addItem;
    SQLiteDatabase db;
    String SQL;
    LinearLayout list;
    FrameLayout main;
    int number;
    TextView noone;
    AlarmManager am;
    AdView ad;
    long backtime;
    Toast toast;

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main = findViewById(R.id.main);

        MobileAds.initialize(this, getString(R.string.adview_app_id));

        ad = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad.loadAd(adRequest);

        ad.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                Log.d("AdMob", "광고 로드 성공");
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 9.0f);
                main.setLayoutParams(param);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.d("AdMob", "광고 로드 실패: " + i );
                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 10.0f);
                main.setLayoutParams(param);
            }
        });

        list = findViewById(R.id.list);
        number = 0;
        noone = findViewById(R.id.noone);
        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        db = openOrCreateDatabase("Alarm", MODE_PRIVATE, null);
        SQL = "SELECT number, year, month, day, hour, minute, enabled, hour1, hour2, hour3, hour6, " +
                "hour9, hour12, hour24, hour48, title, content " +
                "FROM Alarm";

        //알림추가 창 생성
        addItem = findViewById(R.id.addItem);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, add_item.class);
                intent.putExtra("number", number + 1);
                startActivity(intent);
            }
        });

        if(!isNotiPermissionAllowed()) {
            openSetting();
        }

        backtime = System.currentTimeMillis();

        Intent cService = new Intent(this, CountService.class);
        startService(cService);

        Intent startintent = new Intent(MainActivity.this, AlarmReceive.class);
        startintent.setAction(Intent.ACTION_TIME_TICK);
        PendingIntent startsender = PendingIntent.getBroadcast(MainActivity.this, 0, startintent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), startsender);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();

        if(!isNotiPermissionAllowed()) {
            openSetting();
        }

        noone.setVisibility(View.INVISIBLE);
        try {
            int recordCount = -1;
            int idCount = 0;

            list.removeAllViews();
            Cursor outCursor = db.rawQuery(SQL, null);
            if (outCursor != null) {
                recordCount = outCursor.getCount();
                if (recordCount == 0) {
                    noone.setVisibility(View.VISIBLE);
                }
                for (int i = 1; i <= recordCount; i++) {
                    outCursor.moveToNext();
                    number = outCursor.getInt(0);
                    int year = outCursor.getInt(1);
                    int month = outCursor.getInt(2);
                    int day = outCursor.getInt(3);
                    int hour = outCursor.getInt(4);
                    int minute = outCursor.getInt(5);
                    int enabled = outCursor.getInt(6);
                    int hour1 = outCursor.getInt(7);
                    int hour2 = outCursor.getInt(8);
                    int hour3 = outCursor.getInt(9);
                    int hour6 = outCursor.getInt(10);
                    int hour9 = outCursor.getInt(11);
                    int hour12 = outCursor.getInt(12);
                    int hour24 = outCursor.getInt(13);
                    int hour48 = outCursor.getInt(14);
                    String title = outCursor.getString(15);
                    String content = outCursor.getString(16);

                    AlarmInfo inner = new AlarmInfo(getApplicationContext());
                    list.addView(inner);

                    Switch Ttitle = findViewById(R.id.title);
                    TextView Tcontent = findViewById(R.id.content);
                    TextView Tendtime = findViewById(R.id.endtime);
                    TextView Thour1 = findViewById(R.id.hour1);
                    TextView Thour2 = findViewById(R.id.hour2);
                    TextView Thour3 = findViewById(R.id.hour3);
                    TextView Thour6 = findViewById(R.id.hour6);
                    TextView Thour9 = findViewById(R.id.hour9);
                    TextView Thour12 = findViewById(R.id.hour12);
                    TextView Thour24 = findViewById(R.id.hour24);
                    TextView Thour48 = findViewById(R.id.hour48);

                    Tendtime.setText(String.format(Locale.KOREA, "%02d / %02d  %02d : %02d 까지", month + 1, day, hour, minute));

                    Ttitle.setText(title);
                    Tcontent.setText(content);

                    Calendar endcal = Calendar.getInstance();
                    endcal.set(year, month, day, hour, minute);
                    endcal.set(Calendar.SECOND, 0);
                    endcal.set(Calendar.MILLISECOND, 0);

                    if (endcal.getTimeInMillis() < System.currentTimeMillis()) {
                        enabled = 0;
                        db.execSQL("UPDATE Alarm SET enabled=" + enabled + " WHERE number=" + number);
                    }

                    switch (enabled) {
                        case 0:
                            Ttitle.setChecked(false);
                            break;
                        case 1:
                            Ttitle.setChecked(true);
                            break;
                    }

                    if (hour1 == 1) {
                        Thour1.setBackgroundResource(R.drawable.timechosen);
                    } if (hour2 == 1) {
                        Thour2.setBackgroundResource(R.drawable.timechosen);
                    } if (hour3 == 1) {
                        Thour3.setBackgroundResource(R.drawable.timechosen);
                    } if (hour6 == 1) {
                        Thour6.setBackgroundResource(R.drawable.timechosen);
                    } if (hour9 == 1) {
                        Thour9.setBackgroundResource(R.drawable.timechosen);
                    } if (hour12 == 1) {
                        Thour12.setBackgroundResource(R.drawable.timechosen);
                    } if (hour24 == 1) {
                        Thour24.setBackgroundResource(R.drawable.timechosen);
                    } if (hour48 == 1) {
                        Thour48.setBackgroundResource(R.drawable.timechosen);
                    }

                    inner.setTag(idCount);
                    Ttitle.setTag(idCount);

                    setListener(inner);
                    setCheckedListener(Ttitle);

                    int innerid = getResources().getIdentifier("inner_" + idCount, "id", getApplicationContext().getPackageName());
                    int titleid = getResources().getIdentifier("title_" + idCount, "id", getApplicationContext().getPackageName());
                    int contentid = getResources().getIdentifier("content_" + idCount, "id", getApplicationContext().getPackageName());
                    int endtimeid = getResources().getIdentifier("endTime_" + idCount, "id", getApplicationContext().getPackageName());
                    int hour1id = getResources().getIdentifier("hour1_" + idCount, "id", getApplicationContext().getPackageName());
                    int hour2id = getResources().getIdentifier("hour2_" + idCount, "id", getApplicationContext().getPackageName());
                    int hour3id = getResources().getIdentifier("hour3_" + idCount, "id", getApplicationContext().getPackageName());
                    int hour6id = getResources().getIdentifier("hour6_" + idCount, "id", getApplicationContext().getPackageName());
                    int hour9id = getResources().getIdentifier("hour9_" + idCount, "id", getApplicationContext().getPackageName());
                    int hour12id = getResources().getIdentifier("hour12_" + idCount, "id", getApplicationContext().getPackageName());
                    int hour24id = getResources().getIdentifier("hour24_" + idCount, "id", getApplicationContext().getPackageName());
                    int hour48id = getResources().getIdentifier("hour48_" + idCount, "id", getApplicationContext().getPackageName());

                    inner.setId(innerid);
                    Ttitle.setId(titleid);
                    Tcontent.setId(contentid);
                    Tendtime.setId(endtimeid);
                    Thour1.setId(hour1id);
                    Thour2.setId(hour2id);
                    Thour3.setId(hour3id);
                    Thour6.setId(hour6id);
                    Thour9.setId(hour9id);
                    Thour12.setId(hour12id);
                    Thour24.setId(hour24id);
                    Thour48.setId(hour48id);

                    idCount++;
                }
            }
        } catch (Exception e) {
            db.execSQL("CREATE TABLE Alarm (" +
                    "_id INTEGER PRIMARY KEY autoincrement," +
                    "number INTEGER," +
                    "year INTEGER," +
                    "month INTEGER," +
                    "day INTEGER," +
                    "hour INTEGER," +
                    "minute INTEGER," +
                    "enabled INTEGER," +
                    "hour1 INTEGER," +
                    "hour2 INTEGER," +
                    "hour3 INTEGER," +
                    "hour6 INTEGER," +
                    "hour9 INTEGER," +
                    "hour12 INTEGER," +
                    "hour24 INTEGER," +
                    "hour48 INTEGER," +
                    "title TEXT," +
                    "content TEXT," +
                    "endtime TEXT);");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationChannel channel1 = new NotificationChannel("alarmChannel01", "Homework Alarm Vibrate", NotificationManager.IMPORTANCE_DEFAULT);
                channel1.setDescription("Notification channel used for HWAlarm");
                channel1.enableVibration(true);
                channel1.setVibrationPattern(new long[]{0, 300});
                channel1.enableLights(true);
                nm.createNotificationChannel(channel1);

                NotificationChannel channel2 = new NotificationChannel("alarmChannel02", "Homework Alarm nonVibrate", NotificationManager.IMPORTANCE_LOW);
                channel2.setDescription("Notification channel used for HWAlarm nonVibrate.");
                channel2.enableVibration(true);
                channel2.setVibrationPattern(new long[]{0});
                channel2.enableLights(false);
                nm.createNotificationChannel(channel2);
            }
        }
    }

    void setListener(final LinearLayout ll) {
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, edit_item.class);
                intent.putExtra("line", ll.getTag().toString());
                startActivity(intent);
            }
        });
    }

    void setCheckedListener(final Switch s) {
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Cursor outCursor = db.rawQuery(SQL, null);
                outCursor.moveToPosition(Integer.parseInt(s.getTag().toString()));
                int number = outCursor.getInt(0);
                int year = outCursor.getInt(1);
                int month = outCursor.getInt(2);
                int day = outCursor.getInt(3);
                int hour = outCursor.getInt(4);
                int minute = outCursor.getInt(5);
                int enabled = outCursor.getInt(6);
                int shour1 = outCursor.getInt(7);
                int shour2 = outCursor.getInt(8);
                int shour3 = outCursor.getInt(9);
                int shour6 = outCursor.getInt(10);
                int shour9 = outCursor.getInt(11);
                int shour12 = outCursor.getInt(12);
                int shour24 = outCursor.getInt(13);
                int shour48 = outCursor.getInt(14);
                String title = outCursor.getString(15);

                Calendar endcal = Calendar.getInstance();
                endcal.set(year, month, day, hour, minute);
                endcal.set(Calendar.SECOND, 0);
                endcal.set(Calendar.MILLISECOND, 0);

                long diff = endcal.getTimeInMillis() - System.currentTimeMillis();
                long days = diff / 86400000;
                long hours = diff % 86400000 / 3600000;
                long minutes = diff % 3600000 / 60000;

                String content = String.format(Locale.KOREA, "%02d/%02d %02d:%02d까지", month + 1, day, hour, minute);

                Intent startintent = new Intent(MainActivity.this, AlarmReceive.class);
                startintent.putExtra("title", title);
                startintent.putExtra("content", content);
                startintent.putExtra("number", number);
                startintent.setAction("AlarmStart");

                PendingIntent startsender = PendingIntent.getBroadcast(MainActivity.this, 10000 + number, startintent, PendingIntent.FLAG_UPDATE_CURRENT); //시작 10000

                Intent endintent = new Intent(MainActivity.this, AlarmReceive.class);
                endintent.putExtra("title", "마감: " + title);
                endintent.putExtra("content", "과제가 마감되었습니다.");
                endintent.putExtra("number", number);
                endintent.setAction("AlarmEnd");

                PendingIntent endsender = PendingIntent.getBroadcast(MainActivity.this, 11000 + number, endintent, PendingIntent.FLAG_UPDATE_CURRENT); //끝 11000

                if (isChecked) {
                    if (endcal.getTimeInMillis() < System.currentTimeMillis()) {
                        s.setChecked(false);
                        Toast.makeText(MainActivity.this, "마감 시간이 이미 지났습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (days == 0) {
                        if (hours == 0) {
                            if(minutes == 0) {
                                Toast.makeText(MainActivity.this, "과제 마감까지 1분 미만 남았습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "과제 마감까지 " + minutes + "분 " + " 남았습니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "과제 마감까지 " + hours + "시간 " + minutes + "분 " + " 남았습니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "과제 마감까지 " + days + "일 " + hours + "시간 " + minutes + "분 남았습니다.", Toast.LENGTH_SHORT).show();
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

                    enabled = 1;
                    db.execSQL("UPDATE Alarm SET enabled=" + enabled + " WHERE number=" + number);
                } else {
                    Intent cancelIntent = new Intent(MainActivity.this, AlarmReceive.class);
                    cancelIntent.putExtra("number", number);
                    cancelIntent.setAction("AlarmCancel");
                    PendingIntent cancelsender = PendingIntent.getBroadcast(MainActivity.this, number, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), cancelsender);

                    enabled = 0;
                    db.execSQL("UPDATE Alarm SET enabled=" + enabled + " WHERE number=" + number);
                }
            }
        });
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
                Intent hourIntent = new Intent(MainActivity.this, AlarmReceive.class);
                hourIntent.putExtra("title", title);
                hourIntent.putExtra("content", "과제 마감까지 " + amount + "시간 남았습니다.");
                hourIntent.putExtra("number", number);
                hourIntent.putExtra("amount", amount);
                hourIntent.putExtra("codeset", codeset);
                hourIntent.setAction("AlarmHour");

                PendingIntent hoursender = PendingIntent.getBroadcast(MainActivity.this, codeset + number, hourIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.setExact(AlarmManager.RTC_WAKEUP, calhour.getTimeInMillis(), hoursender);
            }
        }
    }

    private boolean isNotiPermissionAllowed() {
        Set<String> notiListenerSet = NotificationManagerCompat.getEnabledListenerPackages(this);
        String myPackageName = getPackageName();

        for(String packageName : notiListenerSet) {
            if(packageName == null) {
                continue;
            }
            if(packageName.equals(myPackageName)) {
                return true;
            }
        }

        return false;
    }

    void openSetting(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("알림 접근 허용이 필요합니다.")
                .setMessage("어플이 실행되기 위해 알림 접근 권한이 필요합니다. 확인을 누르고 \"과제 알리미\"를 활성화 시켜주십시오")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!isNotiPermissionAllowed()) {
                            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton("닫기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backtime + 1500) {
            backtime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로가기를 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            finish();
            toast.cancel();
        }
    }
}