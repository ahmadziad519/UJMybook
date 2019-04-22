package com.uj.mybook.main;

public class Book {
    private String bookName;
    private String imageUrl;
    private String author;
    private String description;
    private String category;
    private String bookType;
    private String price;

    public Book() {
    }

    public Book(String bookName, String imageUrl, String author, String description, String category, String bookType, String price) {
        this.bookName = bookName;
        this.imageUrl = imageUrl;
        this.author = author;
        this.description = description;
        this.category = category;
        this.bookType = bookType;
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCatagory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookName='" + bookName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", catagory='" + category + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
