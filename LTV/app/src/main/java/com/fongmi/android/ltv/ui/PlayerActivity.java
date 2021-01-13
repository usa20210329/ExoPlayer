package com.fongmi.android.ltv.ui;

import android.content.Context;
import android.content.res.Configuration;
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

import com.fongmi.android.ltv.R;
import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.databinding.ActivityPlayerBinding;
import com.fongmi.android.ltv.impl.AsyncCallback;
import com.fongmi.android.ltv.impl.KeyDownImpl;
import com.fongmi.android.ltv.network.ApiService;
import com.fongmi.android.ltv.network.task.FileTask;
import com.fongmi.android.ltv.receiver.VerifyReceiver;
import com.fongmi.android.ltv.source.TvBus;
import com.fongmi.android.ltv.ui.adapter.PlayerAdapter;
import com.fongmi.android.ltv.utils.Clock;
import com.fongmi.android.ltv.utils.FileUtil;
import com.fongmi.android.ltv.utils.KeyDown;
import com.fongmi.android.ltv.utils.Notify;
import com.fongmi.android.ltv.utils.Prefers;
import com.fongmi.android.ltv.utils.Token;
import com.fongmi.android.ltv.utils.Utils;
import com.google.android.exoplayer2.Player;
import com.king.player.exoplayer.ExoPlayer;
import com.king.player.kingplayer.AspectRatio;
import com.king.player.kingplayer.IPlayer;
import com.king.player.kingplayer.KingPlayer;
import com.king.player.kingplayer.source.DataSource;

import java.util.List;

public class PlayerActivity extends AppCompatActivity implements VerifyReceiver.Callback, KeyDownImpl {

	private ActivityPlayerBinding binding;
	private PlayerAdapter mAdapter;
	private KeyDown mKeyDown;
	private Handler mHandler;
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
		VerifyReceiver.create(this);
		TvBus.get().init();
		showProgress();
		Token.check();
		setView();
	}

	private void initEvent() {
		mAdapter.setOnItemListener(this::onClick);
		binding.video.setOnErrorListener(this::onRetry);
		binding.video.setOnPlayerEventListener(this::onPrepared);
	}

	private void setView() {
		binding.recycler.setLayoutManager(new LinearLayoutManager(this));
		binding.recycler.setAdapter(mAdapter = new PlayerAdapter());
		binding.video.setAspectRatio(AspectRatio.AR_MATCH_PARENT);
		binding.video.setPlayer(new ExoPlayer(this));
		Clock.start(binding.epg.time);
	}

	@Override
	public void onVerified() {
		ApiService.getConfig(new AsyncCallback() {
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
		checkKeep();
	}

	private void onClick(Channel item) {
		showProgress();
		showEpg(item);
		getUrl(item);
		getEpg(item);
		hideUI();
	}

	private void getUrl(Channel item) {
		ApiService.getUrl(item, new AsyncCallback() {
			@Override
			public void onResponse(String url) {
				playVideo(item, url);
			}
		});
	}

	private void getEpg(Channel item) {
		ApiService.getEpg(item, new AsyncCallback() {
			@Override
			public void onResponse(String epg) {
				setEpg(epg);
			}
		});
	}

	private void checkKeep() {
		if (Prefers.getKeep().isEmpty()) showUI();
		else onFind(Prefers.getKeep());
	}

	private void onRetry(int event, @Nullable Bundle bundle) {
		if (++retry > 5) onError(); else getUrl(mAdapter.getCurrent());
	}

	private void onError() {
		Notify.show(R.string.channel_error);
		binding.video.reset();
		hideProgress();
		retry = 0;
	}

	private void onPrepared(int event, @Nullable Bundle bundle) {
		if (event != KingPlayer.Event.EVENT_ON_STATUS_CHANGE) return;
		if (bundle == null || bundle.getInt(KingPlayer.EventBundleKey.KEY_ORIGINAL_EVENT) != Player.STATE_READY) return;
		hideProgress();
	}

	private void playVideo(Channel item, String url) {
		if (FileUtil.isFile(url)) FileTask.start(item.getUrl()); else FileTask.destroy();
		DataSource source = new DataSource(url);
		source.getHeaders().put("User-Agent", item.getProvider());
		binding.video.setDataSource(source);
		binding.video.start();
	}

	private final Runnable mRunnable = this::hideEpg;

	private void showProgress() {
		if (binding.widget.progress.getVisibility() == View.GONE) binding.widget.progress.setVisibility(View.VISIBLE);
	}

	private void hideProgress() {
		if (binding.widget.progress.getVisibility() == View.VISIBLE) binding.widget.progress.setVisibility(View.GONE);
	}

	private boolean isUIVisible() {
		return binding.recycler.getAlpha() == 1;
	}

	private boolean isEpgVisible() {
		return binding.epg.getRoot().getAlpha() == 1;
	}

	private void showUI() {
		if (Prefers.isPad()) Utils.showView(binding.widget.keypad.getRoot());
		Utils.showViews(binding.recycler, binding.widget.gear);
	}

	private void hideUI() {
		if (Prefers.isPad()) Utils.hideView(binding.widget.keypad.getRoot());
		Utils.hideViews(binding.recycler, binding.widget.gear);
	}

	private void hideEpg() {
		Utils.hideViews(binding.epg.getRoot());
	}

	private void showEpg(Channel item) {
		binding.epg.name.setSelected(true);
		binding.epg.name.setText(item.getName());
		binding.epg.number.setText(item.getNumber());
		Utils.showViews(binding.epg.getRoot());
		mHandler.removeCallbacks(mRunnable);
	}

	private void setEpg(String epg) {
		binding.epg.play.setText(epg);
		binding.epg.play.setSelected(true);
		mHandler.postDelayed(mRunnable, 5000);
	}

	private void setCustomSize() {
		binding.widget.info.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 4 + 30);
		binding.epg.number.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 2 + 16);
		binding.epg.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 2 + 16);
		binding.epg.time.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 2 + 16);
		binding.epg.play.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 2 + 16);
		ViewGroup.LayoutParams params = binding.recycler.getLayoutParams();
		params.width = Utils.dp2px(Prefers.getSize() * 20 + 260);
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

	public void onToggle(View view) {
		if (isEpgVisible()) hideEpg();
		if (isUIVisible()) hideUI(); else showUI();
	}

	public void onGear(View view) {
		Notify.showDialog(this);
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
		mKeyDown.onKeyDown(Integer.parseInt(view.getTag().toString()) + KeyEvent.KEYCODE_0);
	}

	@Override
	public void onShow(String number) {
		binding.widget.info.setVisibility(View.VISIBLE);
		binding.widget.info.setText(mAdapter.getInfo(number));
	}

	@Override
	public void onFind(String number) {
		int position = mAdapter.getIndex(number);
		binding.recycler.scrollToPosition(position);
		binding.widget.info.setVisibility(View.GONE);
		binding.widget.info.setText("");
		mAdapter.setPosition(position);
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
		if (Prefers.isOk()) hideEpg();
		if (Prefers.isOk()) showUI();
	}

	@Override
	public void onKeyLeft() {
		mAdapter.addCount();
	}

	@Override
	public void onKeyRight() {
		Notify.showDialog(this, View.VISIBLE);
	}

	@Override
	public void onKeyCenter() {
		if (isEpgVisible()) hideEpg();
		if (isUIVisible()) mAdapter.setChannel(); else showUI();
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
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Utils.hideSystemUI(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) Utils.hideSystemUI(this);
	}

	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
		Utils.enterPIP(this);
	}

	@Override
	public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
		if (isInPictureInPictureMode) {
			hideEpg(); hideUI();
		} else if (binding.video.getPlayerState() == IPlayer.STATE_PAUSED) {
			finish();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (Utils.hasPIP()) binding.video.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!Utils.hasPIP()) binding.video.start();
	}

	@Override
	protected void onPause() {
		if (!Utils.hasPIP()) binding.video.pause();
		super.onPause();
	}

	@Override
	protected void onStop() {
		if (Utils.hasPIP()) binding.video.pause();
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		if (isEpgVisible()) hideEpg();
		else if (isUIVisible()) hideUI();
		else super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		binding.video.release();
		FileTask.destroy();
		Clock.destroy();
		TvBus.destroy();
		super.onDestroy();
		System.exit(0);
	}
}
