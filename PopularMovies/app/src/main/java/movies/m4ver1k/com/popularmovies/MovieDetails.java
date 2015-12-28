package movies.m4ver1k.com.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Movie movie = (Movie) getIntent().getExtras().getSerializable("movie");

        ((TextView) findViewById(R.id.detailsTitle)).setText(movie.getTitle());
        ((TextView) findViewById(R.id.detailsOverview)).setText(movie.getOverview());
        ((TextView) findViewById(R.id.detailsRatingAvg)).setText(movie.getVoteAvg());
        ((TextView) findViewById(R.id.detailsDate)).setText(movie.getReleaseDate());

        ImageView imageView = (ImageView) findViewById(R.id.detailsImage);

        Picasso.with(this).
                load(movie.getPosterPath()).
                resize(600, 800).centerCrop().
                into(imageView);
    }

}
