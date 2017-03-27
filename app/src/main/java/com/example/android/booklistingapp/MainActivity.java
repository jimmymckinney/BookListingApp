package com.example.android.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private String mRequestUrl;
    private EditText searchText;
    private ImageButton searchButton;
    private BookAdapter mAdapter;
    public static final String LOG_TAG = MainActivity.class.getName();
    private static final int BOOK_LOADER_ID = 1;
    private TextView mEmptyStateTextView;
    private String mUserInput;
    private ProgressBar progress;
    private ListView bookListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        bookListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        progress = (ProgressBar) findViewById(R.id.progress_bar);
        if (!checkNetworkActivity()) {
            progress.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_connection);
        } else {
            // Create a new {@link ArrayAdapter} of books
            mAdapter = new BookAdapter(this, new ArrayList<Book>());

            // Set the adapter on the {@link ListView}
            // so the list can be populated in the user interface
            bookListView.setAdapter(mAdapter);

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        }


        //Find the Button object
        searchButton = (ImageButton) findViewById(R.id.search_button);

        //Search Button action
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //bookListView.setVisibility(View.GONE);
                mEmptyStateTextView.setText(null); //Sets emptyText to null so that progress loader doesn't overlay emptyText
                mAdapter.clear(); //Clears adapter so progress loader doesn't overlay the last search results
                if (!checkNetworkActivity()) {
                    progress.setVisibility(View.GONE);
                    mEmptyStateTextView.setText(R.string.no_connection);
                } else {
                    searchText = (EditText) findViewById(R.id.search_text);
                    String userInput = searchText.getText().toString();
                    if (userInput.isEmpty()) {
                        Context context = getApplicationContext();
                        CharSequence text = "Nothing Entered in Search";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                        //Exits the method early because nothing else is needed
                        return;

                    } else {
                        mUserInput = userInput.replaceAll(" ", "%20");
                        mRequestUrl = "https://www.googleapis.com/books/v1/volumes?q=" + mUserInput + "&maxResults=10";
                    }
                    // Get a reference to the LoaderManager, in order to interact with loaders.
                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                    Log.v("Context", mRequestUrl);
                }
            }
        });
    }

    protected boolean checkNetworkActivity() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        progress.setVisibility(View.VISIBLE);
        Log.v("Loader", "onCreateLoader");
        // Create a new loader for the given URL
        return new BookLoader(this, mRequestUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        progress.setVisibility(View.GONE);
        //bookListView.setVisibility(View.VISIBLE);

        //Set empty state text
        mEmptyStateTextView.setText(R.string.no_books);

        if (mUserInput == null) {
            mEmptyStateTextView.setText(R.string.instructions);
        }

        Log.v("Loader", "onLoadFinished");
        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        Log.v("Loader", "onLoaderReset");
        mAdapter.clear();
    }
}
