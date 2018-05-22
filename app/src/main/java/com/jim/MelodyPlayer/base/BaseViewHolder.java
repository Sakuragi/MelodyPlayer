package com.jim.MelodyPlayer.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;
    private Context mContext;
    private View convertView;
    private int viewtype;
    private int position;


    public BaseViewHolder(View itemView) {
        this(itemView, itemView.getContext(), 0);
    }

    public BaseViewHolder(View itemView, Context context, int viewtype) {
        super(itemView);
        mContext = context;
        convertView = itemView;
        mViews = new SparseArray<>();
        this.viewtype = viewtype;
    }

    public int getViewtype() {
        return viewtype;
    }

    public void updatePosition(int position) {
        this.position = position;
    }

    public View getItemView() {
        return convertView;
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public BaseViewHolder setWidth(int viewId, int pixels) {
        TextView textView = getView(viewId);
        textView.setWidth(pixels);
        return this;
    }

    public BaseViewHolder setText(int viewId, int resId) {
        TextView textView = getView(viewId);
        textView.setText(resId);
        return this;
    }


    public BaseViewHolder setText(int viewId, String text) {
        TextView textView = getView(viewId);
        if (textView != null) {
            textView.setText(text);
        }
        return this;
    }

    public BaseViewHolder setText(int viewId, SpannableStringBuilder text) {
        TextView textView = getView(viewId);
        if (textView != null) {
            textView.setText(text);
        }
        return this;
    }


    public BaseViewHolder setText(int viewId, CharSequence content) {
        TextView tv = (TextView) getView(viewId);
        if (tv != null) {
            tv.setText(content);
        }
        return this;
    }

    public BaseViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    public BaseViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

    public BaseViewHolder setImageDrawable(int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        view.setImageDrawable(drawable);
        return this;
    }


    public BaseViewHolder setImageWithUrl(int viewId, String url) {
        ImageView iv = (ImageView) getView(viewId);
        if (iv != null) {
            RequestOptions options=new RequestOptions();
            options.fitCenter();
            Glide.with(mContext)
                    .load(url)
                    .apply(options)
                    .into(iv);
        }
        return this;
    }





    public BaseViewHolder setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    public BaseViewHolder setBackgroundResource(int viewId, int backgroundRes) {
        View view = getView(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    public BaseViewHolder setTextColor(int viewId, int textColor) {
        TextView view = getView(viewId);
        view.setTextColor(textColor);
        return this;
    }


    @SuppressLint("NewApi")
    public BaseViewHolder setAlpha(int viewId, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView(viewId).setAlpha(value);
        } else {
            // Pre-honeycomb hack to set Alpha value
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            getView(viewId).startAnimation(alpha);
        }
        return this;
    }

    public BaseViewHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }


    public BaseViewHolder setTag(int viewId, Object tag) {
        View view = getView(viewId);
        view.setTag(tag);
        return this;
    }

    public BaseViewHolder setTag(int viewId, int key, Object tag) {
        View view = getView(viewId);
        view.setTag(key, tag);
        return this;
    }

    public BaseViewHolder setChecked(int viewId, boolean checked) {
        Checkable view = (Checkable) getView(viewId);
        view.setChecked(checked);
        return this;
    }

    /**
     * 关于事件监听
     */
    public BaseViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {

        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    public BaseViewHolder setOnTouchListener(int viewId, View.OnTouchListener listener) {
        View view = getView(viewId);
        view.setOnTouchListener(listener);
        return this;
    }

    public BaseViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        View view = getView(viewId);
        view.setOnLongClickListener(listener);
        return this;
    }

    public BaseViewHolder setClickable(int viewId, boolean clickable) {
        View view = getView(viewId);
        if (view != null) {
            view.setClickable(clickable);
        }
        return this;
    }

    public BaseViewHolder setLayoutManager(int viewId, RecyclerView.LayoutManager layoutManager) {
        RecyclerView recyclerView = (RecyclerView) getView(viewId);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(layoutManager);
        }
        return this;
    }

    public BaseViewHolder setRcvAdapter(int viewId, RecyclerView.Adapter adapter) {
        RecyclerView recyclerView = (RecyclerView) getView(viewId);
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
        }
        return this;
    }


    public BaseViewHolder setVisibility(int viewId, int visibility) {
        View view = getView(viewId);
        if (view != null) {
            view.setVisibility(visibility);
        }
        return this;
    }

}
