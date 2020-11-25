package com.fongmi.android.ltv.ui;

import android.content.Context;
import android.media.AudioManager;
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

import com.devbrackets.android.exomedia.core.video.scale.ScaleType;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.fongmi.android.ltv.R;
import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.impl.AsyncCallback;
import com.fongmi.android.ltv.impl.KeyDownImpl;
import com.fongmi.android.ltv.network.ApiService;
import com.fongmi.android.ltv.receiver.VerifyReceiver;
import com.fongmi.android.ltv.source.Force;
import com.fongmi.android.ltv.source.TvBus;
import com.fongmi.android.ltv.utils.KeyDown;
import com.fongmi.android.ltv.utils.Notify;
import com.fongmi.android.ltv.utils.Prefers;
import com.fongmi.android.ltv.utils.Token;
import com.fongmi.android.ltv.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class MainActivity extends AppCompatActivity implements VerifyReceiver.Callback, KeyDownImpl {

	@BindView(R.id.recyclerView) RecyclerView mRecyclerView;
	@BindView(R.id.videoView) VideoView mVideoView;
	@BindView(R.id.progress) ProgressBar mProgress;
	@BindView(R.id.keypad) ViewGroup mKeypad;
	@BindView(R.id.number) TextView mNumber;
	@BindView(R.id.info) ViewGroup mInfo;
	@BindView(R.id.gear) ImageView mGear;
	@BindView(R.id.name) TextView mName;

	private VerifyReceiver mReceiver;
	private MainAdapter mAdapter;
	private KeyDown mKeyDown;
	private Handler mHandler;
	private int retry;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Utils.hideSystemUI(this);
		ButterKnife.bind(this);
		initView();
		initEvent();
	}

	private void initView() {
		mHandler = new Handler();
		mKeyDown = new KeyDown(this, mInfo, mNumber, mName);
		mReceiver = VerifyReceiver.create(this);
		TvBus.get().init();
		Force.get().init();
		setRecyclerView();
		showProgress();
		Token.check();
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

	@Override
	public void onVerified() {
		ApiService.getInstance().getConfig(new AsyncCallback() {
			@Override
			public void onResponse(List<Channel> items) {
				setConfig(items);
			}
		});
	}

	private void setConfig(List<Channel> items) {
		mKeyDown.setChannels(items);
		mAdapter.addAll(items);
		setCustomSize();
		setScaleType();
		hideProgress();
		setKeypad();
		checkKeep();
	}

	private void getUrl(Channel item) {
		ApiService.getInstance().getUrl(item, new AsyncCallback() {
			@Override
			public void onResponse(String url) {
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
	}

	private void onPrepared() {
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, 2000);
		hideProgress();
		retry = 0;
	}

	private boolean onRetry(Exception e) {
		if (++retry > 3) onError(e);
		else mAdapter.setChannel();
		return true;
	}

	private void onError(Exception e) {
		Notify.show(R.string.channel_error);
		e.printStackTrace();
		mVideoView.reset();
		hideProgress();
	}

	private void playVideo(String url) {
		runOnUiThread(() -> {
			mVideoView.setVideoPath(url);
			mVideoView.start();
		});
	}

	private Runnable mRunnable = this::hideUi;

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
		mHandler.removeCallbacks(mRunnable);
		Utils.showViews(mRecyclerView, mGear);
		if (Prefers.isPad()) Utils.showView(mKeypad);
	}

	private void hideUi() {
		mHandler.removeCallbacks(mRunnable);
		Utils.hideViews(mRecyclerView, mGear);
		if (Prefers.isPad()) Utils.hideView(mKeypad);
	}

	private void setCustomSize() {
		mNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 4 + 30);
		mName.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 4 + 30);
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

	public void setKeypad() {
		mKeypad.setVisibility(Prefers.isPad() ? View.VISIBLE : View.GONE);
	}

	@OnTouch(R.id.videoView)
	public boolean onTouch(MotionEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP) toggleUi();
		return true;
	}

	@OnClick(R.id.gear)
	public void onGear() {
		Notify.showDialog(this);
		mHandler.removeCallbacks(mRunnable);
	}

	@OnClick(R.id.add)
	public void onAdd() {
		AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	}

	@OnClick(R.id.sub)
	public void onSub() {
		AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
	}

	@Override
	public void onFind(Channel item) {
		mRecyclerView.scrollToPosition(mAdapter.getIndex(item));
		mAdapter.setPosition(mAdapter.getIndex(item));
		mAdapter.setChannel();
	}

	public void onKeyDown(View view) {
		mHandler.removeCallbacks(mRunnable);
		mKeyDown.onKeyDown(Integer.parseInt(view.getTag().toString()) + KeyEvent.KEYCODE_0);
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
		if (isGone()) showUi();
	}

	@Override
	public void onKeyLeft() {
		mAdapter.addCount();
	}

	@Override
	public void onKeyRight() {
		mHandler.removeCallbacks(mRunnable);
		Notify.showDialog(this, View.VISIBLE);
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
		if (hasFocus) Utils.hideSystemUI(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		mAdapter.setVisible(true);
		mAdapter.setChannel();
	}

	@Override
	public void onStop() {
		super.onStop();
		TvBus.get().stop();
		mAdapter.setVisible(false);
		mVideoView.stopPlayback();
	}

	@Override
	public void onBackPressed() {
		if (isVisible()) hideUi();
		else super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mReceiver.cancel(this);
		TvBus.get().destroy();
		Force.get().destroy();
	}
}
