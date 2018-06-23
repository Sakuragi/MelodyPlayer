package com.jim.melodyplayer.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jim.melodyplayer.R;
import com.jim.melodyplayer.model.Banner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by muyang on 2016/3/24.
 */
public class LoodView extends FrameLayout {
    //轮播图图片数量
    private final static int IMAGE_COUNT = 7;
    //自动轮播时间间隔
    private final static int TIME_INTERVAL = 3;
    //自动轮播启用开关
    private final static boolean isAutoPlay = true;
    //ImageView资源ID
    private int[] imageResIds;
    private FPagerAdapter fPagerAdapter;
    private ArrayList<String> imageNet = new ArrayList<>();
    private List<ImageView> imageViewList;
    private List<View> dotViewList;
    private ViewPager viewPager;
    private boolean isFromCache = true;
    private Context mContext;
    //当前轮播页面
    private int currentItem = 0;
    private List<Banner> mBanners;
    //定时任务
    private ScheduledExecutorService scheduledExecutorService;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            viewPager.setCurrentItem(currentItem);
        }
    };


    public LoodView(Context context) {
        super(context);
        mContext = context;
    }

    public LoodView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
        mContext = context;
    }

    public LoodView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        mContext = context;
        initImageView();

    }

    public void upDateUI(List<Banner> banners) {
        mBanners = banners;
        initUI(mContext);
        if (isAutoPlay) {
            startPlay();
        }
    }

    public void onDestroy() {
        scheduledExecutorService.shutdownNow();
        scheduledExecutorService = null;
    }

    /**
     * 开始轮播图切换
     */

    public void startPlay() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new LoopTask(), 1, TIME_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * 停止切换
     */
    private void stopPlay() {
        scheduledExecutorService.shutdown();
    }

    /**
     * 初始化UI
     *
     * @param context
     */
    private void initUI(Context context) {
        LayoutInflater.from(context).inflate(R.layout.load_view, this, true);
        for (int i = 0; i < mBanners.size(); i++) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageURI(Uri.parse(mBanners.get(i).imag_url));
            imageViewList.add(imageView);
        }
        dotViewList.add(findViewById(R.id.v_dot1));
        dotViewList.add(findViewById(R.id.v_dot2));
        dotViewList.add(findViewById(R.id.v_dot3));
        dotViewList.add(findViewById(R.id.v_dot4));
        dotViewList.add(findViewById(R.id.v_dot5));
        dotViewList.add(findViewById(R.id.v_dot6));
        dotViewList.add(findViewById(R.id.v_dot7));

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setFocusable(true);
        fPagerAdapter = new FPagerAdapter();
        viewPager.setAdapter(fPagerAdapter);
        viewPager.addOnPageChangeListener(new MyPageChangeListener());
    }

    private void initImageView() {
        imageResIds = new int[]{
                R.mipmap.first,
                R.mipmap.second,
                R.mipmap.third,
                R.mipmap.fourth,
                R.mipmap.five,
                R.mipmap.six,
                R.mipmap.seven
        };

        for (int i = 0; i < 7; i++) {
            imageNet.add("");
        }

        imageViewList = new ArrayList<>();
        dotViewList = new ArrayList<>();
    }

    private class FPagerAdapter extends PagerAdapter {


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageViewList.get(position));
            return imageViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViewList.get(position));
        }

        @Override
        public int getCount() {
            return imageViewList.size();
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            super.restoreState(state, loader);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(ViewGroup container) {
            super.startUpdate(container);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {
        boolean isAutoPlay = false;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currentItem = position;
            for (int i = 0; i < dotViewList.size(); i++) {
                if (i == position) {
                    dotViewList.get(i).setBackgroundResource(R.drawable.ic_point_white);
                } else {
                    dotViewList.get(i).setBackgroundResource(R.drawable.ic_point_grey);
                }
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                //手势滑动，空闲
                case 1:
                    isAutoPlay = false;
                    stopPlay();
                    startPlay();
                    break;
                //界面切换中
                case 2:
                    isAutoPlay = true;
                    break;
                //滑动完毕，继续回到第一张图
                case 0:
                    if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                        viewPager.setCurrentItem(0);
                    }
                    //当前为第一张，从左向右滑
                    else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
                    }
                    break;
            }

        }
    }

    //解决滑动冲突
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private class LoopTask implements Runnable {
        @Override
        public void run() {
            synchronized (viewPager) {
                currentItem = (currentItem + 1) % imageViewList.size();
                handler.obtainMessage().sendToTarget();

            }

        }
    }

    /**
     * 销毁ImageView回收资源
     */
    private void destoryBitmaps() {
        for (int i = 0; i < IMAGE_COUNT; i++) {
            ImageView imageView = imageViewList.get(i);
            Drawable drawable = imageView.getDrawable();
            if (drawable != null)
                //解除drawable对view的引用
                drawable.setCallback(null);
        }
    }
}

