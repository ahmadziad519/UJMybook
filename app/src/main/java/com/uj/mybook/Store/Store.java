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

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private SharedPreferences preferences;
    private ImageView userPicture;
    private TextView username;
    private Button btnLatest, btnBest;
    private List<Book> books;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BooksAdapter booksAdapter;
    private int buttonID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        books = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recyclerView_sooq);

        //get catagory from Intent


        String cat= getIntent().getStringExtra("cat");
        Toast.makeText(this,cat,Toast.LENGTH_LONG).show();

        if(cat!=null)
        getBooksFromFirebase(cat);
else
    Toast.makeText(this,"no string",Toast.LENGTH_LONG).show();

    }

    public void getBooksFromFirebase(String id){
        //String cat as parameter
        final ProgressDialog progressDialog = new ProgressDialog(Store.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        DatabaseReference dbReference;
        books.clear();
            dbReference = FirebaseDatabase.getInstance().getReference(id);
            dbReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Book book = snapshot.getValue(Book.class);
                        books.add(book);
                    }

                    booksAdapter = new BooksAdapter(books,Store.this);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(booksAdapter);
                    progressDialog.dismiss();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),"Error Happened",Toast.LENGTH_LONG).show();

                }
            });


    }
}
