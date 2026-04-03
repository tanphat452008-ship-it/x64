package com.samp.online.launcher.fragments;

import static com.samp.online.Config.GAME_PATH;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;

import es.dmoral.toasty.Toasty;
import com.samp.online.App;
import com.samp.online.R;
import com.samp.online.Utils;
import com.samp.online.launcher.Preferences;
import com.samp.online.launcher.activities.DownloadActivity;

public class SettingsFragment extends Fragment
{
	private EditText nickName;
	private Button repairGame;
	private Button btnFov75, btnFov120, btnFov144;
	private ToggleButton btnNotifications, btnFpsCounter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view;
		view = inflater.inflate(R.layout.fragment_settings, container, false);
		nickName = view.findViewById(R.id.editTextTextNickName);
		repairGame = view.findViewById(R.id.repairGame);
		btnNotifications = view.findViewById(R.id.switch1);
		btnFpsCounter = view.findViewById(R.id.switch2);
		btnFov75 = view.findViewById(R.id.FOV75);
		btnFov120 = view.findViewById(R.id.FOV120);
		btnFov144 = view.findViewById(R.id.FOV144);
		// ввод ника
		nickName.setText(Preferences.getString(getActivity(), Preferences.NICKNAME));
		nickName.setOnEditorActionListener((v, actionId, event) -> {
			if (actionId == EditorInfo.IME_ACTION_SEARCH ||
					actionId == EditorInfo.IME_ACTION_DONE ||
					event.getAction() == KeyEvent.ACTION_DOWN &&
							event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
				String obj = nickName.getText().toString();
				if (obj.isEmpty()) {
					Toasty.warning(getActivity(), getResources().getString(R.string.enterNik), Toast.LENGTH_LONG).show();
				} else if (obj.length() < 4) {
					Toasty.warning(getActivity(), getResources().getString(R.string.minLengthNik), Toast.LENGTH_LONG).show();
				} else {
					Preferences.putString(getActivity(), Preferences.NICKNAME, obj);
					Toasty.success(getActivity(), "Biệt hiệu của bạn đã được lưu: "+obj).show();
					File settings = new File(GAME_PATH+"SAMP/settings.ini");
					if(settings.exists()) {
						try {
							Wini w = new Wini(settings);
							w.put("client", "name", obj);
							w.store();
						} catch (IOException e) {
							Utils.writeLog(getActivity(), 'e', "Loi: "+e.getMessage());
						}
					} else {
						Utils.writeLog(getActivity(), 'e', "Không có tập tin settings.ini");
					}
				}
			}
			return false;
		});
		// кнопка починить игру (переустановить)
		repairGame.setOnClickListener(view1 -> {
			delete(new File(GAME_PATH));
			App.getInstance().downloadID = App.INSTALL_TYPE_GAMEFILES;
			Intent intent = new Intent(getActivity(), DownloadActivity.class);
			intent.putExtras(getActivity().getIntent());
			startActivity(intent);
			getActivity().finish();
		});
		// уведомления и счетчик фпс
		btnNotifications.setChecked(Preferences.getBoolean(getActivity(), Preferences.NOTIFICATION, true));
		btnNotifications.setOnCheckedChangeListener((compoundButton, b) -> Preferences.putBoolean(getActivity(), Preferences.NOTIFICATION, b));
		//
		btnFpsCounter.setChecked(false);
		File f = new File(GAME_PATH+"SAMP/settings.ini");
		if(f.exists()) {
			Wini w = null;
			try {
				w = new Wini(f);
				Integer fps = new Integer(w.get("gui", "fpscounter"));
				if(fps == 1) {
					btnFpsCounter.setChecked(true);
				}
				w.store();
			} catch (IOException e) {
				Utils.writeLog(getActivity(), 'e', "lỗi: "+e.getMessage());
			}
		}
		btnFpsCounter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				Wini w = null;
				try {
					w = new Wini(f);
					if(b) {
						w.put("gui", "fpscounter", 1);
					} else w.put("gui", "fpscounter", 0);
					w.store();
				} catch (IOException e) {
					Utils.writeLog(getActivity(), 'e', "Lỗi: "+e.getMessage());
				}
			}
		});

		btnFov75.setOnClickListener(view1 -> {
			Wini w = null;
			try {
				w = new Wini(f);
				w.put("gui", "FOV", 75);
				w.store();
				Toasty.success(getActivity(), "Bạn đã điều chỉnh góc nhìn FOV thành 75").show();
			} catch (IOException e) {
				Utils.writeLog(getActivity(), 'e', "Lỗi: "+e.getMessage());
			}
		});

		btnFov120.setOnClickListener(view1 -> {
			Wini w = null;
			try {
				w = new Wini(f);
				w.put("gui", "FOV", 120);
				w.store();
				Toasty.success(getActivity(), "Bạn đã điều chỉnh góc nhìn FOV thành 120").show();
			} catch (IOException e) {
				Utils.writeLog(getActivity(), 'e', "Lỗi: "+e.getMessage());
			}
		});

		btnFov144.setOnClickListener(view1 -> {
			Wini w = null;
			try {
				w = new Wini(f);
				w.put("gui", "FOV", 144);
				w.store();
				Toasty.success(getActivity(), "Bạn đã điều chỉnh góc nhìn FOV thành 144").show();
			} catch (IOException e) {
				Utils.writeLog(getActivity(), 'e', "Lỗi: "+e.getMessage());
			}
		});
		return view;
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	private void delete(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File delete : file.listFiles()) {
					delete(delete);
				}
				file.delete();
				return;
			}
			file.delete();
		}
	}

	private void loadFragment(Fragment fragment) {
		FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_place, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}
}
