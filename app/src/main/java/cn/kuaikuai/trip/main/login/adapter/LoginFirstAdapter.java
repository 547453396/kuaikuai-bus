package cn.kuaikuai.trip.main.login.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class LoginFirstAdapter extends PagerAdapter {
    private ArrayList<View> views = new ArrayList<>();

    public LoginFirstAdapter(ArrayList<View> temps) {
        if (temps != null) {
            views.clear();
            views.addAll(temps);
        }
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView(views.get(position));
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        view.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
