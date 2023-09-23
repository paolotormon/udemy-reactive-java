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
    void namesFluxMap() {
        StepVerifier.create(fluxAndMonoGeneratorService.namesFlux_map())
                .expectNext("ALEX")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesFluxImmutability() {
        StepVerifier.create(fluxAndMonoGeneratorService.namesFlux_immutability())
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();
    }

}
