package com.tgf.kcwc.view.SmoothListView;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ListView;

public abstract class AbsHeaderView<T> {

    protected Activity mActivity;
    protected LayoutInflater mInflate;
    protected T mEntity;

    public AbsHeaderView(Activity activity) {
        this.mActivity = activity;
        mInflate = LayoutInflater.from(activity);
    }

    public boolean fillView(T t, ListView listView) {
        if (t == null) {
            return false;
        }
        if ((t instanceof List) && ((List) t).size() == 0) {
            return false;
        }
        this.mEntity = t;
        getView(t, listView);
        return true;
    }

    protected abstract void getView(T t, ListView listView);

}
