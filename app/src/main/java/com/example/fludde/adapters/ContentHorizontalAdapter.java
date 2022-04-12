package com.example.fludde.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fludde.R;
import com.example.fludde.model.BooksContent;
import com.example.fludde.model.Contents;
import com.example.fludde.model.MovieContent;

import java.util.ArrayList;
import java.util.List;

public class ContentHorizontalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
   Context context;
   List<MovieContent> movieContentList;
    List<BooksContent> booksContentList;
    private List<Contents> contentItemList;
    ImageView ivContentImage;
    TextView tvImageContentTitle;

//    public ContentHorizontalAdapter(Context context, List<Content> contentList) {
//        this.context = context;
//        this.contentList = contentList;
//    }
    public ContentHorizontalAdapter(Context context, List<Contents> contentItemList)
    {
        this.context = context;
        this.contentItemList = contentItemList;
    }


//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//        View contentView = LayoutInflater.from(context).inflate(R.layout.content_post_img,parent,false);
//        return new ViewHolder(contentView);
//    }

//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//     Content content = contentList.get(position);
//     holder.bind(content);
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view;

        switch (viewType) {
            case Contents.TYPE_MOVIES:
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_post_img, parent, false);
                return new MovieViewHolder(view);
            case Contents.TYPE_BOOKS:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.book_content_post_img, parent, false);
                return new BookViewHolder(view);
            case Contents.TYPE_MUSIC:

                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.book_content_post_img, parent, false);
                return new MusicViewHolder(view);
        }
//        if (viewType == ITEM_TYPE_MOVIES) {
//           view = layoutInflater.inflate(R.layout.content_post_img, parent, false);
//
//            return new MovieViewHolder(view);
//        } else if (viewType == ITEM_TYPE_BOOKS) {
//           view = layoutInflater.inflate(R.layout.book_content_post_img, parent, false);
//
//            return new BookViewHolder(view);
//        }
//        else {
//          view =  layoutInflater.inflate(R.layout.book_content_post_img, parent, false);
//            return new MusicViewHolder(view);
//        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case Contents.TYPE_MOVIES:
                ((MovieViewHolder) holder).bind(position);
                break;
            case Contents.TYPE_BOOKS:
                ((BookViewHolder) holder).bind(position);
                break;
            case Contents.TYPE_MUSIC:
                ((MusicViewHolder) holder).bind(position);
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {

    return contentItemList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        if (contentItemList == null) {
            return 0;
        } else {
            return contentItemList.size();
        }
    }

    class MovieViewHolder extends  RecyclerView.ViewHolder{

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            ivContentImage = itemView.findViewById(R.id.ivContentImage);
            tvImageContentTitle = itemView.findViewById(R.id.tvImageContentTitle);
        }

        public void bind(int position) {
            MovieContent moviesContent = (MovieContent) contentItemList.get(position);
            tvImageContentTitle.setText(moviesContent.getTitle());

            String imageURL;
            imageURL = moviesContent.getImagePath();
            Glide.with(context).load(imageURL).into(ivContentImage);
        }
    }
    class BookViewHolder extends  RecyclerView.ViewHolder{

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            ivContentImage = itemView.findViewById(R.id.ivContentImage);
            tvImageContentTitle = itemView.findViewById(R.id.tvImageContentTitle);
        }

        public void bind(int position) {

            BooksContent booksContent = (BooksContent) contentItemList.get(position);
            tvImageContentTitle.setText(booksContent.getTitle());

            String imageURL;
            imageURL = booksContent.getImageURL();
//            Glide.with(context).load(imageURL).into(ivContentImage);
        }
    }
    class MusicViewHolder extends  RecyclerView.ViewHolder{

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            ivContentImage = itemView.findViewById(R.id.ivContentImage);
            tvImageContentTitle = itemView.findViewById(R.id.tvImageContentTitle);
        }

        public void bind(int position) {
        }
    }

    //    @Override
//    public int getItemCount() {
//        return contentList.size();
//    }



//    class ViewHolder extends  RecyclerView.ViewHolder{
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            ivContentImage = itemView.findViewById(R.id.ivContentImage);
//            tvImageContentTitle = itemView.findViewById(R.id.tvImageContentTitle);
//
//
//        }
//
//        public void bind(Content content) {
//            tvImageContentTitle.setText(content.getTitle());
//
//            String imageURL;
//            imageURL = content.getImagePath();
//            Glide.with(context).load(imageURL).into(ivContentImage);
//
//        }
//
//    }





}
