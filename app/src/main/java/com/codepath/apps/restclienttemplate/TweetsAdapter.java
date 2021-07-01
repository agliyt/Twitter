package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;

    public interface onClickListener{
        void onItemRetweeted(int position);
        void onItemLiked(int position);
        void onUserClicked(int position);
    }

    onClickListener onClickListener;

    // pass in context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets, onClickListener onClickListener) {
        this.context = context;
        this.tweets = tweets;
        this.onClickListener = onClickListener;
    }

    // for each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the data at position
        Tweet tweet = tweets.get(position);

        // bind the tweet with the view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // define a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvTimeStamp;
        ImageView ivTweetImage;
        ImageButton retweet;
        ImageButton like;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            ivTweetImage = itemView.findViewById(R.id.ivTweetImage);
            retweet = itemView.findViewById(R.id.retweet);
            like = itemView.findViewById(R.id.heart);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvTimeStamp.setText(tweet.timeStamp);
            // handle retweeting/liking
            if (tweet.retweeted) {
                retweet.setBackgroundResource(R.drawable.ic_vector_retweet);
                retweet.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.inline_action_retweet)));
            } else {
                retweet.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);
                retweet.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.medium_gray)));
            }
            if (tweet.favorited) {
                like.setBackgroundResource(R.drawable.ic_vector_heart);
                like.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.inline_action_like)));
            } else {
                like.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
                like.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.medium_gray)));
            }
            Glide.with(context)
                    .load(tweet.user.profileImageUrl)
                    .into(ivProfileImage);
            if (tweet.imageUrl != "null") {
                ivTweetImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(tweet.imageUrl)
                        .into(ivTweetImage);
            } else {
                ivTweetImage.setVisibility(View.GONE);
            }

            retweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onItemRetweeted(getAdapterPosition());
                }
            });

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onItemLiked(getAdapterPosition());
                }
            });

            ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onUserClicked(getAdapterPosition());
                }
            });
        }
    }
}
