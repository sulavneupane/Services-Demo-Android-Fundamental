package com.nepalicoders.servicesdemo.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nepalicoders.servicesdemo.R;
import com.nepalicoders.servicesdemo.services.MyMessengerService;

/**
 * Created by Sulav on 8/4/17.
 */

public class MyMessengerActivity extends AppCompatActivity {

    boolean mIsBound = false;
    private TextView txvResult;

    private Messenger mService = null;

    private class IncomingResponseHandler extends Handler {

        @Override
        public void handleMessage(Message msgFromService) {

            switch (msgFromService.what) {

                case 87:

                    Bundle bundle = msgFromService.getData();
                    int result = bundle.getInt("result", 0);

                    txvResult.setText("Result: " + result);
                    break;

                default:
                    super.handleMessage(msgFromService);

            }
        }
    }

    private Messenger incomingMessenger = new Messenger(new IncomingResponseHandler());

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mService = new Messenger(service);
            mIsBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mIsBound = false;

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        txvResult = (TextView) findViewById(R.id.txvResult);

    }

    public void performAddOperation(View view) {

        EditText etNumOne = (EditText) findViewById(R.id.etNumOne);
        EditText etNumTwo = (EditText) findViewById(R.id.etNumTwo);

        int num1 = Integer.parseInt(etNumOne.getText().toString());
        int num2 = Integer.parseInt(etNumTwo.getText().toString());

        Message msgToService = Message.obtain(null, 43);

        Bundle bundle = new Bundle();
        bundle.putInt("numOne", num1);
        bundle.putInt("numTwo", num2);

        msgToService.setData(bundle);
        msgToService.replyTo = incomingMessenger;

        try {
            mService.send(msgToService);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void bindService(View view) {
        Intent intent = new Intent(this, MyMessengerService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    public void unbindService(View view) {
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }
}
