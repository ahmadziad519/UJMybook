package com.uj.mybook.Store;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uj.mybook.R;
import com.uj.mybook.main.Book;
import com.uj.mybook.main.BooksAdapter;
import com.uj.mybook.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class Store extends AppCompatActivity {
    private List<Book> books;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BooksAdapter booksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        books = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recyclerView_sooq);

        String stCategory = getIntent().getStringExtra("category");
        String stBookType = getIntent().getStringExtra("bookType");

        getBooksFromFirebase(stCategory, stBookType);
    }

    public void getBooksFromFirebase(String category, String bookType) {
        final ProgressDialog progressDialog = new ProgressDialog(Store.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        DatabaseReference dbReference;
        books.clear();
        dbReference = FirebaseDatabase.getInstance().getReference("Books");
        dbReference.child(bookType).child(category).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    books.add(book);
                }

                booksAdapter = new BooksAdapter(books, Store.this, R.layout.book_card);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(booksAdapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error Happened", Toast.LENGTH_LONG).show();

            }
        });
    }
}
