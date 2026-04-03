package com.samp.online.gui;


/*

         - Project made by Weikton (vk.com/ne.weikton)

*/

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nvidia.devtech.NvEventQueueActivity;
import com.samp.online.R;
import com.samp.online.gui.util.Utils;

import org.json.JSONObject;

import java.util.LinkedList;

public class BrNotification {
    public static int mActiveNotifications = 0;
    public static boolean mHiddenAll = false;
    public static BrNotification[] mNotifications;
    public static LinkedList<BrNotification> mQueuedNotifications;
    private PopupWindow mWindow = null;
    private NvEventQueueActivity mActivity = NvEventQueueActivity.getInstance();
    private View mView = null;
    private int mId = -1;
    public int mSubid = -1;
    private int mDuration = -1;
    private CountDownTimer mTimer = null;
    private ProgressBar mProgressBar = null;
    private Activity aactivity;
    public static BrNotification newInstance() {
        return new BrNotification();
    }

    public void show(JSONObject jSONObject) {
        if (mNotifications == null) {
            mQueuedNotifications = new LinkedList<>();
            mNotifications = new BrNotification[3];
            for (int i = 0; i < 3; i++) {
                mNotifications[i] = null;
            }
        }
        if (this.mWindow == null) {
            this.mView = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.notification_twitter, (ViewGroup) null, false);
            PopupWindow popupWindow = new PopupWindow(this.mView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

            this.mWindow = popupWindow;
            popupWindow.setAnimationStyle(R.style.Animation_AppCompat_Dialog);
            this.mWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        String massage = jSONObject.optString("m");
        int delay = jSONObject.optInt("d");
        String playername = jSONObject.optString("n");
        String imageUrl = jSONObject.optString("u");
        this.mDuration = delay;
        TextView Playername = (TextView) this.mView.findViewById(R.id.playername_tw);
        TextView textmsg = (TextView) this.mView.findViewById(R.id.massage);
        ImageView imageView = (ImageView) this.mView.findViewById(R.id.image);
        final ProgressBar progressBar = (ProgressBar) this.mView.findViewById(R.id.br_not_progress);
        this.mProgressBar = progressBar;
        int i2 = this.mDuration;
        if (i2 != -1) {
            progressBar.setMax(i2 * 1000);
            progressBar.setProgress(this.mDuration * 1000);
        }
        ((LinearLayout) this.mView.findViewById(R.id.dw_root)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(BrNotification.this.mActivity, R.anim.button_click));
                BrNotification.this.mView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BrNotification.this.close(1);
                    }
                }, 100);
            }
        });
        Playername.setText(Utils.transfromColors(playername));
        if (massage != null && !massage.isEmpty()) {
            textmsg.setText(massage);
        } else {
            textmsg.setText("อัพโหลดรูปภาพ");
        }

        Glide.with(BrNotification.this.mActivity)
                .load(imageUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(1040, 1040)
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        imageView.setVisibility(View.GONE);
                        return true;
                    }

                    @Override public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);
        this.mWindow.setTouchable(true);
        this.mWindow.setFocusable(false);
        this.mWindow.setOutsideTouchable(false);
        mActiveNotifications++;
        int firstFreeSlot = getFirstFreeSlot();
        if (firstFreeSlot == -1) {
            mQueuedNotifications.push(this);
            return;
        }
        progressBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                progressBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                startCountdown();
            }
        });
        int yPosForNotification = getYPosForNotification(firstFreeSlot);
        mNotifications[firstFreeSlot] = this;
        this.mWindow.showAtLocation(mActivity.getmRootFrame(), Gravity.LEFT | Gravity.CENTER_VERTICAL, 0, yPosForNotification);

        //this.mWindow.showAtLocation(mActivity.getmRootFrame(), 0, 300, yPosForNotification);
    }

    public void startCountdown() {
        CountDownTimer countDownTimer = this.mTimer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            this.mTimer = null;
        }
        if (this.mDuration != -1) {
            CountDownTimer r0 = new CountDownTimer((long) this.mProgressBar.getProgress(), 100) {
                @Override
                public void onTick(long j) {
                    if (Build.VERSION.SDK_INT >= 24) {
                        mProgressBar.setProgress((int) j, true);
                    } else {
                        mProgressBar.setProgress((int) j);
                    }
                }

                @Override
                public void onFinish() {
                    mProgressBar.setProgress(0);
                    close(1);
                }
            };
            this.mTimer = r0;
            r0.start();
        }
    }

    public static void closeNotificationById(int i) {
        if (mNotifications != null) {
            for (int i2 = 0; i2 < 4; i2++) {
                BrNotification[] brNotificationArr = mNotifications;
                if (brNotificationArr[i] != null && brNotificationArr[i].mSubid == i) {
                    brNotificationArr[i].close(2);
                    mNotifications[i] = null;
                }
            }
        }
    }

    public static void hideAllNotifications() {
        for (int i = 0; i < 4; i++) {
            BrNotification[] brNotificationArr = mNotifications;
            if (brNotificationArr[i] != null) {
                if (brNotificationArr[i].mTimer != null) {
                    brNotificationArr[i].mTimer.cancel();
                }
                mNotifications[i].mWindow.dismiss();
            }
        }
        mHiddenAll = true;
    }

    public static void resumeNotifications() {
        for (int i = 0; i < 4; i++) {
            BrNotification[] brNotificationArr = mNotifications;
            if (brNotificationArr[i] != null) {
                brNotificationArr[i].mWindow.showAtLocation(brNotificationArr[i].mActivity.getmRootFrame(), 81, 300, mNotifications[i].getYPosForNotification(i));
            }
        }
        for (int i2 = 0; i2 < 4; i2++) {
            BrNotification[] brNotificationArr2 = mNotifications;
            if (brNotificationArr2[i2] != null) {
                brNotificationArr2[i2].startCountdown();
            }
        }
    }

    public void close(int i) {
        CountDownTimer countDownTimer = this.mTimer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (i != 6) {
            close((JSONObject) null);
        }
    }

    int getFirstFreeSlot() {
        int i = 0;
        while (true) {
            BrNotification[] brNotificationArr = mNotifications;
            if (i >= brNotificationArr.length) {
                return -1;
            }
            if (brNotificationArr[i] == null) {
                return i;
            }
            i++;
        }
    }

    int getYPosForNotification(int i) {
        return (NvEventQueueActivity.dpToPx(110.0f, this.mActivity) * i) + ((i + 1) * NvEventQueueActivity.dpToPx(10.0f, this.mActivity));
    }

    public void close(JSONObject jSONObject) {
        BrNotification[] brNotificationArr;
        PopupWindow popupWindow = this.mWindow;
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        int i = 0;
        while (true) {
            BrNotification[] brNotificationArr2 = mNotifications;
            if (i >= brNotificationArr2.length) {
                i = -1;
                break;
            } else if (brNotificationArr2[i] == this) {
                brNotificationArr2[i] = null;
                break;
            } else {
                i++;
            }
        }
        if (i != -1) {
            while (true) {
                brNotificationArr = mNotifications;
                if (i >= brNotificationArr.length - 1) {
                    break;
                }
                int i2 = i + 1;
                brNotificationArr[i] = brNotificationArr[i2];
                i = i2;
            }
            brNotificationArr[brNotificationArr.length - 1] = null;
            if (mQueuedNotifications.size() > 0) {
                BrNotification[] brNotificationArr3 = mNotifications;
                brNotificationArr3[brNotificationArr3.length - 1] = mQueuedNotifications.getFirst();
                mQueuedNotifications.removeFirst();
                BrNotification[] brNotificationArr4 = mNotifications;
                BrNotification brNotification = brNotificationArr4[brNotificationArr4.length - 1];
                brNotification.mWindow.showAtLocation(brNotification.mActivity.getmRootFrame(), Gravity.LEFT | Gravity.CENTER_VERTICAL, 0, brNotification.getYPosForNotification(mNotifications.length - 1));
                brNotification.startCountdown();
            }
        }
        int i3 = 0;
        while (true) {
            BrNotification[] brNotificationArr5 = mNotifications;
            if (i3 < brNotificationArr5.length) {
                if (brNotificationArr5[i3] != null) {
                    brNotificationArr5[i3].mWindow.update(0, getYPosForNotification(i3), -1, -1);
                }
                i3++;
            } else {
                mActiveNotifications--;
                return;
            }
        }
    }

}