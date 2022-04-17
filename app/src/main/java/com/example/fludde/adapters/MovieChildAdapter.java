package com.example.fludde.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fludde.R;
import com.example.fludde.model.MovieContent;

import java.util.List;

public class MovieChildAdapter extends RecyclerView.Adapter<MovieChildAdapter.MovieViewHolder> {
     Context context;
    private List<MovieContent> movieContentList;
    private  OnMovieContentListener movieContentListener;
    private  int selectedItem = -1;
    private  int currentPosition = -1;

//    private OnItemClickListener listener;

//    public interface  OnItemClickListener{
//        void onItemClick(MovieContent aMovie);
//    }
//    public MovieChildAdapter(Context context, List<MovieContent> movieContentList, OnItemClickListener listener) {
//        this.context = context;
//        this.movieContentList = movieContentList;
//        this.listener = listener;
//    }
public MovieChildAdapter(Context context, List<MovieContent> movieContentList, OnMovieContentListener movieContentListener) {
    this.context = context;
    this.movieContentList = movieContentList;
    this.movieContentListener = movieContentListener;
}

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View movies = LayoutInflater.from(context).inflate(R.layout.content_post_img,parent,false);
        return new MovieViewHolder(movies,movieContentListener);
    }

    @Override
//    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
//        MovieContent movieContent = movieContentList.get(position);
//        holder.bind(movieContent,listener);
//
//    }
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position)  {
        MovieContent movieContent = movieContentList.get(position);



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
                holder.onMovieContentListener.onMovieContentClick(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
        holder.bind(movieContent);

    }

    @Override
    public int getItemCount() {
        return movieContentList.size();
    }


    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView ivContentImage;
        TextView tvImageContentTitle;
        CardView cvContentPostLayout;
        OnMovieContentListener onMovieContentListener;


        public MovieViewHolder(@NonNull View itemView, OnMovieContentListener onMovieContentListener) {
            super(itemView);
            ivContentImage = itemView.findViewById(R.id.ivContentImage);
            tvImageContentTitle = itemView.findViewById(R.id.tvImageContentTitle);
            cvContentPostLayout = itemView.findViewById(R.id.cvContentPostLayout);
            this.onMovieContentListener = onMovieContentListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        public void bind(final MovieContent movieContent) {
            tvImageContentTitle.setText(movieContent.getTitle());

//            String imageURL;
//            imageURL = aMovie.getImagePath();
            Glide.with(context).load(movieContent.getImagePath()).into(ivContentImage);

//        public void bind(final MovieContent aMovie, final OnItemClickListener listener) {
//            tvImageContentTitle.setText(aMovie.getTitle());
//
////            String imageURL;
////            imageURL = aMovie.getImagePath();
//            Glide.with(context).load(aMovie.getImagePath()).into(ivContentImage);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    listener.onItemClick(aMovie);
//                }
//            });

        }

        @Override
        public void onClick(View view) {


            notifyDataSetChanged();

        }

        @Override
        public boolean onLongClick(View view) {
                if(onMovieContentListener != null)
            {onMovieContentListener.onMovieContentLongClick(getAdapterPosition());}

            return true;
        }
    }
    public interface OnMovieContentListener{
    void onMovieContentClick(int position);
    void onMovieContentLongClick(int position);
    }
}
