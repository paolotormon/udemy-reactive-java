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

    @Test
    void hotPublisherTest_autoConnect() {
        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1));

        Flux<Integer> hotSource = flux.publish().autoConnect(2);

        hotSource.subscribe(integer -> System.out.println("Subscriber 1 : " + integer));
        delay(2000);
        hotSource.subscribe(integer -> System.out.println("Subscriber 2 : " + integer));
        delay(2000);
        System.out.println("2 subscribers connected");

        hotSource.subscribe(integer -> System.out.println("Subscriber 3 : " + integer));
        delay(10000);
    }

    @Test
    void hotPublisherTest_refCount() {
        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1))
                .doOnCancel(() -> System.out.println("Received Cancel Signal"));

        Flux<Integer> hotSource = flux.publish().refCount(2);

        var disposable1 = hotSource.subscribe(
                integer -> System.out.println("Subscriber 1 : " + integer)
        );
        delay(2000);

        var disposable2 = hotSource.subscribe(
                integer -> System.out.println("Subscriber 2 : " + integer)
        );
        System.out.println("2 subscribers connected");
        delay(2000);

        //cancel the subscription
        disposable1.dispose();
        disposable2.dispose();

        //try to attach subscriber to see if it is emitting values. MUST BE 2, otherwise wont
        // satisfy 2 condition
        hotSource.subscribe(integer -> System.out.println("Subscriber 3 : " + integer));
        hotSource.subscribe(integer -> System.out.println("Subscriber 4 : " + integer));
        delay(10000);
    }
}
