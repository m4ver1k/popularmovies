package movies.m4ver1k.com.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieListing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_listing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FetchData fetchData = new FetchData(this);
        fetchData.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort_popular) {
            FetchData fetchData = new FetchData(this);
            fetchData.execute("popularity.desc");
            return true;
        }
        if(id== R.id.action_sort_rating){
            FetchData fetchData = new FetchData(this);
            fetchData.execute("vote_average.desc");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchData extends AsyncTask<String,Integer,Map<String,Movie>> {

        private Activity context ;
        private Map<String,Movie> moviesMap;
        public FetchData(Activity context){
            this.context=context;
        }

        @Override
        protected Map<String, Movie> doInBackground(String... params) {

            final String SCHEME="http";
            final String BASE_URL="api.themoviedb.org";
            final String VERSION="3";
            final String DISCOVER_API="discover";
            final String MOVIE_API="movie";
            final String IMAGE_BASE="http://image.tmdb.org/t/p/w185/";
            //TODO Insert API KEY Here.
            final String API_KEY="";


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String popularMoviesJsonStr;

            try {

                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.scheme(SCHEME)
                        .authority(BASE_URL)
                        .appendPath(VERSION)
                        .appendPath(DISCOVER_API)
                        .appendPath(MOVIE_API)
                        .appendQueryParameter("api_key",API_KEY);

                if(params!=null && params.length > 0) {
                    uriBuilder.appendQueryParameter("sort_by", params[0]);
                }

                URL url = new URL(uriBuilder.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // cannot proceed further.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.

                }else {
                    popularMoviesJsonStr = buffer.toString();

                    JSONObject popularMoviesJson= new JSONObject(popularMoviesJsonStr);
                    JSONArray popularMovies = popularMoviesJson.getJSONArray("results");
                     moviesMap = new HashMap<>();
                    Movie movie ;
                    for(int i=0;i<popularMovies.length();i++ ){
                        JSONObject movieObj = popularMovies.getJSONObject(i);
                        movie = new Movie();
                        movie.setId(movieObj.getString("id"));
                        movie.setOverview(movieObj.getString("overview"));
                        movie.setPosterPath(IMAGE_BASE + movieObj.getString("poster_path"));
                        movie.setReleaseDate(movieObj.getString("release_date"));
                        movie.setTitle(movieObj.getString("title"));
                        movie.setVoteAvg(movieObj.getString("vote_average"));
                        moviesMap.put(movie.getId(),movie);
                    }
                }
            } catch (IOException e) {
                Log.e("MovieListing", "Error ", e);
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Listing Activity", "Error closing stream", e);
                    }
                }
            }
            return moviesMap;
        }

        @Override
        protected void onPostExecute(Map<String, Movie> stringMovieMap) {
            GridView gridView = (GridView) findViewById(R.id.gridView);
            gridView.setAdapter(new MovieListAdapter(context,new ArrayList<>(moviesMap.values())));

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent  = new Intent(context,MovieDetails.class);
                    intent.putExtra("movie",new ArrayList<>(moviesMap.values()).get(position));
                    startActivity(intent);
                }
            });
        }
    }
}
