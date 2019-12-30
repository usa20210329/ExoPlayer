package com.fongmi.android.tv.ui.adapter;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fongmi.android.tv.R;
import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.ui.holer.ChannelHolder;

import java.util.ArrayList;
import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelHolder> {

	private OnItemClickListener mItemClickListener;
	private List<Channel> mItems;
	private Handler mHandler;
	private boolean mVisible;
	private boolean mWaiting;
	private int mPosition;

	public ChannelAdapter() {
		this.mItems = new ArrayList<>();
		this.mHandler = new Handler();
	}

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			if (mVisible) mItemClickListener.onItemClick(mItems.get(getPosition()));
		}
	};

	public interface OnItemClickListener {

		void onItemClick(Channel item);
	}

	public void setOnItemClickListener(OnItemClickListener itemClickListener) {
		this.mItemClickListener = itemClickListener;
	}

	private int getPosition() {
		return mPosition;
	}

	public void addAll(List<Channel> items) {
		mItems.clear();
		mItems.addAll(items);
		notifyDataSetChanged();
	}

	public int onMoveUp(boolean wait) {
		mWaiting = wait;
		mPosition = getPosition() > 0 ? --mPosition : mItems.size() - 1;
		setChannel(wait ? 10000 : 500);
		return mPosition;
	}

	public int onMoveDown(boolean wait) {
		mWaiting = wait;
		mPosition = getPosition() < mItems.size() - 1 ? ++mPosition : 0;
		setChannel(wait ? 10000 : 500);
		return mPosition;
	}

	public void setChannel(int delay) {
		if (getPosition() < 0 || getPosition() > mItems.size() - 1) return;
		for (Channel item : mItems) item.deselect();
		mItems.get(getPosition()).select();
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, delay);
		notifyDataSetChanged();
	}

	public int getIndex(Channel channel) {
		return mItems.indexOf(channel);
	}

	public void onCenter() {
		if (mWaiting) setChannel(0);
		mWaiting = false;
	}

	public void setPosition(int position) {
		this.mPosition = position;
		setChannel(0);
	}

	public void setVisible(boolean visible) {
		this.mVisible = visible;
	}

	public void onItemClick(int position) {
		mItemClickListener.onItemClick(mItems.get(position));
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	@NonNull
	@Override
	public ChannelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new ChannelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_channel, parent, false), this);
	}

	@Override
	public void onBindViewHolder(@NonNull ChannelHolder holder, int position) {
		holder.setChannel(mItems.get(position));
	}
}
