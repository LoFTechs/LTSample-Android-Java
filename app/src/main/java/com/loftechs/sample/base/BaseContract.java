package com.loftechs.sample.base;

public interface BaseContract {

    interface View<T> {
        void setPresenter(T presenter);
    }

    interface Presenter {
        void create();

        void resume();

        void pause();

        void destroy();
    }
}
