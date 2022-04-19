# Fludde

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description

An application to share and review other opinions of contents such as books, music, movies that they have seen, read, or heard. Think Twitter meets Rotten Tomatoes. 

A user can follow other users. The user will be able to visit a home page timeline of reviews by those they follow, a search functionality that utilizes different API sources, and a user’s profile of their reviews.

### App Evaluation
- **Category:** Social Networking 
- **Mobile:** Mobile, first experience.
- **Story:** Allows users to share reviews on various content (ex. movies, music, books).
- **Market:** Anyone who views content and wants to share their opinion with others.
- **Habit:** Users can use this application as a journal to leave behind their overall review or initial thoughts while experiencing content. Users can explore their friends’ or influencers’ thoughts on content they have viewed.
- **Scope:** Fludde would start with a basic interface of viewing feeds and a user profile. We are only focusing on movies, music, and books, we can see this expanding towards TV shows, comics, video games, live performances, ect. After this, we could see a recommendation algorithm being added to the application along with adding pictures/videos to reviews. You can create lists of content that you can share with friends or like minded individuals.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

- [x] Users can register a new account
- [x] User can log in
- [x] User can see timeline that includes reviews of users they follow
- [ ] User can search for other users profiles
- [x] User can search for other content (music, movie, books)
- [x] User can view their own profile
- [x] User can create review that can be seen from their profile and on timeline

**Optional Nice-to-have Stories**

- [ ] User can favorite content
- [ ] User can reshare reviews, think “re-tweet”
- [ ] User can do a search reviews on specific content
- [ ] User can click on content and see detail on content (ex. Trailer, description of content)

### 2. Screen Archetypes

* Log in
   * User can login to their account
* Register - User can sign up or log into their account
   * User can register a new account
* Stream - User can scroll through timeline of other user’s reviews
   * User can follow other user’s and add their reviews to their timeline
* Creation
   * User can create review of content
* Profile
   * Timeline of user’s own reviews of content

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Stream
* Search
* Profile
* Create

**Flow Navigation** (Screen to Screen)

* Log in
   * Stream
* Register - User can sign up or log into their account
   * Stream
* Stream - User can scroll through timeline of other user’s reviews
   * Search
* Creation
   * Stream
* Profile
   * Detail

## Wireframes
<img src="https://i.imgur.com/BNGuswR.jpg" width=600>

## Schema 

### Models

Post

| Property | Type | Description |
| --- | --- | --- |
| `objectId` | String | Unique id for the user to post (default field) |
| `author` | Pointer to User | Review author |
| `createdAt` | DateTime | Date when post is created (default field) |
| `description` | String | Title of content |
| `review` | String | Review typed up by author |
| `category` | String | Type of content (movie, book or music) |



### Networking

- Login Screen 
  - GET request verify if user is already login
	
	```
	if(ParseUser.getCurrentUser() != null){
       		goMainActivity();    }
	```

  - POST request to authenticate user
	
	```
	private void loginUser(String username, String password) {
  
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {

                } else {
                  
                }
 	}
	```

- SignUp Screen
  - POST create new user
      
      ```
        newUser.setUsername(userName);
        newUser.setEmail(userEmail);
        newUser.setPassword(userPass);
	
	newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
   
                }
                else {


                } } })
	```


- Home Timeline Screen
  -	(Read/GET) Posts from other users
  -	(Create/Post) Post with rating and review
	
- Search
  -	(Read/GET) Existing users or reviews on specific content by search or category
	
- User Profile
  -	(Read/GET) All posts created by user
  -	(Create/Post) Post with rating and review

### App Walkthough GIF

<img src='fludde.gif' width=250><br>

Current view of Review Post:
<br><br><img src='review_attempt_1.gif' width=250><br>

Needs to work on choosing different content. (As of 4/11/22)

	

