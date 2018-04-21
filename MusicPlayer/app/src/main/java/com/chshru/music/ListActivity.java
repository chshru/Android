package com.chshru.music;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.ServiceConnection;
import android.os.Bundle;

import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chshru.music.util.*;
import com.chshru.music.view.*;


/**
 * Created by chshru on 2017/2/25.
 */

public class ListActivity extends Activity implements View.OnClickListener, Player.MusicListener {


    private ListView list;
    private TextView name;
    private ImageView pause;
    private Controller mController;
    private MusicList mList;
    private ListAdapter mAdapter;
    private Player mPlayer;
    private Intent intent;
    private AppContext app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initialize();
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
                mPlayer.addMusicListener(ListActivity.this);
            } else {
                mPlayer = new Player(mController, mList);
                mPlayer.addMusicListener(ListActivity.this);
                mPlayer.choose(mPlayer.getPosition());
                app.setPlayer(mPlayer);
            }
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
        unbindService(conn);
    }


    @Override
    protected void onResume() {
        super.onResume();
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onPlayerStatusChange() {
        int res = mPlayer.isPlaying() ?
                R.drawable.main_run :
                R.drawable.main_pause;
        pause.setImageResource(res);
        name.setText(mPlayer.getCurName());
    }

}
