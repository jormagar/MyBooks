package es.jormagar.myBooks;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import es.jormagar.myBooks.modelo.BookItem;

public class BookDetailFragment extends Fragment {
    private final String TAG = BookDetailFragment.class.getSimpleName();

    public static final String ITEM_KEY_ID = "item_id";
    public static final String ITEM_KEY_TITLE = "item_title";

    LiveData<BookItem> data;

    private String mTitle;

    private TextView mAuthor;
    private TextView mPublishedDate;
    private TextView mDescription;

    private CollapsingToolbarLayout appBarLayout;

    public BookDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ITEM_KEY_TITLE)) {

            mTitle = getArguments().getString(ITEM_KEY_TITLE);

            System.out.println("Titulo en fragment " + mTitle);

            appBarLayout = this.getActivity().findViewById(R.id.toolbar_layout);

            //Si no estamos en tablet estableceremos el título del detalle
            if (appBarLayout != null) {
                appBarLayout.setTitle(mTitle);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Guardamos referencias de la vista
        View rootView = inflater.inflate(R.layout.book_detail, container, false);

        mAuthor = rootView.findViewById(R.id.book_author);
        mPublishedDate = rootView.findViewById(R.id.book_published);
        mDescription = rootView.findViewById(R.id.book_description);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Iniciamos consulta de datos y adjuntamos un observer
        //cuando estén disponibles los datos los añadimos a las vistas
        System.out.println("mTitle en BookDetailFragment " + mTitle);

        BookViewModel mBookViewModel =
                ViewModelProviders.of(this).get(BookViewModel.class);

        if (mTitle != null) {
            data = mBookViewModel.getBookByTitle(mTitle);
            data.observe(BookDetailFragment.this, new Observer<BookItem>() {
                @Override
                public void onChanged(@Nullable BookItem bookItem) {
                    updateViews(bookItem);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (data != null && data.hasObservers()) {
            Log.d(TAG, "Eliminamos observer");
            data.removeObservers(this);
        }
    }

    public void updateViews(BookItem bookItem) {

        String author = "";
        String description = "";
        String date = "";

        //Debido a que no hay una estrategia definida en la práctica
        //si hay un fallo al intentar recuperar un item de la DB se muestra un diálogo
        //y se inicializan los valores a cadenas vacías

        //Forzar error
        //bookItem = null;

        if (bookItem != null) {
            //Si es móvil. En tablet no hay appBarLayout
            if (appBarLayout != null) {
                Picasso.get()
                        .load(bookItem.getImageUrl())
                        .into((ImageView) this.getActivity().findViewById(R.id.header));
            }

            author = bookItem.getAuthor();
            description = bookItem.getDescription();
            date = bookItem.getPublishedDate();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(R.string.data_not_available)
                    .setTitle(R.string.alert_title)
            .setPositiveButton(R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        mAuthor.setText(author);
        mPublishedDate.setText(date);
        mDescription.setText(description);
    }
}
