package com.khnsoft.hwalarm;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class CountService extends Service {
    AlarmReceive ar;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("CountService", "서비스 생성");
        ar = new AlarmReceive();
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(ar, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("CountService", "서비스 호출");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("CountService", "서비스 제거");
        unregisterReceiver(ar);
        super.onDestroy();
    }
}
