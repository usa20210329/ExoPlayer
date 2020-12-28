package com.fongmi.android.ltv.ui;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fongmi.android.ltv.R;
import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.databinding.ActivityPlayerBinding;
import com.fongmi.android.ltv.impl.AsyncCallback;
import com.fongmi.android.ltv.impl.KeyDownImpl;
import com.fongmi.android.ltv.network.ApiService;
import com.fongmi.android.ltv.network.task.DownloadTask;
import com.fongmi.android.ltv.receiver.VerifyReceiver;
import com.fongmi.android.ltv.source.TvBus;
import com.fongmi.android.ltv.utils.FileUtil;
import com.fongmi.android.ltv.utils.KeyDown;
import com.fongmi.android.ltv.utils.Notify;
import com.fongmi.android.ltv.utils.Prefers;
import com.fongmi.android.ltv.utils.Token;
import com.fongmi.android.ltv.utils.Utils;
import com.king.player.exoplayer.ExoPlayer;
import com.king.player.kingplayer.source.DataSource;

import java.util.List;
import java.util.Timer;

public class PlayerActivity extends AppCompatActivity implements VerifyReceiver.Callback, KeyDownImpl {

	private ActivityPlayerBinding binding;
	private VerifyReceiver mReceiver;
	private PlayerAdapter mAdapter;
	private KeyDown mKeyDown;
	private Handler mHandler;
	private Timer mTimer;
	private int retry;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityPlayerBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		Utils.hideSystemUI(this);
		initView();
		initEvent();
	}

	private void initView() {
		mHandler = new Handler();
		mKeyDown = new KeyDown(this);
		mReceiver = VerifyReceiver.create(this);
		TvBus.get().init();
		setBindingView();
		showProgress();
		Token.check();
	}

	private void initEvent() {
		mAdapter.setOnItemListener(this::getUrl);
		binding.video.setOnErrorListener(this::onRetry);
		binding.recycler.addOnScrollListener(mScrollListener);
	}

	private void setBindingView() {
		binding.recycler.setLayoutManager(new LinearLayoutManager(this));
		binding.recycler.setAdapter(mAdapter = new PlayerAdapter());
		binding.video.setPlayer(new ExoPlayer(this));
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
		mAdapter.addAll(items);
		setCustomSize();
		hideProgress();
		setKeypad();
		checkKeep();
	}

	private void getUrl(Channel item) {
		ApiService.getInstance().getUrl(item, new AsyncCallback() {
			@Override
			public void onResponse(String url) {
				playVideo(item, url);
			}
		});
	}

	private void checkKeep() {
		if (Prefers.getKeep().isEmpty()) return;
		onFind(Prefers.getKeep());
	}

	private void onRetry(int event, @Nullable Bundle bundle) {
		if (++retry > 3) onError();
		else mAdapter.setChannel();
	}

	private void onError() {
		Notify.show(R.string.channel_error);
		binding.video.reset();
		hideProgress();
		retry = 0;
	}

	private void playVideo(Channel item, String url) {
		mHandler.removeCallbacks(mRunnable); mHandler.postDelayed(mRunnable, 2000);
		if (FileUtil.isFile(url)) setTimer(item.getUrl()); else cancelTimer();
		DataSource dataSource = new DataSource(url);
		dataSource.getHeaders().put("User-Agent", item.getProvider());
		binding.video.setDataSource(dataSource);
		binding.video.start();
	}

	private void cancelTimer() {
		if (mTimer != null) mTimer.cancel();
	}

	private void setTimer(String url) {
		cancelTimer();
		mTimer = new Timer();
		mTimer.schedule(new DownloadTask(url), 0, 1000);
	}

	private final Runnable mRunnable = this::hideUi;

	private final RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
		@Override
		public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
			if (newState == RecyclerView.SCROLL_STATE_DRAGGING) mHandler.removeCallbacks(mRunnable);
		}
	};

	private void showProgress() {
		if (binding.widget.progress.getVisibility() == View.GONE) binding.widget.progress.setVisibility(View.VISIBLE);
	}

	private void hideProgress() {
		if (binding.widget.progress.getVisibility() == View.VISIBLE) binding.widget.progress.setVisibility(View.GONE);
	}

	private boolean isVisible() {
		return binding.recycler.getAlpha() == 1;
	}

	private boolean isGone() {
		return binding.recycler.getAlpha() == 0;
	}

	private void toggleUi() {
		if (isVisible()) hideUi(); else showUi();
	}

	private void showUi() {
		mHandler.removeCallbacks(mRunnable);
		Utils.showViews(binding.recycler, binding.widget.gear);
		if (Prefers.isPad()) Utils.showView(binding.widget.keypad.getRoot());
	}

	private void hideUi() {
		mHandler.removeCallbacks(mRunnable);
		Utils.hideViews(binding.recycler, binding.widget.gear);
		if (Prefers.isPad()) Utils.hideView(binding.widget.keypad.getRoot());
	}

	private void setCustomSize() {
		binding.widget.number.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 4 + 30);
		binding.widget.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 4 + 30);
		ViewGroup.LayoutParams params = binding.recycler.getLayoutParams();
		params.width = Utils.dp2px(250 + Prefers.getSize() * 20);
		binding.recycler.setLayoutParams(params);
	}

	public void onSizeChange(int progress) {
		Prefers.putSize(progress);
		mAdapter.notifyDataSetChanged();
		setCustomSize();
	}

	public void setKeypad() {
		binding.widget.keypad.getRoot().setVisibility(Prefers.isPad() ? View.VISIBLE : View.GONE);
	}

	public void onTouch(View view) {
		toggleUi();
	}

	public void onGear(View view) {
		Notify.showDialog(this);
		mHandler.removeCallbacks(mRunnable);
	}

	public void onAdd(View view) {
		AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	}

	public void onSub(View view) {
		AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
	}

	public void onKeyDown(View view) {
		mHandler.removeCallbacks(mRunnable);
		mKeyDown.onKeyDown(Integer.parseInt(view.getTag().toString()) + KeyEvent.KEYCODE_0);
	}

	@Override
	public void onShow(String number) {
		binding.widget.number.setText(number);
		binding.widget.name.setText(mAdapter.getName(number));
		binding.widget.info.setVisibility(View.VISIBLE);
	}

	@Override
	public void onFind(String number) {
		int index = mAdapter.getIndex(number);
		binding.widget.name.setText("");
		binding.widget.info.setVisibility(View.GONE);
		binding.recycler.scrollToPosition(index);
		mAdapter.setPosition(index);
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
		binding.recycler.scrollToPosition(isNext ? mAdapter.onMoveDown() : mAdapter.onMoveUp());
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
	protected void onResume() {
		super.onResume();
		mAdapter.setVisible(true);
		binding.video.start();
	}

	@Override
	protected void onPause() {
		mAdapter.setVisible(false);
		binding.video.pause();
		super.onPause();
	}

	@Override
	public void onBackPressed() {
		if (isVisible()) hideUi();
		else super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		binding.video.release();
		TvBus.get().destroy();
		mReceiver.cancel();
		super.onDestroy();
		System.exit(0);
	}
}
