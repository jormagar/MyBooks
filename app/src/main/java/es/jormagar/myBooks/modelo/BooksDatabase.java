package es.jormagar.myBooks.modelo;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

//Para esta versión no se ha definido el esquema de la base de datos en json
//por eso exportSchema a false
@Database(entities = {BookItem.class}, version = 1, exportSchema = false)
public abstract class BooksDatabase extends RoomDatabase {

    private static volatile BooksDatabase INSTANCE;
    private static final String DATABASE_NAME = "books.db";

    public abstract BookDao bookDao();

    public static BooksDatabase getInstance(Context ctx) {
        if (INSTANCE == null) {
            synchronized (BooksDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(),
                            BooksDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

