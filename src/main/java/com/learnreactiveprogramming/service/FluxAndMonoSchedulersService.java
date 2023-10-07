package com.learnreactiveprogramming.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

@Slf4j
public class FluxAndMonoSchedulersService {

    static List<String> namesList = List.of("alex", "ben", "chloe");
    static List<String> namesList1 = List.of("adam", "jill", "jack");

    //publishOn is for downstream only, subscribeOn for upstream
    // Notice despite having 6 names, it runs 3 seconds. Both fluxes run concurrently.
    Flux<String> explore_publishOn() {
        //Schedulers.parallel for cpu-bound
        //Schedulers.boundedElastic for io-bound
        var namesFlux = Flux.fromIterable(namesList)
                .publishOn(Schedulers.parallel())
                .map(this::upperCase) //has a blocking call
                .map(s -> {
                    log.info("name is: " + s);
                    return s;
                })
                .log();
        var namesFlux1 = Flux.fromIterable(namesList1)
                .publishOn(Schedulers.boundedElastic())
                .map(this::upperCase)
                .map(s -> {
                    log.info("name is: " + s);
                    return s;
                })
                .log();

        return namesFlux.mergeWith(namesFlux1);
    }

    public ParallelFlux<String> explore_parallel() {

        System.out.println(Runtime.getRuntime().availableProcessors());
        // Runtime of this is
        // Ceiling of (N of elements divided by number of threads)
        // 3 names with 8 threads = 1 second
        // 8 names with 8 threads = 1 second
        // 9 names with 8 threads = 2 seconds

        return Flux.fromIterable(namesList)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(this::upperCase) //has a blocking call
                .map(s -> {
                    log.info("name is: " + s);
                    return s;
                })
                .log();
    }

    public Flux<String> explore_parallel_using_flatMap() {
        //Run test, see that the order is not guaranteed
        return Flux.fromIterable(namesList)
                .flatMap(name ->
                        Mono.just(name)
                                .map(this::upperCase)  //has a blocking call
                                .subscribeOn(Schedulers.parallel()))
                .log();
    }

    Flux<String> explore_parallel_using_flatMap_mergeWith() {
        //Schedulers.parallel for cpu-bound
        //Schedulers.boundedElastic for io-bound
        var namesFlux = Flux.fromIterable(namesList)
                .flatMap(name ->
                        Mono.just(name)
                                .map(this::upperCase)  //has a blocking call
                                .subscribeOn(Schedulers.parallel()))
                .log();
        var namesFlux1 = Flux.fromIterable(namesList1)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(name ->
                        Mono.just(name)
                                .map(this::upperCase)  //has a blocking call
                                .subscribeOn(Schedulers.parallel()))
                .log();

        return namesFlux.mergeWith(namesFlux1);
    }

    // subscribeOn if u have no control of upstream
    Flux<String> explore_subscribeOn() {
        var namesFlux = getFlux(namesList)
                .subscribeOn(Schedulers.boundedElastic())
                .map(s -> {
                    log.info("name is: " + s);
                    return s;
                })
                .log();

        var namesFlux1 = getFlux(namesList1)
                .subscribeOn(Schedulers.boundedElastic())
                .map(s -> {
                    log.info("name is: " + s);
                    return s;
                })
                .log();

        return namesFlux.mergeWith(namesFlux1);
    }


    //example this is a library u have no control over, which has a blocking call (uppercase)
    private Flux<String> getFlux(List<String> namesList) {
        return Flux.fromIterable((namesList))
                .map(this::upperCase);
    }

    private String upperCase(String name) {
        delay(1000);
        return name.toUpperCase();
    }

}
