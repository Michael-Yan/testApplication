package com.zzz.monitor.home;


import com.zzz.monitor.adapter.CameraAdapter;
import com.zzz.monitor.base.BasePresenter;
import com.zzz.monitor.base.BaseView;

public interface HomeContract {
    interface Presenter extends BasePresenter {
    }

    interface View extends BaseView<Presenter> {
        void init();

        void setAdapter(CameraAdapter adapter);

        void hideSwipe();

        void showEmptyMsg();

        void hideEmptyMsg();

        void lock(boolean isLocked);
    }
}
