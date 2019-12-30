package com.fongmi.android.tv.ui;

import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fongmi.android.tv.R;
import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.utils.Notify;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {

	private OnItemClickListener mItemClickListener;
	private List<Channel> mItems;
	private List<Channel> mHides;
	private Handler mHandler;
	private boolean mVisible;
	private boolean mWaiting;
	private int mPosition;
	private int mCount;

	ChannelAdapter() {
		this.mItems = new ArrayList<>();
		this.mHides = new ArrayList<>();
		this.mHandler = new Handler();
	}

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			if (mVisible) mItemClickListener.onItemClick(mItems.get(getPosition()));
		}
	};

	interface OnItemClickListener {
		void onItemClick(Channel item);
	}

	void setOnItemClickListener(OnItemClickListener itemClickListener) {
		this.mItemClickListener = itemClickListener;
	}

	private void resetCount() {
		this.mCount = 0;
	}

	private int getPosition() {
		return mPosition;
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
		}
	}

	void addAll(List<Channel> items) {
		mHides.clear();
		mItems.clear();
		mItems.addAll(items);
		removeHiddenChannel();
		notifyDataSetChanged();
	}

	private void removeHiddenChannel() {
		Iterator<Channel> iterator = mItems.iterator();
		while (iterator.hasNext()) {
			Channel item = iterator.next();
			if (item.isHidden()) {
				mHides.add(item);
				iterator.remove();
			}
		}
	}

	void addCount() {
		if (mHides.isEmpty() || ++mCount < 5) return;
		mItems.addAll(mHides);
		notifyDataSetChanged();
		Notify.show(R.string.channel_unlock);
		mHides.clear();
		resetCount();
	}

	int onMoveUp(boolean wait) {
		mWaiting = wait;
		mPosition = getPosition() > 0 ? --mPosition : mItems.size() - 1;
		setChannel(wait ? 10000 : 500);
		return mPosition;
	}

	int onMoveDown(boolean wait) {
		mWaiting = wait;
		mPosition = getPosition() < mItems.size() - 1 ? ++mPosition : 0;
		setChannel(wait ? 10000 : 500);
		return mPosition;
	}

	void setChannel(int delay) {
		if (getPosition() < 0 || getPosition() > mItems.size() - 1) return;
		for (Channel item : mItems) item.deselect();
		mItems.get(getPosition()).select();
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, delay);
		notifyDataSetChanged();
	}

	int getIndex(Channel channel) {
		return mItems.indexOf(channel);
	}

	void onCenter() {
		if (mWaiting) setChannel(0);
		mWaiting = false;
	}

	void resetUrl() {
		for (Channel item : mItems) item.setReal("");
		notifyDataSetChanged();
		setChannel(0);
	}

	void setPosition(int position) {
		this.mPosition = position;
		setChannel(0);
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
		return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_main, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		Channel item = mItems.get(position);
		holder.info.setText(item.getInfo());
		holder.info.setSelected(item.isSelect());
		holder.info.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.getTextSize());
	}
}
