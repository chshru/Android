package com.chshru.music.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.chshru.music.datautil.Config;
import com.chshru.music.datautil.CtrlPlayer;

/**
 * Created by chshru on 2017/4/17.
 */

public class TelReceiver extends BroadcastReceiver {
    private boolean preMusicPlaying;

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        preMusicPlaying = Config.musicPlaying;
        if (preMusicPlaying) {
            CtrlPlayer.getInstance(context).pause();
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        if (tm.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
            if (preMusicPlaying) {
                if (!Config.musicPlaying) {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    CtrlPlayer.getInstance(context).start();
                }
            }
        }
    }

    PhoneStateListener listener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (preMusicPlaying)
                        if (!Config.musicPlaying)
                            CtrlPlayer.getInstance(context).start();
                    break;

            }
        }
    };
}
