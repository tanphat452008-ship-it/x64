package com.samp.online.launcher.fragments;

import static com.samp.online.Config.GAME_PATH;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

import com.samp.online.App;
import com.samp.online.R;
import com.samp.online.Utils;
import com.samp.online.launcher.activities.DownloadActivity;

public class UpdateClient extends Fragment {
    private Button yes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_update, container, false);
        yes = view.findViewById(R.id.button_update);
        yes.setOnClickListener(view12 -> {
            view12.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.button_click));
            App.getInstance().downloadID = App.INSTALL_TYPE_CLIENT;
            Intent intent = new Intent(getActivity(), DownloadActivity.class);
            intent.putExtras(getActivity().getIntent());
            startActivity(intent);
            getActivity().finish();

            File settings = new File(GAME_PATH+"SAMP/settings.ini");
            if(settings.exists()) {
                try {
                    Wini w = new Wini(settings);
                    w.put("suqarestudio", "last_server", "1");
                    w.store();
                } catch (IOException e) {
                    Utils.writeLog(this.getContext(), 'e', "ข้อผิดพลาด: "+e.getMessage());
                }
            } else {
                Utils.writeLog(this.getContext(), 'e', "ไม่มีไฟล์ settings.ini");
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
