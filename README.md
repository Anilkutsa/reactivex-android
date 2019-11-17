# reactivex-android
ReactiveX , RxJava and RxAndroid concepts and demo for android

##### What is reactive programming?   
- In computing, reactive programming is a declarative programming paradigm concerned with data streams and the propagation of change.
- with this paradigm it is possible to express static (e.g., arrays) or dynamic (e.g., event emitters) data streams with ease, and also communicate that an inferred dependency within the associated execution model exists, which facilitates the automatic propagation of the changed data flow.
- In reactive programming the consumer code blocks reacts to the data as it comes in.

##### What is ReactiveX?   
- ReactiveX is a short word for reactive extensions.
- ReactiveX is a project which provides implementations for this reactive programming concept for different programming languages
- ReactiveX is a combination of the best ideas from the Observer pattern, the Iterator pattern, and functional programming.     
##### What is RxJava? 
- RxJava is a Java VM implementation of ReactiveX.

##### What is the difference between RxJava and RxAndroid?
- RxAndroid does not replace rxjava.
- RxAndroid is just a layer on top of RxJava which provides android specific support.

##### Why and When should we use RxJava?  What are the benefits?
- Interfaces and methods provided by Reactive Extensions (Rx) provide a way to developers solve problems rapidly. We can save a lot of time we usually spend for writing complex algorithms and bug fixing.
- As it provides a common structure we can write easy to understand and easy to maintain code. RxJava support us to write clean and simpler code.
- RxJava allow a different approach of programming than imperative(way we usually code in Android) approach. It is reactive based, where codes are not activate until it knows it’s end mean.
- RxJava makes multi-threading super easy. Using imperative approach, moving a piece of code to background thread is hard work. However in RxJava, we could easily define what thread each part of the chain would be in.
- It provide extensibility to our code. RxJava code can be extended with custom operators.
- RxJava is composable, Rx operators can be combined to produce more complicated operations.
- Rx operators can transform one type of data to another, reducing, mapping or expanding streams as needed.
UI events like mouse move , button click, domain events like property changed, collection updated can be easily use to generate and handle data streams with RxJava.
- Error handling becomes much easier with RxJava. You don’t need to worry about adding try catch blocks everywhere.
- In mobile applications, we cannot control the app lifecycle. Sometimes we need to terminate current processes rapidly to response to a app lifecycle change. RxJava provide simple yet profound mechanism to terminate background processes rapidly.
- Particularly, callBack occurs when performing several asynchronous operations in a series where the next action depends on the results of the previous operation, making the code untidy and hard to manage. RxJava renders a better way to terminate the urgency to build layers of callbacks.
- The RxJava library was designed to give a control over a wide range of data, simultaneously on the events in the real-time. - This allows us to build highly responsive mobile applications.
Some people say Ios apps has better performance than Android apps. That's not true if we use RxJava. we can provide same or even better performance to native Android applications with RxJava.

## Basics Building Blocks of RxJava

<img width="600" alt="Screenshot 2019-11-17 at 7 25 26 PM" src="https://user-images.githubusercontent.com/3065517/69008402-0e5a2f80-0970-11ea-8494-e0b933882531.png">

We can recognize two main constructs in RxJava. If you study any Rx Java implemented code, all of the time, you will find two main constructs.
### Observables
Instances of Observable class. Observables observe data streams and emit them to subscribed Observers.       
### Observers 
Instances of observer interface .Observers consume data emitted by the Observables.

One Observable can have many observers. An observable emit data, if there is at least one observer subscribed for the data. If there is no subscription observable will not emit data.

Main observer methods(there are other methods too)
- **onNext()**: Each time an Observable emits data it  calls to Observer's  onNext() method  passing that data.
- **onError()**: If any error occur Observable calls to Observer's onError() method.
- **onComplete()**: Observable invokes Observer's onComplete() method, when the data emission is over.

## Other main building blocks
### Schedulers
To perform operations of Observable on different threads(multi-threading). With the help of Schedulers we handle multithreading in RxJava. A scheduler can be recognized as a thread pool managing one or more threads. Whenever a Scheduler needs to execute a task, it will take a thread from its pool and run the task in that thread. Different types of Schedulers available in RxJava include - 
  - **Schedulers.io()**: This can have a limitless thread pool. Used for non CPU intensive tasks. Such as database interactions, performing network  communications and interactions with the file system.
  - **AndroidSchedulers.mainThread()**: This is the main thread or the UI thread. This is where user interactions happen
  - **Schedulers.newThread()**: This scheduler creates a new thread for each unit of work scheduled.
  - **Schedulers.single()**: This scheduler has a single thread executing tasks one after another following the given order.
  - **Schedulers.trampoline()**: This scheduler executes tasks following first in, first out basics.We use this when implementing recurring tasks.
  - **Schedulers.from(Executor executor)**: This creates and returns a custom scheduler backed by the specified executor.

### Disposables

_How memory leaks happen, most of the time ?_
In mobile applications we cannot control the app life-cycle. Let’s say in an app you created you have written code to run a network call to a REST API and update the view accordingly. If a user initiate a view but decide to go back before the completion of the network call, What will happen? The activity or fragment will be destroyed. But the observer subscription will be there. When observer trying to update the User Interface, in this scenario as the view already destroyed,  it can cause a memory leak. And your app will freeze or crash as a result. 

**DisposableObserver** class implements both Observer and Disposable interfaces. DisposableObserver is much efficient than Observer if you have more than one observers in the activity or fragment. Observer implementation had four overridden methods. onSubscribe() method was mainly there to receive the disposable. But, DisposableObserver implementation has only three overridden methods. DisposableObserver  does not need onSubscribe method, as DisposableObserver can dispose by itself. Now at onDestroy method we can call to DisposableObserver's dispose method directly to terminate the processes.

### Composite Disposable
In one class you can have more than one observers . So you will have so many observers to dispose. When we have more than one observers we use CompositeDisposable. We can add disposable to CompositeDisposable like below - 
'''compositeDisposable.add(myObserver)'''
CompositeDisposable Can maintain a list of subscriptions in a pool and can dispose them all at once like below.
'''compositeDisposable.clear()'''

**_What is the difference between clear() and dispose() ?_**
When you are using CompositeDisposable, If you call to dispose() method, you will no longer be able to add disposables to that composite disposable.
But if you call to clear() method you can still add disposable to the composite disposable . Clear() method just clears the disposables that are currently held within the instance. 

### Operators
- **Just Operator**: Just() operator takes a list of arguments and converts the items into Observable items. It takes arguments between one to ten (But the official document says one to nine, may be it’s language specific).
- **From()**: Unlike just, From() creates an Observable from set of items using an Iterable, which means each item is emitted one at a time.
- **FromArray Operator**: Given an array object to Just() operator, Just() operator emits all the elements at once. Instead, if you the elements to be emitted one after one, use FromArray operator.  
- **Range**:Range() creates an Observable from a sequence of generated integers. The function generates sequence of integers by taking starting number and length. So the above same examples can be modified as Observable.range(1, 10).
- **Create**: You can create an Observable from scratch by using the Create operator. You pass this operator a function that accepts the observer as its parameter. Write this function so that it behaves as an Observable — by calling the observer’s onNext, onError, and onCompleted methods appropriately.
- **Map**: Map operator transform each item emitted by an Observable and emits the modified item. Consider using Map operator where there is an offline operations needs to be done on emitted data. 
- **FlatMap**: To better understand FlatMap, consider a scenario where you have a network call to fetch Users with name and gender. Then you have another network that gives you address of each user. Now the requirement is to create an Observable that emits Users with name, gender and address properties. To achieve this, you need to get the users first, then make separate network call for each user to fetch his address. This can be done easily using FlatMap operator.
- **ConcatMap**: Now consider the same example of FlatMap but replacing the operator with ConcatMap. Technically the both operators produces the same output but the sequence the data emitted changes. ConcatMap() maintains the order of items and waits for the current Observable to complete its job before emitting the next one. ConcatMap is more suitable when you want to maintain the order of execution.
- **SwitchMap**: SwitchMap is best suited when you want to discard the response and consider the latest one. Let’s say you are writing an Instant Search app which sends search query to server each time user types something. In this case multiple requests will be sent to server with multiple queries, but we want to show the result of latest typed query only. For this case, SwitchMap is best operator to use.
- **Buffer**: Buffer gathers items emitted by an Observable into batches and emit the batch instead of emitting one item at a time. Consider we have an Observable that emits integers from 1-9. When buffer(3) is used, it emits 3 integers at a time.
- **Filter**: filter() allows the Observable to emit the only values those passes a test. The filter() method takes a Predicate test and apply the test on each item that is in the list. Consider, the condition integer % 2 == 0 is applied on each number and the numbers which returns true, they will be emitted.
- **Distinct**: Distinct operator filters out items emitted by an Observable by avoiding duplicate items in the list.
- **Skip**: You can ignore the first n items emitted by an Observable and attend only to those items that come after, by modifying the Observable with the Skip operator.
- **SkipLast**: You can ignore the final n items emitted by an Observable and attend only to those items that come before them, by modifying the Observable with the SkipLast operator.

### Subjects
Subject classes extends the Observable class and implements the Observer interface. That’s why they can act like both. Widely used RxJava Subjects include 
- **Async Subject**: Only emits the last value of the Observable .  
- **Behavior Subject**: Emits the most recently emitted item and all the subsequent items of the  Observable  .   
- **Publish Subject**: Emits all the subsequent items of the source Observable at the time of subscription  . 
- **Replay Subject**: Emits all the items of the source Observable, regardless of when the subscriber subscribes .
