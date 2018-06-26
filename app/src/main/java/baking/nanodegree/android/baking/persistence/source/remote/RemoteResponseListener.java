package baking.nanodegree.android.baking.persistence.source.remote;

import java.util.ArrayList;

public interface RemoteResponseListener<T> {
    void onSuccess(ArrayList<T> result);

    void onFailure(Throwable throwable);
}
