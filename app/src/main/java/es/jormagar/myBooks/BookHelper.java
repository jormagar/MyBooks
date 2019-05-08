package es.jormagar.myBooks;

import java.util.ArrayList;
import java.util.List;

import es.jormagar.myBooks.modelo.BookItem;

public class BookHelper {

    public static List<BookItem> mBooks = new ArrayList<>();

    public static boolean isEven(String title) {
        int size = mBooks.size();

        boolean result = false;

        for (int i = 0; i < size; i++) {
            if (mBooks.get(i).getTitle().compareTo(title) == 0) {
                result = (i % 2 == 0);
                break;
            }
        }
        return result;
    }
}
