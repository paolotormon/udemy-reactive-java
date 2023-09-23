package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

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
                .flatMap(this::splitString)
                .log();
    }

    Flux<String> namesFlux_flatMapAsync(int stringLength) {
        return Flux.fromIterable(nameList)
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringWithDelay)
                .log();
    }

    private Flux<String> splitString(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    private Flux<String> splitStringWithDelay(String name) {
        var charArray = name.split("");
        int random = new Random().nextInt(1000);
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(random));
    }


}
