package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class FluxAndMonoGeneratorService {

    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux()
                .subscribe(System.out::println);
        fluxAndMonoGeneratorService.nameMono()
                .subscribe(System.out::println);
    }

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe")).log(); //db or a
        // remote service call
    }

    public Mono<String> nameMono() {
        return Mono.just("adam");
    }
}
