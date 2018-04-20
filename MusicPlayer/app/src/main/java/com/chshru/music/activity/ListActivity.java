package com.chshru.music.activity;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.chshru.music.R;
import com.chshru.music.datautil.Config;
import com.chshru.music.datautil.CtrlPlayer;
import com.chshru.music.datautil.MusicList;
import com.chshru.music.service.DialogFactory;
import com.chshru.music.service.ListAdapter;
import com.chshru.music.datautil.FileCtrl;
import com.chshru.music.service.PlayService;
import com.chshru.music.service.VirtualKey;


/**
 * Created by chshru on 2017/2/25.
 */

public class ListActivity extends Activity implements View.OnClickListener {


    private ListView listView;
    private TextView musicName;
    private ImageView musicStatus;
    private static ListActivity listActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initialize();

    }



    private void initialize() {
        listActivity = ListActivity.this;
        musicName = (TextView) findViewById(R.id.runname);
        musicStatus = (ImageView) findViewById(R.id.pauseicon);
        listView = (ListView) findViewById(R.id.musicList);
        VirtualKey.assistActivity(findViewById(R.id.musicList));
        ListAdapter adapter = new ListAdapter(MusicList.getInstance(this).getList(), this);
        listView.setAdapter(adapter);
        initializeService();
        if (!MusicList.getInstance(this).getList().isEmpty()) {
            setOnClickListener();
        }
        reFreshView();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pauseicon:
                if (Config.musicPlaying) CtrlPlayer.getInstance(this).pause();
                else CtrlPlayer.getInstance(this).start();
                break;
            case R.id.runname:
                Intent playing = new Intent(this, PlayActivity.class);
                startActivity(playing);
                break;
        }
    }

    private void setOnClickListener() {
        musicName.setOnClickListener(this);
        musicStatus.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CtrlPlayer.getInstance(ListActivity.this).choose(position);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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


    private void initializeService() {
        if (!Config.serviceRunning) {
            Config.serviceRunning = true;
            Intent intent = new Intent().setClass(this, PlayService.class);
            this.startService(intent);
            if (!MusicList.getInstance(this).getList().isEmpty()) {
                Config.curPosition = FileCtrl.getInstance(this).getPosition();
                String path = MusicList.getInstance(this).getList().get(Config.curPosition).getPath();
                CtrlPlayer.getInstance(this).prepare(path);
                CtrlPlayer.getInstance(this).quickRun(FileCtrl.getInstance(this).getProcess());
            }
        }
    }

    public void reFreshView() {
        boolean flag = MusicList.getInstance(this).getList().isEmpty();
        musicName.setText(flag ? getString(R.string.none_music) :
                MusicList.getInstance(this).getList().get(Config.curPosition).getName());
        musicStatus.setImageResource(Config.musicPlaying ? R.drawable.main_run :
                R.drawable.main_pause);
    }

    @Override
    protected void onDestroy() {
        try {
            FileCtrl.getInstance(this).saveUserInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public static ListActivity getListAty() {
        return listActivity;
    }
}
