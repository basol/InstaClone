package com.example.basol.insta;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    EditText editText;
    ImageView imageView;
    Uri image;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;

    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        editText = findViewById(R.id.editText);
        imageView = findViewById(R.id.imageView);
        mStorageRef = FirebaseStorage.getInstance().getReference();

    }


    public void chooseImage(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE},2);
            }
            else {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            image = data.getData();

            try {
                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
                imageView.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void upload(View view) {

        UUID random = UUID.randomUUID();
        String imageUnique = "images/" + random + ".jpg";

        StorageReference imageRef = mStorageRef.child(imageUnique);
        imageRef.putFile(image)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                        String email = mAuth.getCurrentUser().getEmail();
                        String comment = editText.getText().toString();

                        UUID uuid = UUID.randomUUID();
                        String uuidString = uuid.toString();

                        myRef.child("Posts").child(uuidString).child("useremail").setValue(email);
                        myRef.child("Posts").child(uuidString).child("comment").setValue(comment);
                        myRef.child("Posts").child(uuidString).child("downloadurl").setValue(downloadUrl);

                        Toast.makeText(UploadActivity.this, "Post Shared", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                        startActivity(intent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });


    }
}
