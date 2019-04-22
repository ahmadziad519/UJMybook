package com.uj.mybook.sell_book;

public class Order {
    private String bookName;
    private String imageUrl;
    private String author;
    private String description;
    private String category;
    private String bookType;
    private String price;
    private String stFullName;
    private String stNumber;
    private String date;
    private String time;
    private String place;

    public Order() {
    }

    public Order(String bookName, String imageUrl, String author, String description,
                 String category, String bookType, String price, String stFullName,
                 String stNumber, String date, String time, String place) {
        this.bookName = bookName;
        this.imageUrl = imageUrl;
        this.author = author;
        this.description = description;
        this.category = category;
        this.bookType = bookType;
        this.price = price;
        this.stFullName = stFullName;
        this.stNumber = stNumber;
        this.date = date;
        this.time = time;
        this.place = place;
    }

    public String getCategory() {
        return category;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStFullName() {
        return stFullName;
    }

    public void setStFullName(String stFullName) {
        this.stFullName = stFullName;
    }

    public String getStNumber() {
        return stNumber;
    }

    public void setStNumber(String stNumber) {
        this.stNumber = stNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
