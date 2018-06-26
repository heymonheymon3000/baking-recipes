package baking.nanodegree.android.baking.persistence.source.local;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class LocalObserver<T> implements Observer<T> {
    @Override
    public void onSubscribe(@NonNull Disposable d) {}

    @Override
    public void onNext(@NonNull T o) {}

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {}
}
