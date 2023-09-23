package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class FluxAndMonoGeneratorService {

    private static final List<String> nameList = List.of("alex", "ben", "chloe");

    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

        //        fluxAndMonoGeneratorService
        //                .namesFlux()
        //                .subscribe(System.out::println);
        //        fluxAndMonoGeneratorService
        //                .nameMono()
        //                .subscribe(System.out::println);
        //        fluxAndMonoGeneratorService
        //                .namesFlux_map()
        //                .subscribe(System.out::println);
        //        fluxAndMonoGeneratorService
        //                .namesFlux_flatMap(3)
        //                .subscribe(System.out::println);
        fluxAndMonoGeneratorService
                .namesFlux_flatMapAsync(3)
                .subscribe(System.out::println);
    }

    public Mono<String> nameMono() {
        return Mono.just("adam");
    }

    public Flux<String> namesFlux() {
        //namelist is a publisher. a publisher is a source of data like db or a remote service call
        return Flux.fromIterable(nameList)
                .log();
    }

    public Flux<String> namesFlux_map() {
        return Flux.fromIterable(nameList)
                .map(String::toUpperCase)
                .log();
    }

    public Flux<String> namesFlux_immutability() {
        var namesFlux = Flux.fromIterable(nameList);
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    Flux<String> namesFlux_flatMap(int stringLength) {
        return Flux.fromIterable(nameList)
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(StringHelper::splitString)
                .log();
    }

    Flux<String> namesFlux_flatMapAsync(int stringLength) {
        return Flux.fromIterable(nameList)
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                .flatMap(StringHelper::splitStringWithDelay)
                .log();
    }

    Flux<String> namesFlux_concatMap(int stringLength) {
        return Flux.fromIterable(nameList)
                .map(String::toUpperCase)
                .filter(name -> name.length() > stringLength)
                .concatMap(name -> StringHelper.splitStringWithDelay(name, 1000))
                .log();
    }

}
