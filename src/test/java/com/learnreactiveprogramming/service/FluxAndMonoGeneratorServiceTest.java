package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class FluxAndMonoGeneratorServiceTest {
    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void nameMono() {
        var nameMono = fluxAndMonoGeneratorService.nameMono();
        StepVerifier.create(nameMono).expectNext("adam").verifyComplete();
    }

    @Test
    void namesFlux() {
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();
        StepVerifier.create(namesFlux)
                //                .expectNext("alex", "ben", "chloe")
                //                .expectNextCount(3)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesFlux_map() {
        StepVerifier.create(fluxAndMonoGeneratorService.namesFlux_map())
                .expectNext("ALEX")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesFlux_immutability() {
        StepVerifier.create(fluxAndMonoGeneratorService.namesFlux_immutability())
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesFlux_flatMap() {
        StepVerifier.create(fluxAndMonoGeneratorService.namesFlux_flatMap(3))
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O")
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void namesFlux_flatMap_async() {
        StepVerifier.create(fluxAndMonoGeneratorService.namesFlux_flatMapAsync(3))
                .expectNextCount(9)
                .verifyComplete();
    }

}
