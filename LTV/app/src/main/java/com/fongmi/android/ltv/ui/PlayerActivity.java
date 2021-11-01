package com.fongmi.android.ltv.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fongmi.android.ltv.R;
import com.fongmi.android.ltv.bean.Channel;
import com.fongmi.android.ltv.bean.Config;
import com.fongmi.android.ltv.bean.Type;
import com.fongmi.android.ltv.databinding.ActivityPlayerBinding;
import com.fongmi.android.ltv.impl.AsyncCallback;
import com.fongmi.android.ltv.impl.KeyDownImpl;
import com.fongmi.android.ltv.network.ApiService;
import com.fongmi.android.ltv.receiver.VerifyReceiver;
import com.fongmi.android.ltv.source.Force;
import com.fongmi.android.ltv.source.TvBus;
import com.fongmi.android.ltv.ui.adapter.ChannelAdapter;
import com.fongmi.android.ltv.ui.adapter.TypeAdapter;
import com.fongmi.android.ltv.ui.custom.ExoPlayer;
import com.fongmi.android.ltv.ui.custom.FlipDetector;
import com.fongmi.android.ltv.utils.Clock;
import com.fongmi.android.ltv.utils.FileUtil;
import com.fongmi.android.ltv.utils.KeyDown;
import com.fongmi.android.ltv.utils.Notify;
import com.fongmi.android.ltv.utils.Prefers;
import com.fongmi.android.ltv.utils.Token;
import com.fongmi.android.ltv.utils.Utils;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;

import java.util.Objects;

public class PlayerActivity extends AppCompatActivity implements Player.Listener, VerifyReceiver.Callback, KeyDownImpl {

	private final Runnable mShowUUID = this::showUUID;
	private final Runnable mHideEpg = this::hideEpg;
	private ActivityPlayerBinding binding;
	private GestureDetector mDetector;
	private ChannelAdapter mChannelAdapter;
	private TypeAdapter mTypeAdapter;
	private ExoPlayer mPlayer;
	private KeyDown mKeyDown;
	private Handler mHandler;

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
		mKeyDown = KeyDown.create(this);
		mPlayer = ExoPlayer.create(this);
		mDetector = FlipDetector.create(this);
		VerifyReceiver.create(this);
		ApiService.getIP();
		showProgress();
		Token.check();
		setView();
	}

	@SuppressLint("ClickableViewAccessibility")
	private void initEvent() {
		mPlayer.addListener(this);
		mTypeAdapter.setOnItemClickListener(this::onItemClick);
		mChannelAdapter.setOnItemClickListener(this::onItemClick);
		binding.video.setOnTouchListener((view, event) -> mDetector.onTouchEvent(event));
	}

	private void setView() {
		Objects.requireNonNull(binding.channel.getItemAnimator()).setChangeDuration(0);
		Objects.requireNonNull(binding.type.getItemAnimator()).setChangeDuration(0);
		binding.channel.setLayoutManager(new LinearLayoutManager(this));
		binding.type.setLayoutManager(new LinearLayoutManager(this));
		binding.channel.setAdapter(mChannelAdapter = new ChannelAdapter());
		binding.type.setAdapter(mTypeAdapter = new TypeAdapter());
		binding.video.setPlayer(mPlayer.get());
		mHandler.postDelayed(mShowUUID, 3000);
		Clock.start(binding.epg.time);
		setCustomSize();
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
		mTypeAdapter.addAll(config.getType());
		TvBus.get().init(config.getCore());
		setNotice(config.getNotice());
		Token.setConfig(config);
		Force.get().init();
		hideProgress();
		checkKeep();
		hideUUID();
	}

	private void onItemClick(Type item, boolean tv) {
		if (item.isSetting()) Notify.showDialog(this, tv);
		else if (item != mChannelAdapter.getType()) mChannelAdapter.addAll(item);
		binding.channel.scrollToPosition(item.getPosition());
	}

	private void onItemClick(Channel item) {
		TvBus.get().stop();
		Force.get().stop();
		showProgress();
		showEpg(item);
		getEpg(item);
		getUrl(item);
		hideUI();
	}

	private void getUrl(Channel item) {
		ApiService.getUrl(item, new AsyncCallback() {
			@Override
			public void onResponse(String url) {
				mPlayer.setDataSource(item.getUa(), url);
			}

			@Override
			public void onError(PlaybackException error) {
				onPlayerError(error);
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
		if (Prefers.getKeep().isEmpty()) {
			mTypeAdapter.setFocus(true);
			showUI();
		} else {
			mChannelAdapter.setFocus(true);
			onFind(Prefers.getKeep());
		}
	}

	@Override
	public void onPlaybackStateChanged(int state) {
		if (state == Player.STATE_BUFFERING) showProgress();
		else if (state == Player.STATE_READY) hideProgress();
	}

	@Override
	public void onPlayerError(@NonNull PlaybackException error) {
		Notify.show(R.string.channel_error);
		TvBus.get().stop();
		Force.get().stop();
		hideProgress();
		mPlayer.stop();
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
		Utils.showViews(binding.recycler);
		if (Prefers.isPad()) Utils.showView(binding.widget.keypad.getRoot());
	}

	private void hideUI() {
		Utils.hideViews(binding.recycler);
		if (Prefers.isPad()) Utils.hideView(binding.widget.keypad.getRoot());
	}

	private void showUUID() {
		binding.widget.uuid.setText(getString(R.string.app_uuid, Utils.getUUID()));
		Utils.showView(binding.widget.uuid);
		hideProgress();
	}

	private void hideUUID() {
		mHandler.removeCallbacks(mShowUUID);
		Utils.hideView(binding.widget.uuid);
	}

	private void hideEpg() {
		Utils.hideViews(binding.epg.getRoot(), binding.widget.digital);
		showController();
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

	private void showController() {
		if (binding.video.isControllerFullyVisible()) return;
		if (mPlayer.isMovie()) binding.video.showController();
	}

	private void setNotice(String notice) {
		binding.widget.notice.setText(notice);
		binding.widget.notice.startScroll();
	}

	private void setCustomSize() {
		binding.widget.digital.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 4 + 30);
		binding.widget.notice.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 2 + 16);
		binding.epg.number.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 2 + 16);
		binding.epg.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 2 + 16);
		binding.epg.time.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 2 + 16);
		binding.epg.play.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefers.getSize() * 2 + 16);
		ViewGroup.LayoutParams params = binding.recycler.getLayoutParams();
		params.width = Utils.dp2px(Prefers.getSize() * 24 + 380);
		binding.recycler.setLayoutParams(params);
	}

	@SuppressLint("NotifyDataSetChanged")
	public void onSizeChange() {
		mChannelAdapter.notifyDataSetChanged();
		mTypeAdapter.notifyDataSetChanged();
		setCustomSize();
	}

	public void setScaleType() {
		binding.video.setResizeMode(Prefers.getRatio());
	}

	public void setKeypad() {
		binding.widget.keypad.getRoot().setVisibility(Prefers.isPad() ? View.VISIBLE : View.GONE);
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
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (Utils.hasEvent(event)) return mKeyDown.onKeyDown(event);
		else return super.dispatchKeyEvent(event);
	}

	@Override
	public void onTapUp() {
		if (isVisible(binding.recycler)) hideUI();
		else showUI();
		hideEpg();
	}

	@Override
	public void onShow(String number) {
		binding.widget.digital.setText(number);
		Utils.showView(binding.widget.digital);
	}

	@Override
	public void onFind(String number) {
		Utils.hideView(binding.widget.digital);
		int[] position = mTypeAdapter.find(number);
		if (position[0] == -1 || position[1] == -1) return;
		mTypeAdapter.setPosition(position[0]); mTypeAdapter.setType();
		mChannelAdapter.setPosition(position[1]); mChannelAdapter.setChannel();
		binding.type.scrollToPosition(position[0]);
		binding.channel.scrollToPosition(position[1]);
	}

	@Override
	public void onFlip(boolean up) {
		if (isVisible(binding.recycler)) return;
		binding.channel.scrollToPosition(up ? mChannelAdapter.onMoveUp(true) : mChannelAdapter.onMoveDown(true));
	}

	@Override
	public void onSeek(boolean forward) {
		if (isVisible(binding.recycler)) return;
		if (forward) mPlayer.seekForward();
		else mPlayer.seekBack();
		showController();
	}

	@Override
	public void onKeyVertical(boolean next) {
		boolean play = !isVisible(binding.recycler);
		if (mChannelAdapter.isFocus()) {
			binding.channel.scrollToPosition(next ? mChannelAdapter.onMoveDown(play) : mChannelAdapter.onMoveUp(play));
		} else if (mTypeAdapter.isFocus()) {
			binding.type.scrollToPosition(next ? mTypeAdapter.onMoveDown() : mTypeAdapter.onMoveUp());
		}
	}

	@Override
	public void onKeyLeft() {
		if (!isVisible(binding.recycler)) return;
		mTypeAdapter.setSelected();
		mChannelAdapter.clearSelect();
		binding.type.scrollToPosition(mTypeAdapter.getPosition());
	}

	@Override
	public void onKeyRight() {
		if (!isVisible(binding.recycler)) return;
		mTypeAdapter.clearSelect();
		mChannelAdapter.setSelected();
		binding.channel.scrollToPosition(mChannelAdapter.getPosition());
	}

	@Override
	public void onKeyCenter() {
		if (isVisible(binding.recycler)) {
			if (mChannelAdapter.isFocus()) mChannelAdapter.setChannel();
			else if (mTypeAdapter.isFocus()) mTypeAdapter.onKeyCenter();
		} else {
			showUI();
			hideEpg();
		}
	}

	@Override
	public void onKeyMenu() {
		Notify.showDialog(this, true);
	}

	private void reposition() {
		Channel c = mChannelAdapter.getCurrent();
		if (c == null) return;
		Type type = c.getType();
		if (type == mChannelAdapter.getType()) {
			binding.channel.scrollToPosition(type.getPosition());
			mChannelAdapter.setPosition(type.getPosition());
			mChannelAdapter.setSelected();
		} else {
			mTypeAdapter.clearSelect();
			mTypeAdapter.setPosition(type.getId());
			mChannelAdapter.clearSelect();
			mChannelAdapter.addAll(type);
			mChannelAdapter.setSelected();
			binding.channel.scrollToPosition(type.getPosition());
		}
	}

	@Override
	public void onKeyBack() {
		mHandler.postDelayed(this::reposition, 250);
		onBackPressed();
	}

	@Override
	public void onLongPress() {
		mChannelAdapter.onKeep();
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
		} else if (!mPlayer.isPlaying()) {
			finish();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mPlayer.start();
	}

	@Override
	protected void onStop() {
		mPlayer.pause();
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
		super.onDestroy();
		TvBus.get().destroy();
		Force.get().destroy();
		mPlayer.release();
		Clock.destroy();
		System.exit(0);
	}
}
