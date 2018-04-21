package com.chshru.music;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.ServiceConnection;
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chshru.music.datautil.*;
import com.chshru.music.service.*;


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
        mList = MusicList.getInstance(getApplicationContext());
        mAdapter = new ListAdapter(mList.getList(), this);
        list.setAdapter(mAdapter);
        ((AppContext) getApplication()).setList(mList);
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
                mPlayer.choose(position);
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DialogFactory.getInstance(ListActivity.this).tipsDialog(
                        getString(R.string.file_path),
                        MusicList.getInstance(ListActivity.this).getList().get(i).getPath(),
                        getString(R.string.shutdown)
                );
                return true;
            }
        });
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mController = (Controller) binder;
            mPlayer = new Player(mController, mList);
            mPlayer.addMusicListener(ListActivity.this);
            mPlayer.setActivity(ListActivity.this);
            onPlayerStatusChange();
            onPlayerPositionChange();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mController = null;
        }
    };

    private final int STATUS_CHANGE = 0;
    private final int POS_CHANGE = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STATUS_CHANGE:
                    int res = mPlayer.isPlaying() ?
                            R.drawable.main_run :
                            R.drawable.main_pause;
                    pause.setImageResource(res);
                    break;
                case POS_CHANGE:
                    String temp = mList.getList().get(msg.arg1).getName();
                    name.setText(temp);
                    break;
            }
        }
    };


    @Override
    protected void onPause() {
        super.onPause();
        boolean isPlay = mPlayer.isPlaying();
        unbindService(conn);
        if (!isPlay) {
            stopService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        intent = new Intent(this, PlayService.class);
        startService(intent);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onPlayerStatusChange() {
        mHandler.sendEmptyMessage(STATUS_CHANGE);
    }

    @Override
    public void onPlayerPositionChange() {
        Message msg = mHandler.obtainMessage();
        msg.what = POS_CHANGE;
        msg.arg1 = mPlayer.getPosition();
        mHandler.sendMessage(msg);
    }
}
