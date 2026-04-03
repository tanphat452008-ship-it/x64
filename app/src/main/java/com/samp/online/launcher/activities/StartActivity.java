package com.samp.online.launcher.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import es.dmoral.toasty.Toasty;
import com.samp.online.App;
import com.samp.online.R;
import com.samp.online.Utils;
import com.samp.online.launcher.Preferences;

public class StartActivity extends AppCompatActivity
{
    Button download;
    EditText nickName;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_start);
        download = findViewById(R.id.button);
        nickName = findViewById(R.id.editTextTextPersonName);

        // ввод ника
        nickName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String obj = nickName.getText().toString();
                if (obj.isEmpty()) {
                    Toasty.warning(this, getResources().getString(R.string.enterNik), Toast.LENGTH_LONG).show();
                } else if (obj.length() < 4) {
                    Toasty.warning(this, getResources().getString(R.string.minLengthNik), Toast.LENGTH_LONG).show();
                } else {
                    Preferences.putString(this, Preferences.NICKNAME, obj);
                    Toasty.success(this, "Biệt hiệu của bạn đã được lưu: "+obj).show();
                    startActivity(new Intent(StartActivity.this, DownloadActivity.class));
                    finish();
                }
            }
            return false;
        });
        // кнопка "продолжить"
        download.setOnClickListener(view -> {
            String obj = nickName.getText().toString();
            if (obj.isEmpty()) {
                Toasty.warning(StartActivity.this, getResources().getString(R.string.enterNik), Toast.LENGTH_LONG).show();
            } else if (obj.length() < 4) {
                Toasty.warning(StartActivity.this, getResources().getString(R.string.minLengthNik), Toast.LENGTH_LONG).show();
            } else {
                Preferences.putString(StartActivity.this, Preferences.NICKNAME, obj);
                Toasty.success(StartActivity.this, "Biệt hiệu của bạn đã được lưu: "+obj).show();
                if(!Utils.isGameInstalled()) {
                    App.getInstance().downloadID = App.INSTALL_TYPE_GAMEFILES;
                    startActivity(new Intent(StartActivity.this, DownloadActivity.class));
                    finish();
                }
            }
        });
    }
}
