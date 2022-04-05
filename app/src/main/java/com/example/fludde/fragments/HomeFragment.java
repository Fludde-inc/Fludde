package com.example.fludde.fragments;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.fludde.Post;
import com.example.fludde.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class HomeFragment extends PostFragment{
    @Override
    protected void queryPosts() {
        super.queryPosts();

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER.toString());
        query.whereEqualTo(Post.KEY_USER.toString(), ParseUser.getCurrentUser());
        query.setLimit(20);
        query.addAscendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                allPosts.clear();

                if (e!=null)
                {
                    Log.e("post", "post error");
                }
                for (Post post : posts){

                    Log.i("Post Query", "Post" +
                            post.getDescription());
                }


                        allPosts.addAll(posts);
                        adapter.notifyDataSetChanged();

            }
        });
    }
}
