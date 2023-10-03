package com.learnreactiveprogramming;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class BackpressureTest {

    @Test
    void testBackPressure() {
        var numberRange = Flux.range(1, 10).log();
        // normal
        //        numberRange.subscribe(num -> log.info("num is {}", num));
        numberRange.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(2);
            }

            @Override
            protected void hookOnNext(Integer value) {
                log.info("value - {}", value);
                if (value == 2) {
                    cancel();
                }
            }

            @Override
            protected void hookOnComplete() {
                super.hookOnComplete();
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                log.info("inside onCancel");
            }
        });
    }

    @Test
    void testBackPressure_1() throws InterruptedException {
        var numberRange = Flux.range(1, 100).log();

        CountDownLatch countDownLatch = new CountDownLatch(1);

        numberRange.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(2);
            }

            @Override
            protected void hookOnNext(Integer value) {
                log.info("value - {}", value);
                if (value % 2 == 0) {
                    request(2);
                } else if (value >= 50) {
                    cancel();
                }
            }

            @Override
            protected void hookOnComplete() {
                super.hookOnComplete();
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                log.info("inside onCancel");
                countDownLatch.countDown();
            }
        });

        assertTrue(countDownLatch.await(5L, TimeUnit.SECONDS));
    }

    //    Will show requestUnbounded
    @Test
    void testBackPressure_drop() throws InterruptedException {
        var numberRange = Flux.range(1, 100).log();

        CountDownLatch countDownLatch = new CountDownLatch(1);

        numberRange
                .onBackpressureDrop(integer -> {
                    log.info("Dropped items are {}", integer);
                })
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(2);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        log.info("hookOnNext - {}", value);
                        //                        if (value % 2 == 0) {
                        //                            request(2);
                        //                        } else if (value >= 50) {
                        //                            cancel();
                        //                        }
                        if (value == 2) {
                            hookOnCancel();
                        }
                    }

                    @Override
                    protected void hookOnComplete() {
                        super.hookOnComplete();
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        super.hookOnError(throwable);
                    }

                    @Override
                    protected void hookOnCancel() {
                        log.info("inside hookOnCancel");
                        countDownLatch.countDown();
                    }
                });

        assertTrue(countDownLatch.await(5L, TimeUnit.SECONDS));
    }
}
