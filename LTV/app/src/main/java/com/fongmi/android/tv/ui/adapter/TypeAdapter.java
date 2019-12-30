package com.fongmi.android.tv.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fongmi.android.tv.R;
import com.fongmi.android.tv.model.Channel;
import com.fongmi.android.tv.model.Type;
import com.fongmi.android.tv.ui.holer.TypeHolder;
import com.fongmi.android.tv.utils.Notify;

import java.util.ArrayList;
import java.util.List;

public class TypeAdapter extends RecyclerView.Adapter<TypeHolder> {

	public OnItemClickListener mItemClickListener;
	private List<Type> mItems;
	private List<Type> mHides;
	private int mCount;

	public TypeAdapter() {
		this.mItems = new ArrayList<>();
		this.mHides = new ArrayList<>();
	}

	public interface OnItemClickListener {

		void onItemClick(Channel item);

		void onTypeClick();
	}

	public void setOnItemClickListener(OnItemClickListener itemClickListener) {
		this.mItemClickListener = itemClickListener;
	}

	private void setCount() {
		this.mCount = 0;
	}

	public void addAll(List<Type> items) {
		mHides.clear();
		mItems.clear();
		addType(items);
		notifyDataSetChanged();
	}

	private void addType(List<Type> items) {
		for (Type item : items) {
			if (item.isHidden()) mHides.add(item);
			else mItems.add(item);
		}
	}

	private void deselect() {
		for (Type type : mItems) {
			for (Channel channel : type.getChannel()) channel.deselect();
		}
	}

	public void onItemClick(Channel item) {
		deselect();
		item.select();
		notifyDataSetChanged();
		mItemClickListener.onItemClick(item);
	}

	public void resetUrl() {
		for (Type type : mItems) for (Channel channel : type.getChannel()) channel.setReal("");
		notifyDataSetChanged();
		//setChannel(0);
	}

	public void addCount() {
		if (mHides.isEmpty() || ++mCount < 5) return;
		mItems.addAll(mHides);
		notifyDataSetChanged();
		Notify.show(R.string.channel_unlock);
		mHides.clear();
		setCount();
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	@NonNull
	@Override
	public TypeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new TypeHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_type, parent, false), this);
	}

	@Override
	public void onBindViewHolder(@NonNull TypeHolder holder, int position) {
		holder.setType(mItems.get(position));
	}
}
