package ru.pomerantsevp.udacity.popularmovies.data;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by pavel on 9/27/15.
 */
public interface MovieService {
    @GET("/3/discover/movie")
    void listMovies(@Query("sort_by") String sortBy,
                    @Query("api_key") String apiKey,
                    Callback<MoviesResponse> cb);

    @GET("/3/movie/{id}/videos")
    void listTrailers(@Path("id") String id,
                      @Query("api_key") String apiKey,
                      Callback<TrailersResponse> cb);
}
