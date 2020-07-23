package com.fongmi.android.ltv.ui;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdularis.app.analogtvnoise.AnalogTvNoise;
import com.devbrackets.android.exomedia.core.video.scale.ScaleType;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.fongmi.android.ltv.R;
import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.impl.KeyDownImpl;
import com.fongmi.android.ltv.network.ApiService;
import com.fongmi.android.ltv.network.AsyncCallback;
import com.fongmi.android.ltv.network.DownloadTask;
import com.fongmi.android.ltv.utils.FileUtil;
import com.fongmi.android.ltv.utils.KeyDown;
import com.fongmi.android.ltv.utils.Notify;
import com.fongmi.android.ltv.utils.Prefers;
import com.fongmi.android.ltv.utils.Token;
import com.fongmi.android.ltv.utils.Utils;

import java.util.List;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class MainActivity extends AppCompatActivity implements KeyDownImpl {

	@BindView(R.id.recyclerView) RecyclerView mRecyclerView;
	@BindView(R.id.videoView) VideoView mVideoView;
	@BindView(R.id.progress) ProgressBar mProgress;
	@BindView(R.id.noise) AnalogTvNoise mNoise;
	@BindView(R.id.info) ViewGroup mInfo;
	@BindView(R.id.number) TextView mNumber;
	@BindView(R.id.gear) ImageView mGear;
	@BindView(R.id.name) TextView mName;
	@BindView(R.id.hide) View mHide;

	private MainAdapter mAdapter;
	private KeyDown mKeyDown;
	private Handler mHandler;
	private Timer mTimer;
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
		setRecyclerView();
		setCustomSize();
		setScaleType();
		getConfig();
	}

	private void initEvent() {
		mAdapter.setOnItemListener(this::onClick);
		mVideoView.setOnErrorListener(this::onRetry);
		mVideoView.setOnPreparedListener(this::onPrepared);
		mRecyclerView.addOnScrollListener(mScrollListener);
	}

	private void setRecyclerView() {
		mAdapter = new MainAdapter();
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setAdapter(mAdapter);
	}

	private void getConfig() {
		ApiService.getInstance().getConfig(new AsyncCallback() {
			@Override
			public void onResponse(List<Channel> items) {
				mKeyDown.setChannels(items);
				mAdapter.addAll(items);
				checkKeep();
			}
		});
	}

	private void getUrl(Channel item) {
		ApiService.getInstance().getUrl(item, new AsyncCallback() {
			@Override
			public void onResponse(String url) {
				if (FileUtil.isFile(url)) setTimer(item);
				else cancelTimer();
				playVideo(url);
			}
		});
	}

	private void checkKeep() {
		if (Prefers.getKeep().isEmpty()) return;
		onFind(Channel.create(Prefers.getKeep()));
	}

	private void onClick(Channel item) {
		Token.setProvider(item);
		showProgress();
		getUrl(item);
		hideError();
	}

	private void onPrepared() {
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, 1500);
		hideProgress();
		retry = 0;
	}

	private boolean onRetry(Exception e) {
		if (++retry > 3) onError(e);
		else mAdapter.setChannel();
		return true;
	}

	private void onError(Exception e) {
		e.printStackTrace();
		mVideoView.reset();
		hideProgress();
		showError();
	}

	private void playVideo(String url) {
		mVideoView.setVideoURI(Uri.parse(url));
		mVideoView.start();
	}

	private void cancelTimer() {
		if (mTimer != null) mTimer.cancel();
	}

	private void setTimer(Channel item) {
		cancelTimer();
		mTimer = new Timer();
		mTimer.schedule(new DownloadTask(item.getUrl()), 0, 1000);
	}

	private Runnable mRunnable = this::hideUi;

	private Runnable mAddCount = new Runnable() {
		@Override
		public void run() {
			mAdapter.addCount();
		}
	};

	private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
		@Override
		public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
			if (newState == RecyclerView.SCROLL_STATE_DRAGGING) mHandler.removeCallbacks(mRunnable);
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

	private boolean isVisible() {
		return mRecyclerView.getAlpha() == 1;
	}

	private boolean isGone() {
		return mRecyclerView.getAlpha() == 0;
	}

	private void toggleUi() {
		if (isVisible()) hideUi(); else showUi();
	}

	private void showUi() {
		Utils.showViews(mRecyclerView, mGear);
	}

	private void hideUi() {
		Utils.hideViews(mRecyclerView, mGear);
	}

	private void setCustomSize() {
		mNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 4 + 40);
		mName.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 4 + 40);
		ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
		params.width = Utils.dp2px(250 + Prefers.getSize() * 20);
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
		if (event.getAction() == KeyEvent.ACTION_UP) toggleUi();
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
	public void onFind(Channel item) {
		mRecyclerView.scrollToPosition(mAdapter.getIndex(item));
		mAdapter.setPosition(mAdapter.getIndex(item));
		mAdapter.setChannel();
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
		mRecyclerView.scrollToPosition(isNext ? mAdapter.onMoveDown() : mAdapter.onMoveUp());
		mHandler.removeCallbacks(mRunnable);
		if (isGone()) showUi();
	}

	@Override
	public void onKeyHorizontal(boolean isLeft) {
		if (isLeft) mHide.performClick();
		else Notify.showDialog(this, View.VISIBLE);
	}

	@Override
	public void onKeyCenter() {
		if (isVisible()) mAdapter.setChannel(); else showUi();
	}

	@Override
	public void onKeyBack() {
		onBackPressed();
	}

	@Override
	public void onLongPress() {
		mAdapter.onKeep();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) Utils.setImmersiveMode(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Token.check(this);
		mAdapter.setVisible(true);
		mAdapter.setChannel();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mAdapter.setVisible(false);
		mVideoView.stopPlayback();
		cancelTimer();
	}

	@Override
	public void onBackPressed() {
		if (isVisible()) hideUi();
		else super.onBackPressed();
	}
}
