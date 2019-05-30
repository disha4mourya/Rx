package com.example.rx;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class CustomDataTypesAOperator extends AppCompatActivity {

    private String TAG = CustomDataTypesAOperator.class.getSimpleName();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        compositeDisposable.add(getTodoObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Todo, Todo>() {
                    @Override
                    public Todo apply(Todo todo) throws Exception {
                        todo.setTodo(todo.getTodo().toUpperCase());
                        return todo;
                    }
                })
                .subscribeWith(getTodoObserver()));
    }

    private Observable<Todo> getTodoObservable() {
        final List<Todo> list = provideTodo();
        return Observable.create(new ObservableOnSubscribe<Todo>() {
            @Override
            public void subscribe(ObservableEmitter<Todo> emitter) throws Exception {
                for (Todo todos : list) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(todos);
                    }
                }

                if (emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        });
    }

    private List<Todo> provideTodo() {
        List<Todo> todosList = new ArrayList<>();

        todosList.add(new Todo(101, "Jogging"));
        todosList.add(new Todo(102, "Healthy Breakfast"));
        todosList.add(new Todo(103, "On Time Work"));
        return todosList;
    }

    private DisposableObserver<Todo> getTodoObserver() {
        return new DisposableObserver<Todo>() {
            @Override
            public void onNext(Todo todos) {
                Log.d(TAG, todos.getTodo());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }


    class Todo {
        int id;
        String todos;

        public Todo(int id, String todos) {
            this.id = id;
            this.todos = todos;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTodo() {
            return todos;
        }

        public void setTodo(String todos) {
            this.todos = todos;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
