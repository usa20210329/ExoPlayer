package com.fongmi.android.ltv.ui.custom;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fongmi.android.ltv.utils.Utils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

public class ExoPlayer {

	private final Context mContext;
	private SimpleExoPlayer mPlayer;

	public ExoPlayer(@NonNull Context context) {
		this(context, null);
	}

	public ExoPlayer(@NonNull Context context, @Nullable SimpleExoPlayer mediaPlayer) {
		this.mContext = context.getApplicationContext();
		init(mediaPlayer);
	}

	private void init(SimpleExoPlayer mediaPlayer) {
		if (mediaPlayer == null) mPlayer = create();
		else mPlayer = mediaPlayer;
		addListener();
	}

	private SimpleExoPlayer create() {
		DefaultRenderersFactory factory = new DefaultRenderersFactory(mContext);
		factory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
		return new SimpleExoPlayer.Builder(mContext, factory).build();
	}

	public SimpleExoPlayer get() {
		return mPlayer;
	}

	public void setDataSource(String userAgent, String url) {
		try {
			mPlayer.setMediaSource(getMedia(userAgent, url));
			mPlayer.prepare();
			mPlayer.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private MediaSource getMedia(String userAgent, String url) {
		Uri videoUri = Uri.parse(url);
		com.google.android.exoplayer2.upstream.DataSource.Factory factory = getFactory(userAgent, url);
		MediaItem mediaItem = new MediaItem.Builder().setUri(videoUri).build();
		int type = Util.inferContentType(videoUri);
		if (type == C.TYPE_HLS || url.contains(".php")) {
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

	private com.google.android.exoplayer2.upstream.DataSource.Factory getFactory(String userAgent, String url) {
		userAgent = TextUtils.isEmpty(userAgent) ? Utils.getUserAgent() : userAgent;
		HttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory().setUserAgent(userAgent).setAllowCrossProtocolRedirects(true);
		return url.startsWith("rtmp") ? new RtmpDataSource.Factory() : new DefaultDataSourceFactory(mContext, httpDataSourceFactory);
	}

	private void addListener() {
		mPlayer.addListener(mEventListener);
	}

	private void resetListener() {
		mPlayer.removeListener(mEventListener);
	}

	private final Player.Listener mEventListener = new Player.Listener() {
		@Override
		public void onPlayerError(@NonNull PlaybackException error) {
		}
	};

	public void start() {
		mPlayer.play();
	}

	public void pause() {
		mPlayer.pause();
	}

	public void release() {
		mPlayer.release();
		resetListener();
	}

	public void stop() {
		mPlayer.stop();
	}

	public void seekForward() {
		mPlayer.seekForward();
	}

	public void seekBack() {
		mPlayer.seekBack();
	}

	public boolean isPlaying() {
		return mPlayer.isPlaying();
	}

	public boolean isMovie() {
		return mPlayer.getDuration() > 30 * 60 * 1000;
	}
}
