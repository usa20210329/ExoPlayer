package com.lodz.android.mmsplayer.ijk.media;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;

import androidx.annotation.NonNull;

import com.lodz.android.mmsplayer.R;
import com.lodz.android.mmsplayer.ijk.bean.MediaInfoBean;
import com.lodz.android.mmsplayer.ijk.bean.TrackAudioInfoBean;
import com.lodz.android.mmsplayer.ijk.bean.TrackVideoInfoBean;
import com.lodz.android.mmsplayer.ijk.services.MediaPlayerService;
import com.lodz.android.mmsplayer.ijk.setting.IjkPlayerSetting;
import com.lodz.android.mmsplayer.ijk.utils.MediaInfoUtils;

import java.io.File;
import java.util.Map;

import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.TextureMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;
import tv.danmaku.ijk.media.player.misc.IMediaFormat;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkMediaFormat;

/**
 * IJK播放控件
 * Created by zhouL on 2016/12/1.
 */
public class IjkVideoView extends FrameLayout implements MediaController.MediaPlayerControl {

	public static final String TAG_INFO_HUD = "InfoHud";

	private Uri mUri;
	private Map<String, String> mHeaders;

	private static final int STATE_ERROR = -1;
	private static final int STATE_IDLE = 0;
	private static final int STATE_PREPARING = 1;
	private static final int STATE_PREPARED = 2;
	private static final int STATE_PLAYING = 3;
	private static final int STATE_PAUSED = 4;
	private static final int STATE_PLAYBACK_COMPLETED = 5;

	private int mCurrentState = STATE_IDLE;
	private int mTargetState = STATE_IDLE;

	private IMediaController mMediaController;
	private IRenderView.ISurfaceHolder mSurfaceHolder = null;
	private IMediaPlayer.OnCompletionListener mOnCompletionListener;
	private IMediaPlayer.OnPreparedListener mOnPreparedListener;
	private IMediaPlayer.OnErrorListener mOnErrorListener;
	private IMediaPlayer.OnInfoListener mOnInfoListener;
	private IjkMediaPlayer mIjkMediaPlayer = null;
	private IMediaPlayer mMediaPlayer = null;

	private int mVideoWidth;
	private int mVideoHeight;
	private int mSurfaceWidth;
	private int mSurfaceHeight;
	private int mVideoRotationDegree;
	private int mCurrentBufferPercentage;
	private long mSeekWhenPrepared;
	private final boolean mCanPause = true;
	private final boolean mCanSeekBack = true;
	private final boolean mCanSeekForward = true;

	private Context mAppContext;
	private IRenderView mRenderView;
	private int mVideoSarNum;
	private int mVideoSarDen;

	private InfoHudViewHolder mHudViewHolder;

	private long mPrepareStartTime = 0;
	private long mPrepareEndTime = 0;

	private long mSeekStartTime = 0;
	private long mSeekEndTime = 0;

	private IjkPlayerSetting mSetting;

	public IjkVideoView(Context context) {
		super(context);
		initVideoView(context);
	}

	public IjkVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initVideoView(context);
	}

	public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initVideoView(context);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initVideoView(context);
	}

	/**
	 * 初始化VideoView
	 *
	 * @param context 上下文
	 */
	private void initVideoView(Context context) {
		mAppContext = context.getApplicationContext();
	}

	/**
	 * 初始化
	 */
	public void init(IjkPlayerSetting setting) {
		mSetting = setting == null ? IjkPlayerSetting.getDefault() : setting;
		initBackground();
		setRenderView(createRenderView(mSetting.renderViewType));
		mVideoWidth = 0;
		mVideoHeight = 0;
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		mCurrentState = STATE_IDLE;
		mTargetState = STATE_IDLE;
		setHudLog(false);
		loadNative();
	}

	private void loadNative() {
		try {
			IjkMediaPlayer.loadLibrariesOnce(null);
			IjkMediaPlayer.native_profileBegin("libijkplayer.so");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置渲染的view
	 *
	 * @param renderView 渲染的view
	 */
	private void setRenderView(IRenderView renderView) {
		if (mRenderView != null) {
			if (mMediaPlayer != null) {
				mMediaPlayer.setDisplay(null);
			}
			View renderUIView = mRenderView.getView();
			mRenderView.removeRenderCallback(mSHCallback);
			mRenderView = null;
			removeView(renderUIView);
		}

		if (renderView == null) {
			return;
		}

		mRenderView = renderView;
		mRenderView.setAspectRatio(mSetting.aspectRatioType);
		if (mVideoWidth > 0 && mVideoHeight > 0) {
			mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
		}
		if (mVideoSarNum > 0 && mVideoSarDen > 0) {
			mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
		}

		View renderUIView = mRenderView.getView();
		FrameLayout.LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
		renderUIView.setLayoutParams(lp);
		addView(renderUIView);

		mRenderView.addRenderCallback(mSHCallback);
		mRenderView.setVideoRotation(mVideoRotationDegree);
	}

	/**
	 * 根据type创建RenderView
	 *
	 * @param renderType 渲染view的类型
	 */
	private IRenderView createRenderView(@IjkPlayerSetting.RenderViewType int renderType) {
		switch (renderType) {
			case IjkPlayerSetting.RenderViewType.TEXTURE_VIEW:
				return new TextureRenderView(getContext());
			case IjkPlayerSetting.RenderViewType.SURFACE_VIEW:
				return new SurfaceRenderView(getContext());
			case IjkPlayerSetting.RenderViewType.NO_VIEW:
			default:
				return null;
		}
	}

	public void setHudLog(boolean isOpen) {
		if (isOpen) {
			mHudViewHolder = new InfoHudViewHolder(getContext());
		}
	}

	/**
	 * Sets video path.
	 *
	 * @param path the path of the video.
	 */
	public void setVideoPath(String userAgent, String path) {
		setVideoURI(Uri.parse(path));
		if (TextUtils.isEmpty(userAgent)) return;
		mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "user_agent", userAgent);
	}

	/**
	 * Sets video URI.
	 *
	 * @param uri the URI of the video.
	 */
	public void setVideoURI(Uri uri) {
		setVideoURI(uri, null);
	}

	/**
	 * Sets video URI using specific headers.
	 *
	 * @param uri     the URI of the video.
	 * @param headers the headers for the URI request.
	 *                Note that the cross domain redirection is allowed by default, but that can be
	 *                changed with key/value pairs through the headers parameter with
	 *                "android-allow-cross-domain-redirect" as the key and "0" or "1" as the value
	 *                to disallow or allow cross domain redirection.
	 */
	public void setVideoURI(Uri uri, Map<String, String> headers) {
		mUri = uri;
		mHeaders = headers;
		mSeekWhenPrepared = 0;
		openVideo();
		requestLayout();
		invalidate();
	}

	public void stopPlayback() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			if (mHudViewHolder != null) {
				mHudViewHolder.setMediaPlayer(null);
			}
			mCurrentState = STATE_IDLE;
			mTargetState = STATE_IDLE;
			AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
			if (am != null) {
				am.abandonAudioFocus(null);
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.M)
	private void openVideo() {
		if (mUri == null || mSurfaceHolder == null) {
			// not ready for playback just yet, will try again later
			return;
		}
		// we shouldn't clear the target state, because somebody might have
		// called start() previously
		release(false);

		AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
		if (am != null) {
			am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		}

		try {
			mMediaPlayer = createPlayer(mSetting.playerType);
			mMediaPlayer.setOnPreparedListener(mPreparedListener);
			mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
			mMediaPlayer.setOnCompletionListener(mCompletionListener);
			mMediaPlayer.setOnErrorListener(mErrorListener);
			mMediaPlayer.setOnInfoListener(mInfoListener);
			mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
			mCurrentBufferPercentage = 0;
			String scheme = mUri.getScheme();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mSetting.isUsingMediaDataSource && (TextUtils.isEmpty(scheme) || scheme.equalsIgnoreCase("file"))) {
				IMediaDataSource dataSource = new FileMediaDataSource(new File(mUri.toString()));
				mMediaPlayer.setDataSource(dataSource);
			} else {
				mMediaPlayer.setDataSource(mAppContext, mUri, mHeaders);
			}
			bindSurfaceHolder(mMediaPlayer, mSurfaceHolder);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setScreenOnWhilePlaying(true);
			mPrepareStartTime = System.currentTimeMillis();
			mMediaPlayer.prepareAsync();

			if (mHudViewHolder != null) {
				mHudViewHolder.setMediaPlayer(mMediaPlayer);
			}

			mCurrentState = STATE_PREPARING;
			attachMediaController();
		} catch (Exception ex) {
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
		}
	}

	public void setMediaController(IMediaController controller) {
		if (mMediaController != null) {
			mMediaController.hide();
		}
		mMediaController = controller;
		attachMediaController();
	}

	private void attachMediaController() {
		if (mMediaPlayer != null && mMediaController != null) {
			mMediaController.setMediaPlayer(this);
			View anchorView = this.getParent() instanceof View ? (View) this.getParent() : this;
			mMediaController.setAnchorView(anchorView);
			mMediaController.setEnabled(isInPlaybackState());
		}
	}

	IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
		@Override
		public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
			mVideoSarNum = mp.getVideoSarNum();
			mVideoSarDen = mp.getVideoSarDen();
			if (mVideoWidth != 0 && mVideoHeight != 0) {
				if (mRenderView != null) {
					mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
					mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
				}
				requestLayout();
			}
		}
	};

	IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
		@Override
		public void onPrepared(IMediaPlayer mp) {
			mPrepareEndTime = System.currentTimeMillis();
			if (mHudViewHolder != null) {
				mHudViewHolder.updateLoadCost(mPrepareEndTime - mPrepareStartTime);
			}
			mCurrentState = STATE_PREPARED;

			if (mOnPreparedListener != null) {
				mOnPreparedListener.onPrepared(mMediaPlayer);
			}
			if (mMediaController != null) {
				mMediaController.setEnabled(true);
			}
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();

			long seekToPosition = mSeekWhenPrepared;
			if (seekToPosition != 0) {
				seekTo(seekToPosition);
			}
			if (mVideoWidth != 0 && mVideoHeight != 0) {
				if (mRenderView != null) {
					mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
					mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
					if (!mRenderView.shouldWaitForResize() || mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
						if (mTargetState == STATE_PLAYING) {
							start();
							if (mMediaController != null) {
								mMediaController.show();
							}
						} else if (!isPlaying() && (seekToPosition != 0 || getCurrentPosition() > 0)) {
							if (mMediaController != null) {
								mMediaController.show(0);
							}
						}
					}
				}
			} else {
				if (mTargetState == STATE_PLAYING) {
					start();
				}
			}
		}
	};

	private final IMediaPlayer.OnCompletionListener mCompletionListener = new IMediaPlayer.OnCompletionListener() {
		@Override
		public void onCompletion(IMediaPlayer mp) {
			mCurrentState = STATE_PLAYBACK_COMPLETED;
			mTargetState = STATE_PLAYBACK_COMPLETED;
			if (mMediaController != null) {
				mMediaController.hide();
			}
			if (mOnCompletionListener != null) {
				mOnCompletionListener.onCompletion(mMediaPlayer);
			}
		}
	};

	private final IMediaPlayer.OnInfoListener mInfoListener = new IMediaPlayer.OnInfoListener() {
		@Override
		public boolean onInfo(IMediaPlayer mp, int arg1, int arg2) {
			if (mOnInfoListener != null) {
				mOnInfoListener.onInfo(mp, arg1, arg2);
			}
			if (arg1 == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED) {
				mVideoRotationDegree = arg2;
				if (mRenderView != null) mRenderView.setVideoRotation(arg2);
			}
			return true;
		}
	};

	private final IMediaPlayer.OnErrorListener mErrorListener = new IMediaPlayer.OnErrorListener() {
		public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) {
			mCurrentState = STATE_ERROR;
			mTargetState = STATE_ERROR;
			if (mMediaController != null) {
				mMediaController.hide();
			}
			if (mOnErrorListener != null) {
				if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
					return true;
				}
			}
			return true;
		}
	};

	private final IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
		@Override
		public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
			mCurrentBufferPercentage = percent;
		}
	};

	private final IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {

		@Override
		public void onSeekComplete(IMediaPlayer mp) {
			mSeekEndTime = System.currentTimeMillis();
			if (mHudViewHolder != null) {
				mHudViewHolder.updateSeekCost(mSeekEndTime - mSeekStartTime);
			}
		}
	};

	/**
	 * Register a callback to be invoked when the media file
	 * is loaded and ready to go.
	 *
	 * @param l The callback that will be run
	 */
	public void setOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
		mOnPreparedListener = l;
	}

	/**
	 * Register a callback to be invoked when the end of a media file
	 * has been reached during playback.
	 *
	 * @param l The callback that will be run
	 */
	public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
		mOnCompletionListener = l;
	}

	/**
	 * Register a callback to be invoked when an error occurs
	 * during playback or setup.  If no listener is specified,
	 * or if the listener returned false, VideoView will inform
	 * the user of any errors.
	 *
	 * @param l The callback that will be run
	 */
	public void setOnErrorListener(IMediaPlayer.OnErrorListener l) {
		mOnErrorListener = l;
	}

	/**
	 * Register a callback to be invoked when an informational event
	 * occurs during playback or setup.
	 *
	 * @param l The callback that will be run
	 */
	public void setOnInfoListener(IMediaPlayer.OnInfoListener l) {
		mOnInfoListener = l;
	}

	private void bindSurfaceHolder(IMediaPlayer mp, IRenderView.ISurfaceHolder holder) {
		if (mp == null) return;
		if (holder == null) {
			mp.setDisplay(null);
			return;
		}
		holder.bindToMediaPlayer(mp);
	}

	IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
		@Override
		public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int w, int h) {
			if (holder.getRenderView() != mRenderView) return;
			mSurfaceWidth = w;
			mSurfaceHeight = h;
			boolean isValidState = (mTargetState == STATE_PLAYING);
			boolean hasValidSize = !mRenderView.shouldWaitForResize() || (mVideoWidth == w && mVideoHeight == h);
			if (mMediaPlayer != null && isValidState && hasValidSize) {
				if (mSeekWhenPrepared != 0) {
					seekTo(mSeekWhenPrepared);
				}
				start();
			}
		}

		@Override
		public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
			if (holder.getRenderView() != mRenderView) return;
			mSurfaceHolder = holder;
			if (mMediaPlayer != null) bindSurfaceHolder(mMediaPlayer, holder);
			else openVideo();
		}

		@Override
		public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {
			if (holder.getRenderView() != mRenderView) return;
			mSurfaceHolder = null;
			releaseWithoutStop();
		}
	};

	public void releaseWithoutStop() {
		if (mMediaPlayer != null) mMediaPlayer.setDisplay(null);
	}

	/*
	 * release the media player in any state
	 */
	public void release(boolean clearState) {
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
			mCurrentState = STATE_IDLE;
			if (clearState) {
				mTargetState = STATE_IDLE;
			}
			AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
			if (am != null) {
				am.abandonAudioFocus(null);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isInPlaybackState() && mMediaController != null) {
			toggleMediaControlsVisiblity();
		}
		return false;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		if (isInPlaybackState() && mMediaController != null) {
			toggleMediaControlsVisiblity();
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_VOLUME_MUTE && keyCode != KeyEvent.KEYCODE_MENU && keyCode != KeyEvent.KEYCODE_CALL && keyCode != KeyEvent.KEYCODE_ENDCALL;
		if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
			if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
				if (mMediaPlayer.isPlaying()) {
					pause();
					mMediaController.show();
				} else {
					start();
					mMediaController.hide();
				}
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
				if (!mMediaPlayer.isPlaying()) {
					start();
					mMediaController.hide();
				}
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
				if (mMediaPlayer.isPlaying()) {
					pause();
					mMediaController.show();
				}
				return true;
			} else {
				toggleMediaControlsVisiblity();
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	private void toggleMediaControlsVisiblity() {
		if (mMediaController.isShowing()) {
			mMediaController.hide();
		} else {
			mMediaController.show();
		}
	}

	@Override
	public void start() {
		if (isInPlaybackState()) {
			mMediaPlayer.start();
			mCurrentState = STATE_PLAYING;
		}
		mTargetState = STATE_PLAYING;
	}

	@Override
	public void pause() {
		if (isInPlaybackState()) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
				mCurrentState = STATE_PAUSED;
			}
		}
		mTargetState = STATE_PAUSED;
	}

	public void suspend() {
		release(false);
	}

	public void resume() {
		openVideo();
		start();
	}

	@Override
	public int getDuration() {
		if (isInPlaybackState()) {
			return (int) mMediaPlayer.getDuration();
		}
		return -1;
	}

	public long getVideoDuration() {
		if (isInPlaybackState()) {
			return mMediaPlayer.getDuration();
		}
		return -1;
	}

	@Override
	public int getCurrentPosition() {
		if (isInPlaybackState()) {
			return (int) mMediaPlayer.getCurrentPosition();
		}
		return 0;
	}

	public long getCurrentPlayPosition() {
		if (isInPlaybackState()) {
			return mMediaPlayer.getCurrentPosition();
		}
		return 0;
	}

	public long getBreakPosition() {
		long position = 0;
		try {
			position = mMediaPlayer.getCurrentPosition();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return position;
	}

	@Override
	public void seekTo(int msec) {
		if (isInPlaybackState()) {
			mSeekStartTime = System.currentTimeMillis();
			mMediaPlayer.seekTo(msec);
			mSeekWhenPrepared = 0;
		} else {
			mSeekWhenPrepared = msec;
		}
	}

	public void seekTo(long position) {
		if (isInPlaybackState()) {
			mSeekStartTime = System.currentTimeMillis();
			mMediaPlayer.seekTo(position);
			mSeekWhenPrepared = 0;
		} else {
			mSeekWhenPrepared = position;
		}
	}

	@Override
	public boolean isPlaying() {
		return isInPlaybackState() && mMediaPlayer.isPlaying();
	}

	public boolean isPause() {
		return isInPlaybackState() && mCurrentState == STATE_PAUSED;
	}

	public boolean isCompleted() {
		return isInPlaybackState() && mCurrentState == STATE_PLAYBACK_COMPLETED;
	}

	public boolean isAlreadySetPath() {
		return mUri != null;
	}

	@Override
	public int getBufferPercentage() {
		if (mMediaPlayer != null) return mCurrentBufferPercentage;
		return 0;
	}

	private boolean isInPlaybackState() {
		return (mMediaPlayer != null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
	}

	@Override
	public boolean canPause() {
		return mCanPause;
	}

	@Override
	public boolean canSeekBackward() {
		return mCanSeekBack;
	}

	@Override
	public boolean canSeekForward() {
		return mCanSeekForward;
	}

	@Override
	public int getAudioSessionId() {
		return 0;
	}

	private static final int[] s_allAspectRatio = {IRenderView.AR_ASPECT_FIT_PARENT, IRenderView.AR_ASPECT_FILL_PARENT, IRenderView.AR_ASPECT_WRAP_CONTENT, IRenderView.AR_MATCH_PARENT, IRenderView.AR_16_9_FIT_PARENT, IRenderView.AR_4_3_FIT_PARENT};
	private int mCurrentAspectRatioIndex = 0;

	public int toggleAspectRatio() {
		mCurrentAspectRatioIndex++;
		mCurrentAspectRatioIndex %= s_allAspectRatio.length;
		int aspectRatio = s_allAspectRatio[mCurrentAspectRatioIndex];
		setAspectRatio(aspectRatio);
		return aspectRatio;
	}

	public void setAspectRatio(@IjkPlayerSetting.AspectRatioType int aspectRatioType) {
		if (mRenderView != null) {
			mSetting.aspectRatioType = aspectRatioType;
			mRenderView.setAspectRatio(aspectRatioType);
		}
	}

	@NonNull
	public String getRenderText(Context context) {
		String text;
		switch (mSetting.renderViewType) {
			case IjkPlayerSetting.RenderViewType.NO_VIEW:
				text = context.getString(R.string.mmsplayer_videoview_render_none);
				break;
			case IjkPlayerSetting.RenderViewType.SURFACE_VIEW:
				text = context.getString(R.string.mmsplayer_videoview_render_surface_view);
				break;
			case IjkPlayerSetting.RenderViewType.TEXTURE_VIEW:
				text = context.getString(R.string.mmsplayer_videoview_render_texture_view);
				break;
			default:
				text = context.getString(R.string.mmsplayer_n_a);
				break;
		}
		return text;
	}

	@NonNull
	public String getPlayerText(Context context) {
		String text;
		switch (mSetting.playerType) {
			case IjkPlayerSetting.PlayerType.PALY_ANDROID_MEDIA:
				text = context.getString(R.string.mmsplayer_videoview_player_androidmediaplayer);
				break;
			case IjkPlayerSetting.PlayerType.PLAY_IJK:
				text = context.getString(R.string.mmsplayer_videoview_player_ijkmediaplayer);
				break;
			default:
				text = context.getString(R.string.mmsplayer_n_a);
				break;
		}
		return text;
	}

	public IMediaPlayer createPlayer(@IjkPlayerSetting.PlayerType int playerType) {
		IMediaPlayer mediaPlayer;
		switch (playerType) {
			case IjkPlayerSetting.PlayerType.PALY_ANDROID_MEDIA:
				mediaPlayer = new AndroidMediaPlayer();
				break;
			case IjkPlayerSetting.PlayerType.PLAY_IJK:
			default: {
				IjkMediaPlayer ijkMediaPlayer = null;
				if (mUri != null) {
					ijkMediaPlayer = new IjkMediaPlayer();
					IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT);
					if (mSetting.isUsingMediaCodec) {
						ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
						if (mSetting.isUsingMediaCodecAutoRotate) {
							ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
						} else {
							ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
						}
						if (mSetting.isMediaCodecHandleResolutionChange) {
							ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
						} else {
							ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);
						}
					} else {
						ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
					}
					if (mSetting.isUsingOpenSLES) {
						ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
					} else {
						ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
					}
					String pixelFormat = getPixelFormat(mSetting.pixelFormatType);
					if (TextUtils.isEmpty(pixelFormat)) {
						ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
					} else {
						ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", pixelFormat);
					}
					ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "reconnect", 3);
					ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
					ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
					ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
					ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
					ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 1024 * 10);
					ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
					ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek");
					ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
				}
				mediaPlayer = ijkMediaPlayer;
				mIjkMediaPlayer = ijkMediaPlayer;
			}
			break;
		}

		if (mSetting.isEnableDetachedSurfaceTexture) {
			mediaPlayer = new TextureMediaPlayer(mediaPlayer);
		}

		return mediaPlayer;
	}

	private String getPixelFormat(int pixelFormatType) {
		switch (pixelFormatType) {
			case IjkPlayerSetting.PixelFormatType.PIXEL_RGB_565:
				return "fcc-rv16";
			case IjkPlayerSetting.PixelFormatType.PIXEL_RGB_888:
				return "fcc-rv24";
			case IjkPlayerSetting.PixelFormatType.PIXEL_RGBX_8888:
				return "fcc-rv32";
			case IjkPlayerSetting.PixelFormatType.PIXEL_YV12:
				return "fcc-yv12";
			case IjkPlayerSetting.PixelFormatType.PIXEL_OPENGL_ES2:
				return "fcc-_es2";
			case IjkPlayerSetting.PixelFormatType.PIXEL_AUTO:
			default:
				return "";
		}
	}

	/**
	 * 是否后台播放
	 */
	private boolean isBackgroundPlay = false;

	/**
	 * 初始化后台播放
	 */
	private void initBackground() {
		isBackgroundPlay = mSetting.isBackgroundPlay;
		if (isBackgroundPlay) {
			MediaPlayerService.intentToStart(getContext());
			mMediaPlayer = MediaPlayerService.getMediaPlayer();
			if (mHudViewHolder != null) {
				mHudViewHolder.setMediaPlayer(mMediaPlayer);
			}
		}
	}

	/**
	 * 是否启用后台播放
	 */
	public boolean isBackgroundPlayEnabled() {
		return isBackgroundPlay;
	}

	/**
	 * 进入后台播放
	 */
	public void enterBackground() {
		MediaPlayerService.setMediaPlayer(mMediaPlayer);
	}

	/**
	 * 停止后台播放
	 */
	public void stopBackgroundPlay() {
		MediaPlayerService.setMediaPlayer(null);
	}

	/**
	 * 获取视频信息
	 */
	public MediaInfoBean getMediaInfo() {
		if (mMediaPlayer == null) {
			return null;
		}

		IMediaPlayer player = mMediaPlayer;

		MediaInfoBean mediaInfoBean = new MediaInfoBean();
		mediaInfoBean.playerName = MediaPlayerCompat.getName(player);
		mediaInfoBean.resolution = MediaInfoUtils.buildResolution(mVideoWidth, mVideoHeight, mVideoSarNum, mVideoSarDen);
		mediaInfoBean.length = MediaInfoUtils.buildTimeMilli(player.getDuration());

		ITrackInfo[] trackInfos = player.getTrackInfo();
		if (trackInfos == null || trackInfos.length == 0) {
			return mediaInfoBean;
		}

		TrackVideoInfoBean videoInfoBean = new TrackVideoInfoBean();
		TrackAudioInfoBean audioInfoBean = new TrackAudioInfoBean();
		int index = -1;
		for (ITrackInfo trackInfo : trackInfos) {
			index++;
			int trackType = trackInfo.getTrackType();
			IMediaFormat mediaFormat = trackInfo.getFormat();
			if (mediaFormat instanceof IjkMediaFormat) {
				switch (trackType) {
					case ITrackInfo.MEDIA_TRACK_TYPE_VIDEO:
						videoInfoBean = getTrackVideoInfoBean(index, player, mediaFormat, trackInfo);
						break;
					case ITrackInfo.MEDIA_TRACK_TYPE_AUDIO:
						audioInfoBean = getTrackAudioInfoBean(index, player, mediaFormat, trackInfo);
						break;
					default:
						break;
				}
			}
		}
		mediaInfoBean.trackVideoInfoBean = videoInfoBean;
		mediaInfoBean.trackAudioInfoBean = audioInfoBean;
		return mediaInfoBean;
	}

	/**
	 * 获取视频轨信息
	 *
	 * @param index       索引
	 * @param player      播放器
	 * @param mediaFormat 视频格式
	 * @param trackInfo   轨道信息
	 */
	private TrackVideoInfoBean getTrackVideoInfoBean(int index, IMediaPlayer player, IMediaFormat mediaFormat, ITrackInfo trackInfo) {
		TrackVideoInfoBean videoInfoBean = new TrackVideoInfoBean();
		int selectedVideoTrack = MediaPlayerCompat.getSelectedTrack(player, ITrackInfo.MEDIA_TRACK_TYPE_VIDEO);
		if (index == selectedVideoTrack) {
			videoInfoBean.isSelected = true;
		}
		videoInfoBean.type = MediaInfoUtils.buildTrackType(getContext(), ITrackInfo.MEDIA_TRACK_TYPE_VIDEO);
		videoInfoBean.language = MediaInfoUtils.buildLanguage(trackInfo.getLanguage());
		videoInfoBean.codec = mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_LONG_NAME_UI);
		videoInfoBean.profileLevel = mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PROFILE_LEVEL_UI);
		videoInfoBean.pixelFormat = mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PIXEL_FORMAT_UI);
		videoInfoBean.resolution = mediaFormat.getString(IjkMediaFormat.KEY_IJK_RESOLUTION_UI);
		videoInfoBean.frameRate = mediaFormat.getString(IjkMediaFormat.KEY_IJK_FRAME_RATE_UI);
		videoInfoBean.bitRate = mediaFormat.getString(IjkMediaFormat.KEY_IJK_BIT_RATE_UI);
		return videoInfoBean;
	}

	/**
	 * 获取音频轨信息
	 *
	 * @param index       索引
	 * @param player      播放器
	 * @param mediaFormat 音频格式
	 * @param trackInfo   轨道信息
	 */
	private TrackAudioInfoBean getTrackAudioInfoBean(int index, IMediaPlayer player, IMediaFormat mediaFormat, ITrackInfo trackInfo) {
		TrackAudioInfoBean audioInfoBean = new TrackAudioInfoBean();
		int selectedAudioTrack = MediaPlayerCompat.getSelectedTrack(player, ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
		if (index == selectedAudioTrack) {
			audioInfoBean.isSelected = true;
		}
		audioInfoBean.type = MediaInfoUtils.buildTrackType(getContext(), ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
		audioInfoBean.language = MediaInfoUtils.buildLanguage(trackInfo.getLanguage());
		audioInfoBean.codec = mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_LONG_NAME_UI);
		audioInfoBean.profileLevel = mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PROFILE_LEVEL_UI);
		audioInfoBean.sampleRate = mediaFormat.getString(IjkMediaFormat.KEY_IJK_SAMPLE_RATE_UI);
		audioInfoBean.channels = mediaFormat.getString(IjkMediaFormat.KEY_IJK_CHANNEL_UI);
		audioInfoBean.bitRate = mediaFormat.getString(IjkMediaFormat.KEY_IJK_BIT_RATE_UI);
		return audioInfoBean;
	}

	public ITrackInfo[] getTrackInfo() {
		if (mMediaPlayer == null) return null;
		return mMediaPlayer.getTrackInfo();
	}

	public void selectTrack(int stream) {
		MediaPlayerCompat.selectTrack(mMediaPlayer, stream);
	}

	public void deselectTrack(int stream) {
		MediaPlayerCompat.deselectTrack(mMediaPlayer, stream);
	}

	public int getSelectedTrack(int trackType) {
		return MediaPlayerCompat.getSelectedTrack(mMediaPlayer, trackType);
	}
}