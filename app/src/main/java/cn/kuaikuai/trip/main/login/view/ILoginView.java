package cn.kuaikuai.trip.main.login.view;

public interface ILoginView {
    void onGetVerifyCodeSucceed();

    void onGetVerifyCodeFailed(String msg);

    void onLoginSucceed();

    void onLoginFailed(String msg);
}
