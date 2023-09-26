package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MovieReactiveServiceTest {
    private MovieInfoService movieInfoService = new MovieInfoService();
    private ReviewService reviewService = new ReviewService();
    private RevenueService revenueService = new RevenueService();
    private MovieReactiveService movieReactiveService = new MovieReactiveService(movieInfoService, reviewService, revenueService);

    @Test
    void getAllMovies() {
        var moviesFlux = movieReactiveService.getAllMovies();
        StepVerifier.create(moviesFlux)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals(2, movie.getReviewList().size());
                    assertEquals("Awesome Movie", movie.getReviewList().get(0).getComment());
                })
                .assertNext(movie -> {
                    assertEquals("The Dark Knight", movie.getMovieInfo().getName());
                    assertEquals(2, movie.getReviewList().size());
                    assertEquals("Awesome Movie", movie.getReviewList().get(0).getComment());
                })
                .assertNext(movie -> {
                    assertEquals("Dark Knight Rises", movie.getMovieInfo().getName());
                    assertEquals(2, movie.getReviewList().size());
                })
                .verifyComplete();
    }

    @Test
    void getMovieById() {
        long movieId = 100L;
        Mono<Movie> movieMono = movieReactiveService.getMovieById(movieId);
        StepVerifier.create(movieMono)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals(2, movie.getReviewList().size());
                })
                .verifyComplete();
    }

    @Test
    void getMovieByIdWithRevenue() {
        long movieId = 100L;
        Mono<Movie> movieMono = movieReactiveService.getMovieByIdWithRevenue(movieId);
        StepVerifier.create(movieMono)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals(2, movie.getReviewList().size());
                    assertNotNull(movie.getRevenue());
                })
                .verifyComplete();
    }
}