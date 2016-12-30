package com.juaby.labs.rpc.test.rxjava;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juaby on 16-6-14.
 */
public class RxJavaTest {

    public static void main(String[] args) {
        Observable<Integer> observableString = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> observer) {
                for (int i = 0; i < 5; i++) {
                    observer.onNext(i);
                }
                observer.onCompleted();
            }
        });
        Subscription subscriptionPrint = observableString.subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println("Observable completed");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("Oh,no! Something wrong happened！");
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("Item is " + item);
            }
        });

        List<Integer> items = new ArrayList<Integer>();
        items.add(1);
        items.add(10);
        items.add(100);
        items.add(200);

        observableString = Observable.from(items);
        subscriptionPrint = observableString.subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println("Observable completed");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("Oh,no! Something wrong happened！");
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("Item is " + item);
            }
        });

        Observable<String> observableString2 = Observable.just(helloWorld());

        subscriptionPrint = observableString2.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                System.out.println("Observable completed");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("Oh,no! Something wrong happened!");
            }

            @Override
            public void onNext(String message) {
                System.out.println(message);
            }
        });

        PublishSubject<String> stringPublishSubject = PublishSubject.create();
        subscriptionPrint = stringPublishSubject.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                System.out.println("Observable completed");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("Oh,no!Something wrong happened!");
            }

            @Override
            public void onNext(String message) {
                System.out.println(message);
            }
        });
        stringPublishSubject.onNext("Hello World");
    }

    private static String helloWorld() {
        return "Hello World";
    }

}
