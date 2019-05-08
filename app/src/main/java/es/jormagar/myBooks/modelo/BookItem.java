package es.jormagar.myBooks.modelo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Comparator;

//Modelo de datos
@Entity(tableName = "Books")
public class BookItem {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "title")
    private String title;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "author")
    private String author;
    @ColumnInfo(name = "imageurl")
    private String imageUrl;
    @ColumnInfo(name = "publisheddate")
    private String publishedDate;

    @Ignore
    public BookItem() {}


    public BookItem(String title, String description, String author, String imageUrl, String publishedDate) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.imageUrl = imageUrl;
        this.publishedDate = publishedDate;
    }

    public static Comparator<BookItem> titleComparator = new Comparator<BookItem>() {
        @Override
        public int compare(BookItem o1, BookItem o2) {
            return o1.title.compareTo(o2.title);
        }
    };

    public static Comparator<BookItem> authorComparator = new Comparator<BookItem>() {
        @Override
        public int compare(BookItem o1, BookItem o2) {
            return o1.author.compareTo(o2.author);
        }
    };

    @Override
    public String toString() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }
}
