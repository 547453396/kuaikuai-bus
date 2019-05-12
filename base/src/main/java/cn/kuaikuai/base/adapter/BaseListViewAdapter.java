package cn.kuaikuai.base.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;


/***
 * listView 基础适配器
 * @param <T>
 */
public abstract class BaseListViewAdapter<T> extends BaseAdapter {

    public String TAG = this.getClass().getSimpleName();

    protected Context mContext;

    private List<T> mData = new ArrayList<>();

    public BaseListViewAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public List<T> getData() {
        return mData;
    }

    @Override
    public T getItem(int position) {
        if (position < mData.size()) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView,
                                 ViewGroup parent);

    /**
     * 更新数据源
     */
    public void updateData(List<T> list) {
        mData.clear();
        if (list != null && list.size() > 0) {
            mData.addAll(list);
        }
        notifyDataSetChanged();
    }

    /**
     * 删除position的数据
     *
     * @param position
     */
    public void deleteItem(int position) {
        if (mData != null && mData.size() > position) {
            mData.remove(position);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加数据源
     */
    public void addData(List<T> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        if (mData != null && mData.size() > 0) {
            mData.addAll(mData.size(), list);
        } else {
            mData.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void addFirst(T t) {
        if (t == null) {
            return;
        }
        mData.add(0, t);
        notifyDataSetChanged();
    }

    public void addEnd(T t) {
        if (t == null) {
            return;
        }
        mData.add(t);
        notifyDataSetChanged();
    }


    public void clearData() {
        mData.clear();
        notifyDataSetChanged();
    }
}
