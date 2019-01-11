package com.zzz.monitor.camera;


import com.zzz.monitor.adapter.GalleryAdapter;
import com.zzz.monitor.base.BasePresenter;
import com.zzz.monitor.base.BaseView;

public interface GalleryContract {
    interface Presenter extends BasePresenter {
        void getGallery(String sn, int page, String start, String end);
    }

    interface View extends BaseView<Presenter> {
        void setAdapter(GalleryAdapter adapter);

        void showProgress(boolean isShow);

        void showEmptyMsg();

        void hideEmptyMsg();

        void lock(boolean isLocked);
    }
}
