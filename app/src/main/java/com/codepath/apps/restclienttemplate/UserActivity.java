package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class UserActivity extends AppCompatActivity implements UserAdapter.onClickListener {

    public static final String TAG = "UserActivity";

    User user;
    TwitterClient client;
    RecyclerView rvUserTweets;
    List<Tweet> tweets;
    UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.twitter_blue)));
        View mActionBarView = getLayoutInflater().inflate(R.layout.my_action_bar, null);
        bar.setCustomView(mActionBarView);
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        user = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

        client = TwitterApp.getRestClient(this);

        // find the recycler view
        rvUserTweets = findViewById(R.id.rvUserTweets);

        // init the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new UserAdapter(this, tweets, this);

        // recycler view setup: layout manager and adapter
        rvUserTweets.setLayoutManager(new LinearLayoutManager(this));
        rvUserTweets.setAdapter(adapter);

        populateUserTimeline();
    }

    private void populateUserTimeline() {
        client.getUserTimeline(user.userID, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess: " + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure " + response, throwable);
            }
        });
    }

    @Override
    public void onItemRetweeted(final int position) {
        final Tweet tweet = tweets.get(position);
        client.retweetTweet(tweet.retweeted, tweet.postID, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess: " + json.toString());
                tweet.retweeted = !tweet.retweeted;
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure " + response, throwable);
            }
        });
    }

    @Override
    public void onItemLiked(final int position) {
        final Tweet tweet = tweets.get(position);
        client.likeTweet(tweet.favorited, tweet.postID, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess: " + json.toString());
                tweet.favorited = !tweet.favorited;
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure " + response, throwable);
            }
        });
    }
}