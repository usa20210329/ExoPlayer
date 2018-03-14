package com.fongmi.android.tv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.fongmi.android.tv.ApiService;
import com.fongmi.android.tv.R;
import com.fongmi.android.tv.impl.KeyDownImpl;
import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncCallback;
import com.fongmi.android.tv.utils.KeyDown;
import com.fongmi.android.tv.utils.Notify;
import com.fongmi.android.tv.utils.Prefers;
import com.fongmi.android.tv.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class ChannelActivity extends AppCompatActivity implements KeyDownImpl {

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.videoView) VideoView mVideoView;
    @BindView(R.id.progress) ProgressBar mProgress;
    @BindView(R.id.splash) ImageView mSplash;
    @BindView(R.id.number) TextView mNumber;
    @BindView(R.id.gear) ImageView mGear;
    @BindView(R.id.hide) View mHide;

    private ChannelAdapter mAdapter;
    private KeyDown mKeyDown;
    private Handler mHandler;
    private long mBackTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Utils.setImmersiveMode(this);
        initView();
        initEvent();
    }

    private void initView() {
        mHandler = new Handler();
        mKeyDown = new KeyDown(this, mNumber);
        Utils.getDatabase(this);
        setRecyclerView();
        showProgress();
        setInfoWidth();
        hideSplash();
        onInit();
    }

    private void initEvent() {
        mAdapter.setOnItemClickListener(new ChannelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Channel item) {
                onPlay(item);
            }
        });
        mVideoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                hideProgress();
            }
        });
        mVideoView.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(Exception e) {
                onRetry();
                return true;
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        mHandler.removeCallbacks(mRunnable);
                        break;
                    case RecyclerView.SCROLL_STATE_IDLE:
                        mHandler.postDelayed(mRunnable, 3000);
                        break;
                }
            }
        });
    }

    private void setRecyclerView() {
        mAdapter = new ChannelAdapter();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void onInit() {
        ApiService.getInstance().onInit(new AsyncCallback() {
            @Override
            public void onResponse(boolean success) {
                getChannels();
            }
        });
    }

    public void getChannels() {
        ApiService.getInstance().getChannels(new AsyncCallback() {
            @Override
            public void onResponse(List<Channel> items) {
                mAdapter.addAll(items);
                hideProgress();
            }
        });
    }

    private void onPlay(Channel channel) {
        if (channel.hasUrl()) {
            playVideo(channel);
        } else {
            showProgress();
            ApiService.getInstance().getChannelUrl(channel, getCallback(channel));
        }
    }

    private AsyncCallback getCallback(final Channel channel) {
        return new AsyncCallback() {
            @Override
            public void onResponse(String url) {
                channel.setRealUrl(url);
                playVideo(channel);
            }
        };
    }

    private void onRetry() {
        mAdapter.resetUrl();
        ApiService.getInstance().onRetry(new AsyncCallback() {
            @Override
            public void onResponse(boolean success) {
                onRetry(success);
            }
        });
    }

    private void onRetry(boolean success) {
        if (success) {
            mAdapter.onResume();
        } else {
            showUI();
            hideProgress();
            Notify.show(R.string.channel_error);
        }
    }

    private void playVideo(Channel channel) {
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, 3000);
        mVideoView.setVideoURI(Uri.parse(channel.getRealUrl()));
        mVideoView.start();
        showProgress();
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            hideUI();
        }
    };

    private Runnable mAddCount = new Runnable() {
        @Override
        public void run() {
            mAdapter.addCount();
        }
    };

    private void showProgress() {
        if (mProgress.getVisibility() == View.GONE) {
            mProgress.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgress() {
        if (mProgress.getVisibility() == View.VISIBLE) {
            mProgress.setVisibility(View.GONE);
        }
    }

    private boolean infoVisible() {
        return mRecyclerView.getAlpha() == 1;
    }

    private boolean playWait() {
        return Prefers.isPlayWait() && infoVisible();
    }

    private boolean backWait() {
        return Prefers.isBackWait() && System.currentTimeMillis() - mBackTime > 2000;
    }

    private void toggleInfo() {
        if (infoVisible()) {
            hideUI();
        } else {
            showUI();
        }
    }

    private void showUI() {
        mHandler.removeCallbacks(mRunnable);
        mGear.animate().alpha(1).setDuration(250).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mGear.setVisibility(View.VISIBLE);
            }
        }).start();
        mRecyclerView.animate().alpha(1).setDuration(250).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }).start();
    }

    private void hideUI() {
        mGear.animate().alpha(0).setDuration(250).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mGear.setVisibility(View.GONE);
            }
        }).start();
        mRecyclerView.animate().alpha(0).setDuration(250).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRecyclerView.setVisibility(View.GONE);
            }
        }).start();
    }

    private void hideSplash() {
        mSplash.animate().setStartDelay(1500).alpha(0).setDuration(250).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSplash.setVisibility(View.GONE);
                Utils.getNotice();
            }
        }).start();
    }

    private void setInfoWidth() {
        mNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 8 + 64);
        ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
        params.width = Utils.dp2px(200 + Prefers.getSize() * 20);
        mRecyclerView.setLayoutParams(params);
    }

    public void onSizeChange(int progress) {
        Prefers.putSize(progress);
        mAdapter.notifyDataSetChanged();
        setInfoWidth();
    }

    @OnTouch(R.id.videoView)
    public boolean onTouch(MotionEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) toggleInfo();
        return true;
    }

    @OnClick(R.id.gear)
    public void onGear() {
        Notify.showDialog(this);
    }

    @OnClick(R.id.hide)
    public void onHide() {
        mHandler.removeCallbacks(mAddCount);
        mHandler.postDelayed(mAddCount, 500);
    }

    @Override
    public void onFind(Channel channel) {
        mAdapter.findChannel(mRecyclerView, channel);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Utils.isDigitKey(keyCode)) {
            return mKeyDown.onKeyDown(keyCode);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (Utils.hasEvent(event)) {
            return mKeyDown.onKeyDown(event);
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public void onKeyVertical(boolean isTop) {
        mHandler.removeCallbacks(mRunnable);
        mRecyclerView.smoothScrollToPosition(isTop ? mAdapter.onMoveUp(playWait()) : mAdapter.onMoveDown(playWait()));
    }

    @Override
    public void onKeyHorizontal(boolean isLeft) {
        if (isLeft) {
            mHide.performClick();
        } else {
            Notify.showDialog(this, View.VISIBLE);
        }
    }

    @Override
    public void onKeyCenter() {
        mAdapter.onCenter();
        toggleInfo();
    }

    @Override
    public void onKeyBack() {
        onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Utils.setImmersiveMode(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.setVisible(true);
        if (!Prefers.isKeep()) mAdapter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.setVisible(false);
        if (!Prefers.isKeep()) mVideoView.pause();
    }

    @Override
    public void onBackPressed() {
        if (infoVisible()) {
            hideUI();
        } else if (backWait()) {
            mBackTime = System.currentTimeMillis();
            Notify.show(R.string.channel_hint_back);
        } else {
            moveTaskToBack(true);
            mAdapter.removeHiddenChannel();
        }
    }
}
