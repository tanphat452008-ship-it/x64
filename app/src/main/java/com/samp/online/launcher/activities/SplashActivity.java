package com.samp.online.launcher.activities;

import static com.samp.online.Config.GAME_PATH;

import static com.samp.online.Utils.WriteJsonFile;
import static com.samp.online.Utils.download;
import static com.samp.online.Utils.filesListArrayList;
import static com.samp.online.Utils.hashFile;

import static java.lang.Integer.*;

import android.Manifest;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.messaging.FirebaseMessaging;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import es.dmoral.toasty.Toasty;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.samp.online.App;
import com.samp.online.BuildConfig;
import com.samp.online.R;
import com.samp.online.Utils;
import com.samp.online.launcher.Preferences;
import com.samp.online.launcher.fragments.UpdateClient;
import com.samp.online.launcher.network.ApiService;
import com.samp.online.launcher.network.FilesList;
import com.samp.online.launcher.network.Links;

public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    // permissions
    private static final int MAIN_PERMISSIONS_REQUEST_CODE = 300;
    private Handler handler;
    String[] main_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler = new Handler();
        if (EasyPermissions.hasPermissions(this, main_permissions)) {
            Utils.writeLog(this, 'i', "app sucessfully started!");
            Init();
        } else {
            Utils.writeLog(this, 'i', "app not have full permissions!!!");
            EasyPermissions.requestPermissions(this,
                    "Ứng dụng phải có quyền ghi vào bộ nhớ.",
                    MAIN_PERMISSIONS_REQUEST_CODE, main_permissions);
        }
    }
    private void Init()
    {
        FileDownloader.setup(this);
        if (!Preferences.getBoolean(this, Preferences.FIRST_START)) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Utils.writeLog(SplashActivity.this, 'e', "FCM Reg failed: "+task.getException());
                    return;
                }
                Preferences.putString(App.getInstance(), Preferences.USER_FCM_KEY, task.getResult());
                Utils.writeLog(SplashActivity.this, 'i', "FCM reg success! Token: "+task.getResult());
            });
            Preferences.putBoolean(SplashActivity.this, Preferences.FIRST_START, true);
            loadAPI();
        }
        else {
            loadAPI();
        }
    }

    static class HashFileTask implements Runnable {
        private final FilesList info;
        private final AtomicInteger completedTasks;
        private final int totalFiles;

        public HashFileTask(FilesList info, AtomicInteger completedTasks, int totalFiles) {
            this.info = info;
            this.completedTasks = completedTasks;
            this.totalFiles = totalFiles;
        }

        @Override
        public void run() {
            try {
                boolean isInstalledSpecialFile = true;
                File file = new File(info.getPath());
                if (!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                if(!file.exists()){
                    if(parseInt(info.getIgnore()) == 1){
                        isInstalledSpecialFile = false;
                    }
                    file.createNewFile();
                }
                String hash = null;
                hash = hashFile(file);
                String resultString = file.getPath().replace(GAME_PATH, "");
                FilesList localFiles = new FilesList(file.getName(), Long.toString(file.length()), hash, file.getAbsolutePath(), "http://MOBILE.com/LauncherMobile/Game/" + resultString, info.getIgnore());
                if (info.getPath().equals(localFiles.getPath())) {
                    if(parseInt(info.getIgnore()) == 0 || !isInstalledSpecialFile) {
                        if (!info.getHash().equals(localFiles.getHash())) {

                            System.out.println("Khong trung hash hoac size " + localFiles.getPath() +" " + info.getPath());
                            System.out.println(localFiles.getHash() +" "+ info.getHash());
                            filesListArrayList.add(new FilesList(info.getName(), info.getSize(), info.getHash(), info.getPath(), info.getUrl(), info.getIgnore()));
                        }
                    }
                    else {
                        System.out.println("bo qua tap tin: " + info.getName() + " Debug: " + localFiles.getName());
                    }
                }
                else {
                    System.out.println("Khong co  " + localFiles.getPath() + " voi " + info.getPath() + ".");
                    filesListArrayList.add(new FilesList(info.getName(), info.getSize(), info.getHash(), info.getPath(), info.getUrl(), info.getIgnore()));
                }
                System.out.println("File: " + info.getName() + ", Hash: " + hash);

                // Callback on completion
                completedTasks.incrementAndGet();
                System.out.println("Completed tasks: " + completedTasks.get() + "/" + totalFiles);

            } catch (NoSuchAlgorithmException | IOException e) {
                System.err.println("Error hashing file " + info.getName() + ": " + e.getMessage());
            }
        }
    }
    public void GetFilesList() {
        final int THREAD_POOL_SIZE = 4; // Adjust based on your system
        String str = GAME_PATH;
        File directory = new File(str);
        //List<FilesList> LocalLists = listFilesRecursively(directory);
        ApiService.getInstance().getApiService().getFiles().enqueue(new Callback<List<FilesList>>() {
            @Override
            public void onResponse(Call<List<FilesList>> call, Response<List<FilesList>> response) {
                List<FilesList> filesLists = response.body();
                AtomicInteger completedTasks = new AtomicInteger(0);
                int totalFiles = filesLists.size();

                ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
                if (filesLists.size() >= 0) {
                    for (int i = 0; i < filesLists.size(); i++) {
                        FilesList info = filesLists.get(i);
                        executor.execute(new HashFileTask(info, completedTasks, totalFiles));
                    }
                }
                executor.shutdown();

                try {
                    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    System.err.println("Task interrupted: " + e.getMessage());
                }

                System.out.println("Hashing complete.");
            }

            @Override
            public void onFailure(Call<List<FilesList>> call, Throwable t) {
                System.out.println("Xynia");
            }
        });
    }
    // загрузка апи, новостей, серверов, итд
    private void loadAPI() {
        ApiService.getInstance().getApiService().getLinks().enqueue(new Callback<Links>() {
            public void onResponse(Call<Links> call, Response<Links> response) {
                if(response.isSuccessful())
                {
                    if(response.body() != null) {
                        // версии
                        App.getInstance().targetClientVersion = response.body().getTargetClientVersion();
                        App.getInstance().targetGameFilesVersion = response.body().getTargetGameFilesVersion();
                        // ссылки на файлы
                        App.getInstance().URL_CLIENT = response.body().getUrlClient();
                        App.getInstance().URL_GAME_FILES = response.body().getUrlFiles();
                        App.getInstance().URL_GAME_FILES_UPDATE = response.body().getUrlFilesUpdate();
                        // загрузка остального
                        //String str = GAME_PATH;
                        //File directory = new File(str);
                        //WriteJsonFile(directory);
                        GetFilesList();

                        runOnUiThread(() -> startLauncher());
                    }
                }
                else {
                    try {
                        String errorMessage = response.errorBody() != null ? response.errorBody().string() : "Không có nội dung lỗi.";

                        // Hiển thị lỗi cụ thể bằng Toast
                        Toasty.error(SplashActivity.this, "Lỗi API: " + errorMessage, Toasty.LENGTH_LONG).show();

                        // Ghi log lỗi đầy đủ
                        Utils.writeLog(SplashActivity.this, 'e', "Đã xảy ra lỗi khi tải mục này. API: " + errorMessage);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toasty.error(SplashActivity.this, "Lỗi khi đọc nội dung lỗi từ API.", Toasty.LENGTH_LONG).show();
                        Utils.writeLog(SplashActivity.this, 'e', "Lỗi IOException khi đọc errorBody(): " + e.getMessage());
                    }
                }
            }
            public void onFailure(Call<Links> call, Throwable th) {
                Utils.writeLog(SplashActivity.this, 'e', "Đã xảy ra lỗi khi tải mục này. API: "+th.getMessage());
                if(!Utils.isInternetConnected(SplashActivity.this))
                {
                    Toasty.error(SplashActivity.this, "Bạn không có kết nối internet.", Toasty.LENGTH_LONG).show();
                    return;
                }
                Toasty.error(SplashActivity.this, "Đã xảy ra lỗi khi kết nối với máy chủ. Máy chủ không khả dụng.", Toasty.LENGTH_LONG).show();
            }
        });
    }

    //-------------------------------------------------------------------

    private void startLauncher() {
        if(/*!BuildConfig.DEBUG && */App.getInstance().targetClientVersion != BuildConfig.VERSION_CODE) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, new UpdateClient());
            transaction.commit();
            return;
        }

        /*Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);
        String targetDateString = API_AGE;
        try {
            Date targetDate = dateFormat.parse(targetDateString);

            if (currentDate.compareTo(targetDate) >= 0) {
                Toasty.error(SplashActivity.this, "การเช่าแอปพลิเคชันหมดอายุแล้วกรุณาติดต่อแอดมิน หรือ SquareStudio", Toasty.LENGTH_LONG).show();
                File settings = new File(GAME_PATH+"SAMP/settings.ini");
                if(settings.exists()) {
                    try {
                        Wini w = new Wini(settings);
                        w.put("suqarestudio", "last_server", "ExpireServerBySquareStudio");
                        w.store();
                    } catch (IOException e) {
                        Utils.writeLog(SplashActivity.this, 'e', "ข้อผิดพลาด: "+e.getMessage());
                    }
                } else {
                    Utils.writeLog(SplashActivity.this, 'e', "ไม่มีไฟล์ settings.ini");
                }
            } else {
                handler.postDelayed((Runnable) () -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    if(Preferences.getString(App.getInstance(), Preferences.NICKNAME).isEmpty())
                    {
                        intent = new Intent(this, StartActivity.class);
                    }
                    intent.putExtras(getIntent());
                    startActivity(intent);
                    finish();
                }, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        handler.postDelayed((Runnable) () -> {
            System.out.println("Debug");
            Intent intent = new Intent(this, MainActivity.class);
            if(Preferences.getString(App.getInstance(), Preferences.NICKNAME).isEmpty())
            {
                System.out.println("Debug 1");
                intent = new Intent(this, MainActivity.class);
            }
            intent.putExtras(getIntent());
            startActivity(intent);
            finish();
        }, 5000);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    public void onPermissionsGranted(int requestCode, List<String> list) {
        if(requestCode == MAIN_PERMISSIONS_REQUEST_CODE) {
            Init();
        }
    }
    public void onPermissionsDenied(int requestCode, List<String> list) {
        if(requestCode == MAIN_PERMISSIONS_REQUEST_CODE) {
            Utils.writeLog(SplashActivity.this, 'i', "Lỗi ủy quyền");
            Toasty.error(SplashActivity.this, "Ứng dụng được yêu cầu phải có tất cả các quyền được yêu cầu.!!!", Toast.LENGTH_LONG).show();
            //finish();
        }
    }
}