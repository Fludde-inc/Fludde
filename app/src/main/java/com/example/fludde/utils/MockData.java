package com.example.fludde.utils;

import com.example.fludde.model.PostUi;
import com.example.fludde.model.UserUi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Tiny factory of offline mock data for UI rendering. */
public final class MockData {
    private MockData() {}

    public static JSONObject tmdbTrendingJson() throws Exception {
        JSONArray results = new JSONArray();

        results.put(new JSONObject()
                .put("poster_path", "https://picsum.photos/500/750?random=1")
                .put("backdrop_path", "lNyLSOKMMeUPr1RsL4KcRuIXwHt.jpg")
                .put("title", "Mock: The Lost City")
                .put("overview", "Two brilliant adventurers embark on a mock journey."));

        results.put(new JSONObject()
                .put("poster_path", "https://picsum.photos/500/750?random=2")
                .put("backdrop_path", "wcKFYIiVDvRURrzglV9kGu7fpfY.jpg")
                .put("title", "Mock: Night Patrol")
                .put("overview", "A stylish mystery that totally doesn't require an API key."));

        results.put(new JSONObject()
                .put("poster_path", "https://picsum.photos/500/750?random=3")
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

        // Fixed: Added contentImageUrl parameter (7 parameters total)
        list.add(new PostUi(
                "Movie",
                "Buddy cops wreak havoc while being lovable.",
                "Bad Boys II",
                "A classic turn-your-brain-off action flick.",
                "https://image.tmdb.org/t/p/w500/kqjL17yufvn9OVLyXYpvtyrFfak.jpg", // contentImageUrl
                "demo",
                "https://i.pravatar.cc/150?img=1"
        ));

        list.add(new PostUi(
                "Book",
                "Wizards, prophecies, and teenage angst—the whole package.",
                "Harry Potter and the Order of the Phoenix",
                "JK Rowling really keeps you guessing!",
                "https://picsum.photos/200/300?image=1020", // contentImageUrl
                "alice",
                "https://i.pravatar.cc/150?img=4"
        ));

        list.add(new PostUi(
                "Music",
                "This song speaks to my soul. Pure emotion.",
                "Someone Like You",
                "Adele never disappoints.",
                "https://picsum.photos/200?image=1069", // contentImageUrl
                "bob",
                "https://i.pravatar.cc/150?img=6"
        ));

        return list;
    }

    public static List<UserUi> mockUsers() {
        List<UserUi> list = new ArrayList<>();
        list.add(new UserUi("demo", "https://i.pravatar.cc/150?img=1"));
        list.add(new UserUi("testuser", "https://i.pravatar.cc/150?img=2"));
        list.add(new UserUi("alice", "https://i.pravatar.cc/150?img=4"));
        list.add(new UserUi("bob", "https://i.pravatar.cc/150?img=6"));
        list.add(new UserUi("charlie", "https://i.pravatar.cc/150?img=7"));
        return list;
    }

    // ========== BOOKS MOCK DATA ==========

    public static JSONObject googleBooksJson() throws Exception {
        JSONArray items = new JSONArray();
        
        items.put(createBookItem(
            "Mock: The Great Gatsby",
            new String[]{"F. Scott Fitzgerald"},
            "https://picsum.photos/200/300?image=1011",
            "A classic American novel set in the Jazz Age."
        ));
        
        items.put(createBookItem(
            "Mock: 1984",
            new String[]{"George Orwell"},
            "https://picsum.photos/200/300?image=1015",
            "A dystopian social science fiction novel."
        ));
        
        items.put(createBookItem(
            "Mock: To Kill a Mockingbird",
            new String[]{"Harper Lee"},
            "https://picsum.photos/200/300?image=1020",
            "A novel about racial injustice in the Deep South."
        ));
        
        items.put(createBookItem(
            "Mock: Pride and Prejudice",
            new String[]{"Jane Austen"},
            "https://picsum.photos/200/300?image=1025",
            "A romantic novel of manners."
        ));
        
        items.put(createBookItem(
            "Mock: The Catcher in the Rye",
            new String[]{"J.D. Salinger"},
            "https://picsum.photos/200/300?image=1030",
            "A story about teenage rebellion and angst."
        ));
        
        JSONObject payload = new JSONObject();
        payload.put("items", items);
        return payload;
    }

    private static JSONObject createBookItem(String title, String[] authors, 
                                             String imageUrl, String description) 
                                             throws Exception {
        JSONArray authorsArray = new JSONArray();
        for (String author : authors) {
            authorsArray.put(author);
        }
        
        JSONObject imageLinks = new JSONObject();
        imageLinks.put("thumbnail", imageUrl);
        
        JSONObject volumeInfo = new JSONObject();
        volumeInfo.put("title", title);
        volumeInfo.put("authors", authorsArray);
        volumeInfo.put("imageLinks", imageLinks);
        volumeInfo.put("description", description);
        volumeInfo.put("publishedDate", "2024");
        
        JSONObject item = new JSONObject();
        item.put("volumeInfo", volumeInfo);
        return item;
    }

    // ========== AUTHENTICATION MOCK DATA ==========

    public static class MockUser {
        public final String username;
        public final String password;
        public final String email;
        public final String profilePictureUrl;
        
        public MockUser(String username, String password, String email, String profilePictureUrl) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.profilePictureUrl = profilePictureUrl;
        }
    }

    private static final List<MockUser> MOCK_AUTH_USERS = new ArrayList<>(Arrays.asList(
        new MockUser("demo", "demo123", "demo@example.com", "https://i.pravatar.cc/150?img=1"),
        new MockUser("testuser", "test123", "test@example.com", "https://i.pravatar.cc/150?img=2"),
        new MockUser("alice", "alice123", "alice@example.com", "https://i.pravatar.cc/150?img=4"),
        new MockUser("bob", "bob123", "bob@example.com", "https://i.pravatar.cc/150?img=6"),
        new MockUser("charlie", "charlie123", "charlie@example.com", "https://i.pravatar.cc/150?img=7")
    ));

    public static boolean isValidLogin(String username, String password) {
        if (username == null || password == null) return false;
        for (MockUser user : MOCK_AUTH_USERS) {
            if (user.username.equals(username) && user.password.equals(password)) {
                return true;
            }
        }
        return false;
    }

    public static MockUser getMockUser(String username) {
        if (username == null) return null;
        for (MockUser user : MOCK_AUTH_USERS) {
            if (user.username.equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static boolean canRegisterUser(String username, String email) {
        if (username == null || email == null) return false;
        for (MockUser user : MOCK_AUTH_USERS) {
            if (user.username.equals(username) || user.email.equals(email)) {
                return false; // Already exists
            }
        }
        return true; // Can register
    }

    // NEW: Method to register a new mock user
    public static void registerMockUser(String username, String password, String email) {
        String profilePictureUrl = "https://i.pravatar.cc/150?img=" + (MOCK_AUTH_USERS.size() + 10);
        MOCK_AUTH_USERS.add(new MockUser(username, password, email, profilePictureUrl));
    }

    // ========== PROFILE MOCK DATA ==========

    public static class MockProfile {
        public final String username;
        public final String email;
        public final String profilePictureUrl;
        public final String bio;
        public final List<PostUi> userPosts;
        
        public MockProfile(String username, String email, String profilePictureUrl, 
                          String bio, List<PostUi> userPosts) {
            this.username = username;
            this.email = email;
            this.profilePictureUrl = profilePictureUrl;
            this.bio = bio;
            this.userPosts = userPosts;
        }
    }

    public static MockProfile mockUserProfile(String username) {
        // Get user info
        MockUser user = getMockUser(username);
        if (user == null) {
            // Default for unknown users
            user = new MockUser(username, "", username + "@example.com", 
                               "https://i.pravatar.cc/150?img=10");
        }
        
        // Get user's posts (filter mockPosts by username)
        List<PostUi> allPosts = mockPosts();
        List<PostUi> userPosts = new ArrayList<>();
        for (PostUi post : allPosts) {
            if (post.getUserName().equals(username)) {
                userPosts.add(post);
            }
        }
        
        String bio = "Mock bio for " + username + ". Loves movies, books, and music!";
        
        return new MockProfile(user.username, user.email, user.profilePictureUrl, 
                              bio, userPosts);
    }
}