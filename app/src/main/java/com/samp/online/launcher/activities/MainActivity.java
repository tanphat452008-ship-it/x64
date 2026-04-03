package com.samp.online.launcher.activities;

import static com.samp.online.Config.GAME_PATH;
import static com.samp.online.Config.PATH_DOWNLOADS;
import static com.samp.online.Utils.filesListArrayList;
import static es.dmoral.toasty.Toasty.LENGTH_SHORT;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.liulishuo.filedownloader.FileDownloader;
import com.samp.online.App;
import com.samp.online.R;
import com.samp.online.Utils;
import com.samp.online.launcher.Preferences;

import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity
{
    Button btnPlay;
    private EditText nickName;
    private boolean doubleBackToExitPressedOnce;
    private VideoView mVideoView;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        if(!filesListArrayList.isEmpty())
        {
            App.getInstance().downloadID = App.INSTALL_TYPE_GAMEFILES;
            startActivity(new Intent(MainActivity.this, DownloadActivity.class));
            finish();
            return;
        }
        /*File gameFiles = new File(GAME_PATH + "data/ver.ini");
        Integer TARGET_GAMEFILES_VERSION = 0;
        if (gameFiles.exists()) {
            try {
                Wini w = new Wini(gameFiles);
                TARGET_GAMEFILES_VERSION = Integer.valueOf(w.get("versions", "gameFilesVersion"));
                w.store();
            } catch (IOException e) {
                Utils.writeLog(this, 'e', e.getMessage());
            }
        }
        if (App.getInstance().targetGameFilesVersion != TARGET_GAMEFILES_VERSION) {
            App.getInstance().downloadID = App.INSTALL_TYPE_UPDATE_GAMEFILES;
            startActivity(new Intent(MainActivity.this, DownloadActivity.class));
            finish();
            return;
        }*/
        nickName = findViewById(R.id.editTextTextNickName);
        nickName.setText(Preferences.getString(MainActivity.this, Preferences.NICKNAME));
        nickName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String obj = nickName.getText().toString();
                if (obj.isEmpty()) {
                    Toasty.warning(this, getResources().getString(R.string.enterNik), Toast.LENGTH_LONG).show();
                }
                /*else if (!obj.contains("_")) {
                    Toasty.warning(this, getResources().getString(R.string.mustContains_), Toast.LENGTH_LONG).show();
                }*/
                else if (obj.length() < 3) {
                    Toasty.warning(this, getResources().getString(R.string.minLengthNik), Toast.LENGTH_LONG).show();
                } else {
                    Preferences.putString(MainActivity.this, Preferences.NICKNAME, obj);
                    Toasty.success(MainActivity.this, "Biệt danh của bạn đã được lưu: "+obj).show();
                    File settings = new File(GAME_PATH+"SAMP/settings.ini");
                    if(settings.exists()) {
                        try {
                            Wini w = new Wini(settings);
                            w.put("client", "name", obj);
                            w.store();
                        } catch (IOException e) {
                            Utils.writeLog(MainActivity.this, 'e', "Lỗi: "+e.getMessage());
                        }
                    } else {
                        Utils.writeLog(MainActivity.this, 'e', "Không có tập tin settings.ini");
                    }
                }
            }
            return false;
        });
        btnPlay = findViewById(R.id.brp_welcome_btn);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play(view);
            }
        });
        File download_path = new File(PATH_DOWNLOADS);
        if(!download_path.exists()) { download_path.mkdirs(); }
    }

    public void play(View v) {
        String obj = nickName.getText().toString();
        if(Preferences.getString(App.getInstance(), Preferences.NICKNAME).isEmpty())
        {
            Toasty.warning(MainActivity.this, "Vui lòng nhập tên của bạn trước khi bắt đầu trò chơi.!").show();
        }else if (obj.isEmpty()) {
            Toasty.warning(this, getResources().getString(R.string.enterNik), Toast.LENGTH_LONG).show();
        } /*else if (!obj.contains("_")) {
            Toasty.warning(this, getResources().getString(R.string.mustContains_), Toast.LENGTH_LONG).show();
        }*/
        else if (obj.length() < 3) {
            Toasty.warning(this, getResources().getString(R.string.minLengthNik), Toast.LENGTH_LONG).show();
        } else {
            Toasty.success(MainActivity.this, "Trò chơi đang bắt đầu").show();
            startActivity(new Intent(MainActivity.this, com.samp.online.core.GTASA.class));
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (doubleBackToExitPressedOnce) {
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
            FileDownloader.getImpl().pauseAll();
            System.exit(0);
            return;
        }
        Toasty.info(MainActivity.this, "Nhấn lần nữa để thoát.", LENGTH_SHORT).show();
        doubleBackToExitPressedOnce = true;
        handler.postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}
