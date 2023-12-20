package com.ssf.mini.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.ssf.mini.project.model.Movie;
import com.ssf.mini.project.service.MovieService;

@RestController
@RequestMapping
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("search/title")
    public ModelAndView searchTitle() {
        ModelAndView mav = new ModelAndView("title");
        return mav;
    }

    @GetMapping("search/genre")
    public ModelAndView searchGenre() {
        ModelAndView mav = new ModelAndView("genre");
        mav.addObject("gcodes", movieService.getGenreCode());
        return mav;
    }

    @GetMapping("search/country")
    public ModelAndView searchCountry() {
        ModelAndView mav = new ModelAndView("country");
        mav.addObject("ccodes", movieService.getCountryCode());
        return mav;
    }

    @GetMapping("/movies/title")
    public ModelAndView getMovieByTitle(@RequestParam String title) {
        ModelAndView mav = new ModelAndView("titlemovies");
        List<Movie> titleMovies = movieService.getMoviesByTitle(title);
        mav.addObject("movies", titleMovies);
        return mav;

    }

    @GetMapping("/movies/genre")
    public ModelAndView getMovieByGenre(@RequestParam String genre) {
        ModelAndView mav = new ModelAndView("genremovies");
        List<Movie> genreMovies = movieService.getMoviesByGenre(genre);
        mav.addObject("movies", genreMovies);
        return mav;
    }

    @GetMapping("/movies/country")
    public ModelAndView getMovieByCountry(@RequestParam String country) {
        ModelAndView mav = new ModelAndView("countrymovies");
        List<Movie> countryMovies = movieService.getMoviesByCountry(country);
        mav.addObject("movies", countryMovies);
        return mav;
    }
}