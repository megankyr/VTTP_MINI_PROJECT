package com.ssf.mini.project.service;

import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

import com.ssf.mini.project.model.CountryCode;
import com.ssf.mini.project.model.GenreCode;
import com.ssf.mini.project.model.Movie;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

@Service
public class MovieService {

        @Value("${accesstoken}")
        private String accessToken;

        private List<GenreCode> gcodes = null;
        private List<CountryCode> ccodes = null;

        public List<Movie> getMoviesByTitle(String title) {
                try {
                        String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());

                        String url = UriComponentsBuilder
                                        .fromUriString("https://api.themoviedb.org/3/search/movie")
                                        .queryParam("query", encodedTitle)
                                        .toUriString();

                        RequestEntity<Void> req = RequestEntity.get(url)
                                        .header("Authorization",
                                                        accessToken)
                                        .build();

                        RestTemplate template = new RestTemplate();
                        ResponseEntity<String> resp = template.exchange(req, String.class);

                        String payload = resp.getBody();
                        JsonReader reader = Json.createReader(new StringReader(payload));
                        JsonObject result = reader.readObject();
                        JsonArray movies = result.getJsonArray("results");

                        return processMovies(movies);

                } catch (Exception e) {
                        e.printStackTrace();
                }

                return null;
        }

        public List<Movie> getMoviesByGenre(String genre) {

                String url = UriComponentsBuilder
                                .fromUriString("https://api.themoviedb.org/3/discover/movie")
                                .queryParam("with_genres", genre)
                                .toUriString();

                RequestEntity<Void> req = RequestEntity.get(url)
                                .header("Authorization",
                                                accessToken)
                                .build();

                RestTemplate template = new RestTemplate();
                ResponseEntity<String> resp = template.exchange(req, String.class);

                String payload = resp.getBody();
                JsonReader reader = Json.createReader(new StringReader(payload));
                JsonObject result = reader.readObject();
                JsonArray movies = result.getJsonArray("results");

                return processMovies(movies);
        }

        public List<Movie> getMoviesByCountry(String country) {

                String url = UriComponentsBuilder
                                .fromUriString("https://api.themoviedb.org/3/discover/movie")
                                .queryParam("with_origin_country", country)
                                .toUriString();

                RequestEntity<Void> req = RequestEntity.get(url)
                                .header("Authorization",
                                                accessToken)
                                .build();

                RestTemplate template = new RestTemplate();
                ResponseEntity<String> resp = template.exchange(req, String.class);

                String payload = resp.getBody();
                JsonReader reader = Json.createReader(new StringReader(payload));
                JsonObject result = reader.readObject();
                JsonArray movies = result.getJsonArray("results");

                return processMovies(movies);
        }

        private List<Movie> processMovies(JsonArray movies) {
                List<Movie> movieList = new ArrayList<>();
                for (JsonValue movieValue : movies) {
                        JsonObject movieObject = (JsonObject) movieValue;

                        String title = movieObject.getString("title", "");
                        String releaseDate = movieObject.getString("release_date", "");
                        String overview = movieObject.getString("overview", "");
                        String imagePath = movieObject.getString("poster_path", "");
                        String imageUrl = "http://image.tmdb.org/t/p/w500" + imagePath;

                        Movie movie = new Movie(title, releaseDate, overview, imageUrl);
                        movieList.add(movie);
                }
                return movieList;
        }

        public List<GenreCode> getGenreCode() {
                if (gcodes == null) {
                        String url = UriComponentsBuilder
                                        .fromUriString("https://api.themoviedb.org/3/genre/movie/list")
                                        .toUriString();

                        RequestEntity<Void> req = RequestEntity
                                        .get(url)
                                        .header("Authorization",
                                                        accessToken)
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
                        gcodes = genreCodes;

                }

                return gcodes;
        }

        public List<CountryCode> getCountryCode() {
                if (ccodes == null) {
                        String url = UriComponentsBuilder
                                        .fromUriString("https://api.themoviedb.org/3/configuration/countries")
                                        .toUriString();

                        RequestEntity<Void> req = RequestEntity
                                        .get(url)
                                        .header("Authorization",
                                                        accessToken)
                                        .build();

                        RestTemplate template = new RestTemplate();

                        ResponseEntity<String> resp = template.exchange(req, String.class);
                        String payload = resp.getBody();

                        JsonReader reader = Json.createReader(new StringReader(payload));
                        JsonArray countries = reader.readArray();

                        List<CountryCode> countryCodes = new ArrayList<>();
                        for (JsonValue countryValue : countries) {
                                JsonObject jsonObject = countryValue.asJsonObject();
                                String code = jsonObject.getString("iso_3166_1");
                                String name = jsonObject.getString("english_name");

                                CountryCode countryCode = new CountryCode(code, name);
                                countryCodes.add(countryCode);
                        }
                        Collections.sort(countryCodes, Comparator.comparing(CountryCode::name));
                        ccodes = countryCodes;
                }
                return ccodes;
        }
}