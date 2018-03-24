package com.chshru.music.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    private final int ALL = 1;
    private static ListActivity listActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        if (hasPermissions()) {
            initialize();
        }
    }

    private boolean hasPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ListActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_SECURE_SETTINGS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.PROCESS_OUTGOING_CALLS,
                        Manifest.permission.INTERNET,
                }, ALL);
                return false;
            }
        }
        return true;
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

    @Override
    public void onRequestPermissionsResult(int status, String permissions[], int[] results) {
        switch (status) {
            case ALL: {
                boolean success = true;
                for (int result : results)
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        success = false;
                        break;
                    }
                if (success) initialize();
                else {
                    DialogFactory.getInstance(this).tipsDialog(
                            "提示", "接受所有授权才能完成初始化,请重新打开软件", "关闭");
                }
                break;
            }
        }
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
