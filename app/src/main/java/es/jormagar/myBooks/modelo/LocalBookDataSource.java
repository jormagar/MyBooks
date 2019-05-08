package es.jormagar.myBooks.modelo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Delete;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.util.List;

public class LocalBookDataSource implements BookDataSource {
    private static BookDao mBookDao;

    //Repositorio de datos
    public LocalBookDataSource(BookDao bookDao) {
        mBookDao = bookDao;
    }

    @Override
    public LiveData<List<BookItem>> getBooks() {
        return mBookDao.getBooks();
    }

    @Override
    public LiveData<BookItem> getBookByTitle(String title) {
        LiveData<BookItem> book = mBookDao.getBookByTitle(title);
        return book;
    }

    @Override
    public void insertBook(final BookItem book) {
        //Insertamos de forma asincrona
        new InsertTask(book).execute();
    }

    @Override
    public void deleteBookByTitle(String title) {
        new DeleteTask(title).execute();
    }

    @Override
    public void deleteAllBooks() {
        mBookDao.deleteAllBooks();
    }

    private static class InsertTask extends AsyncTask<Void, Void, Void> {
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

    private static class DeleteTask extends AsyncTask<Void, Void, Void> {
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