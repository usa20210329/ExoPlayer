package com.fongmi.android.tv.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fongmi.android.tv.R;
import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.model.Type;
import com.fongmi.android.tv.ui.holer.ChannelHolder;

import java.util.ArrayList;
import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelHolder> {

	private OnItemClickListener mItemClickListener;
	private List<Channel> mItems;
	private Type mType;

	public ChannelAdapter() {
		this.mItems = new ArrayList<>();
	}

	public interface OnItemClickListener {

		void onItemClick(Channel item);

		boolean onLongClick();
	}

	public OnItemClickListener getListener() {
		return mItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener itemClickListener) {
		this.mItemClickListener = itemClickListener;
	}

	public Type getType() {
		return mType;
	}

	private void setType(Type type) {
		this.mType = type;
	}

	public void addAll(Type type) {
		setType(type);
		mItems.clear();
		mItems.addAll(type.getChannel());
		notifyDataSetChanged();
	}

	public Channel getItem(int position) {
		return mItems.get(position);
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
