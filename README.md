# Fludde

## Table of Contents
1. [Overview](#Overview)
2. [Features](#Features)
3. [Installation](#Installation)
4. [Usage](#Usage)
5. [Schema](#Schema)
6. [Wireframes](#Wireframes)

## Overview

### Description

Fludde is a social platform that allows users to share and review various forms of content, such as books, music, and movies. Users can follow others, create posts, and view a personalized timeline filled with reviews from the users they follow. The platform integrates with various APIs to enhance the multimedia experience.

### App Evaluation
- **Category:** Social Networking 
- **Platform:** Mobile-first experience, primarily for Android.
- **Purpose:** To provide users with a space to share their opinions on different types of content and explore reviews from their network.
- **Target Audience:** Anyone interested in sharing their thoughts on content and discovering reviews from others.
- **Scope:** Initially focusing on movies, music, and books, with potential expansion to other forms of media like TV shows, comics, and video games.

## Features

### Core Features

- User Registration: Users can sign up and create an account.
- User Authentication: Users can log in and out of their accounts securely.
- Home Timeline: Users can view a feed of reviews from the people they follow.
- Content Search: Users can search for content or other users.
- User Profiles: Users can view their own profile, which includes all their reviews.
- Review Creation: Users can create and share reviews of content, categorized by type (e.g., movie, music, book).

### Future Enhancements

- Content Information: Users can view additional information about the content in reviews.
- Favorite Content: Users can favorite content for quick access later.
- Social Sharing: Users can reshare reviews, similar to a retweet.
- Advanced Search: Users can search specifically for reviews or detailed content information (e.g., trailers, descriptions).

## Installation

### Prerequisites

- Android Studio
- A configured Android device or emulator

### Setup

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/josuejero/Fludde.git
   ```

2. **Navigate to the Project Directory:**

   ```bash
   cd fludde
   ```

3. **Configure API Keys:**

   Create a file named `apikey.properties` in the root of the project and add your API keys:

   ```
   NY_TIMES_API_KEY=your-nytimes-api-key-here
   SPOTIFY_KEY=your-spotify-api-key-here
   TMDB_API_KEY=your-tmdb-api-key-here
   ```

4. **Open the Project in Android Studio:**

   Open Android Studio, then go to `File > Open` and select the `fludde` directory.

5. **Build and Run:**

   Build and run the app on your device or emulator by clicking the "Run" button in Android Studio.

## Usage

Once the app is running, you can:

- **Sign Up or Log In:** Create an account or log in to your existing account.
- **View Timeline:** Explore the timeline filled with reviews from users you follow.
- **Search Content:** Search for specific content or users to see their reviews.
- **Create Reviews:** Share your thoughts on movies, music, or books.

## Schema

### Models

#### Post

| Property     | Type            | Description                                      |
| ------------ | --------------- | ------------------------------------------------ |
| `objectId`   | String          | Unique id for the post (default field)           |
| `author`     | Pointer to User | The user who created the review                  |
| `createdAt`  | DateTime        | Date when the post was created (default field)   |
| `description`| String          | Description or title of the content              |
| `review`     | String          | Review text authored by the user                 |
| `category`   | String          | Type of content (e.g., movie, book, music)       |

### Networking

#### Login Screen
- **GET**: Verify if the user is already logged in.
  ```java
  if (ParseUser.getCurrentUser() != null) {
      goMainActivity();
  }
  ```

- **POST**: Authenticate user credentials.
  ```java
  ParseUser.logInInBackground(username, password, new LogInCallback() {
      @Override
      public void done(ParseUser user, ParseException e) {
          if (e != null) {

          } else {

          }
      }
  });
  ```

#### SignUp Screen
- **POST**: Create a new user account.
  ```java
  newUser.setUsername(userName);
  newUser.setEmail(userEmail);
  newUser.setPassword(userPass);

  newUser.signUpInBackground(new SignUpCallback() {
      @Override
      public void done(ParseException e) {
          if (e != null) {

          } else {

          }
      }
  });
  ```

#### Home Timeline Screen
- **GET**: Fetch posts from users the current user follows.
- **POST**: Create a new post with a review and rating.

#### Search Screen
- **GET**: Search for users or reviews based on content or categories.

#### User Profile Screen
- **GET**: Fetch all posts created by the user.
- **POST**: Create a new post directly from the profile.

## Wireframes
<img src="https://i.imgur.com/BNGuswR.jpg" width=600>


This is the current status of the app. The content information is available when making the review.



---

## Contributing

Fludde is an open-source project and contributions are welcome! Whether it's bug fixes, feature requests, or improvements, feel free to open an issue or submit a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

