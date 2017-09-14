package com.nepalicoders.servicesdemo.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Sulav on 8/4/17.
 */

public class MyStartedService extends Service {

    private static final String TAG = MyStartedService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate, Thread name " + Thread.currentThread().getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand, Thread name " + Thread.currentThread().getName());

        // Perform Tasks [ Short Duration Task: Don't block the UI ]

        int sleepTime = intent.getIntExtra("sleepTime", 1);

        new MyAsyncTask().execute(sleepTime);

//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY; // services is restarted but intent is lost i.e. it becomes null
//        return START_REDELIVER_INTENT; // services is restarted without loosing intent
//        return START_NOT_STICKY; // services is note restarted and the intent is also lost i.e. it becomes null
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind, Thread name " + Thread.currentThread().getName());
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy, Thread name " + Thread.currentThread().getName());
        super.onDestroy();
    }

    class MyAsyncTask extends AsyncTask<Integer, String, String> {

        private final String TAG = MyAsyncTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "onPreExecute, Thread name " + Thread.currentThread().getName());
        }

        @Override // Perform our Long Running Task
        protected String doInBackground(Integer... params) {
            Log.i(TAG, "doInBackground, Thread name " + Thread.currentThread().getName());

            int sleepTime = params[0];

            int ctr = 1;

            // Dummy Long Operation
            while (ctr <= sleepTime) {
                publishProgress("Counter is now " + ctr);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ctr++;
            }

            return "Counter Stopped at " + ctr + " seconds";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Toast.makeText(MyStartedService.this, values[0], Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Counter Value " + values[0] + " onProgressUpdate, Thread name " + Thread.currentThread().getName());
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);

            stopSelf(); // Destroy the Service form within the Service class itself
            Log.i(TAG, "onPostExecute, Thread name " + Thread.currentThread().getName());

            Intent intent = new Intent("action.service.to.activity");
            intent.putExtra("startServiceResult", str);
            sendBroadcast(intent);
        }
    }
}
