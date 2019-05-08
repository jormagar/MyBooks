package es.jormagar.myBooks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class BookDetailActivity extends AppCompatActivity {

    private final String TAG = BookDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Preparamos action bar para poder volver
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Si no existe el fragment se añade. Según el ciclo de vida
        // de los fragment este es reinsertado ante eventos de tipo
        // rotación de pantalla, etc.
        if (savedInstanceState == null) {
            Bundle mBundle = new Bundle();

            int itemId = -1;
            String title = getIntent().getStringExtra("book_title");

            if (title == null) {
                itemId = getIntent().getIntExtra(BookDetailFragment.ITEM_KEY_ID, 0);
                title = getIntent().getStringExtra(BookDetailFragment.ITEM_KEY_TITLE);
            }

            int mNotificationId = getIntent().getIntExtra("notification_id", -1);

            if (mNotificationId > -1) {
                NotificationManagerCompat.from(this).cancel(mNotificationId);
            }

            mBundle.putInt(BookDetailFragment.ITEM_KEY_ID, itemId);
            mBundle.putString(BookDetailFragment.ITEM_KEY_TITLE, title);

            BookDetailFragment fragment = new BookDetailFragment();

            fragment.setArguments(mBundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.book_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
