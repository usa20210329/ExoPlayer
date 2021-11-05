package com.fongmi.android.ltv.utils;

import android.net.Uri;
import android.text.TextUtils;

import com.fongmi.android.ltv.App;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

public class ExoUtil {

	private static final long VOD_DURATION = 10 * 60 * 1000; //10 minute

	public static boolean isVoD(long duration) {
		return duration > VOD_DURATION;
	}

	public static MediaSource getSource(String userAgent, String url) {
		Uri videoUri = Uri.parse(url);
		DataSource.Factory factory = getFactory(userAgent, url);
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

	private static DataSource.Factory getFactory(String userAgent, String url) {
		userAgent = TextUtils.isEmpty(userAgent) ? Utils.getUserAgent() : userAgent;
		HttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory().setUserAgent(userAgent).setAllowCrossProtocolRedirects(true);
		return url.startsWith("rtmp") ? new RtmpDataSource.Factory() : new DefaultDataSource.Factory(App.get(), httpDataSourceFactory);
	}
}
