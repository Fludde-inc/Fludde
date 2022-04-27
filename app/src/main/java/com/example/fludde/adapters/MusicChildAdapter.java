package com.example.fludde.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fludde.R;
import com.example.fludde.model.MusicContent;

import java.util.List;

public class MusicChildAdapter extends RecyclerView.Adapter<MusicChildAdapter.MusicViewHolder> {

    Context context;
    private List<MusicContent> musicContentList;
    private OnMusicContentListener musicContentListener;
    private  int selectedItem = -1;


    public MusicChildAdapter(Context context, List<MusicContent> musicContentList, OnMusicContentListener musicContentListener) {
        this.context = context;
        this.musicContentList = musicContentList;
        this.musicContentListener = musicContentListener;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View music = LayoutInflater.from(context).inflate(R.layout.music_content_post,parent,false);
        return new MusicViewHolder(music,musicContentListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        MusicContent musicContent = musicContentList.get(position);

        if(selectedItem == holder.getAdapterPosition())     //add the position to
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#33F5FF")); //Color to change the selected Item too


        }
        else
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff")); //change it back to write if its not click

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItem=holder.getAdapterPosition();
                holder.onMusicContentListener.onMusicContentClick(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
        holder.bind(musicContent);


    }

    @Override
    public int getItemCount() {
        return musicContentList.size();
    }


    class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        OnMusicContentListener onMusicContentListener;
        ImageView ivAlbumCoverImage;
        TextView tvSongTitle;
        TextView tvArtistName;


        public MusicViewHolder(@NonNull View itemView, OnMusicContentListener onMusicContentListener) {
            super(itemView);

            ivAlbumCoverImage = itemView.findViewById(R.id.ivAlbumCoverImage);
            tvSongTitle = itemView.findViewById(R.id.tvSongTitle);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            this.onMusicContentListener =onMusicContentListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }



        public void bind(MusicContent musicContent) {
            tvSongTitle.setText(musicContent.getTitle());
            tvArtistName.setText(musicContent.getArtist());


            Glide.with(context).load(musicContent.getCoverIMGUrl()).into(ivAlbumCoverImage);


        }
        @Override
        public void onClick(View view) {
            notifyDataSetChanged();
        }

        @Override
        public boolean onLongClick(View view) {
            return false;
        }


    }

    public interface OnMusicContentListener{
        void onMusicContentClick(int position);
        void onMusicContentLongClick(int position);
    }
}
