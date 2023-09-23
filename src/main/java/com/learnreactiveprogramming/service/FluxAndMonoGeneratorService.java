package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class FluxAndMonoGeneratorService {

    private static final List<String> nameList = List.of("alex", "ben", "chloe");

    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

        fluxAndMonoGeneratorService
                .namesFlux()
                .subscribe(System.out::println);
        fluxAndMonoGeneratorService
                .nameMono()
                .subscribe(System.out::println);
        fluxAndMonoGeneratorService
                .namesFlux_map()
                .subscribe(System.out::println);
    }

    public Flux<String> namesFlux() {
        //namelist is a publisher. a publisher is a source of data like db or a remote service call
        return Flux.fromIterable(nameList)
                .log();
    }

    public Flux<String> namesFlux_map() {
        //namelist is a publisher. a publisher is a source of data like db or a remote service call
        return Flux.fromIterable(nameList)
                .map(String::toUpperCase)
                .log();
    }

    public Mono<String> nameMono() {
        return Mono.just("adam");
    }
}
