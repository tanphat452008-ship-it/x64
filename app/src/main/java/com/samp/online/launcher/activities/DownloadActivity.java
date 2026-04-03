package com.samp.online.launcher.activities;

import static com.samp.online.Config.GAME_PATH;
import static com.samp.online.Config.PATH_DOWNLOADS;
import static com.samp.online.Utils.download;
import static com.samp.online.Utils.filesListArrayList;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.StatFs;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.PointerIconCompat;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.akexorcist.roundcornerprogressbar.indeterminate.IndeterminateCenteredRoundCornerProgressBar;
//import com.hzy.libp7zip.P7ZipApi;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;

import org.ini4j.Wini;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ExecutionException;

import es.dmoral.toasty.Toasty;
import com.samp.online.App;
import com.samp.online.BuildConfig;
import com.samp.online.R;
import com.samp.online.Utils;
import com.samp.online.launcher.Preferences;
import com.samp.online.launcher.network.FilesList;

public class DownloadActivity extends AppCompatActivity {
    private TextView downloadText, downloadPercent;
    private RoundCornerProgressBar downloadProgress;
    private IndeterminateCenteredRoundCornerProgressBar unZipProgress;

    public int taskDownload = 0;
    private Handler handler;

    public int downloadId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(this, config);
        handler = new Handler(Looper.getMainLooper());
        setContentView(R.layout.activity_load);
        downloadProgress = findViewById(R.id.progressView);
        unZipProgress = findViewById(R.id.progressBarUnzip);
        downloadText = findViewById(R.id.textView2);
        downloadPercent = findViewById(R.id.textView15);
        /*long freeMemory = getFreeMemory();
        if (freeMemory < 3000) {
            handler.postDelayed(() -> {
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.item_dialog_settings);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.background_dialog_full);
                dialog.getWindow().setLayout(-1, -2);
                ((TextView) dialog.findViewById(R.id.message)).setText("Bạn sắp hết bộ nhớ và không thể cài đặt. (Tối thiểu: 3,5 GB)");
                ((TextView) dialog.findViewById(R.id.ok)).setOnClickListener(view1 -> {
                    view1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.button_click));
                    handler.postDelayed(() -> dialog.dismiss(), 200);
                });
                dialog.show();
            }, 200);
            Utils.writeLog(this, 'i', "Bộ nhớ thấp");
            return;
        }*/
        //
        if(App.getInstance().downloadID == null) {
            Utils.writeLog(this, 'e', "error, downloadid is null");
            return;
        }
        if(App.getInstance().downloadID != App.INSTALL_TYPE_GAMEFILES) {
            startDownload(App.getInstance().downloadID);
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    handler.postDelayed(() -> {
                        try {
                            downloadGameFile(0);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }, 2000);
            }
        }
    }
    private boolean isCompletelyWritten(File file) {
        RandomAccessFile stream = null;
        try {
            stream = new RandomAccessFile(file, "rw");
            return true;
        } catch (Exception e) {
            System.out.println("Skipping file " + file.getName() + " for this iteration due it's not completely written");
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    System.out.println("Exception during closing file " + file.getName());
                }
            }
        }
        return false;
    }
    @SuppressLint("SetTextI18n")
    private void downloadGameFile(int task) throws IOException, InterruptedException {
        AsyncTaskRunner runner = new AsyncTaskRunner();
        String sleepTime = String.valueOf(task);
        runner.execute(sleepTime);
    }
    private void finishDownloadGameFile() {
        File settings = new File(GAME_PATH+"SAMP/settings.ini");
        if(settings.exists()) {
            try {
                Wini w = new Wini(settings);
                w.put("client", "name", Preferences.getString(DownloadActivity.this, Preferences.NICKNAME));
                w.store();
            } catch (IOException e) {
                Utils.writeLog(DownloadActivity.this, 'e', "ข้อผิดพลาด: "+e.getMessage());
            }
        }
        Toasty.success(DownloadActivity.this, "Đã cài đặt trò chơi!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(DownloadActivity.this, MainActivity.class);
        intent.putExtras(getIntent());
        startActivity(intent);
        finish();
    }
    private long getFreeMemory() {
        try {
            StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
            return (statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong()) / 1048576;
        } catch (Exception unused) {
            return 268435455;
        }
    }

    private void startDownload(final int id)
    {
        try {
            clearDownloadsPath();
            downloadId = createDownloadTask(id).start();
        } catch (Exception e) {
            Utils.writeLog(DownloadActivity.this, 'e', "Lỗi khi bắt đầu tải xuống:" + e.getMessage());
        }
    }

    private BaseDownloadTask createDownloadTask(final int position)
    {
        String url = "";
        boolean isDir = false;
        String path = "null";

        switch (position) {
            case 1:
                url = App.getInstance().URL_CLIENT;
                path = PATH_DOWNLOADS + "client.apk";
                break;
            case 2:
                url = App.getInstance().URL_GAME_FILES;
                path = PATH_DOWNLOADS + "game.zip";
                break;
            case 3:
                url = App.getInstance().URL_GAME_FILES_UPDATE;
                path = PATH_DOWNLOADS + "files_upd.zip";
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + position);
        }

        return FileDownloader.getImpl().create(url).
                setPath(path, false).
                setCallbackProgressTimes(300).
                setMinIntervalUpdateSpeed(400).
                setListener(new FileDownloadSampleListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.pending(task, soFarBytes, totalBytes);
                    }
                    @SuppressLint("SetTextI18n")
                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.progress(task, soFarBytes, totalBytes);
                        final float percent = soFarBytes / (float) totalBytes;
                        downloadProgress.setMax(100);
                        downloadProgress.setProgress((int) (percent * 100));
                        downloadText.setText("Đang tải tập tin trò chơi...");
                        downloadPercent.setText((int) (percent * 100)+"%");
                        createNotification("Đang tải tập tin trò chơi... - "+(int) (percent * 100)+"%\n" +
                                Utils.formatFileSize(soFarBytes) + "/" +
                                Utils.formatFileSize(totalBytes) + " tốc độ: " +
                                Utils.formatFileSize(task.getSpeed() * 1024));
                        if(position == 1) {
                            downloadText.setText("Cập nhật trình khởi chạy...");
                            downloadPercent.setText((int) (percent * 100)+"%");
                            createNotification("Cập nhật trình khởi chạy... - "+(int) (percent * 100)+"%\n" +
                                    Utils.formatFileSize(soFarBytes) + "/" +
                                    Utils.formatFileSize(totalBytes) + " ความเร็ว: " +
                                    Utils.formatFileSize(task.getSpeed() * 1024));
                        }
                        if(position == 3) {
                            downloadText.setText("Cập nhật tập tin trò chơi...");
                            downloadPercent.setText((int) (percent * 100)+"%");
                            createNotification("Cập nhật tập tin trò chơi... - "+(int) (percent * 100)+"%\n" +
                                    Utils.formatFileSize(soFarBytes) + "/" +
                                    Utils.formatFileSize(totalBytes) + " tốc độ: " +
                                    Utils.formatFileSize(task.getSpeed() * 1024));
                        }
                        if(position == 4) {
                            downloadText.setText("Đang tải đồ họa...");
                            downloadPercent.setText((int) (percent * 100)+"%");
                            createNotification("Tải xuống đồ họa cập nhật... - "+(int) (percent * 100)+"%\n" +
                                    Utils.formatFileSize(soFarBytes) + "/" +
                                    Utils.formatFileSize(totalBytes) + " tốc độ: " +
                                    Utils.formatFileSize(task.getSpeed() * 1024));
                        }
                    }
                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        Utils.writeLog(DownloadActivity.this, 'e', "Lỗi FileDownloader: " + e);
                        Toasty.error(DownloadActivity.this, "Lỗi: "+e, Toast.LENGTH_LONG).show();
                    }
                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                        Utils.setDownloading(true);
                    }
                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.paused(task, soFarBytes, totalBytes);
                        Utils.setDownloading(false);
                    }
                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
                        if(position == 1) {
                            Toasty.info(DownloadActivity.this, "Xác nhận cài đặt").show();
                            Utils.setDownloading(false);
                            installAPK("client");
                        }
                        else if(position == 2) {
                            downloadText.setText("Giải nén tập tin...");
                            UnZipZip("game");
                        }
                        else if (position == 3) {
                            downloadText.setText("Trích xuất tập tin cập nhật...");
                            String filePath = GAME_PATH + "data/ver.ini";
                            String sectionHeader1 = "[versions]\ngameFilesVersion" + " = " + App.getInstance().targetGameFilesVersion.toString();
                            writeFile(filePath, sectionHeader1);
                            UnZipZip("files_upd");
                        }
                        else if (position == 4) {
                            downloadText.setText("Trích xuất tập tin đồ họa...");
                            UnZipZip("files_graph");
                        }
                    }
                    @Override
                    protected void warn(BaseDownloadTask task) { super.warn(task); }
                });
    }

    public void UnZipZip(final String zipname)
    {
        UnZipTask unzipTask = new UnZipTask(this);
        unzipTask.execute(PATH_DOWNLOADS, GAME_PATH, zipname+".zip");
    }

    public static void writeFile(String path, String str)
    {
        File file = new File(path);
        try { if (!file.exists()) file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File(path), false);
            fileWriter.write(str);
            fileWriter.flush();
        } catch (IOException e) { e.printStackTrace(); } finally { try { if (fileWriter != null) fileWriter.close(); } catch (IOException e) { e.printStackTrace(); }}
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        private FilesList file;
        private int debugVersion = 1;
        @Override
        protected String doInBackground(String... params) {
            taskDownload = Integer.parseInt(params[0]);
            FilesList info = filesListArrayList.get(taskDownload);

            // Danh sách các tệp cần kiểm tra trước khi tải
            String[] filesToCheck = {
                    "Adjustable.cfg", "gta_sa.set", "gta3.cfg",
                    "gtastelem.set", "KeyboardMappings.cfg", "logcat.txt",
                    "scache.txt", "scache_small.txt", "scache_small_low.txt", "stream.cfg"
            };

            // Kiểm tra xem tệp hiện tại có trong danh sách không
            for (String fileToCheck : filesToCheck) {
                if (info.getName().equals(fileToCheck) ||
                        info.getPath().endsWith("/" + fileToCheck) ||
                        info.getPath().endsWith("\\" + fileToCheck)) {

                    // Kiểm tra xem tệp đã tồn tại chưa
                    File existingFile = new File(info.getPath());
                    if (existingFile.exists() && existingFile.length() > 0) {
                        // Tệp đã tồn tại, bỏ qua việc tải xuống
                        System.out.println(fileToCheck + " đã tồn tại, bỏ qua tải xuống");
                        // Chuyển sang tệp tiếp theo
                        return "FILE_EXISTS";
                    }
                    break;
                }
            }

            // Nếu không phải là Adjustable.cfg hoặc tệp chưa tồn tại, tiếp tục tải
            File file = new File(info.getPath());
            try {
                publishProgress(String.valueOf(0));
                System.out.println("Downloading: " + info.getUrl());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && debugVersion == 0) {
                    download(info.getUrl(), file);
                }
                else {
                    URL url = new URL(info.getUrl());
                    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                    httpConn.setRequestMethod("POST");
                    int fileLength = httpConn.getContentLength();
                    InputStream is = httpConn.getInputStream();
                    OutputStream os = new FileOutputStream(file);
                    byte buffer[] = new byte[256000];
                    long totalDownloaded = 0;
                    int count;

                    while ((count = is.read(buffer)) != -1) {
                        totalDownloaded += count;
                        publishProgress(String.valueOf((int) (totalDownloaded * 100 / fileLength)));
                        os.write(buffer, 0, count);
                    }

                    os.flush();
                    os.close();
                    is.close();
                }
                System.out.println("Downloaded: " + info.getName());

            } catch (MalformedURLException e) {
                System.out.println("Invalid URL");
            } catch (IOException e) {
                System.err.println("Error downloading " + info.getUrl() + ": " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... text) {
            FilesList info = filesListArrayList.get(taskDownload);
            Utils.setDownloading(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && debugVersion == 0) {
                downloadProgress.setMax(filesListArrayList.size());
                downloadProgress.setProgress((int) (taskDownload));
            }
            else {
                downloadProgress.setMax(100);
                downloadProgress.setProgress(Integer.parseInt(text[0]));
            }
            downloadText.setText("Đang tải: " + info.getName());
            downloadPercent.setText(""+(taskDownload + 1) + "/" + (filesListArrayList.size()));
        }

        @Override
        protected void onPostExecute(String result) {
            Utils.setDownloading(false);
            downloadProgress.setProgress(0);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();

            // Nếu tệp đã tồn tại hoặc tải xuống hoàn tất, chuyển sang tệp tiếp theo
            if(taskDownload < filesListArrayList.size() - 1) {
                try {
                    downloadGameFile(taskDownload + 1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                filesListArrayList.clear();
                Utils.setDownloading(false);
                downloadProgress.setProgress(0);
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
                finishDownloadGameFile();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    private class UnZipTask extends AsyncTask<String, Integer, String> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public UnZipTask(Context context) { this.context = context; }
        @Override
        protected String doInBackground(String... params) {
            String filePath = params[0];
            String destinationPath = params[1];
            String fileName = params[2];
            launcher.samp.game.gui.DecompressFast df= new launcher.samp.game.gui.DecompressFast(filePath+fileName, destinationPath);
            df.unzip();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Utils.setDownloading(true);
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();
            downloadPercent.setVisibility(View.GONE);
            downloadProgress.setVisibility(View.GONE);
            unZipProgress.setVisibility(View.VISIBLE);
            downloadText.setText("Các tâp tin trò chơi đang được giải nén...");
            createNotification("Các tâp tin trò chơi đang được giải nén...");
        }

        @Override
        protected void onProgressUpdate(Integer... progress) { super.onProgressUpdate(progress); }

        @Override
        protected void onPostExecute(String result) {
            // окончание распаковки
            mWakeLock.release();
            clearDownloadsPath();
            Utils.setDownloading(false);
            downloadProgress.setProgress(0);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
            if (result != null) {
                Toasty.error(DownloadActivity.this, "Đã xảy ra lỗi khi giải nén tập tin: " + result, Toast.LENGTH_SHORT).show();
            } else {
                File settings = new File(GAME_PATH+"SAMP/settings.ini");
                if(settings.exists()) {
                    try {
                        Wini w = new Wini(settings);
                        w.put("client", "name", Preferences.getString(DownloadActivity.this, Preferences.NICKNAME));
                        w.store();
                    } catch (IOException e) {
                        Utils.writeLog(DownloadActivity.this, 'e', "ข้อผิดพลาด: "+e.getMessage());
                    }
                }
                Toasty.success(DownloadActivity.this, "Đã cài đặt trò chơi!", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(DownloadActivity.this, MainActivity.class);
            intent.putExtras(getIntent());
            startActivity(intent);
            finish();
        }

        private void mkdirs(java.io.File outdir, String path)
        {
            java.io.File d = new java.io.File(outdir, path);
            if(!d.exists()) d.mkdirs();
        }

        private String dirpart(String name)
        {
            int s = name.lastIndexOf(java.io.File.separatorChar);
            return s == -1 ? null : name.substring(0, s);
        }
    }

    private void installAPK(String apkname) {
        try {
            File file = new File(PATH_DOWNLOADS, apkname + ".apk");
            Intent intent;
            if (file.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri apkUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
                    intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(apkUri);
                } else {
                    Uri apkUri = Uri.fromFile(file);
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intent);
            }
        } catch (Exception e) {
            Utils.writeLog(this, 'e', "Lỗi cài đặt:" + e.getMessage());
        }
    }

    private void clearDownloadsPath()
    {
        File download_path = new File(PATH_DOWNLOADS);
        if (!download_path.exists()) {
            download_path.mkdirs();
        } else {
            File client = new File(PATH_DOWNLOADS, "client.apk");
            if(client.exists()) client.delete();
            File launcher = new File(PATH_DOWNLOADS, "launcher.apk");
            if(launcher.exists()) launcher.delete();
            File gameFiles = new File(PATH_DOWNLOADS, "game.zip");
            if(gameFiles.exists()) gameFiles.delete();
            File filesUpd = new File(PATH_DOWNLOADS, "files_upd.zip");
            if(filesUpd.exists()) filesUpd.delete();
            File filesGraph = new File(PATH_DOWNLOADS, "files_graph.zip");
            if(filesGraph.exists()) filesGraph.delete();
        }
    }

    @SuppressLint("RestrictedApi")
    public void createNotification(String str) {
        NotificationManager notifManager = null;
        NotificationCompat.Builder builder = null;
        Intent intent;
        PendingIntent pendingIntent;
        notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notifManager.getNotificationChannel("downloading_channel_1") == null) {
                NotificationChannel notificationChannel = new NotificationChannel("downloading_channel_1", "Đang tải", NotificationManager.IMPORTANCE_LOW);
                notificationChannel.setDescription("Đang tải xuống và giải nén");
                notificationChannel.enableVibration(false);
                notificationChannel.setLightColor(SupportMenu.CATEGORY_MASK);
                notificationChannel.setImportance(NotificationManager.IMPORTANCE_LOW); // IMPORTANCE_HIGH
                notificationChannel.setVibrationPattern(new long[]{0});
                notifManager.createNotificationChannel(notificationChannel);
            }
            builder = new NotificationCompat.Builder(this, "downloading_channel_1");
            intent = new Intent(this, getClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            builder.setContentTitle("Square Studio").setSmallIcon(R.mipmap.ic_launcher_round).setVibrate(new long[]{0}).setContentText(str).setAutoCancel(true).setContentIntent(pendingIntent).setTicker("Square Studio").setOnlyAlertOnce(true).setOngoing(true);
        } else {
            intent = new Intent(this, getClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            builder.setContentTitle("Square Studio").setSmallIcon(R.mipmap.ic_launcher_round).setVibrate(new long[]{0}).setContentText(str).setAutoCancel(true).setContentIntent(pendingIntent).setTicker("Square Studio").setOnlyAlertOnce(true).setOngoing(true).setPriority(1);
        }
        notifManager.notify(PointerIconCompat.TYPE_HAND, builder.build());
    }
}
