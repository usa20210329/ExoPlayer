package com.fongmi.android.ltv.ui;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fongmi.android.ltv.utils.Utils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoSize;
import com.google.common.net.HttpHeaders;
import com.king.player.kingplayer.KingPlayer;
import com.king.player.kingplayer.source.DataSource;
import com.king.player.kingplayer.util.LogUtils;

public class ExoPlayer extends KingPlayer<SimpleExoPlayer> {

	private SimpleExoPlayer mMediaPlayer;
	private DataSource mDataSource;
	private final Context mContext;
	private final Bundle mBundle;

	public ExoPlayer(@NonNull Context context) {
		this(context, null);
	}

	public ExoPlayer(@NonNull Context context, @Nullable SimpleExoPlayer mediaPlayer) {
		this.mContext = context.getApplicationContext();
		this.mBundle = obtainBundle();
		init(mediaPlayer);
	}

	private void init(SimpleExoPlayer mediaPlayer) {
		if (mediaPlayer == null) mMediaPlayer = create();
		else mMediaPlayer = mediaPlayer;
		addListener();
	}

	private SimpleExoPlayer create() {
		return new SimpleExoPlayer.Builder(mContext, new DefaultRenderersFactory(mContext)).setTrackSelector(new DefaultTrackSelector(mContext)).build();
	}

	@Override
	public void setDataSource(@NonNull DataSource source) {
		try {
			mDataSource = source;
			mMediaPlayer.setMediaSource(getMedia(source));
			mMediaPlayer.prepare();
			mCurrentState = STATE_PREPARED;
			sendPlayerEvent(Event.EVENT_ON_DATA_SOURCE_SET);
		} catch (Exception e) {
			handleException(e, false);
			mCurrentState = STATE_ERROR;
			sendErrorEvent(ErrorEvent.ERROR_EVENT_COMMON);
		}
	}

	private MediaSource getMedia(@NonNull DataSource source) {
		Uri videoUri = Uri.parse(source.getPath());
		com.google.android.exoplayer2.upstream.DataSource.Factory factory = getFactory(source);
		MediaItem mediaItem = new MediaItem.Builder().setUri(videoUri).build();
		int type = Util.inferContentType(videoUri);
		if (type == C.TYPE_HLS || source.getPath().contains(".php")) {
			return new HlsMediaSource.Factory(factory).createMediaSource(mediaItem);
		} else if (type == C.TYPE_DASH) {
			return new DashMediaSource.Factory(factory).createMediaSource(mediaItem);
		} else if (type == C.TYPE_SS) {
			return new SsMediaSource.Factory(factory).createMediaSource(mediaItem);
		} else if (type == C.TYPE_RTSP) {
			return new RtspMediaSource.Factory().createMediaSource(mediaItem);
		} else {
			return new ProgressiveMediaSource.Factory(factory).createMediaSource(mediaItem);
		}
	}

	private com.google.android.exoplayer2.upstream.DataSource.Factory getFactory(DataSource source) {
		String userAgent = source.getHeaders().get(HttpHeaders.USER_AGENT);
		userAgent = TextUtils.isEmpty(userAgent) ? Utils.getUserAgent() : userAgent;
		HttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory().setUserAgent(userAgent).setAllowCrossProtocolRedirects(true);
		return source.getPath().startsWith("rtmp") ? new RtmpDataSource.Factory() : new DefaultDataSourceFactory(mContext, httpDataSourceFactory);
	}

	private void addListener() {
		if (available()) mMediaPlayer.addListener(mEventListener);
	}

	private void resetListener() {
		if (available()) mMediaPlayer.removeListener(mEventListener);
	}

	private void recycleBundle() {
		mBundle.clear();
	}

	private final Player.Listener mEventListener = new Player.Listener() {

		@Override
		public void onVideoSizeChanged(@NonNull VideoSize videoSize) {
			sendVideoSizeChangeEvent(videoSize.width, videoSize.height);
		}

		@Override
		public void onRenderedFirstFrame() {
			sendPlayerEvent(Event.EVENT_ON_VIDEO_RENDER_START);
		}

		@Override
		public void onTimelineChanged(@NonNull Timeline timeline, int reason) {
			sendPlayerEvent(Event.EVENT_ON_TIMER_UPDATE);
		}

		@Override
		public void onIsLoadingChanged(boolean isLoading) {
			sendPlayerEvent(isLoading ? Event.EVENT_ON_BUFFERING_START : Event.EVENT_ON_BUFFERING_END);
		}

		@Override
		public void onPlaybackStateChanged(int state) {
			switch (state) {
				case Player.STATE_READY:
					LogUtils.d("Player.STATE_READY");
					break;
				case Player.STATE_BUFFERING:
					LogUtils.d("Player.STATE_BUFFERING");
					sendBufferingUpdateEvent((int) getBufferPercentage());
					break;
				case Player.STATE_ENDED:
					LogUtils.d("Player.STATE_ENDED");
					sendPlayerEvent(Event.EVENT_ON_PLAY_COMPLETE);
					break;
				case Player.STATE_IDLE:
					LogUtils.d("Player.STATE_IDLE");
					break;
			}
			mBundle.putInt(EventBundleKey.KEY_ORIGINAL_EVENT, state);
			sendPlayerEvent(Event.EVENT_ON_STATUS_CHANGE, mBundle);
			recycleBundle();
		}

		@Override
		public void onPlayerError(@NonNull PlaybackException error) {
			handleException(error, false);
			mBundle.putInt(EventBundleKey.KEY_ORIGINAL_EVENT, error.errorCode);
			sendErrorEvent(ErrorEvent.ERROR_EVENT_COMMON, mBundle);
			recycleBundle();
		}
	};

	private boolean available() {
		return mMediaPlayer != null;
	}

	private boolean hasDataSource() {
		return available() && mDataSource != null;
	}

	@Override
	public void start() {
		if (hasDataSource() && (mCurrentState == STATE_PREPARED || mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
			mMediaPlayer.play();
			mCurrentState = STATE_PLAYING;
			sendPlayerEvent(Event.EVENT_ON_START);
		}
	}

	@Override
	public void pause() {
		if (hasDataSource() && (mCurrentState == STATE_PREPARED || mCurrentState == STATE_PLAYING || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
			mMediaPlayer.pause();
			mCurrentState = STATE_PAUSED;
			sendPlayerEvent(Event.EVENT_ON_PAUSE);
		}
	}

	@Override
	public void stop() {
		if (hasDataSource() && (mCurrentState == STATE_PREPARED || mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
			mMediaPlayer.stop();
			mCurrentState = STATE_STOPPED;
			sendPlayerEvent(Event.EVENT_ON_STOP);
		}
	}

	@Override
	public void release() {
		if (available()) {
			mMediaPlayer.release();
			mCurrentState = STATE_IDLE;
			sendPlayerEvent(Event.EVENT_ON_RELEASE);
			resetListener();
		}
	}

	@Override
	public void reset() {
		if (available()) {
			mMediaPlayer.stop();
			mCurrentState = STATE_IDLE;
			sendPlayerEvent(Event.EVENT_ON_RESET);
		}
	}

	@Override
	public boolean isPlaying() {
		if (available()) return mMediaPlayer.isPlaying();
		return false;
	}

	@Override
	public void setVolume(float volume) {
		if (available()) mMediaPlayer.setVolume(volume);
	}

	@Override
	public void seekTo(int position) {
		if (available() && (mCurrentState == STATE_PREPARED || mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
			mMediaPlayer.seekTo(position);
			Bundle bundle = obtainBundle();
			bundle.putInt(EventBundleKey.KEY_TIME, position);
			sendPlayerEvent(Event.EVENT_ON_SEEK_TO, bundle);
		}
	}

	@Override
	public int getCurrentPosition() {
		if (!available()) return -1;
		return (int) mMediaPlayer.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		if (!available()) return -1;
		return (int) mMediaPlayer.getDuration();
	}

	@Override
	public float getBufferPercentage() {
		if (available()) return mMediaPlayer.getBufferedPercentage();
		return super.getBufferPercentage();
	}

	@Override
	public void setSpeed(float speed) {
		if (available() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			PlaybackParameters parameters = new PlaybackParameters(speed, 1f);
			mMediaPlayer.setPlaybackParameters(parameters);
		}
	}

	@Override
	public float getSpeed() {
		if (available()) return mMediaPlayer.getPlaybackParameters().speed;
		return 1.0f;
	}

	@Override
	public void setLooping(boolean looping) {
		if (available()) mMediaPlayer.setRepeatMode(looping ? Player.REPEAT_MODE_ALL : Player.REPEAT_MODE_OFF);
	}

	@Override
	public SimpleExoPlayer getPlayer() {
		return mMediaPlayer;
	}

	@Override
	public void setSurface(@NonNull SurfaceHolder surfaceHolder) {
		mMediaPlayer.setVideoSurfaceHolder(surfaceHolder);
		sendPlayerEvent(Event.EVENT_ON_SURFACE_HOLDER_UPDATE);
	}

	@Override
	public void setSurface(@NonNull Surface surface) {
		mMediaPlayer.setVideoSurface(surface);
		sendPlayerEvent(Event.EVENT_ON_SURFACE_UPDATE);
	}

	@Override
	public void setSurface(@NonNull SurfaceTexture surfaceTexture) {
		setSurface(new Surface(surfaceTexture));
	}

	@Override
	public void updateSurface(int width, int height) {
	}

	@Override
	public void surfaceDestroy() {
	}
}
