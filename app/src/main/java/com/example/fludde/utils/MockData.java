package com.example.fludde.utils;

import com.example.fludde.model.PostUi;
import com.example.fludde.model.UserUi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** Tiny factory of offline mock data for UI rendering. */
public final class MockData {
    private MockData() {}

    public static JSONObject tmdbTrendingJson() throws Exception {
        JSONArray results = new JSONArray();

        results.put(new JSONObject()
                .put("poster_path", "kqjL17yufvn9OVLyXYpvtyrFfak.jpg")
                .put("backdrop_path", "lNyLSOKMMeUPr1RsL4KcRuIXwHt.jpg")
                .put("title", "Mock: The Lost City")
                .put("overview", "Two brilliant adventurers embark on a mock journey."));

        results.put(new JSONObject()
                .put("poster_path", "6DrHO1jr3qVrViUO6s6kFiAGM7.jpg")
                .put("backdrop_path", "wcKFYIiVDvRURrzglV9kGu7fpfY.jpg")
                .put("title", "Mock: Night Patrol")
                .put("overview", "A stylish mystery that totally doesn't require an API key."));

        results.put(new JSONObject()
                .put("poster_path", "t6HIqrRAclMCA60NsSmeqe9RmNV.jpg")
                .put("backdrop_path", "s16H6tpK2utvwDtzZ8Qy4qm5Emw.jpg")
                .put("title", "Mock: Ocean Deep")
                .put("overview", "An epic dive into mock data excellence."));

        JSONObject payload = new JSONObject();
        payload.put("results", results);
        return payload;
    }

    public static JSONObject itunesSearchJson() throws Exception {
        JSONArray results = new JSONArray();

        results.put(new JSONObject()
                .put("name", "Mock Song One")
                .put("artistName", "The Mockers")
                .put("artworkUrl100", "https://picsum.photos/200?image=1069"));

        results.put(new JSONObject()
                .put("name", "Dreams in JSON")
                .put("artistName", "Null & Void")
                .put("artworkUrl100", "https://picsum.photos/200?image=1027"));

        results.put(new JSONObject()
                .put("name", "Offline Anthem")
                .put("artistName", "Airplane Mode")
                .put("artworkUrl100", "https://picsum.photos/200?image=1003"));

        JSONObject payload = new JSONObject();
        payload.put("results", results);
        return payload;
    }

    public static List<PostUi> mockPosts() {
        List<PostUi> list = new ArrayList<>();

        list.add(new PostUi(
                "Movie",
                "Buddy cops wreak havoc while being lovable.",
                "Bad Boys II",
                "A classic turn-your-brain-off action flick.",
                // poster
                "https://image.tmdb.org/t/p/w342/yCvB5fG5aEPqa1St7ihY6KEAsHD.jpg",
                // user
                "johndoe2016",
                "https://i.pravatar.cc/150?img=12"
        ));

        list.add(new PostUi(
                "Book",
                "A sweeping mock fantasy adventure across lands unknown.",
                "The Winds of JSON",
                "Compelling world-building; waiting for the next chapter…",
                "https://picsum.photos/342/513?image=1067",
                "mockreader",
                "https://i.pravatar.cc/150?img=3"
        ));

        list.add(new PostUi(
                "Music",
                "A synthwave album that makes you want to code.",
                "Neon Arrays",
                "Track 3 slaps. Would recommend for late-night sessions.",
                "https://picsum.photos/342/513?image=1039",
                "airplanemode",
                "https://i.pravatar.cc/150?img=20"
        ));

        return list;
    }

    public static List<UserUi> mockUsers(String query) {
        String q = query == null ? "" : query.toLowerCase();
        List<UserUi> base = new ArrayList<>();
        base.add(new UserUi("johndoe2016", "https://i.pravatar.cc/150?img=12"));
        base.add(new UserUi("janedoe", "https://i.pravatar.cc/150?img=5"));
        base.add(new UserUi("mockreader", "https://i.pravatar.cc/150?img=3"));
        base.add(new UserUi("airplanemode", "https://i.pravatar.cc/150?img=20"));
        base.add(new UserUi("nullpointer", "https://i.pravatar.cc/150?img=8"));

        if (q.isEmpty()) return base;
        List<UserUi> filtered = new ArrayList<>();
        for (UserUi u : base) {
            if (u.getUsername().toLowerCase().contains(q)) filtered.add(u);
        }
        return filtered;
    }
}
