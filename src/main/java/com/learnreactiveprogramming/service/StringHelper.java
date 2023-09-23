package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class StringHelper {
    public static Flux<String> splitString(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public static Flux<String> splitStringWithDelay(String name) {
        return splitStringWithDelay(name, 0);
    }

    public static Flux<String> splitStringWithDelay(String name, int delayMilliseconds) {
        var charArray = name.split("");
        int random = new Random().nextInt(1000);
        delayMilliseconds = delayMilliseconds > 0 ? delayMilliseconds : random;
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(delayMilliseconds));
    }

    public static Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray);
        return Mono.just(charList);
    }
}
