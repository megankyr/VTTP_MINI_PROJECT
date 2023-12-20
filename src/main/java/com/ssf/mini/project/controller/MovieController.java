package com.ssf.mini.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
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

    @GetMapping("/index")
    public ModelAndView getIndex() {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("gcodes", movieService.getGenreCode());
        mav.addObject("ccodes", movieService.getCountryCode());
        return mav;
    }

    @GetMapping("/movies")
    public ModelAndView getMovieData(@RequestParam MultiValueMap<String, String> queryParams) {
        ModelAndView mav = new ModelAndView("movies");
        String genre = queryParams.getFirst("genre");
        String country = queryParams.getFirst("country");
        List<Movie> movies = movieService.getMovies(genre, country);
        mav.addObject("genre", genre);
        mav.addObject("country",country);
        mav.addObject("movies", movies);

        return mav;
    }

}
