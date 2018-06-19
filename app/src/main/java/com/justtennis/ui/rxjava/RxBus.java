package com.justtennis.ui.rxjava;

import android.util.Log;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * Used for subscribing to and publishing to subjects. Allowing you to send data between activities, fragments, etc.
 * <p>
 * Created by Pierce Zaifman on 2017-01-02.
 */

final class RxBus {

    private SparseArray<PublishSubject<Object>> sSubjectMap = new SparseArray<>();
    private Map<Object, CompositeDisposable> sSubscriptionsMap = new HashMap<>();

    RxBus() {}

    /**
     * Subscribe to the specified subject and listen for updates on that subject. Pass in an object to associate
     * your registration with, so that you can unsubscribe later.
     * <br/><br/>
     * <b>Note:</b> Make sure to call {@link RxBus#unregister(Object)} to avoid memory leaks.
     */
    void subscribe(int subject, @NonNull Object lifecycle, @NonNull Consumer<Object> action) {
        Log.i(this.getClass().getName(), "-----------------------> subscribe subject:" + subject + " lifecycle:" + lifecycle + " action:" + action);
        Disposable disposable = getSubject(subject).subscribe(action);
        getCompositeDisposable(lifecycle).add(disposable);
    }

    /**
     * Publish an object to the specified subject for all subscribers of that subject.
     */
    void publish(int subject, @NonNull Object message) {
        Log.i(this.getClass().getName(), "-----------------------> publish subject:" + subject + " message:" + message);
        getSubject(subject).onNext(message);
    }

    /**
     * Unregisters this object from the bus, removing all subscriptions.
     * This should be called when the object is going to go out of memory.
     */
    void unregister(@NonNull Object lifecycle) {
        Log.e(this.getClass().getName(), "-----------------------> unregister " + lifecycle.getClass().getName());
        //We have to remove the composition from the map, because once you dispose it can't be used anymore
        CompositeDisposable compositeDisposable = sSubscriptionsMap.remove(lifecycle);
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

    /**
     * Get the subject or create it if it's not already in memory.
     */
    @NonNull
    private PublishSubject<Object> getSubject(int subjectCode) {
        PublishSubject<Object> subject = sSubjectMap.get(subjectCode);
        if (subject == null) {
            subject = PublishSubject.create();
            subject.subscribeOn(AndroidSchedulers.mainThread());
            sSubjectMap.put(subjectCode, subject);
        }

        return subject;
    }

    /**
     * Get the CompositeDisposable or create it if it's not already in memory.
     */
    @NonNull
    private  CompositeDisposable getCompositeDisposable(@NonNull Object object) {
        CompositeDisposable compositeDisposable = sSubscriptionsMap.get(object);
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
            sSubscriptionsMap.put(object, compositeDisposable);
        }

        return compositeDisposable;
    }
}