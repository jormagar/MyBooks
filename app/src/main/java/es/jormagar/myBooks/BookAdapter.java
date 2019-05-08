package es.jormagar.myBooks;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import es.jormagar.myBooks.modelo.BookItem;

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

    private List<BookItem> mBooks;

    private final BookListActivity mParentActivity;
    private final boolean mTwoPane;

    public BookAdapter(BookListActivity parent, boolean twoPane) {
        mBooks = new ArrayList<>();
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Diseño alternativo tipo card siguiendo guías Material Design
        //https://material.io/design/components/cards.html#specs
        int layout = R.layout.book_list_content_card;

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        return new BookViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        //¿es par o impar?
        return position % 2;
    }

    @Override
    public void onBindViewHolder(final BookViewHolder holder, int position) {

        BookItem book = mBooks.get(position);

        //Mapeamos datos con elementos de la vista
        holder.mTitle.setText(book.getTitle());
        holder.mAuthor.setText(book.getAuthor());

        //Cargamos imagen remota aplicando transformación
        //a blanco y negro
        Picasso.get()
                .load(book.getImageUrl())
                .placeholder(R.drawable.pic)
                .transform(new GrayScaleTransformation(Picasso.get()))
                .into(holder.mPoster);

        //Establecemos listener en la fila
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = holder.getLayoutPosition();
                String title = holder.mTitle.getText().toString();

                //Si es tablet, cargamos datos en la ventana detalle contenedor
                //si no cargamos la actividad detalle en una nueva activity
                if (mTwoPane) {
                    Bundle arguments = new Bundle();

                    arguments.putInt(BookDetailFragment.ITEM_KEY_ID, position);
                    arguments.putString(BookDetailFragment.ITEM_KEY_TITLE, title);

                    BookDetailFragment fragment = new BookDetailFragment();
                    fragment.setArguments(arguments);

                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.book_detail_container, fragment)
                            .commit();
                } else {
                    Context ctx = view.getContext();

                    Intent intent = new Intent(ctx, BookDetailActivity.class);
                    intent.putExtra(BookDetailFragment.ITEM_KEY_ID, position);
                    intent.putExtra(BookDetailFragment.ITEM_KEY_TITLE, title);

                    ctx.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    public List<BookItem> getItems() {
        return mBooks;
    }

    public void setItems(List<BookItem> items) {
        mBooks = items;
        notifyDataSetChanged();
    }

    public void removeItem(String title) {
        int size = getItemCount();

        for (int i = 0; i < size; i++) {
            if (mBooks.get(i).getTitle().compareTo(title) == 0) {
                removeAt(i);
                break;
            }
        }
    }

    public void removeAt(int position) {
        mBooks.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mBooks.size());
    }
}