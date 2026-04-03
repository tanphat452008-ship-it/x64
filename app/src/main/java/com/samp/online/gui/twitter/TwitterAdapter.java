package com.samp.online.gui.twitter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.samp.online.R;

import java.util.List;

public class TwitterAdapter extends RecyclerView.Adapter implements Filterable {

    private List<TwitterData> mTwitterData;
    public TwitterAdapter(List<TwitterData> twitterData) {
        this.mTwitterData = twitterData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_twitter, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindViewHolder((ViewHolder) holder, position);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        TwitterData data = this.mTwitterData.get(position);
        if(data.getUrl() == null){
            holder.url.setVisibility(View.GONE);
        }
        Glide.with(context)
                .load(data.getUrl())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(1040, 1040)
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.url.setVisibility(View.GONE);
                        return true;
                    }

                    @Override public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                    .into(holder.url);
                holder.url.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(data.getMsg())){
                holder.msg.setVisibility(View.GONE);
            } else {
                holder.msg.setText(String.valueOf(data.getMsg()));
            }

            holder.url.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView largeImageView = ((Activity) context).findViewById(R.id.largeImageView);
                    largeImageView.setVisibility(View.VISIBLE);

                    Glide.with(context)
                            .load(data.getUrl())
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .centerCrop()
                            .into(largeImageView);

                    largeImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            largeImageView.setVisibility(View.GONE);
                        }
                    });
                }
            });
            holder.name.setText(String.valueOf(data.getName()));
    }

    @Override
    public int getItemCount() {
        return this.mTwitterData.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public android.view.View mView;
        public ConstraintLayout View;
        public ImageView url;
        public TextView name;
        public TextView msg;
        public ViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            this.url = (ImageView) itemView.findViewById(R.id.image_comment);
            this.name = (TextView) itemView.findViewById(R.id.txt_name);
            this.msg = (TextView) itemView.findViewById(R.id.txt_detail);
        }

        public View getView() {
            return this.mView;
        }
    }
}
