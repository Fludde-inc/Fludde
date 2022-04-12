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
import com.example.fludde.Post;
import com.example.fludde.R;
import com.parse.ParseFile;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    private Context context;
    private List<Post> posts;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      Post post = posts.get(position);
      holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvContentCategory;
        private TextView tvContentDescription;
        private TextView tvContentTitle;
        private TextView tvUsername;
        private TextView tvReview;
        private ImageView ivUserPic;
        private ImageView ivContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvContentCategory = itemView.findViewById(R.id.tvContentCategory);
            tvContentDescription = itemView.findViewById(R.id.tvContentDescription);
            tvContentTitle = itemView.findViewById(R.id.tvContentTitle);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvReview = itemView.findViewById(R.id.tvReview);
            ivUserPic = itemView.findViewById(R.id.ivUserPic);
            ivContent = itemView.findViewById(R.id.ivContent);

        }

        public void bind(Post post) {

            tvContentCategory.setText(post.getCategory());
            tvContentDescription.setText(post.getDescription());
            tvContentTitle.setText(post.getContentTitle());
            tvUsername.setText(post.getUser().getUsername());
            tvReview.setText(post.getReview());

            ParseFile contentImage = post.getContentImage();

            ParseFile userImage = post.getUser().getParseFile("image"); // check to see if the current user has a login picture


            if(contentImage != null) {
                Glide.with(context).load(post.getContentImage().getUrl()).into(ivContent);
            }

            if (userImage != null){
                Glide.with(context).load(post.getUser().getParseFile("image").getUrl()).into(ivUserPic); //get current login user picture
            }



        }
    }
}
