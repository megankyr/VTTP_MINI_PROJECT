package com.ssf.mini.project.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ssf.mini.project.model.GenreCode;
import com.ssf.mini.project.model.Movie;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

@Service
public class MovieService {

    @Value("${movieapi.key}")
    private String apiKey;

    private List<GenreCode> codes = null;

    // to retrieve the list of movies by genre
    public List<Movie> getMovies(String genre) {

        String url = UriComponentsBuilder
                .fromUriString("https://api.themoviedb.org/3/discover/movie")
                .queryParam("with_genres", genre)
                .toUriString();

        RequestEntity<Void> req = RequestEntity.get(url)
                .header("Authorization",
                        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJiMDZhMDkyNzU3MDNiNDY3MGFhZWJkZjRlNTk1NTNhOSIsInN1YiI6IjY1ODExMGE1YmYwZjYzMDhhZTYyM2YzZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.sxCJm3qOl4locHE9EM2jpGdmqF4sHVAUGW0OLr-bhuQ")
                .build();

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> resp = template.exchange(req, String.class);

        String payload = resp.getBody();
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject result = reader.readObject();
        JsonArray movies = result.getJsonArray("results");

        return processMovies(movies);
    }

    // to append fields to the movies retrieved
    private List<Movie> processMovies(JsonArray movies) {
        List<Movie> movieList = new ArrayList<>();
        for (JsonValue movieValue : movies) {
            JsonObject movieObject = (JsonObject) movieValue;

            String title = movieObject.getString("title");
            String releaseDate = movieObject.getString("release_date");
            String overview = movieObject.getString("overview");
            String imagePath = movieObject.getString("poster_path");
            String imageUrl = "http://image.tmdb.org/t/p/w500" + imagePath;

            Movie movie = new Movie(title, releaseDate, overview, imageUrl);
            movieList.add(movie);

        }
        return movieList;
    }

    public List<GenreCode> getGenreCode() {
        if (codes == null) {
            String url = UriComponentsBuilder
                    .fromUriString("https://api.themoviedb.org/3/genre/movie/list")
                    .toUriString();

            RequestEntity<Void> req = RequestEntity
                    .get(url)
                    .header("Authorization",
                            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJiMDZhMDkyNzU3MDNiNDY3MGFhZWJkZjRlNTk1NTNhOSIsInN1YiI6IjY1ODExMGE1YmYwZjYzMDhhZTYyM2YzZCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.sxCJm3qOl4locHE9EM2jpGdmqF4sHVAUGW0OLr-bhuQ")
                    .build();

            RestTemplate template = new RestTemplate();

            ResponseEntity<String> resp = template.exchange(req, String.class);
            String payload = resp.getBody();

            JsonReader reader = Json.createReader(new StringReader(payload));
            JsonObject result = reader.readObject();
            JsonArray genres = result.getJsonArray("genres");

            List<GenreCode> genreCodes = new ArrayList<>();
            for (JsonValue genreValue : genres) {
                JsonObject genreObject = (JsonObject) genreValue;
                Integer code = genreObject.getInt("id");
                String name = genreObject.getString("name");

                GenreCode genreCode = new GenreCode(code, name);
                genreCodes.add(genreCode);
            }

            Collections.sort(genreCodes, Comparator.comparing(GenreCode::name));
            codes = genreCodes;

        }

        return codes;
    }
}