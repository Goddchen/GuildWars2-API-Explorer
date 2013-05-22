package de.goddchen.android.gw2.api.async;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by Goddchen on 22.05.13.
 */
public abstract class FixedAsyncTaskLoader<D> extends AsyncTaskLoader<D> {

    private D mData;

    public FixedAsyncTaskLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (mData == null) {
            forceLoad();
        } else {
            deliverResult(mData);
        }
    }
}
