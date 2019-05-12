package cn.kuaikuai.base.view;


public interface IBaseView {
    /**
     * 展示加载条
     */
    void showLoading();

    /**
     * 隐藏加载条
     */
    void hideLoading();

    void showException(Throwable pe);
}
