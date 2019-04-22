package com.uj.mybook.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uj.mybook.R;
import com.uj.mybook.main.Book;
import com.uj.mybook.main.BooksAdapter;
import com.uj.mybook.sell_book.OrderDialog;

import java.util.List;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.MyViewHolder> {
    private List<Book> books;
    private Context myContext;
    private int layout;
    private SharedPreferences preferences;

    public MyOrdersAdapter(List<Book> books, Context myContext, int layout) {
        this.books = books;
        this.myContext = myContext;
        this.layout = layout;
        preferences =myContext.getSharedPreferences("user",Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public MyOrdersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(myContext).inflate(layout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final String stBookName, stBookImageUrl, stAuthor, stCategory ,stBookType, stDescription, stPrice, userID;
        final Book book = books.get(position);
        final DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference("Books");

        stBookName = book.getBookName();
        stBookImageUrl = book.getImageUrl();
        stAuthor = book.getAuthor();
        stCategory = book.getCategory();
        stPrice = book.getPrice();
        stDescription = book.getDescription();
        stBookType = book.getBookType();
        userID = preferences.getString("id","");

        holder.bookName.setText(stBookName);
        Glide.with(myContext).load(stBookImageUrl).into(holder.bookImage);
        holder.author.setText(stAuthor);
        holder.category.setText(stCategory);
        holder.price.setText(stPrice);

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book1 = new Book(stBookName, stBookImageUrl, stAuthor, stDescription, stCategory, stBookType, stPrice);
                dbReference.child(stBookType).child(stCategory).push().setValue(book);
            }
        });

        holder.description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(myContext).create();
                dialog.setMessage(book.getDescription());
                dialog.setTitle("Book Description");
                dialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView bookName;
        private ImageView bookImage;
        private TextView author;
        private TextView category;
        private TextView description;
        private TextView price;
        private Button btnCancel;

        public MyViewHolder(View itemView) {
            super(itemView);
            bookName = itemView.findViewById(R.id.bookName);
            bookImage = itemView.findViewById(R.id.bookImage);
            author = itemView.findViewById(R.id.author);
            category = itemView.findViewById(R.id.category);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            btnCancel = itemView.findViewById(R.id.cancel);

        }

    }
}
