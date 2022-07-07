package com.tmdbapp.views;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tmdbapp.R;
import com.tmdbapp.api.APIClient;
import com.tmdbapp.models.Genres;
import com.tmdbapp.models.MovieModel;
import com.tmdbapp.utils.date.DateUtils;
import com.tmdbapp.viewmodels.MovieDetailsViewModel;
import com.tmdbapp.viewmodels.MovieViewModel;
import com.tmdbapp.viewmodels.ViewModelFactory;


public class MovieDetailsActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    private MovieDetailsViewModel movieDetailsViewModel;
    private MovieModel movie;
    private Toast toastMessage;
    private Target target;
    private String fromWhere;

    private ScrollView scrollView;
    private ImageView constraintLayout;
    private ImageView posterImage;
    private TextView firstProducer;
    private TextView genreTimeDate;
    private TextView movieTitle;
    private TextView yearReleased;
    private TextView overviewDescription;
    private RatingBar ratings;
    private SwipeRefreshLayout refresh;
    private MovieViewModel movieViewModel;

    public void setRefresh(SwipeRefreshLayout refresh) {
        this.refresh = refresh;
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        int id = this.getIntent().getIntExtra("id", 0);
        this.fromWhere = this.getIntent().getStringExtra("fromWhere");
        ViewModelFactory viewModelFactory = ViewModelFactory.createFactory(this);
        this.movieDetailsViewModel = new ViewModelProvider(this, viewModelFactory).get(MovieDetailsViewModel.class);

        this.scrollView = findViewById(R.id.scrollview_movie_details);
        this.constraintLayout = findViewById(R.id.constraint_layout_movie_details);
        this.posterImage = findViewById(R.id.poster_image);
        this.firstProducer = findViewById(R.id.first_producer);
        this.genreTimeDate = findViewById(R.id.genre_time_date);
        this.movieTitle = findViewById(R.id.title_movie);
        this.yearReleased = findViewById(R.id.release_year);
        this.overviewDescription = findViewById(R.id.overview_description);
        this.ratings = findViewById(R.id.movie_rating);
        this.refresh= findViewById(R.id.refresDetails);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(MovieDetailsActivity.this, MovieDetailsActivity.class);
                intent.putExtra("id", movie.getId());
                intent.putExtra("fromWhere", fromWhere);
                startActivity(intent);

            }
        });

        this.movieDetailsViewModel.setMovie(id).observe(this, this::loadMovie);
        this.movieDetailsViewModel.getFavoriteMessage().observe(this, this::displayFavoriteToast);
    }
    private void loadMovie(MovieModel movie) {
        if (movie == null)
            return;

        this.movie = movie;
        this.firstProducer.setText(movie.getDirectorName());
        String poster = APIClient.getFullPosterPath(this.movie.getPosterPath());
        String backgroundPoster = APIClient.getFullPosterPath(this.movie.getBackdropPath());
        Glide.with(this).load(backgroundPoster).transform(new CenterCrop()).into(constraintLayout);
        Glide.with(this).load(poster).transform(new CenterCrop()).into(posterImage);
        this.movieTitle.setText(this.movie.getTitle());

        String date = DateUtils.formatDate(this.movie.getReleaseDate());
        this.yearReleased.setText(String.format("(%s)", date.substring(date.length() - 4)));
        this.ratings.setRating(Float.parseFloat(String.valueOf(this.movie.getVoteAverage())) / 2);

        if (!(this.movie.getOverview() != null && this.movie.getOverview().isEmpty()))
            this.overviewDescription.setText(this.movie.getOverview());
        else
            this.overviewDescription.setText(this.getResources().getString(R.string.no_description));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.overviewDescription.setJustificationMode(Layout.JUSTIFICATION_MODE_INTER_WORD);
        }

        StringBuilder genres = new StringBuilder();
        for (int id : this.movie.getGenreIds())
            genres.append(Genres.getNameById(id)).append(", ");
        genres = new StringBuilder(genres.substring(0, genres.length() - 2));

        this.genreTimeDate.setText(String.format(String.valueOf(genres), DateUtils.formatDate(this.movie.getReleaseDate())));
    }
    private void displayFavoriteToast(String message) {
        if (this.toastMessage != null)
            this.toastMessage.cancel();

        this.toastMessage = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        this.toastMessage.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.languaje,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id= item.getItemId();
        if (id== R.id.langEng){
            APIClient.LANGUAGE= "en";
            Intent intent = new Intent(MovieDetailsActivity.this, MainActivity.class);
            intent.putExtra("id", movie.getId());
            intent.putExtra("fromWhere", fromWhere);
            startActivity(intent);
            finish();

        }
        if (id== R.id.langSpa){
            APIClient.LANGUAGE= "es";
            Intent intent = new Intent(MovieDetailsActivity.this, MainActivity.class);
            intent.putExtra("id", movie.getId());
            intent.putExtra("fromWhere", fromWhere);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
