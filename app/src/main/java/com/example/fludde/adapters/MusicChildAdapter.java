package com.example.fludde.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fludde.R;
import com.example.fludde.model.MusicContent;
import com.example.fludde.utils.GlideExtensions;
import com.example.fludde.utils.Haptics;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class MusicChildAdapter extends RecyclerView.Adapter<MusicChildAdapter.MusicViewHolder> {

    private final Context context;
    private final List<MusicContent> items;
    private final OnMusicContentListener listener;

    private int selectedPos = RecyclerView.NO_POSITION;

    public MusicChildAdapter(@NonNull Context context,
                             @NonNull List<MusicContent> items,
                             @NonNull OnMusicContentListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        String key = items.get(position).getTitle() + items.get(position).getArtist() + position;
        return key.hashCode();
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.music_content_post, parent, false);
        v.setBackgroundResource(R.drawable.list_item_bg);
        return new MusicViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        holder.bind(items.get(position), position == selectedPos);
    }

    @Override
    public int getItemCount() { return items.size(); }

    class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView ivAlbumCoverImage;
        TextView tvSongTitle;
        TextView tvArtistName;

        MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAlbumCoverImage = itemView.findViewById(R.id.ivAlbumCoverImage);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void bind(MusicContent data, boolean selected) {
            tvSongTitle.setText(data.getTitle());
            tvArtistName.setText(data.getArtist());
            GlideExtensions.loadSquare(ivAlbumCoverImage, data.getCoverIMGUrl());

            MaterialCardView card = (MaterialCardView) itemView;
            card.setChecked(selected);
            itemView.setSelected(selected);
        }

        @Override
        public void onClick(View v) {
            int pos = getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            int old = selectedPos;
            selectedPos = pos;
            if (old != RecyclerView.NO_POSITION) notifyItemChanged(old);
            notifyItemChanged(selectedPos);

            Haptics.tick(v);
            if (listener != null) listener.onMusicContentClick(pos);
        }

        @Override
        public boolean onLongClick(View v) {
            int pos = getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                Haptics.longPress(v);
                listener.onMusicContentLongClick(pos);
                return true;
            }
            return false;
        }
    }

    public interface OnMusicContentListener {
        void onMusicContentClick(int position);
        void onMusicContentLongClick(int position);
    }
}
