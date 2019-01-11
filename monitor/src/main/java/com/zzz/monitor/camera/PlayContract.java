package com.zzz.monitor.camera;


import com.zzz.monitor.base.BasePresenter;
import com.zzz.monitor.base.BaseView;

public interface PlayContract {
    interface Presenter extends BasePresenter {
        void play(String sn);

        void keepPlay(String sn);

        void takePhoto();

        void ptz(String direction);

        void sendAudio(String audio);

        void cancelPlay();
    }

    interface View extends BaseView<Presenter> {
        void liveStart(String url);

        void liveFailure();
    }
}
