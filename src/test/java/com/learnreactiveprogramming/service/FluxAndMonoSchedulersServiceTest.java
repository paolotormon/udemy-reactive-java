package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class FluxAndMonoSchedulersServiceTest {

    FluxAndMonoSchedulersService fluxAndMonoSchedulersService = new FluxAndMonoSchedulersService();

    @Test
    void explore_publishOn() {
        var flux = fluxAndMonoSchedulersService.explore_publishOn();
        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void explore_subscribeOn() {
        var flux = fluxAndMonoSchedulersService.explore_subscribeOn();
        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void explore_parallel() {
        var flux = fluxAndMonoSchedulersService.explore_parallel();
        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void explore_parallel_using_flatMap() {
        var flux = fluxAndMonoSchedulersService.explore_parallel_using_flatMap();
        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void explore_parallel_using_flatMap_mergeWith() {
        var flux = fluxAndMonoSchedulersService.explore_parallel_using_flatMap_mergeWith();
        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void explore_parallel_using_flatMapSequential() {
        var flux = fluxAndMonoSchedulersService.explore_parallel_using_flatMapSequential();
        StepVerifier.create(flux)
                //                .expectNextCount(3)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }
}