package com.example.basol.insta;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Basol on 9.07.2018.
 */

public class PostClass extends ArrayAdapter<String> {

    private final ArrayList<String> userEmail;
    private final ArrayList<String> userImage;
    private final ArrayList<String> userComment;
    private final Activity context;

    public PostClass(ArrayList<String> userEmail, ArrayList<String> userImage, ArrayList<String> userComment, Activity context) {
        super(context, R.layout.activity_feed, userEmail);
        this.userEmail = userEmail;
        this.userImage = userImage;
        this.userComment = userComment;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = context.getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.feed_layout, null, true);

        TextView textView = customView.findViewById(R.id.emailTextView);
        TextView commentView = customView.findViewById(R.id.commentTextView);
        ImageView imageView = customView.findViewById(R.id.postImageView);
        EditText addCommentText = customView.findViewById(R.id.addCommentText);

        textView.setText(userEmail.get(position));
        commentView.setText(userComment.get(position));

        Picasso.get().load(userImage.get(position)).into(imageView);

        return customView;
    }
}
