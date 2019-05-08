package es.jormagar.myBooks.modelo;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class BookContent {

    public static List<BookItem> ITEMS = new ArrayList<>();

    private static BookDataSource mProvideUserDataSource;

    public BookContent(Context ctx) {
        //Inicializamos
        mProvideUserDataSource = provideUserDataSource(ctx);
    }

    public static BookDataSource provideUserDataSource(Context context) {
        //Obtenemos instancia de DAO para acceder a los datos
        return new LocalBookDataSource(BooksDatabase.getInstance(context).bookDao());
    }

    public static LiveData<BookItem> getBookByTitle(String title) {
        //Obtenemos LiveData del libro que buscamos
        return mProvideUserDataSource.getBookByTitle(title);
    }

    public static LiveData<List<BookItem>> getBooks() {
        //Obtenemos LiveData de la lista de libros
        return mProvideUserDataSource.getBooks();
    }


    public static void insert(final BookItem book) {
        //La estrategia de persistencia trata de insertar un dato en la db
        //si este ya existiera (se utiliza el título del libro como PK)
        //se ignora la inserción por tanto, solo se inserta si no existe en la DB
        //tal y como proponía el ejercicio
        mProvideUserDataSource.insertBook(book);
    }

    public static void deleteBookByTitle(String title) {
        mProvideUserDataSource.deleteBookByTitle(title);
    }

    public static boolean isEven(String title) {
        int size = ITEMS.size();

        boolean result = false;

        for (int i = 0; i < size; i++) {
            if (ITEMS.get(i).getTitle().compareTo(title) == 0) {
                result = (i % 2 == 0);
                break;
            }
        }
        return result;
    }
}
