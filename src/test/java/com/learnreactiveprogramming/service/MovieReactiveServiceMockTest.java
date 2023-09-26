package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.MovieException;
import com.learnreactiveprogramming.exception.NetworkException;
import com.learnreactiveprogramming.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MovieReactiveServiceMockTest {

    @InjectMocks
    MovieReactiveService movieReactiveService;
    @Mock
    MovieInfoService movieInfoService;
    @Mock
    ReviewService reviewService;

    @Test
    void getAllMovies() {
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenCallRealMethod();
        var moviesFlux = movieReactiveService.getAllMovies();

        StepVerifier.create(moviesFlux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void getAllMovie_errorScenario() {
        var errorMessage = "Exception occured";
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new MovieException(errorMessage));

        var moviesFlux = movieReactiveService.getAllMovies();

        StepVerifier.create(moviesFlux)
                .expectError(MovieException.class)
                .verify();
    }

    @Test
    void getAllMovies_retry() {
        var errorMessage = "Exception occured";
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new MovieException(errorMessage));

        var moviesFlux = movieReactiveService.getAllMovies_retry();

        StepVerifier.create(moviesFlux)
                .expectError(MovieException.class)
                .verify();
        verify(reviewService, times(4)).retrieveReviewsFlux(anyLong());
        verify(reviewService, times(4)).retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMovies_retryWhen() {
        var errorMessage = "Exception occured";
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new NetworkException(errorMessage));

        var moviesFlux = movieReactiveService.getAllMovies_retryWhen();

        StepVerifier.create(moviesFlux)
                .expectError(MovieException.class)
                .verify();
        verify(reviewService, times(4)).retrieveReviewsFlux(anyLong());
        verify(reviewService, times(4)).retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMovies_retryWhen_serviceException() {
        var errorMessage = "Exception occured";
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new ServiceException(errorMessage));

        var moviesFlux = movieReactiveService.getAllMovies_retryWhen();

        StepVerifier.create(moviesFlux)
                .expectError(ServiceException.class)
                .verify();

        verify(reviewService, times(1)).retrieveReviewsFlux(anyLong());
    }

    @Test
    void getAllMovies_repeat() {
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenCallRealMethod();

        var moviesFlux = movieReactiveService.getAllMovies_repeat();

        StepVerifier.create(moviesFlux)
                .expectNextCount(6)
                .thenCancel()
                .verify();
        verify(reviewService, times(6)).retrieveReviewsFlux(anyLong());
    }

    @Test
    void getAllMovies_repeat_n() {
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenCallRealMethod();

        var numOfTimes = 2L;

        var moviesFlux = movieReactiveService.getAllMovies_repeat_n(numOfTimes);

        StepVerifier.create(moviesFlux)
                .expectNextCount(9)
                .verifyComplete();
        verify(reviewService, times(9)).retrieveReviewsFlux(anyLong());
    }
}