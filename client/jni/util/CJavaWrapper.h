#pragma once

#include <jni.h>

#include <string>

#define EXCEPTION_CHECK(env) \
	if ((env)->ExceptionCheck()) \ 
	{ \
		(env)->ExceptionDescribe(); \
		(env)->ExceptionClear(); \
		return; \
	}

class CJavaWrapper
{
	jobject activity;

	jmethodID s_GetClipboardText;

	jmethodID s_ShowInputLayout;
	jmethodID s_HideInputLayout;

	jmethodID s_ShowClientSettings;
	jmethodID s_SetUseFullScreen;

	jmethodID s_MakeDialog;
	
	jmethodID s_showHud;
    jmethodID s_hideHud;
	jmethodID s_updateHudInfo;
	
	jmethodID s_showTabWindow;
	jmethodID s_setTabStat;
	// -- Crash fix
	jmethodID s_FinishActivity;
	
	jmethodID s_setPauseState;

	jmethodID s_showSpeed;
    jmethodID s_hideSpeed;
	jmethodID s_updateSpeedInfo;
	jmethodID s_showphone;
	jmethodID s_hidephone;
	jmethodID s_posttwitter;
	jmethodID s_notification;
	jmethodID s_showWelcome;

	jmethodID s_showDonate;
	jmethodID s_updateDonate;
	jmethodID s_show_sc;
	jmethodID s_brNotification;
public:
	JNIEnv* GetEnv();

	std::string GetClipboardString();

	void ShowInputLayout();
	void HideInputLayout();

	void ShowClientSettings();
	void SetUseFullScreen(int b);
	
	void MakeDialog(int dialogId, int dialogTypeId, char* caption, char* content, char* leftBtnText, char* rightBtnText); // Диалоги
	
	void UpdateHudInfo(int health, int armour, int weaponid, int ammo, int ammoinclip, int money, int wanted);
	void ShowHud();
    void HideHud();

	void UpdateSpeedInfo(int speed, int fuel, int hp, int mileage, int engine, int light, int belt, int lock);
	void ShowSpeed();
    void HideSpeed();
    // -- Crash fix
	void FinishActivity();
	
	void ShowTabWindow();
	void SetTabStat(int id, char* name, int score, int ping);
	
	void SetPauseState(bool a1);
	void ShowWelcome(bool a);
	void ShowPhone();
	void HidePhone();
	void PostTwitter(const char* msg, int duration, const char* url, const char* playername);
	void ShowNotification(int delay, const char* name, const char* msg, const char* url);

	void ShowDonate(int money, int bc);
    void ShowBrNotification(const char* text, int type, int duration);
    
	void UpdateDonate(int money, int bc);
	void show_sc(int money, int bc);

	CJavaWrapper(JNIEnv* env, jobject activity);
	~CJavaWrapper();
};

extern CJavaWrapper* g_pJavaWrapper;
