package es.jormagar.myBooks;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BookViewHolder extends RecyclerView.ViewHolder {

    TextView mTitle;
    TextView mAuthor;
    ImageView mPoster;

    public BookViewHolder(View view) {
        super(view);
        mTitle = view.findViewById(R.id.title);
        mAuthor = view.findViewById(R.id.author);
        mPoster = view.findViewById(R.id.poster);
    }
}
