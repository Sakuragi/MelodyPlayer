package com.jim.AudioPlayerDemo.base;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jim on 2017/10/11 0011.
 */

public abstract class CommonAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int STATE_NO_DATA = 1000;
    public static final int STATE_GET_DATA_ERROR = 1001;
    public static final int STATE_DATA_AVAILABLE = 1002;

    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;

    private Context mContext;
    private List<T> mDatas;
    private int mLayoutId;
    private int dataState;

    private SparseArrayCompat<View> mHeaderViews;
    private SparseArrayCompat<View> mFootViews;

    private Object mObject = new Object();

    public CommonAdapter(Context context, int layoutId, List<T> datas) {
        mContext = context;
        mDatas = datas;
        mLayoutId = layoutId;
    }

    public void addHeaderView(View v) {
        if (mHeaderViews == null) {
            mHeaderViews = new SparseArrayCompat<>();
        }
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, v);
    }

    public void addFooterView(View v) {
        if (mFootViews == null) {
            mFootViews = new SparseArrayCompat<>();
        }
        mFootViews.put(mFootViews.size() + BASE_ITEM_TYPE_FOOTER, v);
    }

    public int getHeadersCount() {
        if (mHeaderViews != null) {
            return mHeaderViews.size();
        }
        return 0;
    }

    public int getFootersCount() {
        if (mFootViews != null) {
            return mFootViews.size();
        }
        return 0;
    }

    private boolean isHeaderView(int position) {
        return position < getHeadersCount();
    }

    private boolean isFooterView(int position) {
        return position >= getHeadersCount() + mDatas.size();
    }

    public void setDatas(List<T> datas) {
        if (datas != null) {
            mDatas = datas;
        }
    }

    public void setDatas(List<T> datas, int dataState) {
        if (datas != null) {
            mDatas = datas;
        }
        this.dataState = dataState;
    }

    public void addDatas(List<T> datas) {
        synchronized (mObject) {
            if (mDatas == null) {
                mDatas = new ArrayList<T>();
            }
            mDatas.addAll(datas);
            notifyItemRangeChanged(mDatas.size() - datas.size() - 1, datas.size());
        }

    }

    public void addData(T data) {
        synchronized (mObject) {
            if (mDatas == null) {
                mDatas = new ArrayList<T>();
            }
            mDatas.add(data);
            notifyItemRangeChanged(mDatas.size() - 1, 1);
        }
    }

    public void addData(int index, T data) {
        Log.d("TAG","index: " + index + " data size: " + mDatas.size());
        synchronized (mObject) {
            if (mDatas == null) {
                mDatas = new ArrayList<T>();
            }
            mDatas.add(index, data);
            notifyItemInserted(index);
            Log.d("TAG","after addData notifyItemInserted");
            notifyItemRangeChanged(index, mDatas.size() - index); //解决notifyItemRemoved后数据错乱
        }
    }


    public void removeData(T data) {
        synchronized (mObject) {
            mDatas.remove(data);
            notifyDataSetChanged();
        }
    }

    public void removeData(int index) {
        Log.d("TAG","index: " + index + " data size: " + mDatas.size());
        synchronized (mObject) {
            mDatas.remove(index);
            notifyItemRemoved(index);
            notifyItemRangeChanged(index, mDatas.size()); //解决notifyItemRemoved后数据错乱
        }
    }

    public void clearData() {
        if (mDatas != null) {
            mDatas.clear();
            notifyDataSetChanged();
        }
    }

    public List<T> getDatas() {
        if (mDatas == null) {
            mDatas = new ArrayList<T>();
        }
        return mDatas;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("TAG","onCreateViewHolder");
        if (mHeaderViews != null && mHeaderViews.get(viewType) != null) {
            return new BaseViewHolder(mHeaderViews.get(viewType), mContext, viewType);
        }
        if (mFootViews != null && mFootViews.get(viewType) != null) {
            return new BaseViewHolder(mFootViews.get(viewType), mContext, viewType);
        }
        View view = LayoutInflater.from(mContext).inflate(mLayoutId, parent, false);
        return new BaseViewHolder(view, mContext, viewType);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
//        Log.d("TAG",onBindViewHolder position: "+position);
        if (isHeaderView(position)) {
            return;
        }
        if (isFooterView(position)) {
            return;
        }
        if (mDatas != null && mDatas.size() > 0) {
            T data = mDatas.get(position - getHeadersCount());
            convert(holder, position, data, dataState);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderView(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isFooterView(position)) {
            return mFootViews.keyAt(position - mDatas.size() - getHeadersCount());
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mDatas != null && mDatas.size() > 0 ? mDatas.size() + getHeadersCount() + getFootersCount() : getFootersCount() + getHeadersCount();
    }

    public void clearHeaderViews() {
        if (mHeaderViews != null && mHeaderViews.size() > 0) {
            mHeaderViews.clear();
        }
        mHeaderViews = null;
    }

    public abstract void convert(BaseViewHolder holder, int position, T data, int dataState);
}
