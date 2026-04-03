package com.samp.online.gui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.*;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.triggertrap.seekarc.SeekArc;

import com.samp.online.R;
import com.samp.online.gui.util.Utils;
import java.util.Formatter;

public class Speedometer {
    public ConstraintLayout mInputLayout;
    public ImageView mLight, mLock, mEngine;
    public TextView mMileage, mSpeed, mCarHP, mFuel;
    public SeekArc mSpeedLine, mSpeedFuel, mSpeedHP;

    public Speedometer(Activity activity){
        mInputLayout = activity.findViewById(R.id.main_speed);
        mSpeed = activity.findViewById(R.id.speed_text);
        mFuel = activity.findViewById(R.id.percent_fuel);
        mCarHP = activity.findViewById(R.id.percent_carhp);
        mMileage = activity.findViewById(R.id.speed_milleage);
        mSpeedLine = activity.findViewById(R.id.speed_line);
        mSpeedFuel = activity.findViewById(R.id.speed_fuel);
        mSpeedHP = activity.findViewById(R.id.speed_hp);
        mEngine = activity.findViewById(R.id.speed_engine);
        mLock = activity.findViewById(R.id.speed_doors);
        mLight = activity.findViewById(R.id.speed_lights);
        Utils.HideLayout(mInputLayout, false);
    }

    public void UpdateSpeedInfo(int speed, int fuel, int hp, int mileage, int engine, int light, int belt, int lock){
        hp= (int) hp/10;
        mFuel.setText(new Formatter().format("%d", Integer.valueOf(fuel)).toString());
        mMileage.setText(new Formatter().format("%06d", Integer.valueOf(mileage)).toString());
        mCarHP.setText(new Formatter().format("%d%s", Integer.valueOf(hp), "%").toString());
        mSpeedLine.setProgress(speed);
        mSpeedHP.setProgress(hp);
        mSpeedFuel.setProgress(fuel);
        mSpeed.setText(String.valueOf(speed));
        if(engine == 1)
            mEngine.setColorFilter(Color.parseColor("#00FF00"), PorterDuff.Mode.SRC_IN);
        else
            mEngine.setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_IN);
        if(lock == 1)
            mLock.setColorFilter(Color.parseColor("#00FF00"), PorterDuff.Mode.SRC_IN);
        else
            mLock.setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_IN);
        if(light == 1)
            mLight.setColorFilter(Color.parseColor("#00FF00"), PorterDuff.Mode.SRC_IN);
        else
            mLight.setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_IN);
    }

    public void ShowSpeed() { Utils.ShowLayout(mInputLayout, false); }

    public void HideSpeed() { Utils.HideLayout(mInputLayout, false); }
}