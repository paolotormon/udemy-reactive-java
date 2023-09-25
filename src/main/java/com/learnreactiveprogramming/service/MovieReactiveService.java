package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.domain.Review;
import com.learnreactiveprogramming.exception.MovieException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class MovieReactiveService {

    private MovieInfoService movieInfoService;
    private ReviewService reviewService;

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
    }

    public Flux<Movie> getAllMovies() {
        Flux<MovieInfo> moviesInfoFlux = movieInfoService.retrieveMoviesFlux();

        Flux<Movie> movieFlux = moviesInfoFlux.flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsListMono = reviewService
                            .retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();

                    // map only runs once in this loop. Just used for creating Movie object
                    Mono<Movie> movieMono = reviewsListMono.map(reviewsList -> new Movie(movieInfo, reviewsList));
                    return movieMono; // Inside flatMap, so Mono<Movie> is converted to Movie
                })
                .onErrorMap(ex -> {
                    log.error("Exception is: ", ex);
                    throw new MovieException(ex.getMessage());
                })
                .log();
        return movieFlux;
    }

    public Mono<Movie> getMovieById(long movieId) {
        Mono<MovieInfo> movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        Mono<List<Review>> reviewsListMono = reviewService
                .retrieveReviewsFlux(movieId)
                .collectList();

        var movie = Mono.zip(
                movieInfoMono,
                reviewsListMono,
                (movieInfo, reviewsList) -> new Movie(movieInfo, reviewsList)).log();
        return movie;
        // Failed:
        //        Mono<Movie> x = null;
        //        movieInfoMono.subscribe(
        //                movieInfo -> {
        //                    reviewsListMono.subscribe(reviewList -> {
        //                        x = new Movie(movieInfo, reviewList);
        //                    });
        //                }
        //        );
        //        return Mono.just(new Movie((MovieInfo) movieInfoMono.subscribe(),
        //                (List<Review>) reviewsListMono.subscribe()));
    }
}
