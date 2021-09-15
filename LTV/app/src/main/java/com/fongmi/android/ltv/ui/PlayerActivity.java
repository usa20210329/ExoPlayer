package com.fongmi.android.ltv.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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
import com.fongmi.android.ltv.bean.Config;
import com.fongmi.android.ltv.databinding.ActivityPlayerBinding;
import com.fongmi.android.ltv.impl.AsyncCallback;
import com.fongmi.android.ltv.impl.KeyDownImpl;
import com.fongmi.android.ltv.network.ApiService;
import com.fongmi.android.ltv.receiver.VerifyReceiver;
import com.fongmi.android.ltv.source.Force;
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
import com.google.common.net.HttpHeaders;
import com.king.player.kingplayer.IPlayer;
import com.king.player.kingplayer.KingPlayer;
import com.king.player.kingplayer.source.DataSource;

public class PlayerActivity extends AppCompatActivity implements VerifyReceiver.Callback, KeyDownImpl {

	private final Runnable mShowUUID = this::showUUID;
	private final Runnable mHideEpg = this::hideEpg;
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
		binding.video.setPlayer(new ExoPlayer(this));
		mHandler.postDelayed(mShowUUID, 3000);
		Clock.start(binding.epg.time);
		setScaleType();
	}

	@Override
	public void onVerified() {
		ApiService.getConfig(new AsyncCallback() {
			@Override
			public void onResponse(Config config) {
				setConfig(config);
			}
		});
	}

	private void setConfig(Config config) {
		FileUtil.checkUpdate(config.getVersion());
		mAdapter.addAll(config.getChannel());
		TvBus.get().init(config.getCore());
		Token.setConfig(config);
		Force.get().init();
		setCustomSize();
		hideProgress();
		checkKeep();
		hideUUID();
	}

	private void onClick(Channel item) {
		TvBus.get().stop();
		Force.get().stop();
		showProgress();
		showEpg(item);
		showBg(item);
		getEpg(item);
		getUrl(item);
		hideUI();
	}

	private void getUrl(Channel item) {
		ApiService.getUrl(item, new AsyncCallback() {
			@Override
			public void onResponse(String url) {
				playVideo(item, url);
			}

			@Override
			public void onFail() {
				onRetry(0, null);
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
		if (++retry > 3 || mAdapter.getCurrent() == null) onError();
		else getUrl(mAdapter.getCurrent());
	}

	private void onError() {
		Notify.show(R.string.channel_error);
		binding.video.reset();
		TvBus.get().stop();
		Force.get().stop();
		hideProgress();
		retry = 0;
	}

	private void onPrepared(int event, @Nullable Bundle bundle) {
		if (event != KingPlayer.Event.EVENT_ON_STATUS_CHANGE) return;
		if (bundle == null || bundle.getInt(KingPlayer.EventBundleKey.KEY_ORIGINAL_EVENT) != Player.STATE_READY) return;
		hideProgress();
	}

	private void playVideo(Channel item, String url) {
		DataSource source = new DataSource(url);
		source.getHeaders().put(HttpHeaders.USER_AGENT, item.getUa());
		binding.video.setDataSource(source);
		binding.video.start();
	}

	private boolean isVisible(View view) {
		return view.getAlpha() == 1;
	}

	private void showProgress() {
		Utils.showView(binding.widget.progress);
	}

	private void hideProgress() {
		Utils.hideView(binding.widget.progress);
	}

	private void showUI() {
		Utils.showViews(binding.recycler, binding.widget.gear);
		if (Prefers.isPad()) Utils.showView(binding.widget.keypad.getRoot());
	}

	private void hideUI() {
		Utils.hideViews(binding.recycler, binding.widget.gear);
		if (Prefers.isPad()) Utils.hideView(binding.widget.keypad.getRoot());
	}

	private void showUUID() {
		binding.widget.uuid.setText(Utils.getUUID());
		Utils.showView(binding.widget.uuid);
		hideProgress();
	}

	private void hideUUID() {
		mHandler.removeCallbacks(mShowUUID);
		Utils.hideView(binding.widget.uuid);
	}

	private void hideEpg() {
		Utils.hideViews(binding.epg.getRoot(), binding.widget.digital);
	}

	private void showEpg(Channel item) {
		mHandler.removeCallbacks(mHideEpg);
		binding.epg.name.setSelected(true);
		binding.epg.name.setText(item.getName());
		binding.epg.number.setText(item.getNumber());
		binding.widget.digital.setText(item.getDigital());
		Utils.showViews(binding.epg.getRoot(), binding.widget.digital);
	}

	private void setEpg(String epg) {
		binding.epg.play.setText(epg);
		binding.epg.play.setSelected(true);
		mHandler.postDelayed(mHideEpg, 5000);
	}

	private void showBg(Channel item) {
		boolean hasBg = !TextUtils.isEmpty(item.getBg());
		binding.bg.setVisibility(hasBg ? View.VISIBLE : View.GONE);
		if (hasBg) item.loadBg(binding.bg);
	}

	private void setCustomSize() {
		binding.widget.digital.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 4 + 30);
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

	public void setScaleType() {
		binding.video.setAspectRatio(Prefers.getRatio());
	}

	public void setKeypad() {
		binding.widget.keypad.getRoot().setVisibility(Prefers.isPad() ? View.VISIBLE : View.GONE);
	}

	public void onToggle(View view) {
		if (isVisible(binding.recycler)) hideUI();
		else showUI();
		hideEpg();
	}

	public void onGear(View view) {
		Notify.showDialog(this, View.GONE);
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
		binding.widget.digital.setText(number);
		Utils.showView(binding.widget.digital);
	}

	@Override
	public void onFind(String number) {
		int position = mAdapter.getIndex(number);
		if (position < 0) Utils.hideView(binding.widget.digital);
		binding.recycler.scrollToPosition(position);
		mAdapter.setPosition(position);
		mAdapter.setChannel();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (Utils.hasEvent(event)) return mKeyDown.onKeyDown(event);
		else return super.dispatchKeyEvent(event);
	}

	@Override
	public void onKeyVertical(boolean isNext) {
		boolean play = !isVisible(binding.recycler);
		binding.recycler.scrollToPosition(isNext ? mAdapter.onMoveDown(play) : mAdapter.onMoveUp(play));
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
		if (isVisible(binding.recycler)) {
			mAdapter.setChannel();
		} else {
			showUI(); hideEpg();
		}
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
		binding.video.start();
	}

	@Override
	protected void onStop() {
		binding.video.pause();
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		if (isVisible(binding.epg.getRoot())) hideEpg();
		else if (isVisible(binding.recycler)) hideUI();
		else super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		binding.video.release();
		TvBus.get().destroy();
		Force.get().destroy();
		Clock.destroy();
		super.onDestroy();
		System.exit(0);
	}
}
