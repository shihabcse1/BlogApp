package com.ardroidplus.blogapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ardroidplus.blogapp.Activities.PostDetailsActivity;
import com.ardroidplus.blogapp.Models.Post;
import com.ardroidplus.blogapp.R;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context myContext;
    List<Post> mData;

    public PostAdapter(Context myContext, List<Post> mData) {
        this.myContext = myContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(myContext).inflate(R.layout.row_post_item, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.MyViewHolder holder, int position) {
        holder.tvTitle.setText(mData.get(position).getTitle());
        Glide.with(myContext).load(mData.get(position).getPicture()).into(holder.imgPost);
        Glide.with(myContext).load(mData.get(position).getUserPhoto()).into(holder.imgPostProfile);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView imgPost;
        ImageView imgPostProfile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.textView_row_post_title);
            imgPost = itemView.findViewById(R.id.imageViewPost);
            imgPostProfile = itemView.findViewById(R.id.imageView_row_post_profile_pic);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent postDetailsActivity = new Intent(myContext, PostDetailsActivity.class);
                    int position = getAdapterPosition();

                    postDetailsActivity.putExtra("title", mData.get(position).getTitle());
                    postDetailsActivity.putExtra("postImage", mData.get(position).getPicture());
                    postDetailsActivity.putExtra("description", mData.get(position).getDescription());
                    postDetailsActivity.putExtra("postKey", mData.get(position).getPostKey());
                    postDetailsActivity.putExtra("userPhoto", mData.get(position).getUserPhoto());
                    // need to add user name to post object
                    // postDetailActivity.putExtra("userName", mDate.get(position).getUsername);
                    long timeStamp = (long) mData.get(position).getTimeStamp();
                    postDetailsActivity.putExtra("postDate", timeStamp);
                    myContext.startActivity(postDetailsActivity);


                }
            });
        }
    }
}
