package com.khnsoft.hwalarm;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class AlarmReceive extends BroadcastReceiver{
    String key;
    NotificationManager nm;
    SQLiteDatabase db;
    Context context;
    AlarmManager am;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action =intent.getAction();
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        int number = intent.getIntExtra("number", 0);
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        key = "HWAlarm";
        db = context.openOrCreateDatabase("Alarm", Context.MODE_PRIVATE, null);
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Log.d("AlarmReceive", "리시버 호출됨");

        if (action.equals(Intent.ACTION_TIME_TICK)) {
            Log.d("AlarmReceive", "타임틱 확인됨");
            Cursor outCursor = db.rawQuery("SELECT number, enabled, endtime, title, month, day, hour, minute FROM Alarm", null);
            int recordCount = -1;

            if (outCursor != null) {
                recordCount = outCursor.getCount();
                for (int i = 1; i <= recordCount; i++) {
                    outCursor.moveToNext();
                    number = outCursor.getInt(0);
                    int enabled = outCursor.getInt(1);
                    long endtime = Long.parseLong(outCursor.getString(2));
                    title = outCursor.getString(3);
                    int month = outCursor.getInt(4);
                    int day = outCursor.getInt(5);
                    int hour = outCursor.getInt(6);
                    int minute = outCursor.getInt(7);

                    if (enabled == 1) {
                        if (endtime > System.currentTimeMillis()) {
                            long diff = endtime - System.currentTimeMillis();
                            long days = diff / 86400000;
                            long hours = diff % 86400000 / 3600000;
                            long minutes = diff % 3600000 / 60000;

                            if (days == 0) {
                                if (hours == 0) {
                                    if(minutes == 0) {
                                        title = "(1분 미만) " + title;
                                    } else {
                                        title = String.format(Locale.KOREA, "(%d분) ", minutes) + title;
                                    }
                                } else {
                                    title = String.format(Locale.KOREA, "(%d시간 %d분) ", hours, minutes) + title;
                                }
                            } else {
                                title = String.format(Locale.KOREA, "(%d일 %d시간 %d분) ", days, hours, minutes) + title;
                            }

                            content = String.format(Locale.KOREA, "%02d/%02d %02d:%02d까지", month + 1, day, hour, minute);

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarmChannel02")
                                    .setSmallIcon(R.drawable.alarm)
                                    .setWhen(System.currentTimeMillis())
                                    .setContentTitle(title)
                                    .setContentText(content)
                                    .setOngoing(true)
                                    .setGroup(key)
                                    .setAutoCancel(false);

                            Intent resultIntent = new Intent(context, MainActivity.class);
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                            stackBuilder.addParentStack(MainActivity.class);
                            stackBuilder.addNextIntent(resultIntent);
                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                            Intent cancelIntent = new Intent(context, AlarmReceive.class);
                            cancelIntent.putExtra("number", number);
                            cancelIntent.setAction("AlarmCancel");
                            PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(context, 12000 + number, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT); //취소 12000

                            builder.setContentIntent(resultPendingIntent);
                            builder.addAction(R.drawable.alarm, "완료", cancelPendingIntent);

                            nm.notify(number, builder.build());
                            Log.d("AlarmReceive", number + "번 알림 시간 업데이트");
                        } else {
                            Intent cancelIntent = new Intent(context, AlarmReceive.class);
                            cancelIntent.putExtra("number", number);
                            cancelIntent.setAction("AlarmCancel");
                            PendingIntent cancelsender = PendingIntent.getBroadcast(context, number, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), cancelsender);

                            db.execSQL("UPDATE Alarm SET enabled=0 WHERE number=" + number);
                            Log.d("AlarmReceive", number + "번 알림 종료");
                        }
                    }
                }
            }
        }

        //재부팅 됐을 때 서비스 실행
        else if (action.equals(Intent.ACTION_BOOT_COMPLETED)){
            Log.d("AlarmReceive", "재부팅 확인됨");
            Intent cService = new Intent(context, CountService.class);
            context.startService(cService);
        }

        //상단바 알림 시작
        else if (action.equals("AlarmStart")) {
            Cursor cursor = db.rawQuery("SELECT endtime FROM Alarm WHERE number=" + number, null);
            cursor.moveToNext();
            long endtime = Long.parseLong(cursor.getString(0));
            long diff = endtime - System.currentTimeMillis();
            long days = diff / 86400000;
            long hours = diff % 86400000 / 3600000;
            long minutes = diff % 3600000 / 60000;

            if (days == 0) {
                if (hours == 0) {
                    if(minutes == 0) {
                        title = "(1분 미만) " + title;
                    } else {
                        title = String.format(Locale.KOREA, "(%d분) ", minutes) + title;
                    }
                } else {
                    title = String.format(Locale.KOREA, "(%d시간 %d분) ", hours, minutes) + title;
                }
            } else {
                title = String.format(Locale.KOREA, "(%d일 %d시간 %d분) ", days, hours, minutes) + title;
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarmChannel01")
                    .setSmallIcon(R.drawable.alarm)
                    .setTicker(title)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(title)
                    .setContentText(content)
                    .setOngoing(true)
                    .setGroup(key)
                    .setAutoCancel(false);

            Intent resultIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent cancelIntent = new Intent(context, AlarmReceive.class);
            cancelIntent.putExtra("number", number);
            cancelIntent.setAction("AlarmCancel");
            PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(context, 12000 + number, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT); //취소 12000

            builder.setContentIntent(resultPendingIntent);
            builder.addAction(R.drawable.alarm, "완료", cancelPendingIntent);

            nm.notify(number, builder.build());

            Log.d("AlarmReceive", number + "번 신규알림");
        }

        //상단바 알림 종료
        else if (action.equals("AlarmEnd")){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarmChannel01")
                    .setSmallIcon(R.drawable.alarm)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(title)
                    .setContentText(content)
                    .setOngoing(false)
                    .setAutoCancel(true);

            Intent resultIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            nm.notify(number, builder.build());

            db.execSQL("UPDATE Alarm SET enabled=0 WHERE number=" + number);

            Log.d("AlarmReceive", number + "번 알림 종료");
        }

        //알림 취소
        else if (action.equals("AlarmCancel")) {
            Intent endintent = new Intent(context, AlarmReceive.class);
            endintent.setAction("AlarmEnd");
            PendingIntent endsender = PendingIntent.getBroadcast(context, 11000 + number, endintent, PendingIntent.FLAG_UPDATE_CURRENT); //끝 11000
            am.cancel(endsender);
            nm.cancel(number);

            nm.cancel(13000 + number);
            Intent hour1Intent = new Intent(context, AlarmReceive.class);
            hour1Intent.setAction("AlarmHour");
            PendingIntent hour1sender = PendingIntent.getBroadcast(context, 13000 + number, hour1Intent, PendingIntent.FLAG_UPDATE_CURRENT); //hour1 13000
            am.cancel(hour1sender);

            nm.cancel(14000 + number);
            Intent hour2Intent = new Intent(context, AlarmReceive.class);
            hour2Intent.setAction("AlarmHour");
            PendingIntent hour2sender = PendingIntent.getBroadcast(context, 14000 + number, hour2Intent, PendingIntent.FLAG_UPDATE_CURRENT); //hour2 14000
            am.cancel(hour2sender);

            nm.cancel(15000 + number);
            Intent hour3Intent = new Intent(context, AlarmReceive.class);
            hour3Intent.setAction("AlarmHour");
            PendingIntent hour3sender = PendingIntent.getBroadcast(context, 15000 + number, hour3Intent, PendingIntent.FLAG_UPDATE_CURRENT); //hour3 15000
            am.cancel(hour3sender);

            nm.cancel(16000 + number);
            Intent hour6Intent = new Intent(context, AlarmReceive.class);
            hour6Intent.setAction("AlarmHour");
            PendingIntent hour6sender = PendingIntent.getBroadcast(context, 16000 + number, hour6Intent, PendingIntent.FLAG_UPDATE_CURRENT); //hour6 16000
            am.cancel(hour6sender);

            nm.cancel(17000 + number);
            Intent hour9Intent = new Intent(context, AlarmReceive.class);
            hour9Intent.setAction("AlarmHour");
            PendingIntent hour9sender = PendingIntent.getBroadcast(context, 17000 + number, hour9Intent, PendingIntent.FLAG_UPDATE_CURRENT); //hour9 17000
            am.cancel(hour9sender);

            nm.cancel(18000 + number);
            Intent hour12Intent = new Intent(context, AlarmReceive.class);
            hour12Intent.setAction("AlarmHour");
            PendingIntent hour12sender = PendingIntent.getBroadcast(context, 18000 + number, hour12Intent, PendingIntent.FLAG_UPDATE_CURRENT); //hour12 18000
            am.cancel(hour12sender);

            nm.cancel(19000 + number);
            Intent hour24Intent = new Intent(context, AlarmReceive.class);
            hour24Intent.setAction("AlarmHour");
            PendingIntent hour24sender = PendingIntent.getBroadcast(context, 19000 + number, hour24Intent, PendingIntent.FLAG_UPDATE_CURRENT); //hour24 19000
            am.cancel(hour24sender);

            nm.cancel(20000 + number);
            Intent hour48Intent = new Intent(context, AlarmReceive.class);
            hour48Intent.setAction("AlarmHour");
            PendingIntent hour48sender = PendingIntent.getBroadcast(context, 20000 + number, hour48Intent, PendingIntent.FLAG_UPDATE_CURRENT); //hour48 20000
            am.cancel(hour48sender);

            db.execSQL("UPDATE Alarm SET enabled=0 WHERE number=" + number);

            Log.d("AlarmReceive", number + "번 알림 취소");
        }

        //시간별 알림
        else if (action.equals("AlarmHour")) {
            int codeset = intent.getIntExtra("codeset", 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alarmChannel01")
                    .setSmallIcon(R.drawable.alarm)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(title)
                    .setContentText(content)
                    .setOngoing(false)
                    .setAutoCancel(true);

            Intent resultIntent = new Intent(context, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            nm.notify(codeset + number, builder.build());

            Log.d("AlarmReceive", number + "번 시간별 알림");
        }
        setsummary();
    }

    //알림 묶음
    void setsummary(){
        Cursor outCursor = db.rawQuery("SELECT COUNT(CASE WHEN enabled=1 THEN 1 END) FROM Alarm", null);
        outCursor.moveToNext();
        int count = outCursor.getInt(0);

        if (count == 0) {
            nm.cancel(10000);
        } else if (count == 1){
            ;
        } else {
            int recordCount = outCursor.getCount();
            String titles = "";
            outCursor = db.rawQuery("SELECT title FROM Alarm WHERE enabled=1", null);
            for (int i = 1; i <= recordCount; i++) {
                outCursor.moveToNext();
                if (titles.isEmpty()) {
                    titles = outCursor.getString(0);
                } else {
                    titles = titles + ", " + outCursor.getString(0);
                }
            }

            NotificationCompat.Builder sBuilder = new NotificationCompat.Builder(context, "alarmChannel02")
                    .setSmallIcon(R.drawable.alarm)
                    .setContentTitle(count + "개의 과제가 있습니다.")
                    .setContentText(titles)
                    .setGroup(key)
                    .setOngoing(true)
                    .setGroupSummary(true);

            nm.notify(10000, sBuilder.build()); //그룹 10000
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
                Intent hourIntent = new Intent(context, AlarmReceive.class);
                hourIntent.putExtra("title", title);
                hourIntent.putExtra("content", "과제 마감까지 " + amount + "시간 남았습니다.");
                hourIntent.putExtra("number", number);
                hourIntent.putExtra("amount", amount);
                hourIntent.putExtra("codeset", codeset);
                hourIntent.setAction("AlarmHour");

                PendingIntent hoursender = PendingIntent.getBroadcast(context, codeset + number, hourIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.setExact(AlarmManager.RTC_WAKEUP, calhour.getTimeInMillis(), hoursender);
            }
        }
    }
}
