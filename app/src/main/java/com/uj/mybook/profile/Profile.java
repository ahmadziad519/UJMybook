package com.uj.mybook.profile;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uj.mybook.R;
import com.uj.mybook.main.Book;
import com.uj.mybook.main.BooksAdapter;
import com.uj.mybook.main.User;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Book> books;
    private BooksAdapter booksAdapter;
    private Button btnMyOrders, btnMyOffers;
    private int buttonID = 0;
    private EditText etEditNumber;
    private ImageView ivPicture;
    private Button btnProfile, btnEditPicture, btnSubmit;
    public static final int REQUEST_CODE = 4747;
    private Uri imageUri;
    private int visibleFlag = 0; // 0 invisible, 1 visible
    private String uploadedImageUrl;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private TextView tvPhoneNumber, tvUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        recyclerView = findViewById(R.id.ordersRecyclerView);
        layoutManager = new LinearLayoutManager(Profile.this);
        books = new ArrayList<>();
        btnMyOrders = findViewById(R.id.myOrders);
        btnMyOffers = findViewById(R.id.myOffers);
        btnProfile = findViewById(R.id.profile);
        etEditNumber = findViewById(R.id.editNumber);
        ivPicture = findViewById(R.id.editPicture);
        btnEditPicture = findViewById(R.id.editPictureButton);
        btnSubmit = findViewById(R.id.submitButton);
        tvPhoneNumber = findViewById(R.id.numberTextView);
        tvUserName = findViewById(R.id.userFullName);

        editProfile();

        booksAdapter = new BooksAdapter(books, Profile.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(booksAdapter);

        preferences = getSharedPreferences("user", MODE_PRIVATE);
        editor = preferences.edit();
        String userNumber = preferences.getString("number", "");
        tvPhoneNumber.setText("Phone Number: " + userNumber);

        final String stImageUrl, stUserName;

        stImageUrl = preferences.getString("imageUrl", "");
        if (!TextUtils.isEmpty(stImageUrl)) {
            Glide.with(this).load(stImageUrl).into(ivPicture);
            Drawable drawable = ivPicture.getDrawable();
        }
        stUserName = preferences.getString("firstName", "") + " " + preferences.getString("lastName", "");
        tvUserName.setText(stUserName);


        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonID != 0) {
                    buttonID = 0;
                    btnEditPicture.setVisibility(View.VISIBLE);
                    btnSubmit.setVisibility(View.VISIBLE);
                    etEditNumber.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    btnMyOrders.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    btnMyOffers.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    btnProfile.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    btnMyOffers.setClickable(true);
                    btnMyOrders.setClickable(true);
                    btnProfile.setClickable(false);

                }
            }
        });

        btnMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonID != 1) {
                    buttonID = 1;
                    if (visibleFlag == 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        btnEditPicture.setVisibility(View.INVISIBLE);
                        btnSubmit.setVisibility(View.INVISIBLE);
                        etEditNumber.setVisibility(View.INVISIBLE);
                    }


                    getBooksFromFirebase(buttonID);
                    btnMyOffers.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    btnProfile.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    btnMyOrders.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    btnMyOrders.setClickable(false);
                    btnMyOffers.setClickable(true);
                    btnProfile.setClickable(true);
                }


            }
        });

        btnMyOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonID != 2) {
                    buttonID = 2;
                    if (visibleFlag == 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        btnEditPicture.setVisibility(View.INVISIBLE);
                        btnSubmit.setVisibility(View.INVISIBLE);
                        etEditNumber.setVisibility(View.INVISIBLE);
                    }

                    getBooksFromFirebase(buttonID);
                    btnMyOrders.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    btnMyOffers.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    btnProfile.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    btnMyOffers.setClickable(false);
                    btnMyOrders.setClickable(true);
                    btnProfile.setClickable(true);

                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String stNumber = etEditNumber.getText().toString();
                if (TextUtils.isEmpty(stNumber) || stNumber.length()!=10) {
                    Toast.makeText(Profile.this, "Please add a valid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ProgressDialog dialog = new ProgressDialog(Profile.this);
                dialog.setMessage("Updating..");
                dialog.show();
                DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Users");
                String userID = preferences.getString("id", "");
                dbReference.child(userID).child("number").setValue(stNumber)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(Profile.this, "Phone number updated successfully", Toast.LENGTH_SHORT).show();
                            etEditNumber.setText("");
                            tvPhoneNumber.setText(stNumber);
                            editor.putString("number", stNumber);
                            editor.commit();
                        }
                    }
                });

            }
        });

    }

    private void editProfile() {
        btnEditPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ivPicture.setImageBitmap(bm);
                Thread.sleep(1000);
                uploadImage();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String getImageExtention(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    public void getBooksFromFirebase(int id) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        DatabaseReference dbReference;
        books.clear();

        if (id == 1) {
            dbReference = FirebaseDatabase.getInstance().getReference("Latest");
            dbReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Book book = snapshot.getValue(Book.class);
                        books.add(book);
                    }
                    booksAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            dbReference = FirebaseDatabase.getInstance().getReference("Best");
            dbReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Book book = snapshot.getValue(Book.class);
                        books.add(book);
                    }
                    booksAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void uploadImage(){
        final ProgressDialog dialog = new ProgressDialog(Profile.this);
        dialog.setMessage("Uploading...");
        dialog.show();
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pictures/" + "1111" + getImageExtention(imageUri));

        UploadTask uploadTask = storageReference.putFile(imageUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    uploadedImageUrl = downloadUri.toString();
                    editor.putString("imageUrl", uploadedImageUrl);
                    editor.commit();
                    dialog.dismiss();
                    Toast.makeText(Profile.this, "Image Uploaded Successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Profile.this, "Something went wrong!", Toast.LENGTH_SHORT).show();

                }
            }
        });


        String userID = preferences.getString("id", "");

        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Users");
        dbReference.child(userID).child("imageUrl").setValue(uploadedImageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                } else {
                    Toast.makeText(Profile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }



}
