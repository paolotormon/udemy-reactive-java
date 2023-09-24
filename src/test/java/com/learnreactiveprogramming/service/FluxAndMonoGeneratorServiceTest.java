package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

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

    @Test
    void namesFlux_concatMap_async() {
        StepVerifier.create(fluxAndMonoGeneratorService.namesFlux_concatMap(3))
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesMono_flatMap() {
        StepVerifier.create(fluxAndMonoGeneratorService.namesMono_flatMap(3))
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    void namesMono_flatMapMany() {
        StepVerifier.create(fluxAndMonoGeneratorService.namesMono_flatMapMany(3))
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform() {
        StepVerifier.create(fluxAndMonoGeneratorService.namesFlux_transform_defaultIfEmpty(3))
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_empty() {
        StepVerifier.create(fluxAndMonoGeneratorService.namesFlux_transform_defaultIfEmpty(6))
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_switchIfEmpty() {
        StepVerifier.create(fluxAndMonoGeneratorService.namesFlux_transform_switchIfEmpty(6))
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .verifyComplete();
    }

    @Test
    void explore_concat() {
        StepVerifier.create(fluxAndMonoGeneratorService.explore_concat())
                .expectNext("a", "b", "c", "d")
                .verifyComplete();
    }

    @Test
    void explore_concatWith() {
        StepVerifier.create(fluxAndMonoGeneratorService.explore_concatWith())
                .expectNext("a", "b", "c", "d")
                .verifyComplete();
    }

    @Test
    void explore_merge() {
        StepVerifier.create(fluxAndMonoGeneratorService.explore_merge())
                .expectNext("a", "c", "b", "d")
                .verifyComplete();
    }

    @Test
    void explore_mergeWith() {
        StepVerifier.create(fluxAndMonoGeneratorService.explore_mergeWith())
                .expectNext("a", "c", "b", "d")
                .verifyComplete();
    }

    //write test for explore_mergeWith_mono
    @Test
    void explore_mergeWith_mono() {
        StepVerifier.create(fluxAndMonoGeneratorService.explore_mergeWith_mono())
                .expectNext("a", "b")
                .verifyComplete();
    }

    @Test
    void explore_mergeSequential() {
        StepVerifier.create(fluxAndMonoGeneratorService.explore_mergeSequential())
                .expectNext("a", "b", "c", "d")
                .verifyComplete();
    }

    @Test
    void explore_zip() {
        StepVerifier.create(fluxAndMonoGeneratorService.explore_zip())
                .expectNext("ace", "bdf")
                .verifyComplete();
    }

    @Test
    void explore_zipWith() {
        StepVerifier.create(fluxAndMonoGeneratorService.explore_zipWith())
                .expectNext("ac", "bd")
                .verifyComplete();
    }

    @Test
    void explore_zip_mono() {
        StepVerifier.create(fluxAndMonoGeneratorService.explore_zip_mono())
                .expectNext("ab")
                .verifyComplete();
    }
}
