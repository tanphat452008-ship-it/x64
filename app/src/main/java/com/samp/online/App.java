package com.samp.online;

import android.app.*;
import android.net.ConnectivityManager;

import androidx.multidex.MultiDexApplication;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import java.io.File;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import com.samp.online.launcher.network.Server;
import com.samp.online.launcher.network.ServerListener;
import com.samp.online.launcher.network.Story;

public class App extends MultiDexApplication
{
	private static App instance;
	//
	public Integer downloadID = null;
	public static final Integer INSTALL_TYPE_CLIENT = 1;
	public static final Integer INSTALL_TYPE_GAMEFILES = 2;
	public static final Integer INSTALL_TYPE_UPDATE_GAMEFILES = 3;
	//
	private String tempNick;
	private String currentGPU;
	//
	public String errorInfo, errorText;
	//
	public static final String ADRENO_TEGRA = "dxt";
	public static final String ETC = "etc";
	public static final String MALI = "etc";//"mali";
	public static final String POWER_VR = "pvr";
	//
	public ArrayList<Server> serverList;
	public ServerListener serverListener;
	public ArrayList<Story> stories;
	// urls - links
	public String URL_CLIENT;
	public String URL_GAME_FILES;
	public String URL_GAME_FILES_UPDATE;
	public Integer targetClientVersion;
	public Integer targetGameFilesVersion;
	//
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		//activateAppMetrica(); // яндекс метрика
		initLogger();
        serverList = new ArrayList<>();
		stories = new ArrayList<>();
	}
	private void initLogger () {
		try {
			File appDir = new File(Config.APP_PATH);
			if (!appDir.exists()) {
				appDir.mkdirs();
			}
			File logcat = new File(Config.APP_PATH + "/logcat.txt");
			if (logcat.exists()) {
				logcat.delete();
			}
			logcat.createNewFile();
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("logcat -f " + logcat.getAbsolutePath());
		} catch (Exception e) {
			Utils.writeLog(getInstance(), 'e', "InitLogger error: "+e.getMessage());
		}
		try {
			((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).setNetworkPreference(ConnectivityManager.DEFAULT_NETWORK_PREFERENCE);
		}
		catch (Exception unused) {}
	}
	public static App getInstance() { return instance; } // get app context
	//
	public String getTempNickName() { return this.tempNick; } // временный ник
	public void setTempNickName(String str) { this.tempNick = str; }
	//
	public ArrayList<Server> getServerList() { return this.serverList; } // список серверов
	public ArrayList<Story> getStories() { return this.stories; } // список сторисов
	//
	public void setGPU(String str) {
		currentGPU = str;
	}

	public String getGPU() {
		return currentGPU;
	}
	//
	public static boolean isExternalStorageAvailable(Activity activity) {
		try {
			activity.getExternalFilesDir((String) null).getAbsolutePath();
			return true;
		} catch (Exception unused) {
			Toasty.error(activity, "Không có quyền truy cập vào bộ nhớ!", Toasty.LENGTH_LONG);
			return false;
		}
	}
	public static boolean isAppInstalledFromMarket(String marketNamePacket) {
		if(getInstance().getPackageManager().
				getInstallerPackageName(getInstance().getPackageName()).equals(marketNamePacket)) {
			return true;
		}
		return false;
	}

    private void activateAppMetrica() {
		YandexMetricaConfig appMetricaConfig;
		appMetricaConfig = YandexMetricaConfig.newConfigBuilder("ที่นี่-ID-ตัวชี้วัดของคุณ")
				.withLocationTracking(true)
				.withLogs()
				.withStatisticsSending(true)
				.build();
		YandexMetrica.activate(getApplicationContext(), appMetricaConfig);
		YandexMetrica.enableActivityAutoTracking(this);

	}
}