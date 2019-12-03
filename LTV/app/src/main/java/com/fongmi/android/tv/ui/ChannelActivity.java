package com.fongmi.android.tv.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdularis.app.analogtvnoise.AnalogTvNoise;
import com.devbrackets.android.exomedia.core.video.scale.ScaleType;
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
	@BindView(R.id.noise) AnalogTvNoise mNoise;
	@BindView(R.id.splash) ImageView mSplash;
	@BindView(R.id.info) LinearLayout mInfo;
	@BindView(R.id.number) TextView mNumber;
	@BindView(R.id.gear) ImageView mGear;
	@BindView(R.id.name) TextView mName;
	@BindView(R.id.hide) View mHide;

	private ChannelAdapter mAdapter;
	private KeyDown mKeyDown;
	private Handler mHandler;
	private int retry;

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
		mKeyDown = new KeyDown(this, mInfo, mNumber, mName);
		Utils.getVersion(this);
		setRecyclerView();
		setCustomSize();
		showProgress();
		setScaleType();
		hideSplash();
		getList();
	}

	private void initEvent() {
		mAdapter.setOnItemClickListener(this::onPlay);
		mVideoView.setOnPreparedListener(this::onPrepared);
		mVideoView.setOnErrorListener((Exception e) -> onRetry());
		mRecyclerView.addOnScrollListener(mScrollListener);
	}

	private void setRecyclerView() {
		mAdapter = new ChannelAdapter();
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setAdapter(mAdapter);
	}

	private void getList() {
		ApiService.getInstance().getList(new AsyncCallback() {
			@Override
			public void onResponse(List<Channel> items) {
				mKeyDown.setChannels(items);
				mAdapter.addAll(items);
				hideProgress();
				checkKeep();
			}
		});
	}

	private void onPlay(Channel channel) {
		if (channel.hasUrl()) {
			playVideo(channel);
		} else {
			showProgress();
			ApiService.getInstance().getUrl(channel, getCallback(channel));
		}
	}

	private AsyncCallback getCallback(final Channel channel) {
		return new AsyncCallback() {
			@Override
			public void onResponse(String url) {
				channel.setReal(url);
				playVideo(channel);
			}
		};
	}

	private void checkKeep() {
		if (Prefers.getKeep() == -1) return;
		onFind(Channel.create(Prefers.getKeep()));
	}

	private void onPrepared() {
		hideProgress();
		retry = 0;
	}

	private boolean onRetry() {
		if (++retry > 1) onError();
		else mAdapter.resetUrl();
		return true;
	}

	private void onError() {
		mVideoView.reset();
		hideProgress();
		showError();
	}

	private void playVideo(Channel channel) {
		Prefers.putKeep(channel.getNumber());
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, 3000);
		mVideoView.setVideoURI(Uri.parse(channel.getReal()));
		mVideoView.start();
		showProgress();
		hideError();
	}

	private Runnable mRunnable = this::hideUI;

	private Runnable mAddCount = new Runnable() {
		@Override
		public void run() {
			mAdapter.addCount();
		}
	};

	private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
		@Override
		public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
			if (newState == RecyclerView.SCROLL_STATE_IDLE) mHandler.postDelayed(mRunnable, 2000);
			else mHandler.removeCallbacks(mRunnable);
		}
	};

	private void showProgress() {
		if (mProgress.getVisibility() == View.GONE) mProgress.setVisibility(View.VISIBLE);
	}

	private void hideProgress() {
		if (mProgress.getVisibility() == View.VISIBLE) mProgress.setVisibility(View.GONE);
	}

	private void showError() {
		if (mNoise.getVisibility() == View.GONE) mNoise.setVisibility(View.VISIBLE);
	}

	private void hideError() {
		if (mNoise.getVisibility() == View.VISIBLE) mNoise.setVisibility(View.GONE);
	}

	private boolean infoVisible() {
		return mRecyclerView.getAlpha() == 1;
	}

	private boolean infoInvisible() {
		return mRecyclerView.getAlpha() == 0;
	}

	private boolean playWait() {
		return Prefers.isEnter() && infoVisible();
	}

	private void toggleUI() {
		if (infoVisible()) hideUI();
		else showUI();
	}

	private void showUI() {
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
			}
		}).start();
	}

	private void setCustomSize() {
		mNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 4 + 40);
		mName.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 4 + 40);
		ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
		params.width = Utils.dp2px(200 + Prefers.getSize() * 20);
		mRecyclerView.setLayoutParams(params);
	}

	public void onSizeChange(int progress) {
		Prefers.putSize(progress);
		mAdapter.notifyDataSetChanged();
		setCustomSize();
	}

	public void setScaleType() {
		mVideoView.setScaleType(Prefers.isFull() ? ScaleType.FIT_XY : ScaleType.FIT_CENTER);
	}

	@OnTouch(R.id.videoView)
	public boolean onTouch(MotionEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP) toggleUI();
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
		mRecyclerView.scrollToPosition(mAdapter.getIndex(channel));
		mAdapter.setPosition(mAdapter.getIndex(channel));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (Utils.isDigitKey(keyCode)) return mKeyDown.onKeyDown(keyCode);
		else return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (Utils.hasEvent(event)) return mKeyDown.onKeyDown(event);
		else return super.dispatchKeyEvent(event);
	}

	@Override
	public void onKeyVertical(boolean isNext) {
		mRecyclerView.smoothScrollToPosition(isNext ? mAdapter.onMoveDown(playWait()) : mAdapter.onMoveUp(playWait()));
		if (infoInvisible()) showUI();
	}

	@Override
	public void onKeyHorizontal(boolean isLeft) {
		if (isLeft) mHide.performClick();
		else Notify.showDialog(this, View.VISIBLE);
	}

	@Override
	public void onKeyCenter() {
		mAdapter.onCenter();
		toggleUI();
	}

	@Override
	public void onKeyBack() {
		onBackPressed();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) Utils.setImmersiveMode(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mAdapter.setVisible(true);
		mAdapter.setChannel(0);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mAdapter.setVisible(false);
		mVideoView.stopPlayback();
	}

	@Override
	public void onBackPressed() {
		if (infoVisible()) hideUI();
		else super.onBackPressed();
	}
}
