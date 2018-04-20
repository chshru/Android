package com.chshru.music.activity;

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

import com.chshru.music.R;
import com.chshru.music.datautil.Config;
import com.chshru.music.datautil.Player;
import com.chshru.music.datautil.MusicList;
import com.chshru.music.service.DialogFactory;
import com.chshru.music.service.ListAdapter;
import com.chshru.music.service.PlayService;
import com.chshru.music.service.VirtualKey;


/**
 * Created by chshru on 2017/2/25.
 */

public class ListActivity extends Activity implements View.OnClickListener {


    private ListView list;
    private TextView name;
    private ImageView pause;
    private PlayService mService;
    private MusicList mList;
    private ListAdapter mAdapter;

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
        mList = MusicList.getInstance(this);
        mAdapter = new ListAdapter(mList.getList(), this);
        list.setAdapter(mAdapter);
        initializeService();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pauseicon:
                if (Config.musicPlaying) Player.getInstance(this).pause();
                else Player.getInstance(this).start();
                break;
            case R.id.runname:
                Intent playing = new Intent(this, PlayActivity.class);
                startActivity(playing);
                break;
        }
    }

    private void setOnClickListener() {
        name.setOnClickListener(this);
        pause.setOnClickListener(this);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Player.getInstance(ListActivity.this).choose(position);
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
            mService = ((PlayService.PlayBinder) binder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };


    private void initializeService() {
        Intent intent = new Intent(this, PlayService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
    }
}
