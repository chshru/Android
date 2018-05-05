package com.chshru.music;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.os.IBinder;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chshru.music.util.*;
import com.chshru.music.view.*;


/**
 * Created by chshru on 2017/2/25.
 */

public class ListActivity extends Activity implements View.OnClickListener, Player.MusicListener {


    private final String[] pm = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.RECORD_AUDIO,
    };
    private final int pmCode = 233333;

    private ListView list;
    private TextView name;
    private ImageView pause;
    private Controller mController;
    private MusicList mList;
    private ListAdapter mAdapter;
    private Player mPlayer;
    private Intent intent;
    private AppContext app;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        if ((flag = checkPrimission())) {
            initialize();
        } else {
            requestPermission();
        }

    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(pm, pmCode);
        }
    }

    private boolean checkPrimission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < pm.length; i++) {
                if (checkSelfPermission(pm[i]) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int rCode, @NonNull String[] pre, @NonNull int[] result) {
        if (rCode == pmCode) {
            flag = true;
            for (int i = 0; i < result.length; i++) {
                if (result[i] != PackageManager.PERMISSION_GRANTED) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                onCreate(null);
            } else {
                showPermissionDialog();
            }
        }
    }

    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog ad = builder.create();
        View v = getLayoutInflater().inflate(R.layout.dialog_tips, null);
        ((TextView) v.findViewById(R.id.tips_title)).setText(R.string.permission_title);
        ((TextView) v.findViewById(R.id.tips_content)).setText(R.string.permission_content);
        Button button = (Button) v.findViewById(R.id.tips_shutdown);
        button.setText(R.string.close);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });
        ad.setView(v);
        ad.show();
    }

    private void initialize() {
        name = (TextView) findViewById(R.id.runname);
        pause = (ImageView) findViewById(R.id.pauseicon);
        list = (ListView) findViewById(R.id.musicList);
        VirtualKey.assistActivity(findViewById(R.id.musicList));
        app = (AppContext) getApplication();
        intent = new Intent(this, PlayService.class);
        startService(intent);
        if (app.getPlayer() != null) {
            mList = app.getPlayer().getList();
        } else {
            mList = MusicList.getInstance(getApplicationContext());
        }
        mAdapter = new ListAdapter(mList.getList(), this);
        list.setAdapter(mAdapter);
        initClickListener();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pauseicon:
                if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                } else {
                    mPlayer.start();
                }
                break;
            case R.id.runname:
                Intent playing = new Intent(this, PlayActivity.class);
                startActivity(playing);
                break;
        }
    }

    private void initClickListener() {
        name.setOnClickListener(this);
        pause.setOnClickListener(this);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPlayer.getPosition() != position) {
                    mPlayer.choose(position);
                }
            }
        });
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mController = (Controller) binder;
            if (app.getPlayer() != null) {
                mPlayer = app.getPlayer();
                mPlayer.setController(mController);
            } else {
                mPlayer = new Player(mController, mList);
                mPlayer.choose(mPlayer.getPosition());
                app.setPlayer(mPlayer);
            }
            mPlayer.addMusicListener(ListActivity.this);
            onPlayerStatusChange();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayer.removeMusicListener(ListActivity.this);
            mController = null;
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        if (flag) {
            unbindService(conn);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (flag) {
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
        }
    }


    @Override
    public void onPlayerStatusChange() {
        int res = mPlayer.isPlaying() ?
                R.drawable.main_run :
                R.drawable.main_pause;
        pause.setImageResource(res);
        name.setText(mPlayer.getCurName());
    }

    @Override
    public void setListenerStatus(boolean status) {

    }

}
