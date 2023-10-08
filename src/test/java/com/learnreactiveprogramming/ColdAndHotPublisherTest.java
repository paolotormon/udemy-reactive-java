package com.learnreactiveprogramming;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

public class ColdAndHotPublisherTest {

    @Test
    void coldPublisherTest() {
        var flux = Flux.range(1, 10);

        flux.subscribe(integer -> System.out.println("Subscriber 1 : " + integer));
        flux.subscribe(integer -> System.out.println("Subscriber 2 : " + integer));
    }

    @Test
    void hotPublisherTest() {
        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<Integer> connectableFlux = flux.publish();
        connectableFlux.connect();

        connectableFlux.subscribe(integer -> System.out.println("Subscriber 1 : " + integer));
        delay(2000);
        connectableFlux.subscribe(integer -> System.out.println("Subscriber 2 : " + integer));
        delay(10000);


    }
}
