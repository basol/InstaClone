package com.example.basol.insta;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FeedActivity extends AppCompatActivity {

    ArrayList<String> feedEmail;
    ArrayList<String> feedComment;
    ArrayList<String> feedUrl;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    PostClass adapter;

    ListView listView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_post, menu);
        //menuInflater.inflate(R.menu.log_out, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.share_post){
            Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.log_out){
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    if (user == null) {

                        // user auth state is changed - user is null
                        // launch login activity

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            };
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        feedEmail = new ArrayList<>();
        feedComment = new ArrayList<>();
        feedUrl = new ArrayList<>();

        listView = findViewById(R.id.listView);

        adapter = new PostClass(feedEmail, feedUrl, feedComment, this);

        listView.setAdapter(adapter);

        getDataFromDatabae();



    }

    public void getDataFromDatabae(){

        DatabaseReference myRef = firebaseDatabase.getReference("Posts");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    for( DataSnapshot ds : dataSnapshot.getChildren()){

                        Log.e("data",dataSnapshot.toString());
                        HashMap<String, String> hm = (HashMap<String, String>) ds.getValue();
                        feedEmail.add(hm.get("useremail"));
                        feedComment.add(hm.get("comment"));
                        feedUrl.add(hm.get("downloadurl"));
                        System.out.println(feedComment.get(0));

                        adapter.notifyDataSetChanged();
                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
