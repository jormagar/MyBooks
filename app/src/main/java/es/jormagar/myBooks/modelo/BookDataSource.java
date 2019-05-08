package es.jormagar.myBooks.modelo;

import android.arch.lifecycle.LiveData;

import java.util.List;

public interface BookDataSource {
    LiveData<List<BookItem>> getBooks();

    LiveData<BookItem> getBookByTitle(String title);

    void insertBook(BookItem book);

    void deleteBookByTitle(String title);

    void deleteAllBooks();
}
