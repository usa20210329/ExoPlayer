package com.fongmi.android.tv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.fongmi.android.tv.ApiService;
import com.fongmi.android.tv.R;
import com.fongmi.android.tv.impl.KeyDownImpl;
import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.AsyncTaskRunnerCallback;
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
    @BindView(R.id.seekBar) AppCompatSeekBar mSeekBar;
    @BindView(R.id.videoView) VideoView mVideoView;
    @BindView(R.id.progress) ProgressBar mProgress;
    @BindView(R.id.setting) ViewGroup mSetting;
    @BindView(R.id.splash) ImageView mSplash;
    @BindView(R.id.number) TextView mNumber;
    @BindView(R.id.gear) ImageView mGear;
    @BindView(R.id.hide) View mHide;

    private ChannelAdapter mAdapter;
    private KeyDown mKeyDown;
    private Handler mHandler;
    private boolean mBackPress;

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
        mSeekBar.setProgress(Prefers.getTextSize());
        ViewCompat.setElevation(mSetting, 8);
        Utils.getDatabase(this);
        setRecyclerView();
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
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Prefers.putTextSize(progress);
                mAdapter.notifyDataSetChanged();
                setInfoWidth();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                hideCard();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
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
        ApiService.getInstance().onInit(new AsyncTaskRunnerCallback() {
            @Override
            public void onResponse() {
                getChannels();
            }
        });
    }

    private void getChannels() {
        ApiService.getInstance().getChannels(new AsyncTaskRunnerCallback() {
            @Override
            public void onResponse(List<Channel> items) {
                mAdapter.addAll(items);
            }
        });
    }

    private void onPlay(Channel channel) {
        if (channel.hasUrl()) {
            playVideo(channel.getRealUrl());
        } else {
            showProgress();
            ApiService.getInstance().getChannelUrl(channel, getCallback(channel));
        }
    }

    private void onRetry() {
        mAdapter.resetUrl();
        ApiService.getInstance().onRetry(new AsyncTaskRunnerCallback() {
            @Override
            public void onResponse() {
                mAdapter.onResume();
            }
        });
    }

    private AsyncTaskRunnerCallback getCallback(final Channel channel) {
        return new AsyncTaskRunnerCallback() {
            @Override
            public void onResponse(String url) {
                channel.setRealUrl(url);
                playVideo(channel.getRealUrl());
            }
        };
    }

    private void playVideo(String url) {
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, 3000);
        mVideoView.setVideoURI(Uri.parse(url));
        mVideoView.start();
        showProgress();
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            hideUI();
        }
    };

    private Runnable mBackHint = new Runnable() {
        @Override
        public void run() {
            mBackPress = false;
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

    private boolean cardVisible() {
        return mSetting.getAlpha() == 1;
    }

    private boolean showBackHint() {
        if (!mBackPress) {
            mBackPress = true;
            return true;
        } else {
            return false;
        }
    }

    private void toggleInfo() {
        if (cardVisible()) {
            hideCard();
        } else if (infoVisible()) {
            hideUI();
        } else {
            showUI();
        }
    }

    private void toggleCard() {
        if (cardVisible()) {
            hideCard();
        } else {
            showCard();
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

    private void showCard() {
        mHandler.removeCallbacks(mRunnable);
        mSetting.animate().alpha(1).setDuration(250).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mSetting.setVisibility(View.VISIBLE);
            }
        }).start();
    }

    private void hideCard() {
        mSetting.animate().alpha(0).setDuration(250).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSetting.setVisibility(View.GONE);
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
        mNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getTextSize() * 8 + 64);
        ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
        params.width = Utils.dp2px(200 + Prefers.getTextSize() * 20);
        mRecyclerView.setLayoutParams(params);
    }

    private void checkCard(int diff, View view) {
        if (cardVisible()) {
            mSeekBar.incrementProgressBy(diff);
        } else {
            view.performClick();
        }
    }

    @OnTouch(R.id.videoView)
    public boolean onTouch(MotionEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) toggleInfo();
        return true;
    }

    @OnClick(R.id.gear)
    public void onGear() {
        toggleCard();
    }

    @OnClick(R.id.hide)
    public void onHide() {
        mHandler.removeCallbacks(mAddCount);
        mHandler.postDelayed(mAddCount, 1000);
    }

    @Override
    public void onFind(Channel channel) {
        mAdapter.findChannel(mRecyclerView, channel);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Utils.isDigitKey(event)) {
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
        boolean wait = infoVisible();
        mHandler.removeCallbacks(mRunnable);
        mRecyclerView.smoothScrollToPosition(isTop ? mAdapter.onMoveUp(wait) : mAdapter.onMoveDown(wait));
    }

    @Override
    public void onKeyHorizontal(boolean isLeft) {
        if (isLeft) {
            checkCard(-1, mHide);
        } else {
            checkCard(1, mGear);
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
        mAdapter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.setVisible(false);
        mVideoView.pause();
    }

    @Override
    public void onBackPressed() {
        if (cardVisible()) {
            hideCard();
        } else if (infoVisible()) {
            hideUI();
        } else if (showBackHint()) {
            mHandler.postDelayed(mBackHint, 3000);
            Notify.show(R.string.channel_back_hint, Toast.LENGTH_SHORT);
        } else {
            moveTaskToBack(true);
            mAdapter.removeHiddenChannel();
        }
    }
}
