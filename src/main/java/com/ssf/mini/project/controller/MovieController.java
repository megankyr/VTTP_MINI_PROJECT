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

    @GetMapping("/genres")
    public ModelAndView getGenres() {
        ModelAndView mav = new ModelAndView("genres");
        mav.addObject("codes", movieService.getGenreCode());
        return mav;
    }

    @GetMapping("/movies")
    public ModelAndView getMovieData(@RequestParam String genre) {
        ModelAndView mav = new ModelAndView("movies");
        List<Movie> movies = movieService.getMovies(genre);
        mav.addObject("genre", genre);
        mav.addObject("movies", movies);

        return mav;
    }

}