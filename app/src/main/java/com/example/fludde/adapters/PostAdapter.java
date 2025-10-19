package com.example.fludde.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fludde.Post;
import com.example.fludde.R;
import com.example.fludde.utils.GlideExtensions;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final Context context;
    private final List<Post> posts;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
        setHasStableIds(true); // stable for jank-free animations
    }

    @Override
    public long getItemId(int position) {
        // Best effort stable id from Parse objectId if available
        try {
            String id = posts.get(position).getObjectId();
            return id != null ? id.hashCode() : position;
        } catch (Exception e) {
            return position;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        // Press/ ripple background on the whole row
        view.setBackgroundResource(R.drawable.list_item_bg);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

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

            // Long-press haptic (discoverability)
            itemView.setOnLongClickListener(v -> {
                com.example.fludde.utils.Haptics.longPress(v);
                return false; // still let context/action propagate
            });
        }

        void bind(Post post) {
            // Defensive null-safety — prevents NPEs if a Post is partially filled.
            String category = safeString(post.getCategory());
            String description = safeString(post.getDescription());
            String title = safeString(post.getContentTitle());
            String review = safeString(post.getReview());

            tvContentCategory.setText(category);
            tvContentDescription.setText(description);
            tvContentTitle.setText(title);
            tvReview.setText(review);

            ParseUser user = null;
            try { user = post.getUser(); } catch (Exception ignore) {}
            String username = user != null && user.getUsername() != null ? user.getUsername() : "";
            tvUsername.setText(username);

            ParseFile contentImage = null;
            try { contentImage = post.getContentImage(); } catch (Exception ignore) {}
            ParseFile userImage = null;
            try { userImage = (user != null) ? user.getParseFile("image") : null; } catch (Exception ignore) {}

            GlideExtensions.loadPoster(ivContent, contentImage != null ? contentImage.getUrl() : null);
            GlideExtensions.loadAvatar(ivUserPic, userImage != null ? userImage.getUrl() : null);
        }

        private String safeString(String s) { return s != null ? s : ""; }
    }
}
