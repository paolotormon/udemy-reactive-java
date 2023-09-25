package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.domain.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class MovieReactiveService {

    private MovieInfoService movieInfoService;
    private ReviewService reviewService;

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
    }

    public Flux<Movie> getAllMovies() {
        Flux<MovieInfo> moviesInfoFlux = movieInfoService.retrieveMoviesFlux();

        var movieFlux = moviesInfoFlux.flatMap(movieInfo -> {
            Mono<List<Review>> reviewsMono = reviewService
                    .retrieveReviewsFlux(movieInfo.getMovieInfoId())
                    .collectList();
            //map only runs once. Just used for creating Movie object
            var movieMono = reviewsMono.map(reviewsList -> new Movie(movieInfo, reviewsList));
            return movieMono;
        });

        return movieFlux;
    }
}
