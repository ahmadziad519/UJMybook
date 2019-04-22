package com.uj.mybook.sell_book;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uj.mybook.R;
import com.uj.mybook.main.Book;
import com.uj.mybook.profile.Profile;

import java.io.FileNotFoundException;
import java.io.IOException;

public class BookSeller extends AppCompatActivity {
    private EditText etBookName, etAuthor, etPrice, etDescription;
    private String stBookName, stAuthor, stPrice, stDescription, stBookType, stBookCategory;
    private RadioGroup rgBookType;
    private Spinner spnBookCategory;
    private Button btnBrowse, btnSubmit, btnCancel;
    public static final int REQUEST_CODE = 4747;
    private Uri imageUri;
    private ImageView ivBookImage;
    private ArrayAdapter<CharSequence> categoryAdapter;
    private String uploadedImageUrl;
    private DatabaseReference dbReference;
    private Book book;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_seller);
        etBookName = findViewById(R.id.bookName);
        etAuthor = findViewById(R.id.author);
        etPrice = findViewById(R.id.price);
        etDescription = findViewById(R.id.description);
        rgBookType = findViewById(R.id.booktype);
        spnBookCategory = findViewById(R.id.category_spinner);
        btnBrowse = findViewById(R.id.browse_button);
        btnSubmit = findViewById(R.id.submit_button);
        btnCancel = findViewById(R.id.cancel_button);
        ivBookImage = findViewById(R.id.bookImage);
        dbReference= FirebaseDatabase.getInstance().getReference("Books");
        categoryAdapter = ArrayAdapter.createFromResource(BookSeller.this, R.array.colleges, android.R.layout.simple_dropdown_item_1line);
        spnBookCategory.setAdapter(categoryAdapter);
        int radioButtonId = rgBookType.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(radioButtonId);
        stBookType = radioButton.getText().toString();

        rgBookType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.academic) {
                    categoryAdapter = ArrayAdapter.createFromResource(BookSeller.this, R.array.colleges, android.R.layout.simple_dropdown_item_1line);

                } else if (checkedId == R.id.non_academic) {
                    categoryAdapter = ArrayAdapter.createFromResource(BookSeller.this, R.array.book_categories, android.R.layout.simple_dropdown_item_1line);

                } else {
                    categoryAdapter = ArrayAdapter.createFromResource(BookSeller.this, R.array.colleges, android.R.layout.simple_dropdown_item_1line);
                }

                RadioButton radioButton = findViewById(checkedId);
                stBookType = radioButton.getText().toString();

                spnBookCategory.setAdapter(categoryAdapter);
            }
        });

        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), REQUEST_CODE);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = new ProgressDialog(BookSeller.this);
                dialog.setMessage("Uploading...");
                stBookName = etBookName.getText().toString();
                stAuthor = etAuthor.getText().toString();
                stPrice = etPrice.getText().toString();
                stDescription = etDescription.getText().toString();
                stBookCategory = spnBookCategory.getSelectedItem().toString();

                if(!checkBookInformation()){
                    return;
                }
                dialog.show();

                final StorageReference stReference = FirebaseStorage.getInstance().getReference(rgBookType + "/" + stBookCategory + "/" + stBookName + System.currentTimeMillis() + getImageExtention(imageUri));

                UploadTask uploadTask = stReference.putFile(imageUri);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        return stReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            uploadedImageUrl = downloadUri.toString();
                            book = new Book(stBookName, uploadedImageUrl, stAuthor, stDescription, stBookCategory,stBookType, stPrice);
                            dbReference.child(stBookType).child(stBookCategory).push().setValue(book).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        dialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Your book added successfully", Toast.LENGTH_LONG).show();
                                        finish();
                                        startActivity(getIntent());
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(BookSeller.this, "Something went wrong!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });

    }

    private boolean checkBookInformation() {
        boolean flag = true;
        if(TextUtils.isEmpty(stBookName)) {
            etBookName.setError("Please enter book name");
            flag = false;
        }
        if(TextUtils.isEmpty(stAuthor)) {
            etAuthor.setError("Please enter author name");
            flag = false;
        }
        if(TextUtils.isEmpty(stDescription)) {
            etDescription.setError("Please enter description");
            flag = false;
        }
        if(imageUri == null){
            Toast.makeText(this, "Please add a book image", Toast.LENGTH_LONG).show();
            flag = false;
        }
        return flag;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ivBookImage.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getImageExtention(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
