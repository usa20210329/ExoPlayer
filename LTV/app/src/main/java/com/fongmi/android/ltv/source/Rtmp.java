package com.fongmi.android.ltv.source;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.devbrackets.android.exomedia.core.source.MediaSourceProvider;
import com.devbrackets.android.exomedia.core.source.builder.DefaultMediaSourceBuilder;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.TransferListener;

public class Rtmp extends DefaultMediaSourceBuilder {

	public static MediaSourceProvider.SourceTypeBuilder builder() {
		return new MediaSourceProvider.SourceTypeBuilder(new Rtmp(), "", "", "rtmp:.*");
	}

	@NonNull
	@Override
	protected DataSource.Factory buildDataSourceFactory(@NonNull Context context, @NonNull String userAgent, @Nullable TransferListener listener) {
		return new RtmpDataSourceFactory(listener);
	}
}
