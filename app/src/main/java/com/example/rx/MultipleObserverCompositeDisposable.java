package com.example.rx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MultipleObserverCompositeDisposable extends AppCompatActivity {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_observer_composite_disposable);

        Observable<String> fruitsObservable = getFruitsObservable();

        DisposableObserver<String> fruitsObserver = getFruitsObserver();

        DisposableObserver<String> allCapsFruitsObserver = getAllCapsFruitsObserver();

        compositeDisposable.add(
                fruitsObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String s) throws Exception {
                                return s.toLowerCase().startsWith("a");
                            }
                        })
                        .subscribeWith(fruitsObserver));

        compositeDisposable.add(
                fruitsObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String s) throws Exception {
                                return s.toUpperCase().startsWith("B");
                            }
                        })
                        .map(new Function<String, String>() {
                            @Override
                            public String apply(String s) throws Exception {
                                return s.toLowerCase();
                            }
                        })
                        .subscribeWith(allCapsFruitsObserver)
        );
    }

    private DisposableObserver<String> getAllCapsFruitsObserver() {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {

                Log.d("getAllCapsFruitsObserver","is: "+s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private DisposableObserver<String> getFruitsObserver() {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                Log.d("getFruitsObserver","is: "+s);

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private Observable<String> getFruitsObservable() {
        return Observable.fromArray("Apple","Grapes","Banana","berries");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
