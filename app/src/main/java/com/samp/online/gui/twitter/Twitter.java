package com.samp.online.gui.twitter;

import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nvidia.devtech.NvEventQueueActivity;
import com.samp.online.R;
import com.samp.online.gui.ImgurUploader;
import com.samp.online.gui.util.LinearLayoutManagerWrapper;
import com.samp.online.gui.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Twitter {
    ConstraintLayout mPhone, mTwitter;
    public List<TwitterData> mTwitterData;
    private TwitterAdapter mTwitterAdapter;
    public RecyclerView mTwitterList;
    public ImageView app_x, show_twitter_post;
    public LinearLayout twitter_post;
    public EditText twitter_inputmsg, twitter_inputurl;
    public Button send, twitter_upload;
    private String textmsg = "";
    private String texturl = "";
    private ActivityResultRegistry registry;
    private ActivityResultLauncher<PickVisualMediaRequest> launcher;
    public LinearLayout back_twitter;
    public View phone_hide;
    public Activity aactivity;
    public Twitter(Activity activity, ActivityResultRegistry registry){
        aactivity = activity;
        mPhone = activity.findViewById(R.id.phone_main);
        mTwitter = activity.findViewById(R.id.main_twitter);
        mTwitterList = (RecyclerView) activity.findViewById(R.id.twitter_list);
        ArrayList arrayList = new ArrayList();
        mTwitterData = arrayList;
        mTwitterAdapter = new TwitterAdapter(arrayList);
        app_x = activity.findViewById(R.id.app_twitter);
        twitter_post = activity.findViewById(R.id.twitter_post);
        show_twitter_post = activity.findViewById(R.id.show_twitter_post);
        twitter_inputurl = activity.findViewById(R.id.twitter_inputurl);
        twitter_inputmsg = activity.findViewById(R.id.twitter_inputmsg);
        send = activity.findViewById(R.id.twitter_send);
        twitter_upload = activity.findViewById(R.id.twitter_upload);
        back_twitter = activity.findViewById(R.id.back_twitter);
        phone_hide = activity.findViewById(R.id.phone_hide);

        phone_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HidePhone();
            }
        });

        back_twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.ShowLayout(mPhone,true);
                Utils.HideLayout(mTwitter,true);
            }
        });

        launcher = registry.register("key", new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri o) {
                if (o == null) {
                    Toast.makeText(activity, "No image Selected", Toast.LENGTH_SHORT).show();
                } else {
//                    Glide.with(context).load(o).into(imageView);
                    uploadImageToImgur(o);
                }
            }
        });

        twitter_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch(new PickVisualMediaRequest.Builder().build());
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textmsg = twitter_inputmsg.getText().toString();
                texturl = twitter_inputurl.getText().toString();
                String message = "/twitterformessage "+textmsg;
                String url = "/twitterforimage "+texturl;
                String all = "/twitterforall "+textmsg+" "+texturl;
                if (TextUtils.isEmpty(twitter_inputurl.getText().toString()) && TextUtils.isEmpty(twitter_inputmsg.getText().toString())) {
                    Toast.makeText(activity.getApplicationContext(), "ยังไม่มีข้อมูล",Toast.LENGTH_SHORT).show();
                } else if (!TextUtils.isEmpty(textmsg) && TextUtils.isEmpty(texturl)) {
                    try {
                        NvEventQueueActivity.getInstance().sendCommand(message.getBytes("UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    twitter_post.setVisibility(View.GONE);
                    twitter_inputurl.setText("");
                    twitter_inputmsg.setText("");
                } else if (!TextUtils.isEmpty(texturl) && TextUtils.isEmpty(textmsg)) {
                    try {
                        NvEventQueueActivity.getInstance().sendCommand(url.getBytes("UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    twitter_post.setVisibility(View.GONE);
                    twitter_inputurl.setText("");
                    twitter_inputmsg.setText("");
                } else {
                    try {
                        NvEventQueueActivity.getInstance().sendCommand(all.getBytes("UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    twitter_post.setVisibility(View.GONE);
                    twitter_inputurl.setText("");
                    twitter_inputmsg.setText("");
                }
            }
        });


        show_twitter_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twitter_post.setVisibility(View.VISIBLE);
            }
        });

        twitter_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                twitter_post.setVisibility(View.GONE);
            }
        });

        app_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.HideLayout(mPhone,true);
                ShowTwitter();
            }
        });



        Utils.HideLayout(mPhone, false);
        Utils.HideLayout(mTwitter, false);
    }

    private File getFileFromUri(Uri uri) throws IOException {
        File file = new File(aactivity.getCacheDir(), "temp_image");
        try (InputStream inputStream = aactivity.getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[4 * 1024]; // 4KB buffer
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
        }
        return file;
    }

    private void uploadImageToImgur(Uri uri) {
        try {
            File file = getFileFromUri(uri);
            ImgurUploader.uploadImage(file, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Toast.makeText(aactivity, "Failed to upload image to Imgur", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        String imageUrl = ImgurUploader.parseImageUrlFromResponse(responseBody);
                        twitter_inputurl.setText(imageUrl);
                    } else {
                        Toast.makeText(aactivity, "Failed to upload image to Imgur: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(aactivity, "Failed to upload image", Toast.LENGTH_SHORT).show();
        }
    }

    public void AddTwitter(String msg, int duration, String url, String playername) {
        this.mTwitterData.add(new TwitterData(msg, duration, url, playername));
        mTwitterAdapter.notifyItemInserted(this.mTwitterData.size() - 1);
        mTwitterList.scrollToPosition(this.mTwitterData.size() - 1);
    }

    public void ShowPhone(){
        Utils.ShowLayout(mPhone,true);
    }

    public void HidePhone(){
        Utils.HideLayout(mPhone,true);
    }

    public void ShowTwitter() {
        mTwitterAdapter = new TwitterAdapter(this.mTwitterData);
        mTwitterAdapter.notifyItemInserted(this.mTwitterData.size() - 1);

        LinearLayoutManager layoutManager = new LinearLayoutManagerWrapper(NvEventQueueActivity.getInstance());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mTwitterList.setLayoutManager(layoutManager);
        mTwitterList.setAdapter(this.mTwitterAdapter);
        mTwitterList.scrollToPosition(-1);  // Scroll to top

        Utils.ShowLayout(mTwitter, true);
    }

}
