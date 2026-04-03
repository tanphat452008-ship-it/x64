package com.samp.online.gui;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.nvidia.devtech.NvEventQueueActivity;
import com.samp.online.R;
import com.samp.online.gui.util.Utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Timer;
import java.util.TimerTask;

public class HudManager {
    public Activity activity;
    public ConstraintLayout hud_layout;

    public Animation animation;

    public TextView hud_money;

    public ImageView hud_weapon;

    public ArrayList<ImageView> hud_wanted;

    public ProgressBar progressHP;
    public ProgressBar progressArmor;

    private TextView mAmmo1;
    private  TextView mAmmo2;
    Button btn_show_hide, btn_esc, btn_tab, btn_alt, btn_f, btn_h, btn_y, btn_n;
    int showhidebutton = 0;
    Button btn_show_hide_quick;
    ImageView btn_inventory, btn_stats, btn_gps, btn_phone, btn_setting;
    LinearLayout btn_show_menu, layout_menu, layout_button;
    int showhidequickbutton = 0;
    public HudManager(Activity aactivity) {
        activity = aactivity;
        hud_layout = aactivity.findViewById(R.id.hud_main);
        hud_layout.setVisibility(View.GONE);

        Animation animation = AnimationUtils.loadAnimation(aactivity, R.anim.button_click);

        progressArmor = aactivity.findViewById(R.id.hud_armour);
        progressHP = aactivity.findViewById(R.id.hud_health);

        hud_money = aactivity.findViewById(R.id.hud_money);
        hud_weapon = aactivity.findViewById(R.id.hud_weapon);

        mAmmo1 = (TextView) aactivity.findViewById(R.id.hud_ammo);
        mAmmo2 = (TextView) aactivity.findViewById(R.id.hud_ammo);

        hud_wanted = new ArrayList<>();
        hud_wanted.add(activity.findViewById(R.id.hud_star1));
        hud_wanted.add(activity.findViewById(R.id.hud_star2));
        hud_wanted.add(activity.findViewById(R.id.hud_star3));
        hud_wanted.add(activity.findViewById(R.id.hud_star4));
        hud_wanted.add(activity.findViewById(R.id.hud_star5));
        //hud_wanted.add(activity.findViewById(R.id.hud_star6));

        layout_button = aactivity.findViewById(R.id.linearLayout_Menu_Button_Keys);
        btn_show_hide = aactivity.findViewById(R.id.button_show_hide);
        btn_esc = aactivity.findViewById(R.id.button_esc);
        btn_tab = aactivity.findViewById(R.id.button_tab);
        btn_alt = aactivity.findViewById(R.id.button_alt);
        btn_f = aactivity.findViewById(R.id.button_f);
        btn_h = aactivity.findViewById(R.id.button_h);
        btn_y = aactivity.findViewById(R.id.button_y);
        btn_n = aactivity.findViewById(R.id.button_n);
        btn_esc.setVisibility(View.INVISIBLE);
        btn_alt.setVisibility(View.INVISIBLE);
        btn_f.setVisibility(View.INVISIBLE);
        btn_h.setVisibility(View.INVISIBLE);
        btn_y.setVisibility(View.INVISIBLE);
        btn_n.setVisibility(View.INVISIBLE);
        btn_tab.setVisibility(View.INVISIBLE);

        btn_show_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showhidebutton == 0)
                {
                    btn_tab.setVisibility(View.VISIBLE);
                    btn_esc.setVisibility(View.VISIBLE);
                    btn_alt.setVisibility(View.VISIBLE);
                    btn_f.setVisibility(View.VISIBLE);
                    btn_h.setVisibility(View.VISIBLE);
                    btn_y.setVisibility(View.VISIBLE);
                    btn_n.setVisibility(View.VISIBLE);
                    btn_show_hide.setText("<<");
                    showhidebutton = 1;
                }
                else {
                    btn_tab.setVisibility(View.INVISIBLE);
                    btn_esc.setVisibility(View.INVISIBLE);
                    btn_alt.setVisibility(View.INVISIBLE);
                    btn_f.setVisibility(View.INVISIBLE);
                    btn_h.setVisibility(View.INVISIBLE);
                    btn_y.setVisibility(View.INVISIBLE);
                    btn_n.setVisibility(View.INVISIBLE);
                    btn_show_hide.setText(">>");
                    showhidebutton = 0;
                }
            }
        });

        btn_esc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = "ESC";
                NvEventQueueActivity.getInstance().sendButton(1, btn.getBytes(), 1);
            }
        });
        btn_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = "TAB";
                NvEventQueueActivity.getInstance().sendButton(1, btn.getBytes(), 2);
            }
        });
        btn_alt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = "ALT";
                NvEventQueueActivity.getInstance().sendButton(1, btn.getBytes(), 3);
            }
        });
        btn_f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = "F";
                NvEventQueueActivity.getInstance().sendButton(1, btn.getBytes(), 4);
            }
        });
        btn_h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = "H";
                NvEventQueueActivity.getInstance().sendButton(1, btn.getBytes(), 5);
            }
        });
        btn_y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = "Y";
                NvEventQueueActivity.getInstance().sendButton(1, btn.getBytes(), 6);
            }
        });
        btn_n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = "N";
                NvEventQueueActivity.getInstance().sendButton(1, btn.getBytes(), 7);
            }
        });

        layout_menu = aactivity.findViewById(R.id.linearLayout_Main_Menu);
        btn_show_menu = aactivity.findViewById(R.id.linearLayout_Show_Menu);
        btn_show_hide_quick = aactivity.findViewById(R.id.button_quick_showhide);
        btn_inventory = aactivity.findViewById(R.id.button_quick_inventory);
        btn_stats = aactivity.findViewById(R.id.button_quick_stats);
        btn_gps = aactivity.findViewById(R.id.button_quick_gps);
        btn_phone = aactivity.findViewById(R.id.button_quick_phone);
        btn_setting = aactivity.findViewById(R.id.button_quick_setting);

        btn_show_menu.setVisibility(View.INVISIBLE);
        btn_inventory.setVisibility(View.INVISIBLE);
        btn_stats.setVisibility(View.INVISIBLE);
        btn_gps.setVisibility(View.INVISIBLE);
        btn_phone.setVisibility(View.INVISIBLE);
        btn_setting.setVisibility(View.INVISIBLE);
        layout_menu.setVisibility(View.INVISIBLE);

        btn_show_hide_quick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (showhidequickbutton == 0)
                {
                    btn_inventory.setVisibility(View.VISIBLE);
                    btn_stats.setVisibility(View.VISIBLE);
                    btn_gps.setVisibility(View.VISIBLE);
                    btn_phone.setVisibility(View.VISIBLE);
                    btn_setting.setVisibility(View.VISIBLE);
                    btn_show_hide_quick.setText("˅");
                    showhidequickbutton = 1;

                    layout_menu.setVisibility(View.VISIBLE);
                }
                else {
                    btn_inventory.setVisibility(View.INVISIBLE);
                    btn_stats.setVisibility(View.INVISIBLE);
                    btn_gps.setVisibility(View.INVISIBLE);
                    btn_phone.setVisibility(View.INVISIBLE);
                    btn_setting.setVisibility(View.INVISIBLE);
                    btn_show_hide_quick.setText("˄");
                    showhidequickbutton = 0;

                    layout_menu.setVisibility(View.GONE);
                }
            }
        });

        btn_inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = "Inventory";
                NvEventQueueActivity.getInstance().sendButtonQuick(1, btn.getBytes(), 1);
            }
        });
        btn_stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = "Stats";
                NvEventQueueActivity.getInstance().sendButtonQuick(1, btn.getBytes(), 2);
            }
        });
        btn_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = "Gps";
                NvEventQueueActivity.getInstance().sendButtonQuick(1, btn.getBytes(), 3);
            }
        });
        btn_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = "Phone";
                NvEventQueueActivity.getInstance().sendButtonQuick(1, btn.getBytes(), 4);
            }
        });
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = "Setting";
                NvEventQueueActivity.getInstance().sendButtonQuick(1, btn.getBytes(), 5);
            }
        });
    }

    public void UpdateHudInfo(int health, int armour, int weaponid, int ammo, int playerid, int money, int wanted)
    {
        progressHP.setProgress(health);
        progressArmor.setProgress(armour);

        DecimalFormat formatter=new DecimalFormat();
        DecimalFormatSymbols symbols= DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(',');
        formatter.setDecimalFormatSymbols(symbols);
        String s=formatter.format(money).toString();
        hud_money.setText(String.valueOf("$" + s));

        int id = activity.getResources().getIdentifier(new Formatter().format("weapon_%d", Integer.valueOf(weaponid)).toString(), "drawable", activity.getPackageName());
        hud_weapon.setImageResource(id);

        mAmmo2.setText(new Formatter().format("/%d", new Object[]{Integer.valueOf(ammo)}).toString());
        mAmmo1.setText(String.valueOf(ammo));

        hud_weapon.setOnClickListener(v -> NvEventQueueActivity.getInstance().onWeaponChanged());
        if(wanted > 6) wanted = 6;
        for (int i2 = 0; i2 < wanted; i2++) {
            hud_wanted.get(i2).setBackgroundResource(R.drawable.ic_y_star);
        }

    }

    private void openTab()
    {
        Timer t = new Timer();
        t.schedule(new TimerTask(){
            @Override
            public void run() {
                NvEventQueueActivity.getInstance().showTab();
            }
        }, 300L);
    }

    public void ShowHud()
    {
        Utils.ShowLayout(hud_layout, false);
    }

    public void HideHud()
    {
        Utils.HideLayout(hud_layout, false);
    }

}
