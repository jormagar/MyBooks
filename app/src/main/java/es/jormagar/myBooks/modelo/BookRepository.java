package es.jormagar.myBooks.modelo;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

/**
 * Clase encargada de la comunicación entre BookViewModel y BookDao
 */
public class BookRepository {
    private BookDao mBookDao;
    private LiveData<List<BookItem>> mBooks;

    public BookRepository(Application application) {
        BooksDatabase db = BooksDatabase.getInstance(application);
        mBookDao = db.bookDao();
        mBooks = mBookDao.getBooks();
    }

    public LiveData<List<BookItem>> getBooks() {
        return mBookDao.getBooks();
    }

    public LiveData<List<BookItem>> getBooksOrderedByTitle() {
        return mBookDao.getBooksOrderedByTitle();
    }

    public LiveData<List<BookItem>> getBooksOrderedByAuthor() {
        return mBookDao.getBooksOrderedByAuthor();
    }

    public LiveData<BookItem> getBookByTitle(String title) {
        LiveData<BookItem> book = mBookDao.getBookByTitle(title);
        return book;
    }

    public void insertBook(final BookItem book) {
        new InsertTask(book).execute();
    }

    public void deleteBookByTitle(String title) {
        new DeleteTask(title).execute();
    }


    public void deleteAllBooks() {
        mBookDao.deleteAllBooks();
    }

    private class InsertTask extends AsyncTask<Void, Void, Void> {
        BookItem mBook;

        public InsertTask(BookItem book) {
            mBook = book;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //Insertamos el libro
            mBookDao.insertBook(mBook);
            return null;
        }
    }

    private class DeleteTask extends AsyncTask<Void, Void, Void> {
        String mTitle;

        public DeleteTask(String title) {
            mTitle = title;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //Eliminamos libro
            mBookDao.deleteBookByTitle(mTitle);
            return null;
        }
    }

}
