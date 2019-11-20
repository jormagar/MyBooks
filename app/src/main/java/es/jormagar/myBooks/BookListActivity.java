package es.jormagar.myBooks;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import es.jormagar.myBooks.modelo.BookContent;
import es.jormagar.myBooks.modelo.BookItem;

public class BookListActivity extends AppCompatActivity {

    /**
     * Flag que indica si ejecutamos en una tablet
     */
    private final String TAG = BookListActivity.class.getSimpleName();
    private final String NOTIFICATION_ID_KEY = "notification_id";
    private final String BOOK_TITLE_KEY = "book_title";

    private boolean mTwoPane;
    private boolean isSigned = false;
    private final int GRID_COLUMNS = 2;
    private BookAdapter adapter;
    private BookContent bookContent;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    private final String FIREBASE_EMAIL = "*";
    private final String FIREBASE_PASSWORD = "*";
    private final String DATABASE_REF_CONNECTED = ".info/connected";
    private final String DATABASE_REF_BOOK_LIST = "books";

    private FirebaseAuth.AuthStateListener authStateListener;

    private SwipeRefreshLayout mSwipeContainer;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        //Inicializamos Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        //Configuramos Cloud Messaging
        setupForCloudMessaging();

        //Preparamos toolbar y título de app
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        // Solo si el layout para resoluciones w900dp está presente
        if (findViewById(R.id.book_detail_container) != null) {
            // Estamos en tablet
            mTwoPane = true;
        }

        mCoordinatorLayout = findViewById(R.id.coordinator);
        mSwipeContainer = findViewById(R.id.swipeContainer);

        //Preparamos vista de refresco
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                fetch();
            }
        });

        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        View recyclerView = findViewById(R.id.book_list);

        if (recyclerView != null) {
            // Preparamos recycler view
            setRecyclerView((RecyclerView) recyclerView);
        }

        if (isSigned == false) {
            //Iniciamos sesión, dispara el proceso de rellenado de lista
            mAuth.signInWithEmailAndPassword(FIREBASE_EMAIL, FIREBASE_PASSWORD);
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
        if (authStateListener == null) {
            //Añadimos listener de autenticación
            authStateListener = getAuthStateListener();
            mAuth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    private void setRecyclerView(RecyclerView recyclerView) {
        Log.d(TAG, "setRecyclerView");
        RecyclerView.LayoutManager lm = getLayoutForRecyclerView();
        recyclerView.setLayoutManager(lm);

        bookContent = new BookContent(getApplicationContext());

        //Creamos adapter sin items
        adapter = new BookAdapter(this, bookContent.ITEMS, mTwoPane);
        recyclerView.setAdapter(adapter);
    }

    private RecyclerView.LayoutManager getLayoutForRecyclerView() {
        RecyclerView.LayoutManager lm;

        if (mTwoPane) {
            lm = new LinearLayoutManager(this);
        } else {
            //Layout alturas asimétricas
            lm = new StaggeredGridLayoutManager(GRID_COLUMNS, StaggeredGridLayoutManager.VERTICAL);
        }

        return lm;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Ordenamos lista segun selección de menú
        switch (item.getItemId()) {
            case R.id.sort_by_title:
                sortListWithComparator(bookContent.ITEMS, BookItem.titleComparator);
                return true;

            case R.id.sort_by_author:
                sortListWithComparator(bookContent.ITEMS, BookItem.authorComparator);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sortListWithComparator(List list, Comparator comparator) {
        Collections.sort(list, comparator);
        //Avisamos al adapter que el origen  de datos ha cambiado para que se refresque
        adapter.notifyDataSetChanged();
    }

    private void successSignIn(FirebaseUser user) {
        Log.d(TAG, "successSignIn");
        fetch();
    }

    private void fetch() {
        Log.d(TAG, "fetch");
        //Comprobamos si tenemos conexión antes de sincronizar base de datos remota con app
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(DATABASE_REF_CONNECTED);

        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                boolean connected = snapshot.getValue(Boolean.class);

                //Si hay conexión procedemos a la sincronización, si no, cargamos base de datos local.
                if (connected) {
                    syncDatabase();
                } else {
                    loadLocalDatabaseItems();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }

    private void syncDatabase() {
        Log.d(TAG, "syncDatabase");

        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference(DATABASE_REF_BOOK_LIST);

        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<BookItem>> genericTypeIndicator = new GenericTypeIndicator<List<BookItem>>() {};
                List<BookItem> bookList = dataSnapshot.getValue(genericTypeIndicator);

                if (bookList != null) {
                    for (int i=0; i < bookList.size(); i++) {
                        //Internamente se establece como clave primaria el título del libro
                        //aprovechando la estrategia de conflicto en el DAO se establece:
                        //@Insert(onConflict = OnConflictStrategy.IGNORE)
                        //Evitando hacer una nueva consulta para ver si existe
                        BookContent.insert(bookList.get(i));
                    }

                    setItems(bookList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "Sincronización remota fallida");
                loadLocalDatabaseItems();
            }
        });
    }

    private void loadLocalDatabaseItems() {
        Log.d(TAG, "loadLocalDatabaseItems");

        BookContent.getBooks().observeForever(new Observer<List<BookItem>>() {
            @Override
            public void onChanged(@Nullable List<BookItem> bookList) {

                //Si tenemos datos locales, rellenamos lista
                if (bookList != null) {
                    setItems(bookList);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

                    builder.setMessage(R.string.data_not_available)
                            .setTitle(R.string.alert_title)
                            .setPositiveButton(R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    private void setItems(List<BookItem> list) {
        Log.d(TAG, "setItems");

        //Mapeamos caché de items en BookContent (util para el filtrado rápido sin consultar la db)
        //Y pasamos lista al adapter
        BookContent.ITEMS = list;
        setAdapterItems(list);
        checkIncomingIntent();
    }

    private void setAdapterItems(List<BookItem> items) {
        Log.d(TAG, "setAdapterItems");

        //Llenamos lista y refrescamos adapter
        adapter.setItems(items);

        //Si estamos en medio de un refresco ocultamos loader de refresco y mostramos snackbar
        if (mSwipeContainer.isRefreshing()) {
            mSwipeContainer.setRefreshing(false);
            Snackbar.make(mCoordinatorLayout, R.string.refreshed, Snackbar.LENGTH_LONG).show();
        }
    }

    private FirebaseAuth.AuthStateListener getAuthStateListener() {
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d(TAG, "FirebaseAuth.AuthStateListener > onAuthStateChanged");
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //Login correcto
                    Log.d(TAG, "loginSuccess");
                    isSigned = true;
                    successSignIn(user);
                } else {
                    Log.d(TAG, "loginError");
                    isSigned = false;
                }
            }
        };
    }

    private void setupForCloudMessaging() {
        Log.d(TAG, "setupForCloudMessaging");

        subscribeToTopic("my_books");
        logFirebaseToken();
    }

    private void checkIncomingIntent(){
        Log.d(TAG, "checkIncomingIntent");

        Intent incomingIntent = getIntent();

        switch (incomingIntent.getAction()) {
            case Intent.ACTION_DELETE:
                handleActionDeleteBook(incomingIntent);
                break;

            case Intent.ACTION_VIEW:
                handleActionDetailView(incomingIntent);
                break;

            default:
                break;
        }
    }

    private void handleActionDeleteBook(Intent intent){
        if (intent.getExtras() != null) {

            intent.setAction("");

            String mTitle = intent.getStringExtra(BOOK_TITLE_KEY);

            int mNotificationId = intent.getIntExtra(NOTIFICATION_ID_KEY, -1);

            if (mNotificationId > -1) {
                NotificationManagerCompat.from(this).cancel(mNotificationId);
            }

            Log.d(TAG, "Book to be deleted: " + mTitle);

            BookContent.deleteBookByTitle(mTitle);
            adapter.removeItem(mTitle);
        }
    }

    private void handleActionDetailView(Intent intent){
        Log.d(TAG, "handleActionDetailView");

        if (mTwoPane && intent.getExtras() != null) {

            intent.setAction("");

            String mTitle = intent.getStringExtra(BOOK_TITLE_KEY);

            int mNotificationId = intent.getIntExtra(NOTIFICATION_ID_KEY, -1);

            if (mNotificationId > -1) {
                NotificationManagerCompat.from(this).cancel(mNotificationId);
            }

            Log.d(TAG, "Book to be viewed: " + mTitle);

            Bundle arguments = new Bundle();
            arguments.putString(BookDetailFragment.ITEM_KEY_TITLE, mTitle);

            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.book_detail_container, fragment)
                    .commit();
        }
    }

    private void logFirebaseToken() {
        Log.d(TAG, "logFirebaseToken");

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(BookListActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void subscribeToTopic(final String topic) {
        Log.d(TAG, "subscribeToTopic" + topic);
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed, topic);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed, topic);
                        }

                        Log.d(TAG, msg);
                        Toast.makeText(BookListActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
