package com.fongmi.android.tv.ui;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fongmi.android.tv.R;
import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.utils.Notify;
import com.fongmi.android.tv.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {

	private List<Channel> mItems;
	private List<Channel> mHides;
	private OnItemClickListener mItemClickListener;
	private Handler mHandler;
	private boolean mVisible;
	private boolean mWaiting;
	private int mPosition;
	private int mCount;

	ChannelAdapter() {
		this.mItems = new ArrayList<>();
		this.mHides = new ArrayList<>();
		this.mHandler = new Handler();
		this.mPosition = -1;
	}

	interface OnItemClickListener {
		void onItemClick(Channel item);
	}

	void setOnItemClickListener(OnItemClickListener itemClickListener) {
		this.mItemClickListener = itemClickListener;
	}

	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		@BindView(R.id.info) TextView info;

		ViewHolder(View view) {
			super(view);
			ButterKnife.bind(this, view);
			view.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			setPosition(getLayoutPosition());
			setChannel(0);
		}
	}

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			if (mVisible) {
				mItemClickListener.onItemClick(mItems.get(mPosition));
			}
		}
	};

	private void resetCount() {
		this.mCount = 0;
	}

	private void setPosition(int position) {
		this.mPosition = position;
	}

	private void setChannel(int delay) {
		for (Channel item : mItems) item.deselect();
		mItems.get(mPosition).select();
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, delay);
		notifyDataSetChanged();
	}

	void addAll(List<Channel> items) {
		mHides.clear();
		mItems.clear();
		mItems.addAll(items);
		removeHiddenChannel();
		notifyDataSetChanged();
		onResume();
	}

	void removeHiddenChannel() {
		Iterator<Channel> iterator = mItems.iterator();
		while (iterator.hasNext()) {
			Channel item = iterator.next();
			if (item.isHidden()) {
				item.deselect();
				mHides.add(item);
				iterator.remove();
			}
		}
	}

	void addCount() {
		if (mHides.size() > 0 && ++mCount > 4) {
			mItems.addAll(mHides);
			notifyDataSetChanged();
			Notify.show(R.string.channel_unlock);
			mHides.clear();
			resetCount();
		}
	}

	void resetUrl() {
		for (Channel item : mItems) item.setRealUrl("");
		notifyDataSetChanged();
	}

	int onMoveUp(boolean wait) {
		mWaiting = wait;
		mPosition = mPosition > 0 ? --mPosition : mItems.size() - 1;
		setChannel(wait ? 10000 : 500);
		return mPosition;
	}

	int onMoveDown(boolean wait) {
		mWaiting = wait;
		mPosition = mPosition < mItems.size() - 1 ? ++mPosition : 0;
		setChannel(wait ? 10000 : 500);
		return mPosition;
	}

	String getNumber() {
		return Utils.getString(R.string.channel_number, mItems.get(mPosition).getNumber());
	}

	void onCenter() {
		if (mWaiting) setChannel(0);
		mWaiting = false;
	}

	void onResume() {
		if (mPosition > -1 && mPosition < mItems.size()) {
			setChannel(0);
		} else if (mPosition != -1) {
			onMoveDown(false);
		}
	}

	void findChannel(RecyclerView recyclerView, Channel channel) {
		if (mItems.contains(channel)) {
			recyclerView.smoothScrollToPosition(mItems.indexOf(channel));
			setPosition(mItems.indexOf(channel));
			setChannel(0);
		} else {
			Notify.show(R.string.channel_empty);
		}
	}

	void setVisible(boolean visible) {
		this.mVisible = visible;
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_main, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Channel item = mItems.get(position);
		holder.info.setText(item.getInfo());
		holder.info.setSelected(item.isSelect());
		holder.info.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.getTextSize());
	}
}
