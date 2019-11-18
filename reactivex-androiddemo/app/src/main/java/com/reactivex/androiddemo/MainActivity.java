package com.reactivex.androiddemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;


public class MainActivity<T> extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText inputText;
    private TextView viewText;
    private Button clearButton;

    // Disposable helps dispose an Observer.
    // Ideally should be used when view is about to get destroyed
    Disposable mDisposable;

    // DisposableObserver class implements both Observer and Disposable interfaces.
    DisposableObserver disposableObserver;

    // CompositeDisposable helps maintain a list of subscriptions in a pool and can dispose them all at once.
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.etInputField);
        viewText = findViewById(R.id.tvInput);
        clearButton = findViewById(R.id.btnClear);

        //basicsComponents();

        //basicOperators();

        basicsOfSubjects();

        basicsOfRxBinding();
    }

    private void basicsComponents() {
        // Just emits single data as soon as an observer subscribes to it
        Observable<String> myObservable = Observable.just("Hello World");
        // Thread on which Observable should execute
        myObservable.subscribeOn(Schedulers.io());
        // Thread on which data should be posted to observer
        myObservable.observeOn(AndroidSchedulers.mainThread());
        // Observer has 4 methods.
        Observer myObserver = getObserver();
        // Observer subscribes to Observable
        myObservable.subscribe(myObserver);

        // Instead of using Disposable seperately, you can use DisposableObserver
        // This will not have onSubscribe method and you can call dispose() method directly on this object
        disposableObserver = getDisposableObserver();
        // disposableObserver subscribes to Observable
        myObservable.subscribe(disposableObserver);

        //compositeDisposable.add(mDisposable);
        //compositeDisposable.add(disposableObserver);
        compositeDisposable.addAll(mDisposable, disposableObserver);
    }

    private void basicOperators() {

        executeJustOperator();

        executeFromArrayOperator();

        executeRangeOperator();

        executeCreateOperator();

        executeMapOperator();

        executeFlatmapOperator();

        executeBufferOperator();

        executeFilterOperator();

        executeDistinctOperator();

        executeSkipOperator();
    }

    private void executeJustOperator() {
        //Just operator emits single item. It cannot ierate through array elements and emit them
        //String[] someArray = {"Hello", "My", "World"};

        //However, if you pass items seperately in just operator, it will emit them seperately
        Observable<String> myObservable = Observable.just("Hello", "My", "World");
        Observer myObserver = getObserver();
        myObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(myObserver);
    }

    private void executeFromArrayOperator() {
        //Unlike just, fromArray iterates through elements and emits one element at a time
        String[] someArray = {"Hello", "My", "World"};
        Observable<String> myObservable = Observable.fromArray(someArray);
        Observer myObserver = getObserver();
        myObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(myObserver);
    }

    private void executeRangeOperator() {
        //Range operator emits elements one after one in that range
        String[] someArray = {"Hello", "My", "World"};
        Observable<Integer> myObservable = Observable.range(1, 10);
        Observer myObserver = getObserver();
        myObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(myObserver);
    }

    private void executeCreateOperator() {
        // Create helps create an Observable from scratch by means of a function
        // With this, we will have control over emission of data
        Observable<Student> myObservable = Observable.create(new ObservableOnSubscribe<Student>() {
            @Override
            public void subscribe(ObservableEmitter<Student> emitter) throws Exception {
                ArrayList<Student> studentArrayList = Student.getStudents();
                for (Student student : studentArrayList) {
                    emitter.onNext(student);
                }
                emitter.onComplete();
            }
        });
        Observer myObserver = getObserver();
        myObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(myObserver);
    }

    private void executeMapOperator() {
        // Map operator takes a data type and helps make operations over it
        // Can can consume data in one form and emit data in another form, thus transforming it completely
        Observable<Student> myObservable = Observable.create(new ObservableOnSubscribe<Student>() {
            @Override
            public void subscribe(ObservableEmitter<Student> emitter) throws Exception {
                ArrayList<Student> studentArrayList = Student.getStudents();
                for (Student student : studentArrayList) {
                    emitter.onNext(student);
                }
                emitter.onComplete();
            }
        });
        Observer myObserver = getObserver();
        myObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Student, Student>() {
                    @Override
                    public Student apply(Student student) throws Exception {
                        student.setName(student.getName().toUpperCase());
                        student.setRegistrationDate("01/01/2019");
                        return student;
                    }
                })
                .subscribeWith(myObserver);
    }

    private void executeFlatmapOperator() {
        // Flatmap operator takes a data type and emits Observable/Observer return type data
        // Alternately, use concat map, if you want to maintain order of the emission
        Observable<Student> myObservable = Observable.create(new ObservableOnSubscribe<Student>() {
            @Override
            public void subscribe(ObservableEmitter<Student> emitter) throws Exception {
                ArrayList<Student> studentArrayList = Student.getStudents();
                for (Student student : studentArrayList) {
                    emitter.onNext(student);
                }
                emitter.onComplete();
            }
        });
        Observer myObserver = getObserver();
        myObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Student, Observable<Student>>() {
                    @Override
                    public Observable<Student> apply(Student student) throws Exception {
                        student.setName(student.getName().toLowerCase());
                        student.setRegistrationDate("NOT AVAILABLE");
                        return Observable.just(student);
                    }
                })
                .subscribeWith(myObserver);
    }


    private void executeBufferOperator() {
        // Buffer periodically gather items into bundles and emits these bundles rather than 1 item at a time
        Integer[] someArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Observable<Integer> myObservable = Observable.fromArray(someArray);
        Observer myObserver = getObserver();
        myObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .buffer(3)
                .subscribe(myObserver);
    }

    private void executeFilterOperator() {
        // Filter emits only those items from an Observable that pass a predicate test
        Integer[] someArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Observable<Integer> myObservable = Observable.fromArray(someArray);
        Observer myObserver = getObserver();
        myObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer % 2 == 0;
                    }
                })
                .subscribe(myObserver);
    }

    private void executeDistinctOperator() {
        // Distinct operator suppresses duplicate items emitted by an observable
        Integer[] someArray = {10, 10, 20, 20, 30, 30, 40, 40, 50, 50};
        Observable<Integer> myObservable = Observable.fromArray(someArray);
        Observer myObserver = getObserver();
        myObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinct()
                .subscribe(myObserver);
    }

    private void executeSkipOperator() {
        // Skip operator suppresses first n items emitted by observable
        // SkipLast operator suppresses last n items emitted by observable
        Integer[] someArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Observable<Integer> myObservable = Observable.fromArray(someArray);
        Observer myObserver = getObserver();
        myObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .skip(5)
                .subscribe(myObserver);
    }

    private Observer<T> getObserver() {
        return new Observer<T>() {
            @Override
            public void onSubscribe(Disposable d) {
                mDisposable = d;
                Log.i(TAG, "onSubscribe");
            }

            @Override
            public void onNext(T t) {
                Log.i(TAG, "myObserver onNext " + t.toString());
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "myObserver onError");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "myObserver onComplete");
            }
        };
    }

    private DisposableObserver<T> getDisposableObserver() {
        return new DisposableObserver<T>() {
            @Override
            public void onNext(T s) {
                Log.i(TAG, "disposableObserver onNext " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "disposableObserver onError");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "disposableObserver onComplete");
            }
        };
    }


    /**
     * Subjects
     * <p>
     * A Subject is a sort of bridge or proxy that is available in some implementations of ReactiveX that acts both as an observer and as an Observable.
     * Because it is an observer, it can subscribe to one or more Observables,
     * and because it is an Observable, it can pass through the items it observes by reemitting them, and it can also emit new items.
     */
    private void basicsOfSubjects() {

          /*
        An AsyncSubject emits the last value (and only the last value) emitted by the source Observable, and only after that source Observable completes.
         */
        //asyncSubjectDemo1();
        //asyncSubjectDemo2();

        /*
        When an observer subscribes to a BehaviorSubject, it begins by emitting the item most recently emitted by the source Observable
        (or a seed/default value if none has yet been emitted) and then continues to emit any other items emitted later by the source Observable(s).
         */
        //behaviorSubjectDemo1();
        //behaviorSubjectDemo2();

        /*
        PublishSubject emits to an observer only those items that are emitted by the source Observable(s) subsequent to the time of the subscription.
         */
        //publishSubjectDemo1();
        //publishSubjectDemo2();

        /*
        ReplaySubject emits to any observer all of the items that were emitted by the source Observable(s), regardless of when the observer subscribes.
         */
        //replaySubjectDemo1();
        replaySubjectDemo2();
    }

    private void asyncSubjectDemo1() {
        /*
        Even though all the observers have subscribed at the same time, they will still get last emitted item
         */
        Observable<String> observable = Observable.just("JAVA", "KOTLIN", "XML", "JSON")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        AsyncSubject<String> asyncSubject = AsyncSubject.create();
        observable.subscribe(asyncSubject);

        asyncSubject.subscribe(getFirstObserver());
        asyncSubject.subscribe(getSecondObserver());
        asyncSubject.subscribe(getThirdObserver());
    }

    private void asyncSubjectDemo2() {
        /*
        Since a subject is both Observable and Observer, subject can emit data by itself
        In this example, even through first observer has subscribed before subject completes, it still gets JSON as value
        Second observer, no wonder gets JSON as value
        However, by the time third observer has subscribed, subject has completed subscription, but it still emits last value which is JSON
         */
        AsyncSubject<String> asyncSubject = AsyncSubject.create();
        asyncSubject.subscribe(getFirstObserver());

        asyncSubject.onNext("JAVA");
        asyncSubject.onNext("KOTLIN");
        asyncSubject.onNext("XML");

        asyncSubject.subscribe(getSecondObserver());
        asyncSubject.onNext("JSON");
        asyncSubject.onComplete();

        asyncSubject.subscribe(getThirdObserver());
    }

    private void behaviorSubjectDemo1() {
        /*
        Since observers have subscribed to observable even before it has started, they will receive all the emitted values
         */
        Observable<String> observable = Observable.just("JAVA", "KOTLIN", "XML", "JSON")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        BehaviorSubject<String> behaviorSubject = BehaviorSubject.create();
        observable.subscribe(behaviorSubject);

        behaviorSubject.subscribe(getFirstObserver());
        behaviorSubject.subscribe(getSecondObserver());
        behaviorSubject.subscribe(getThirdObserver());
    }

    private void behaviorSubjectDemo2() {
        /*
        Since first observer subscribed to observable when it has not yet started, it will receive all the items
        Since second observer subscribed to observable while its running, it will receive last emitted value and subsequent items
        Since third observer subscribed to observable after it has completed, it will not receive any items.
         */
        BehaviorSubject<String> behaviorSubject = BehaviorSubject.create();
        behaviorSubject.subscribe(getFirstObserver());

        behaviorSubject.onNext("JAVA");
        behaviorSubject.onNext("KOTLIN");
        behaviorSubject.onNext("XML");

        behaviorSubject.subscribe(getSecondObserver());
        behaviorSubject.onNext("JSON");
        behaviorSubject.onComplete();

        behaviorSubject.subscribe(getThirdObserver());
    }

    private void publishSubjectDemo1() {
        /*
        Since observers have subscribed to observable even before it has started, they will receive all the emitted values
         */
        Observable<String> observable = Observable.just("JAVA", "KOTLIN", "XML", "JSON")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        PublishSubject<String> publishSubject = PublishSubject.create();
        observable.subscribe(publishSubject);

        publishSubject.subscribe(getFirstObserver());
        publishSubject.subscribe(getSecondObserver());
        publishSubject.subscribe(getThirdObserver());
    }

    private void publishSubjectDemo2() {
         /*
        Since first observer subscribed to observable when it has not yet started, it will receive all the items
        Since second observer subscribed to observable while its running, it will receive only the subsequent items
        Since third observer subscribed to observable after it has completed, it will not receive any items.
         */
        PublishSubject<String> publishSubject = PublishSubject.create();

        publishSubject.subscribe(getFirstObserver());
        publishSubject.onNext("JAVA");
        publishSubject.onNext("KOTLIN");
        publishSubject.onNext("XML");

        publishSubject.subscribe(getSecondObserver());
        publishSubject.onNext("JSON");
        publishSubject.onComplete();

        publishSubject.subscribe(getThirdObserver());
    }

    private void replaySubjectDemo1() {
         /*
        Invarient of when observers have subscribed to observable, they will receive all the emitted values
         */
        Observable<String> observable = Observable.just("JAVA", "KOTLIN", "XML", "JSON")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        ReplaySubject<String> replaySubject = ReplaySubject.create();
        observable.subscribe(replaySubject);

        replaySubject.subscribe(getFirstObserver());
        replaySubject.subscribe(getSecondObserver());
        replaySubject.subscribe(getThirdObserver());
    }

    private void replaySubjectDemo2() {
        /*
        Invarient of when observers have subscribed to observable, they will receive all the emitted values
         */
        ReplaySubject<String> replaySubject = ReplaySubject.create();

        replaySubject.subscribe(getFirstObserver());
        replaySubject.onNext("JAVA");
        replaySubject.onNext("KOTLIN");
        replaySubject.onNext("XML");

        replaySubject.subscribe(getSecondObserver());
        replaySubject.onNext("JSON");
        replaySubject.onComplete();

        replaySubject.subscribe(getThirdObserver());
    }

    private Observer<String> getFirstObserver() {
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, " First Observer onSubscribe ");
            }

            @Override
            public void onNext(String s) {
                Log.i(TAG, " First Observer onNext " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, " First Observer onError ");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, " First Observer onComplete ");
            }
        };
        return observer;
    }

    private Observer<String> getSecondObserver() {
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, " Second Observer onSubscribe ");
            }

            @Override
            public void onNext(String s) {
                Log.i(TAG, " Second Observer onNext " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, " Second Observer onError ");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, " Second Observer onComplete ");
            }
        };
        return observer;
    }

    private Observer<String> getThirdObserver() {
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, " Third Observer onSubscribe ");
            }

            @Override
            public void onNext(String s) {
                Log.i(TAG, " Third Observer onNext " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, " Third Observer onError ");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, " Third Observer onComplete ");
            }
        };
        return observer;
    }

    /*
       With RxBinding, you can convert view elements into Rx Observables
       For instance, below example shows when a text is entered in inputText, a preview of same is shown in viewText
       Clear button onClick clears both the views.

       The same can be done using RxBinding by converting view objects into observables
    */
    private void basicsOfRxBinding() {
        /*inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewText.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputText.setText("");
                viewText.setText(" ");
            }
        });*/

        Disposable disposable1 = RxTextView.textChanges(inputText)
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        viewText.setText(charSequence);
                    }
                });

        Disposable disposable2 = RxView.clicks(clearButton)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        inputText.setText("");
                        viewText.setText("");
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // mDisposable has been initialized in basicsComponents() method
        mDisposable.dispose();
        // disposableObserver has been initialized in basicsComponents() method
        disposableObserver.dispose();
        // if you call to clear() method you can still add disposable to the composite disposable
        // whereas, If you call to dispose() method, you will no longer be able to add disposables to that composite disposable
        compositeDisposable.clear();
    }
}
