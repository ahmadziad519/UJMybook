package com.uj.mybook.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uj.mybook.R;
import com.uj.mybook.Store.Store;
import com.uj.mybook.login_signup.Login;
import com.uj.mybook.profile.Profile;
import com.uj.mybook.sell_book.BookSeller;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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
    private int buttonID = 0; // best seller id =0, latest books id = 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnLatest = findViewById(R.id.latest);
        btnBest = findViewById(R.id.best);
        preferences = getSharedPreferences("user", MODE_PRIVATE);
        books = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recyclerView);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //Fill recyclerView with books from Firebase
        getBooksFromFirebase(-1);

        //set navigation header picture and name
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        setNavigationHeaderContent(headerView);

        //buttons latest and best
        btnLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonID == 0) {
                    getBooksFromFirebase(buttonID);
                    btnBest.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    btnLatest.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    btnLatest.setClickable(false);
                    btnBest.setClickable(true);
                    buttonID = 1;

                }

            }
        });

        btnBest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonID==1) {
                    getBooksFromFirebase(buttonID);
                    btnLatest.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    btnBest.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    btnBest.setClickable(false);
                    btnLatest.setClickable(true);
                    buttonID = 0;
                }

            }

        });

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        int id = item.getItemId();
        if (id == R.id.academic) {
            showColleges("Academic" );
        } else if (id == R.id.non_academic) {
            showColleges("Non-Academic");
        }else if (id == R.id.paper_studies) {
            showColleges("PaperStudies");
        } else if (id == R.id.sell) {
            startActivity(new Intent(MainActivity.this, BookSeller.class));

        } else if (id == R.id.account) {
            startActivity(new Intent(MainActivity.this, Profile.class));

        } else if (id == R.id.about) {
            startActivity(new Intent(MainActivity.this, About.class));
        } else if (id == R.id.signout) {
            FirebaseAuth.getInstance().signOut();
            deleteUserInformation();
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void deleteUserInformation() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", " ");
        editor.putString("firstName", " ");
        editor.putString("lastName", " ");
        editor.putString("number", " ");
        editor.putString("college", " ");
        editor.putString("imageUrl", " ");
        editor.commit();
    }

    private void showColleges(final String bookType) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, toolbar);
        if(bookType.equalsIgnoreCase("non-academic")) {
            popupMenu.getMenuInflater().inflate(R.menu.categories_popup_menu, popupMenu.getMenu());
        } else{
            popupMenu.getMenuInflater().inflate(R.menu.colleges_popup_menu, popupMenu.getMenu());
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String category = item.getTitle().toString();
                Intent intent=new Intent(MainActivity.this,Store.class);
                intent.putExtra("category",category);
                intent.putExtra("bookType",bookType);
                startActivity(intent);
                return true;
            }
        });

        popupMenu.show();
    }

    //id = -1 means the recyclerView and the adapter should initiate
    //0 means adapter should fill with the latest books
    //1 means adapter should fill with the best seller books
    public void getBooksFromFirebase(int id){
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        DatabaseReference dbReference;
        books.clear();
        if(id==-1) {
            dbReference = FirebaseDatabase.getInstance().getReference("Best");
            dbReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Book book = snapshot.getValue(Book.class);
                        books.add(book);
                    }

                    booksAdapter = new BooksAdapter(books,MainActivity.this, R.layout.book_card);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(booksAdapter);
                    progressDialog.dismiss();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else if(id==0) {
            dbReference = FirebaseDatabase.getInstance().getReference("Latest");
            dbReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
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
        }else{
            dbReference = FirebaseDatabase.getInstance().getReference("Best");
            dbReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
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

    public void setNavigationHeaderContent(View headerView){
        userPicture = headerView.findViewById(R.id.userPicture);
        username = headerView.findViewById(R.id.userName);

        String stFirstName = preferences.getString("firstName", "");
        String stLastName = preferences.getString("lastName", "");
        String stImageUrl = preferences.getString("imageUrl", "");

        username.setText(stFirstName + " " + stLastName);
        if (!TextUtils.isEmpty(stImageUrl)) {
            Glide.with(this).load(stImageUrl).into(userPicture);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String stImageUrl = preferences.getString("imageUrl", "");
        Log.i("hhh", "onRestart: " + stImageUrl);
        if (!TextUtils.isEmpty(stImageUrl)) {
            Glide.with(this).load(stImageUrl).into(userPicture);
        }
        userPicture.invalidate();

    }

}
