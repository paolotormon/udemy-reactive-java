package com.learnreactiveprogramming;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class ColdAndHotPublisherTest {

    @Test
    void coldPublisherTest() {
        var flux = Flux.range(1, 10);

        flux.subscribe(integer -> System.out.println("Subscriber 1 : " + integer));
        flux.subscribe(integer -> System.out.println("Subscriber 2 : " + integer));
    }
}
