package com.asdf1st.mydemo.Base.View;


import android.content.Context;

import com.asdf1st.mydemo.Base.Presenter.IPresenter;

public interface IView<T extends IPresenter> {
    T createPresenter();

    public void dismissWaittingDialog();

    public void showWaittingDialog();

    public void showMessage(String message);

    public Context getContext();

    public void showAlert();
    
}
