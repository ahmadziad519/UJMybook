package com.uj.mybook.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.uj.mybook.OrderInformation;
import com.uj.mybook.R;
import com.uj.mybook.sell_book.OrderDialog;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.MyViewHolder> {
    List<Book> books;
    Context myContext;
    private OnItemLongClickListner listner;


    public interface OnItemLongClickListner{
        void onItemClick(int position);
    }

    public  void setOnItemLongClickListner(OnItemLongClickListner clickListner){
        listner = clickListner;
    }
    public BooksAdapter(List<Book> books, Context myContext) {
        this.books = books;
        this.myContext = myContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(myContext).inflate(R.layout.book_card, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view, listner);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final String stBookName, stBookImageUrl, stAuthor, stCategory, stPrice;
        final Book book = books.get(position);
        stBookName = book.getBookName();
        stBookImageUrl = book.getImageUrl();
        stAuthor = book.getAuthor();
        stCategory = book.getCategory();
        stPrice = book.getPrice();


        holder.bookName.setText(stBookName);
        Glide.with(myContext).load(stBookImageUrl).into(holder.bookImage);
        holder.author.setText(stAuthor);
        holder.category.setText(stCategory);
        holder.price.setText(stPrice);

        holder.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), OrderInformation.class);
                intent.putExtra("bookName", stBookName);
                intent.putExtra("imageUrl", stBookImageUrl);
                intent.putExtra("author", stAuthor);
                intent.putExtra("price", stPrice);
                OrderDialog dialog = new OrderDialog(myContext, stBookName, stBookImageUrl, stAuthor, stPrice);
                dialog.show();
                //v.getContext().startActivity(intent);
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
        private Button btnOrder;

        public MyViewHolder(View itemView, final OnItemLongClickListner listner) {
            super(itemView);
            bookName = itemView.findViewById(R.id.bookName);
            bookImage = itemView.findViewById(R.id.bookImage);
            author = itemView.findViewById(R.id.author);
            category = itemView.findViewById(R.id.catagory);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            btnOrder = itemView.findViewById(R.id.order);

        }

    }
}
