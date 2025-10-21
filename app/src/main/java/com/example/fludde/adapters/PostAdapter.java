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
import com.example.fludde.model.PostUi;
import com.example.fludde.utils.GlideExtensions;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final Context context;
    private final List<PostUi> posts;

    public PostAdapter(Context context, List<PostUi> posts) {
        this.context = context;
        this.posts = posts;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        String key = posts.get(position).getTitle() + "|" + posts.get(position).getUserName() + "|" + position;
        return key.hashCode();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        view.setBackgroundResource(R.drawable.list_item_bg);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() { return posts.size(); }

    class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvContentCategory;
        private final TextView tvContentDescription;
        private final TextView tvContentTitle;
        private final TextView tvUsername;
        private final TextView tvReview;
        private final ImageView ivUserPic;
        private final ImageView ivContent;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContentCategory = itemView.findViewById(R.id.tvContentCategory);
            tvContentDescription = itemView.findViewById(R.id.tvContentDescription);
            tvContentTitle = itemView.findViewById(R.id.tvContentTitle);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvReview = itemView.findViewById(R.id.tvReview);
            ivUserPic = itemView.findViewById(R.id.ivUserPic);
            ivContent = itemView.findViewById(R.id.ivContent);

            itemView.setOnLongClickListener(v -> {
                com.example.fludde.utils.Haptics.longPress(v);
                return false;
            });
        }

        void bind(PostUi post) {
            tvContentCategory.setText(post.getCategory());
            tvContentDescription.setText(post.getDescription());
            tvContentTitle.setText(post.getTitle());
            tvReview.setText(post.getReview());
            tvUsername.setText(post.getUserName());

            GlideExtensions.loadPoster(ivContent, post.getContentImageUrl());
            GlideExtensions.loadAvatar(ivUserPic, post.getUserImageUrl());
        }
    }
}
