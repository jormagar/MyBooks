package es.jormagar.myBooks;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import es.jormagar.myBooks.modelo.BookItem;
import es.jormagar.myBooks.modelo.BookRepository;

/**
 * Clase encargada de la comunicación entre las vistas y los datos
 */
public class BookViewModel extends AndroidViewModel {

    BookRepository mRepository;
    private LiveData<List<BookItem>> mBooks;

    public BookViewModel(@NonNull Application application) {
        super(application);

        mRepository = new BookRepository(application);
        mBooks = mRepository.getBooks();
    }

    public LiveData<List<BookItem>> getBooks() {
        return mBooks;
    }

    public LiveData<List<BookItem>> getBooksOrderedByTitle() {
        return mRepository.getBooksOrderedByTitle();
    }

    public LiveData<List<BookItem>> getBooksOrderedByAuthor() {
        return mRepository.getBooksOrderedByAuthor();
    }

    public LiveData<BookItem> getBookByTitle(String title) {
        return mRepository.getBookByTitle(title);
    }


    public void insert(final BookItem book) {
        mRepository.insertBook(book);
    }

    public void deleteAll() {
        mRepository.deleteAllBooks();
    }

    public void deleteBookByTitle(String title) {
        mRepository.deleteBookByTitle(title);
    }
}
