package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.domain.Revenue;
import com.learnreactiveprogramming.domain.Review;
import com.learnreactiveprogramming.exception.MovieException;
import com.learnreactiveprogramming.exception.NetworkException;
import com.learnreactiveprogramming.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class MovieReactiveService {

    private MovieInfoService movieInfoService;
    private ReviewService reviewService;

    private RevenueService revenueService;

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

    public Flux<Movie> getAllMovies_retry() {
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
                .retry(3)
                .log();
        return movieFlux;
    }

    public Flux<Movie> getAllMovies_retryWhen() {

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
                    if (ex instanceof NetworkException) {
                        throw new MovieException(ex.getMessage());
                    } else {
                        throw new ServiceException(ex.getMessage());
                    }
                })
                .retryWhen(getRetryBackoffSpec())
                .log();
        return movieFlux;
    }

    public Flux<Movie> getAllMovies_repeat() {

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
                    if (ex instanceof NetworkException) {
                        throw new MovieException(ex.getMessage());
                    } else {
                        throw new ServiceException(ex.getMessage());
                    }
                })
                .repeat()
                .log();
        return movieFlux;
    }

    public Flux<Movie> getAllMovies_repeat_n(long n) {

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
                    if (ex instanceof NetworkException) {
                        throw new MovieException(ex.getMessage());
                    } else {
                        throw new ServiceException(ex.getMessage());
                    }
                })
                .repeat(n)
                .log();
        return movieFlux;
    }

    private RetryBackoffSpec getRetryBackoffSpec() {
        RetryBackoffSpec retryBackOffSpec = Retry.backoff(3, Duration.ofMillis(500))
                .filter(ex -> ex instanceof MovieException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        Exceptions.propagate(retrySignal.failure()));
        return retryBackOffSpec;
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

    public Mono<Movie> getMovieByIdWithRevenue(long movieId) {
        Mono<MovieInfo> movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        Mono<List<Review>> reviewsListMono = reviewService
                .retrieveReviewsFlux(movieId)
                .collectList();
        // Not calling the below because it is called immediately...blocking the current thread
        // until the result is available.
        // Mono<Revenue> revenue = Mono.just(revenueService.getRevenue(movieId));
        Mono<Revenue> revenueMono = Mono.fromCallable(() -> revenueService.getRevenue(movieId))
                .subscribeOn(Schedulers.boundedElastic());

        return movieInfoMono
                .zipWith(reviewsListMono, (movieInfo, reviewsList) -> new Movie(movieInfo, reviewsList))
                .zipWith(revenueMono, (movie, revenue) -> {
                    movie.setRevenue(revenue);
                    return movie;
                })
                .log();

        //        return movie;
    }
}
