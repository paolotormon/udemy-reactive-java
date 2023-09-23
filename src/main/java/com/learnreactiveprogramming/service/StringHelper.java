package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;

public class StringHelper {
    public static Flux<String> splitString(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public static Flux<String> splitStringWithDelay(String name) {
        var charArray = name.split("");
        int random = new Random().nextInt(1000);
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(random));
    }
}
