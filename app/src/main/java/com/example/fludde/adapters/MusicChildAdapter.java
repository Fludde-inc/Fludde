package com.example.fludde.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fludde.R;
import com.example.fludde.model.MusicContent;
import com.example.fludde.utils.GlideExtensions;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class MusicChildAdapter extends RecyclerView.Adapter<MusicChildAdapter.MusicViewHolder> {

    public interface OnMusicContentListener {
        void onMusicContentClick(int position);
        void onMusicContentLongClick(int position);
    }

    private final Context context;
    private final List<MusicContent> contents;
    private final OnMusicContentListener listener;

    public MusicChildAdapter(Context context, List<MusicContent> contents, OnMusicContentListener listener) {
        this.context = context;
        this.contents = contents;
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override public long getItemId(int position) {
        String key = contents.get(position).getTitle() + "|" + position;
        return key.hashCode();
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.content_post_img, parent, false);
        v.setBackgroundResource(R.drawable.list_item_bg);
        return new MusicViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        holder.bind(contents.get(position));
    }

    @Override
    public int getItemCount() { return contents.size(); }

    class MusicViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView ivPoster;
        private final TextView tvTitle;

        MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivContentImage);
            tvTitle = itemView.findViewById(R.id.tvImageContentTitle);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMusicContentClick(pos);
                }
            });

            itemView.setOnLongClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMusicContentLongClick(pos);
                }
                return true;
            });
        }

        void bind(MusicContent c) {
            tvTitle.setText(c.getTitle());
            // FIXED: Changed getImageURL() to getCoverIMGUrl()
            GlideExtensions.loadPoster(ivPoster, c.getCoverIMGUrl());
        }
    }
}