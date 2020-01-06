package com.fongmi.android.tv.ui;

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
import com.fongmi.android.tv.R;
import com.fongmi.android.tv.impl.KeyDownImpl;
import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.network.ApiService;
import com.fongmi.android.tv.network.AsyncCallback;
import com.fongmi.android.tv.utils.KeyDown;
import com.fongmi.android.tv.utils.Notify;
import com.fongmi.android.tv.utils.Prefers;
import com.fongmi.android.tv.utils.Time;
import com.fongmi.android.tv.utils.Token;
import com.fongmi.android.tv.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class MainActivity extends AppCompatActivity implements KeyDownImpl {

	@BindView(R.id.recyclerView) RecyclerView mRecyclerView;
	@BindView(R.id.videoView) VideoView mVideoView;
	@BindView(R.id.progress) ProgressBar mProgress;
	@BindView(R.id.channel) ViewGroup mChannel;
	@BindView(R.id.noise) AnalogTvNoise mNoise;
	@BindView(R.id.info) ViewGroup mInfo;
	@BindView(R.id.number) TextView mNumber;
	@BindView(R.id.gear) ImageView mGear;
	@BindView(R.id.time) TextView mTime;
	@BindView(R.id.name) TextView mName;
	@BindView(R.id.hide) View mHide;

	private MainAdapter mAdapter;
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
		setScaleType();
		Token.setKey();
		getList();
	}

	private void initEvent() {
		mAdapter.setOnItemListener(this::getUrl);
		mVideoView.setOnErrorListener(this::onRetry);
		mVideoView.setOnPreparedListener(this::onPrepared);
		mRecyclerView.addOnScrollListener(mScrollListener);
	}

	private void setRecyclerView() {
		mAdapter = new MainAdapter();
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setAdapter(mAdapter);
	}

	private void getList() {
		ApiService.getInstance().getList(new AsyncCallback() {
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
				item.setReal(url);
				playVideo(item);
			}
		});
	}

	private void checkKeep() {
		if (Prefers.getKeep().isEmpty()) return;
		onFind(Channel.create(Prefers.getKeep()));
	}

	private void onPrepared() {
		hideProgress();
		retry = 0;
	}

	private boolean onRetry(Exception e) {
		if (++retry > 3) onError(e);
		else mAdapter.setChannel(0);
		return true;
	}

	private void onError(Exception e) {
		e.printStackTrace();
		mVideoView.reset();
		hideProgress();
		showError();
	}

	private void playVideo(Channel item) {
		Prefers.putKeep(item.getNumber());
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, 3000);
		mVideoView.setVideoURI(Uri.parse(item.getReal()));
		mVideoView.start();
		showProgress();
		hideError();
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
			mHandler.removeCallbacks(mRunnable);
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

	private boolean isUiVisible() {
		return mChannel.getAlpha() == 1;
	}

	private boolean isUiGone() {
		return mChannel.getAlpha() == 0;
	}

	private boolean playWait() {
		return Prefers.isEnter() && isUiVisible();
	}

	private void toggleUi() {
		if (isUiVisible()) hideUi();
		else showUi();
	}

	private void showUi() {
		Utils.showViews(mChannel, mGear);
		mHandler.removeCallbacks(mRunnable);
	}

	private void hideUi() {
		Utils.hideViews(mChannel, mGear);
		mHandler.removeCallbacks(mRunnable);
	}

	private void setCustomSize() {
		mNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 4 + 40);
		mName.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 4 + 40);
		mTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 2 + 16);
		ViewGroup.LayoutParams params = mChannel.getLayoutParams();
		params.width = Utils.dp2px(200 + Prefers.getSize() * 20);
		mChannel.setLayoutParams(params);
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
		mHandler.removeCallbacks(mRunnable);
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
		mAdapter.setChannel(0);
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
		if (isUiGone()) showUi();
	}

	@Override
	public void onKeyHorizontal(boolean isLeft) {
		if (isLeft) mHide.performClick();
		else Notify.showDialog(this, View.VISIBLE);
	}

	@Override
	public void onKeyCenter(boolean isLongPress) {
		if (isLongPress) mAdapter.onKeep();
		else mAdapter.onCenter();
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
		Time.getInstance().set(this);
		mAdapter.setVisible(true);
		mAdapter.setChannel(0);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Time.getInstance().cancel();
		mAdapter.setVisible(false);
		mVideoView.stopPlayback();
	}

	@Override
	public void onBackPressed() {
		if (isUiVisible()) hideUi();
		else super.onBackPressed();
	}
}
