package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

    private static final List<String> nameList = List.of("alex", "ben", "chloe");

    private static final Flux<String> abFlux = Flux.just("a", "b");
    private static final Flux<String> cdFlux = Flux.just("c", "d");
    private static final Flux<String> efFlux = Flux.just("e", "f");

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
                .doOnNext(name -> {
                    System.out.println("name is " + name);
                })
                .doOnSubscribe(subscription -> {
                    //see this prints before doOnNext because subscribe happens first
                    System.out.println("subscription is " + subscription);
                })
                .doOnComplete(() -> System.out.println("Inside onComplete callback"))
                .doFinally(signalType -> System.out.println("Inside doFinally. signalType: " + signalType))
                .log();
    }

    public Flux<String> namesFlux_immutability() {
        var namesFlux = Flux.fromIterable(nameList);
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    public Mono<String> namesMono_map_filter(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .log();
    }

    public Mono<List<String>> namesMono_flatMap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(StringHelper::splitStringMono)
                .log();
    }

    public Flux<String> namesMono_flatMapMany(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(StringHelper::splitString)
                .log();
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

    Flux<String> namesFlux_transform_defaultIfEmpty(int stringLength) {

        Function<Flux<String>, Flux<String>> filterAndMap =
                nameFlux -> nameFlux
                        .filter(name -> name.length() > stringLength)
                        .map(String::toUpperCase);

        return Flux.fromIterable(nameList)
                .transform(filterAndMap)
                .flatMap(StringHelper::splitString)
                .defaultIfEmpty("default")
                .log();
    }

    Flux<String> namesFlux_transform_switchIfEmpty(int stringLength) {

        Function<Flux<String>, Flux<String>> filterAndMap =
                nameFlux -> nameFlux
                        .filter(name -> name.length() > stringLength)
                        .flatMap(StringHelper::splitString)
                        .map(String::toUpperCase);

        var defaultFlux = Flux.just("default")
                .transform(filterAndMap);

        return Flux.fromIterable(nameList)
                .transform(filterAndMap)
                .switchIfEmpty(defaultFlux)
                .log();
    }

    public Flux<String> explore_concat() {
        return Flux.concat(abFlux, cdFlux)
                .log();
    }

    public Flux<String> explore_concatWith() {
        return abFlux.concatWith(cdFlux).log();
    }

    Flux<String> explore_merge() {
        var abFluxWithDelay = abFlux.delayElements(Duration.ofMillis(100));
        var cdFluxWithDelay = cdFlux.delayElements(Duration.ofMillis(100));
        return Flux.merge(abFluxWithDelay, cdFluxWithDelay)
                .log();
    }

    Flux<String> explore_mergeWith() {
        var abFluxWithDelay = abFlux.delayElements(Duration.ofMillis(100));
        var cdFluxWithDelay = cdFlux.delayElements(Duration.ofMillis(110));
        return abFluxWithDelay.mergeWith(cdFluxWithDelay)
                .log();
    }

    Flux<String> explore_mergeWith_mono() {
        var aMono = Mono.just("a");
        var bMono = Mono.just("b");
        return aMono.mergeWith(bMono)
                .log();
    }

    Flux<String> explore_mergeSequential() {
        //Concat vs mergeSequential
        //mergeSequential is similar to concat but it runs the publishers in parallel
        //the results are just cached and then merged in the order they were subscribed to
        //https://stackoverflow.com/questions/67857350/project-reactor-what-are-differences-between-flux-concat-flux-mergesequential
        var abFluxWithDelay = abFlux.delayElements(Duration.ofMillis(100));
        var cdFluxWithDelay = cdFlux.delayElements(Duration.ofMillis(110));
        return Flux.mergeSequential(abFluxWithDelay, cdFluxWithDelay)
                .log();
    }

    Flux<String> explore_zip() {
        return Flux.zip(abFlux, cdFlux, efFlux)
                .map(t -> t.getT1() + t.getT2() + t.getT3())
                .log();
    }

    Flux<String> explore_zipWith() {
        return abFlux.zipWith(cdFlux)
                .map(t -> t.getT1() + t.getT2())
                .log();
    }

    Mono<String> explore_zip_mono() {
        var aMono = Mono.just("a");
        var bMono = Mono.just("b");
        return Mono.zip(aMono, bMono, (string1, string2) -> string1 + string2)
                .log();

    }

}
