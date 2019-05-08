package es.jormagar.myBooks.modelo;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM Books")
    LiveData<List<BookItem>> getBooks();

    @Query("SELECT * FROM Books ORDER BY title ASC")
    LiveData<List<BookItem>> getBooksOrderedByTitle();

    @Query("SELECT * FROM Books ORDER BY author ASC")
    LiveData<List<BookItem>> getBooksOrderedByAuthor();

    @Query("SELECT * FROM Books WHERE title = :title")
    LiveData<BookItem> getBookByTitle(String title);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertBook(BookItem book);

    @Query("DELETE FROM Books WHERE title = :title")
    void deleteBookByTitle(String title);

    @Query("DELETE FROM Books")
    void deleteAllBooks();
}
